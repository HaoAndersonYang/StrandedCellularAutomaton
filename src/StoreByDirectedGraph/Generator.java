package StoreByDirectedGraph;

import java.util.HashMap;

public class Generator {
    private TurningRule turningRule;
    private CrossingRule crossingRule;

    private String[] input;
    CellNode[] checkingRow;
    CellNode[] currentRow;
    CellNode[] initialRow;
    CellNode[] drawingRow;
    int[][] threadsMap;
    int[] availableThreads;
    boolean checkingDuplicate;

    int width;
    int height;
    private HashMap<Character, TurningStatus> turningStatusHashMap;
    private HashMap<Character, CrossingStatus> crossingStatusHashMap;
    private BruteForceSearch.InitialConditionContainer initialConditionContainer;
    private BruteForceSearch.DuplicateConditionContainer duplicateConditionContainer;

    public Generator(BruteForceSearch.InitialConditionContainer initialConditionContainer, BruteForceSearch.DuplicateConditionContainer dCC, CrossingRule crossingRule, TurningRule turningRule, String[] input, int width, int height, HashMap<Character, TurningStatus> turningStatusHashMap, HashMap<Character, CrossingStatus> crossingStatusHashMap) throws Exception {
        this.initialConditionContainer = initialConditionContainer;
        this.duplicateConditionContainer = dCC;
        this.turningRule = turningRule;
        this.crossingRule = crossingRule;
        this.input = input;
        checkingRow = new CellNode[width];
        currentRow = new CellNode[width];
        threadsMap = new int[2 * width][2 * width];
        //Mark the threads that exist
        availableThreads = new int[2 * width];
        this.width = width;
        this.height = height;
        this.turningStatusHashMap = turningStatusHashMap;
        this.crossingStatusHashMap = crossingStatusHashMap;
        checkingDuplicate = true;
    }


    public void initialize(boolean checkCycle) throws Exception {
        //Set up the initial row
        for (int col = 0; col < width; col++) {
            TurningStatus[] turningStatuses = new TurningStatus[2];
            int[] threads = new int[2];
            turningStatuses[0] = turningStatusHashMap.get(input[col].charAt(0));
            turningStatuses[1] = turningStatusHashMap.get(input[col].charAt(1));
            if (turningStatuses[0] != TurningStatus.No) {
                threads[0] = col * 2;
                availableThreads[col * 2] = 1;
                //System.out.println("Thread " + threads[0] + " comes in from col " + col);
            } else {
                //For debugging
                threads[0] = -1;
            }
            if (turningStatuses[1] != TurningStatus.No) {
                threads[1] = col * 2 + 1;
                availableThreads[col * 2 + 1] = 1;
                //System.out.println("Thread " + threads[1] + " comes in from col " + col);
            } else {
                //For debugging
                threads[1] = -1;
            }
            CrossingStatus crossingStatus = crossingStatusHashMap.get(input[col].charAt(2));
            //If the crossing status is not "No Crossing" and we do not have 2 threads, the initial condition must be wrong.
            //Display a message that indicates where and what the problem is.
            if (threads[0] + threads[1] != col * 4 + 1 && crossingStatus != CrossingStatus.NoCross) {
                throw new Exception("Incorrect Crossing Status and Turning Status at initial row, col " + col + ".\n");
            }
            currentRow[col] = new CellNode(threads, turningStatuses, crossingStatus, !checkCycle, new MapContainer(threadsMap));
        }
        //Initialize checking row to be row 0.
        if (height - 1 == 0) {
            drawingRow = currentRow;
        }
        //Keep Row 0 as reference. It act as the first checking row and should be restored.
        initialRow = currentRow;
        checkingRow = currentRow;
        currentRow = nextRow(currentRow, 1, false);
    }

    /**
     * Implemented the first part of Brent's algorithm.
     * Since we only care about whether the threads holds together or not, we do not need to find the length of the cycle.
     * We only need to make sure that we find the cycle so that we can decide if the pattern holds or not.
     *
     * @param checkCycle
     * @return If a cycle length is found, return the cycle length. If we are not finding the cycle, return -1
     * @throws Exception
     */
    public int generateCell(boolean checkCycle) throws Exception {
        initialize(checkCycle);
        int power = 1, lam = 1, row = 1, checking = 0;
        //Treat row 0 as the bottom of the grid. Our current row is row 1.
        if (checkCycle) {
            while (!(checking % 2 == row % 2 && compareRow(currentRow, checkingRow))) {
                // We compare currentRow and the checking row to see if they are equal.
                // If the currentRow (Hare) is equal to checkingRow (Tortoise), we break.
                // If exactly one of the two rows are wrapping around, we consider them to be different rows.
                if (power == lam) {
                    checkingRow = currentRow;
                    checking = row;
                    power *= 2;
                    lam = 0;
                }
                if (currentRow == null) {
                    return -1;
                }
                currentRow = nextRow(currentRow, row + 1, false);
                row++;
                lam++;
            }
            checkingDuplicate = false;
            // Generate the directed graph based on the cycle
            for (int i = 0; i <= lam; i++) {
                currentRow = nextRow(currentRow, row + 1, true);
                row++;
            }
        }
        // Notice that if the row generated are less than the minimum height required by the user, we keep generating until we
        // fulfill the requirement.
        while (row < height) {
            // If we checkCycle, we do not need to deal with graph now
            // If we don't check the holding-together property based on cycle, we fill the graph
            currentRow = nextRow(currentRow, row + 1, !checkCycle);
            row++;
        }
        if (checkCycle) {
            return lam;
        }
        return -1;
    }

    /**
     * The method returns the next row based on inputRow;
     *
     * @param inputRow The currentRow
     * @param row      An integer contains the number of row we are generating
     * @return toGenerate, which is a CellNode array contains the row we generated.
     */
    public CellNode[] nextRow(CellNode[] inputRow, int row, boolean drawGraph) {
        MapContainer mapContainer = new MapContainer(threadsMap);
        CellNode[] toGenerate = new CellNode[width];
        for (int col = 0; col < width; col++) {
            //System.out.println("Generating cell for row " + row + " col " + col);
            if (row % 2 == 0) {
                // If row % 2 = 0, it means that the current row is not wrapping around.
                CellNode[] neighbors = new CellNode[2];
                neighbors[0] = inputRow[(col - 1 + width) % width];
                neighbors[1] = inputRow[col];
                toGenerate[col] = new CellNode(neighbors, turningRule, crossingRule, mapContainer, drawGraph);
            } else {
                // If row % 2 = 1, it means that the current row is wrapping around.
                CellNode[] neighbors = new CellNode[2];
                neighbors[0] = inputRow[col];
                neighbors[1] = inputRow[(col + 1) % width];
                toGenerate[col] = new CellNode(neighbors, turningRule, crossingRule, mapContainer, drawGraph);
            }
        }
        if (row == height - 1) {
            drawingRow = toGenerate;
        }
        String rowString = "";
        for (int i = 0; i < width; i++) {
            rowString += toGenerate[i].toString();
        }
        if (initialConditionContainer.iCM.containsKey(rowString) && checkingDuplicate) {
            if (initialConditionContainer.iCM.get(rowString) == 1) {
                return null;
            } else {
                duplicateConditionContainer.dCA.add(rowString);
            }
        }
        return toGenerate;
    }

    // Compare whether two row of cells are equal. O(Width) efficiency obviously
    public boolean compareRow(CellNode[] row1, CellNode[] row2) {
        if (row1 == null || row2 == null) {
            return false;
        }
        if (row1.length != row2.length) {
            //This shall not happen if the program is generating everything correctly
            return false;
        }
        for (int i = 0; i < row1.length; i++) {
            if (row1[i].getCrossingStatus() == row2[i].getCrossingStatus()
                    && row1[i].getTurningStatus()[0] == row2[i].getTurningStatus()[0]
                    && row1[i].getTurningStatus()[1] == row2[i].getTurningStatus()[1]
                    && row1[i].getThreads()[0] == row2[i].getThreads()[0]
                    && row1[i].getThreads()[1] == row2[i].getThreads()[1]
            ) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    public void displayPattern() {
        Drawer drawer = new Drawer(height, width, drawingRow);
        drawer.displayGraph();
    }


    //Inner Class for StoreByArray.Cell to update the map
    class MapContainer {
        public int[][] theMap;

        public MapContainer(int[][] threadsMap) {
            this.theMap = threadsMap;
        }
    }


    public int[][] getThreadsMap() {
        return threadsMap;
    }

    public int[] getAvailableThreads() {
        return availableThreads;
    }
}
