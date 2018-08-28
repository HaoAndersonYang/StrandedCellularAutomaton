package StoreByDirectedGraph;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class BruteForceRepeatTest {
    static ArrayList<String[]> holdsTogetherPattern;
    static int maxCycle;
    static int maxCycleCrossingRule;
    static HashMap<Character, TurningStatus> turningStatusHashMap = new HashMap<>();
    static HashMap<Character, CrossingStatus> crossingStatusHashMap = new HashMap<>();
    static HashMap<Integer, String> cellStatusHashMap = new HashMap<>();
    static TurningRule turningRule;
    private static PrintWriter writer;
    private static int maxDistinctPatterns;
    private static int maxPatternCrossingRule;
    private static String[] maxCycleCondition;
    private static int totalCount;
    private static boolean onlyTwoStrands;

    public static void main(String[] args) throws Exception {
        turningStatusHashMap.put('U', TurningStatus.Upright);
        turningStatusHashMap.put('S', TurningStatus.Slanted);
        turningStatusHashMap.put('N', TurningStatus.No);
        crossingStatusHashMap.put('L', CrossingStatus.LeftTop);
        crossingStatusHashMap.put('R', CrossingStatus.RightTop);
        crossingStatusHashMap.put('N', CrossingStatus.NoCross);
        cellStatusHashMap.put(0, "SSR");
        cellStatusHashMap.put(1, "SSL");
        cellStatusHashMap.put(2, "NNN");
        cellStatusHashMap.put(3, "NSN");
        cellStatusHashMap.put(4, "SNN");
        // We currently fix the turning rule to be 511, which will ensure that the output will be slanted.
        int turningRuleInput = 511;
        turningRule = new TurningRule(turningRuleInput);
        Scanner s = new Scanner(System.in);
        System.out.println("Please enter the width:");
        int width = s.nextInt();
        System.out.println("If you want to only check the case where all cells will have two strands, enter 1. Otherwise enter 0");
        onlyTwoStrands = s.nextInt() == 1;
        repeatTest(width, true);
//        //Uncomment the following lines to repeatedly generate output
//        for (int i = 2; i <= width; i++) {
//            repeatTest(i, true);
//        }
    }


    /**
     * Shift the input string left by 1 bit
     *
     * @param input
     * @return
     */
    public static String[] rotateString(String[] input) {
        String[] result = new String[input.length];
        for (int i = 0; i < result.length - 1; i++) {
            result[i] = input[i + 1];
        }
        result[result.length - 1] = input[0];
        return result;
    }

    /**
     * @param input
     * @return
     */
    public static String arrayToString(String[] input) {
        String temp = "";
        for (int j = 0; j < input.length; j++) {
            temp += input[j];
        }
        return temp;
    }

    /**
     * @param width
     * @param N
     * @return
     */
    public static InitialConditionContainer generateInitialConditions(int width, int N) {
        HashMap<String, Integer> initialConditionMap = new HashMap<>();
        // Use the n,k gray code to enumerate the possible combinations
        // Credit to Generalized Gray Codes with Applications by DAH-JYH GUAN
        int[] n = new int[width]; /* the maximum for each digit */
        int[] g = new int[width]; /* the Gray code */
        int[] u = new int[width]; /* +1 or −1 */
        int i, k;

        for (i = 0; i < width; i++) {
            g[i] = 0;
            u[i] = 1;
            n[i] = N;
        }
        InitialConditionContainer iCC = new InitialConditionContainer(initialConditionMap);
        while (true) {
            String[] input = new String[width];
            input[0] = "SSR";
            //Convert the current gray code to initial condition
            for (int j = 1; j < width; j++) {
                input[j] = cellStatusHashMap.get(g[j - 1]);
            }

            iCC.iCM.put(arrayToString(input), 0);
            // Start enumerate next Gray code
            i = 0;
            k = g[0] + u[0];
            while (k >= n[i] || k < 0) {
                u[i] = -u[i];
                i++;
                k = g[i] + u[i];
            }
            g[i] = k;
            if (g[width - 1] != 0) {
                break;
            }
        }
        return iCC;
    }

    /**
     * @param width
     * @param writeFile
     * @throws Exception
     */
    public static void repeatTest(int width, boolean writeFile) throws Exception {
        holdsTogetherPattern = new ArrayList<>();
        maxCycle = 0;
        maxCycleCrossingRule = 0;
        maxDistinctPatterns = 0;
        maxPatternCrossingRule = 0;
        totalCount = 0;
        maxCycleCondition = new String[width];
        String twoStrands = " with all possibilities";
        if (onlyTwoStrands) {
            twoStrands = " when each cell always has two strands";
        }
        if (writeFile) {
            File toWrite = new File("output/" + twoStrands);
            toWrite.mkdirs();
            writer = new PrintWriter("output/" + twoStrands + "/Width = " + width + ".txt", "UTF-8");
        }
        InitialConditionContainer iCC;

        if (onlyTwoStrands) {
            iCC = generateInitialConditions(width, 2);
            // These will cover all cases when there will always be two strands in cell
            SearchCrossingRule(width, 0, iCC, writeFile);
            SearchCrossingRule(width, 1, iCC, writeFile);
            SearchCrossingRule(width, 4, iCC, writeFile);
            SearchCrossingRule(width, 5, iCC, writeFile);
            SearchCrossingRule(width, 64, iCC, writeFile);
            SearchCrossingRule(width, 65, iCC, writeFile);
            SearchCrossingRule(width, 68, iCC, writeFile);
            SearchCrossingRule(width, 69, iCC, writeFile);
            SearchCrossingRule(width, 256, iCC, writeFile);
            SearchCrossingRule(width, 257, iCC, writeFile);
            SearchCrossingRule(width, 260, iCC, writeFile);
            SearchCrossingRule(width, 261, iCC, writeFile);
            SearchCrossingRule(width, 320, iCC, writeFile);
            SearchCrossingRule(width, 321, iCC, writeFile);
            SearchCrossingRule(width, 324, iCC, writeFile);
            SearchCrossingRule(width, 325, iCC, writeFile);
        } else {
            iCC = generateInitialConditions(width, 5);
            for (int crossingRuleNum = 0; crossingRuleNum < 512; crossingRuleNum++) {
                SearchCrossingRule(width, crossingRuleNum, iCC, writeFile);
            }
        }

        String cycleLengthOutput = "For width " + width + " we have:\n";
        if (!onlyTwoStrands) {
            cycleLengthOutput += "The maximum cycle is " + maxCycle + ".\n";
            cycleLengthOutput += "It is reached with initial condition ";
            for (int j = 0; j < width; j++) {
                cycleLengthOutput += maxCycleCondition[j] + " ";
            }
            cycleLengthOutput += "and crossing rule #" + maxCycleCrossingRule + ".\n";
        }
//        cycleLengthOutput += "The maximum distinct patterns of width " + width + " is " + maxDistinctPatterns + ".\n";
//        cycleLengthOutput += "It is reached with crossing rule #" + maxPatternCrossingRule + "\n\n";
        cycleLengthOutput += "The number of total distinct pattern is " + totalCount + ".\n\n";
        System.out.println(cycleLengthOutput);
        if (writeFile) {
            writer.println(cycleLengthOutput);
            writer.close();
        }
    }

    /**
     * @param width
     * @param crossingRuleNum
     * @param iCC
     * @param writeFile
     * @throws Exception
     */
    public static void SearchCrossingRule(int width, int crossingRuleNum, InitialConditionContainer iCC, boolean writeFile) throws Exception {
        //Reset ICC
        for (String str : iCC.iCM.keySet()) {
            iCC.iCM.replace(str, 0);
        }
//            //Enable me check progress
//            System.out.println("Generating rule " + crossingRuleNum + " on width " + width);
//            System.out.println("Total Progress is " + (Math.round(((double) crossingRuleNum) / 511.0 * 100.0)) + "%");
//            System.out.println();
        int holdtogether = 0;
        if (writeFile) {
            writer.println("For crossing rule " + crossingRuleNum + ":");
            writer.println("The following initial conditions (if any) will produce distinct weaving patterns:");
        }
        CrossingRule crossingRule = new CrossingRule(crossingRuleNum);
        for (String inputStr : iCC.iCM.keySet()) {
            // Check if the initial condition (or its equivalent) is mapped before.
            boolean repeat = false;
            // The following line split input string into array for every 3 characters.
            String[] input = inputStr.split("(?<=\\G...)");
            for (int r = 0; r < width; r++) {
                input = rotateString(input);
                String temp = arrayToString(input);
                if (iCC.iCM.containsKey(temp) && iCC.iCM.get(temp) != 0) {
                    repeat = true;
                    break;
                }
            }

            if (repeat) {
//                    System.out.println("skip");
                continue;
            }
            //Convert string to array
            //Generate the cells
            Generator cellGenerator = new Generator(iCC, crossingRule, turningRule, input, width, 1, turningStatusHashMap, crossingStatusHashMap);
            //Update max cycle length
            int cycle = cellGenerator.generateCell(true);
            if (cycle > maxCycle) {
                maxCycleCrossingRule = crossingRuleNum;
                maxCycleCondition = input;
                maxCycle = cycle;
            }
            //Check whether holds together or not. If the generating program aborts, we skip this process
            if (cycle != -1) {
                ThreadHoldChecker threadHoldChecker = new ThreadHoldChecker(cellGenerator.getThreadsMap(), cellGenerator.getAvailableThreads());
                boolean hold = threadHoldChecker.stronglyConnectedCheck(false);
                if (hold && writeFile) {
//                    writer.print("Cycle is: " + cycle);
//                    writer.println();
                    for (int j = 0; j < width; j++) {
                        writer.print(input[j] + " ");
                    }
                    writer.println();
                    holdtogether++;
                }
            }
            // Rotate the input array and mark the equivalent initial condition as checked
            for (int r = 0; r < input.length; r++) {
                input = rotateString(input);
                String temp = arrayToString(input);
                if (iCC.iCM.containsKey(temp)) {
                    iCC.iCM.replace(temp, 1);
                }
            }
        }
        if (holdtogether > maxDistinctPatterns) {
            maxDistinctPatterns = holdtogether;
            maxPatternCrossingRule = crossingRuleNum;
        }
        totalCount += holdtogether;
        if (writeFile) {
            writer.println("For Crossing Rule " + crossingRuleNum + ". There are " + holdtogether + " distinct weaving patterns.\n\n");
        }
    }


    public static class InitialConditionContainer {
        HashMap<String, Integer> iCM;

        public InitialConditionContainer(HashMap<String, Integer> iCM) {
            this.iCM = iCM;
        }
    }

}
