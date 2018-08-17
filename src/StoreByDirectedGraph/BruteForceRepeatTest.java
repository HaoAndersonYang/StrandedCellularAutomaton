package StoreByDirectedGraph;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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

    public static void main(String[] args) throws Exception {
        turningStatusHashMap.put('U', TurningStatus.Upright);
        turningStatusHashMap.put('S', TurningStatus.Slanted);
        turningStatusHashMap.put('N', TurningStatus.No);
        crossingStatusHashMap.put('L', CrossingStatus.LeftTop);
        crossingStatusHashMap.put('R', CrossingStatus.RightTop);
        crossingStatusHashMap.put('N', CrossingStatus.NoCross);
        cellStatusHashMap.put(0, "SSR");
        cellStatusHashMap.put(1, "SSL");
        cellStatusHashMap.put(2, "SNN");
        cellStatusHashMap.put(3, "NSN");
        cellStatusHashMap.put(4, "NNN");
        // We currently fix the turning rule to be 511, which will ensure that the output will be slanted.
        int turningRuleInput = 511;
        turningRule = new TurningRule(turningRuleInput);
        Scanner s = new Scanner(System.in);
        System.out.println("Please enter the width:");
        int width = s.nextInt();
        repeatTest(width, true);
//        Uncomment the following lines to repeatedly generate output
//        for (int i = 2; i <= width; i++) {
//            repeatTest(i, false);
//        }
    }

    public static void repeatTest(int width, boolean writeFile) throws Exception {
        holdsTogetherPattern = new ArrayList<>();
        maxCycle = 0;
        maxCycleCrossingRule = 0;
        String[] maxCycleCondition = new String[width];
        if (writeFile) {
            writer = new PrintWriter("Output for width = " + width + ".txt", "UTF-8");
        }
        double totalCount = Math.pow(5, width - 1);
        for (int crossingRuleNum = 0; crossingRuleNum < 512; crossingRuleNum++) {
            int holdtogether = 0;
            if (writeFile) {
                writer.println("Crossing Rule " + crossingRuleNum + ":");
            }
            double count = 0;
            CrossingRule crossingRule = new CrossingRule(crossingRuleNum);
            HashMap<String[], Boolean> initialConditionMap = new HashMap<>();
            // Use the n,k gray code to enumerate the possible combinations
            // Credit to Generalized Gray Codes with Applications by DAH-JYH GUAN
            int N = 5;
            int[] n = new int[width]; /* the maximum for each digit */
            int[] g = new int[width]; /* the Gray code */
            int[] u = new int[width]; /* +1 or −1 */
            int i, k;

            for (i = 0; i < width; i++) {
                g[i] = 0;
                u[i] = 1;
                n[i] = N;
            }
            while (true) {
                String[] input = new String[width];
                input[0] = "SSR";
                //Convert the current gray code to initial condition
                for (int j = 1; j < width; j++) {
                    input[j] = cellStatusHashMap.get(g[j - 1]);
                }
                initialConditionMap.put(input, false);
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
            double progress = 0;
            for (String[] input : initialConditionMap.keySet()) {
                //Enable me check progress
//                if (count / totalCount > 0.1 + progress) {
//                    progress = progress + 0.1;
//                    System.out.println("Generated " + (Math.round(progress * 100)) + "% for rule " + crossingRuleNum + " on width " + width);
//                    System.out.println("Total Progress is " + (Math.round(((double) crossingRuleNum) / 511.0 * 100.0)) + "%");
//                }

//                System.out.println();

                //Generate the cells
                Generator cellGenerator = new Generator(crossingRule, turningRule, input, width, 1, turningStatusHashMap, crossingStatusHashMap);
                //Update max cycle length
                int cycle = cellGenerator.generateCell(true);
                if (cycle > maxCycle) {
                    maxCycleCrossingRule = crossingRuleNum;
                    maxCycleCondition = input;
                    maxCycle = cycle;
                }
                //Check whether holds together or not
                ThreadHoldChecker threadHoldChecker = new ThreadHoldChecker(cellGenerator.getThreadsMap(), cellGenerator.getAvailableThreads());
                boolean hold = threadHoldChecker.stronglyConnectedCheck(false);
                if (hold && writeFile) {
                    holdtogether++;
                    for (int j = 0; j < width; j++) {
                        writer.print(input[j] + " ");
                    }
                    writer.print("Cycle is: " + cycle);
                    writer.println();
                }
                count++;
            }
            if (writeFile) {
                writer.println("For Crossing Rule " + crossingRuleNum + ". There are " + holdtogether + " initial conditions that make it holds together.\n\n");
            }
        }

        String cycleLengthOutput = "";
        cycleLengthOutput += "The maximum cycle of width " + width + " is " + maxCycle + ".\n";
        cycleLengthOutput += "It is reached with initial condition ";
        for (int j = 0; j < width; j++) {
            cycleLengthOutput += maxCycleCondition[j] + " ";
        }
        cycleLengthOutput += "and crossing rule #" + maxCycleCrossingRule;
        System.out.println(cycleLengthOutput);
        if (writeFile) {
            writer.println(cycleLengthOutput);
            writer.close();
        }
    }
}
