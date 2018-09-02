package StoreByDirectedGraph;


/**
 * For future reference:
 * turningStatus[0] contains information for threads coming from the left bottom part of cell.
 * turningStatus[1] contains information for threads coming from the right bottom part of cell.
 * threads[0] is the thread comes from the left bottom part of cell.
 * threads[1] is the thread comes from the right bottom part of cell.
 * neighbors[0] is the neighbor on the left.
 * neighbors[1] is the neighbor on the right.
 * Left/Right Child is the Left/Right CellNode above the Node.
 */

public class CellNode {
    private TurningStatus[] turningStatus;
    private CrossingStatus crossingStatus;
    private TurningRule turningRule;
    private CrossingRule crossingRule;
    private CellNode[] neighbors;
    private int[] threads;

    @Override
    public String toString() {
        return "" + turningStatus[0].toString().charAt(0) + turningStatus[1].toString().charAt(0) + crossingStatus.toString().charAt(0);
    }

    public int[] getThreads() {
        return threads;
    }

    public void setThreads(int[] threads) {
        this.threads = threads;
    }


    public CellNode(CellNode[] neighbors, TurningRule turningRule, CrossingRule crossingRule, Generator.MapContainer mapContainer, boolean drawGraph) {
        this.neighbors = neighbors;
        this.turningRule = turningRule;
        this.crossingRule = crossingRule;
//        System.out.println(neighbors[0]+" "+neighbors[1]);
        this.turningStatus = turningRule.generateStatus(this);
//        System.out.println(this.turningStatus[0].toString() + " " + this.turningStatus[1].toString());
        if (turningStatus[0] == turningStatus[1] && turningStatus[1] == TurningStatus.Slanted) {
            this.crossingStatus = crossingRule.generateStatus(this);
        } else {
            this.crossingStatus = CrossingStatus.NoCross;
        }
        generateThreads();
        if (drawGraph) {
            if (crossingStatus == CrossingStatus.LeftTop) {
                mapContainer.theMap[threads[1]][threads[0]] = 1;
            } else if (crossingStatus == CrossingStatus.RightTop) {
                mapContainer.theMap[threads[0]][threads[1]] = 1;
            }
        }
    }

    private void generateThreads() {
        threads = new int[2];
        if (neighbors[0].getTurningStatus()[0] == TurningStatus.Slanted) {
            threads[0] = neighbors[0].getThreads()[0];
            //System.out.println(threads[0] + " comes slanted from left.");
        } else if (neighbors[0].getTurningStatus()[1] == TurningStatus.Upright) {
            threads[0] = neighbors[0].getThreads()[1];
            //System.out.println(threads[0] + " comes upright from left.");
        } else {
            threads[0] = -1;
        }
        if (neighbors[1].getTurningStatus()[0] == TurningStatus.Upright) {
            threads[1] = neighbors[1].getThreads()[0];
            //System.out.println(threads[1] + " comes upright from right.");
        } else if (neighbors[1].getTurningStatus()[1] == TurningStatus.Slanted) {
            threads[1] = neighbors[1].getThreads()[1];
            //System.out.println(threads[1] + " comes slanted from right.");
        } else {
            threads[1] = -1;
        }
    }

    public CellNode(int[] threads, TurningStatus[] turningStatus, CrossingStatus crossingStatus, boolean drawGraph, Generator.MapContainer mapContainer) {
        this.turningStatus = turningStatus;
        this.crossingStatus = crossingStatus;
        this.threads = threads;
        if (drawGraph) {
            if (crossingStatus == CrossingStatus.LeftTop) {
                mapContainer.theMap[threads[1]][threads[0]] = 1;
            } else if (crossingStatus == CrossingStatus.RightTop) {
                mapContainer.theMap[threads[0]][threads[1]] = 1;
            }
        }
    }

    public CellNode[] getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(CellNode[] neighbors) {
        this.neighbors = neighbors;
    }

    public void updateSelf() {
        this.crossingStatus = crossingRule.generateStatus(this);
        this.turningStatus = turningRule.generateStatus(this);
    }


    public CrossingStatus getCrossingStatus() {
        return crossingStatus;
    }

    public String printSelf() {
        return ("[" + turningStatus[0] + " " + turningStatus[1] + " " + crossingStatus + "]");
    }


    /*
    TODO: FIX THE DISPLAY OF UPRIGHT (CONSIDER REMOVE SOME SPACES)
     */
    public String getFirstLine() {
        String firsthalf = " ";
        String secondhalf = "";
        switch (turningStatus[0]) {
            case No:
                firsthalf += "      ";
                break;
            case Slanted:
                secondhalf += "  / /";
                break;
            case Upright:
                firsthalf += "|||  ";
                break;
        }
        switch (turningStatus[1]) {
            case No:
                secondhalf += "      ";
                break;
            case Slanted:
                firsthalf += "\\ \\  ";
                break;
            case Upright:
                secondhalf += "  |||";
                break;
        }
        return firsthalf + secondhalf + " ";
    }

    public String getSecondLine() {
        String secondline = "";
        switch (crossingStatus) {
            case LeftTop:
                secondline = "    \\\\\\\\    ";
                break;
            case NoCross:
                if (turningStatus[0].equals(TurningStatus.Upright)) {
                    secondline += " |||  ";
                } else {
                    secondline += "      ";
                }
                if (turningStatus[1].equals(TurningStatus.Upright)) {
                    secondline += "  ||| ";
                } else {
                    secondline += "      ";
                }
                break;
            case RightTop:
                secondline = "    ////    ";
                break;
        }
        return secondline;
    }

    public String getThirdLine() {
        String firsthalf = " ";
        String secondhalf = "";
        switch (turningStatus[0]) {
            case No:
                firsthalf += "      ";
                break;
            case Slanted:
                firsthalf += "/ /  ";
                break;
            case Upright:
                firsthalf += "|||  ";
                break;
        }
        switch (turningStatus[1]) {
            case No:
                secondhalf += "      ";
                break;
            case Slanted:
                secondhalf += "  \\ \\";
                break;
            case Upright:
                secondhalf += "  |||";
                break;
        }
        return firsthalf + secondhalf + " ";
    }

    public TurningStatus[] getTurningStatus() {
        return turningStatus;
    }
}
