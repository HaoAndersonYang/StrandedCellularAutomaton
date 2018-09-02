package StoreByDirectedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

/**
 * For future reference
 * ThreadsMap[i][j] = 1 Means that thread i is on top of thread j at some place.
 */

public class TextBasedInterface {


    public static void main(String[] args) throws Exception {
        //Set up hashmap for reading input.
        HashMap<Character, TurningStatus> turningStatusHashMap = new HashMap<>();
        HashMap<Character, CrossingStatus> crossingStatusHashMap = new HashMap<>();
        turningStatusHashMap.put('U', TurningStatus.Upright);
        turningStatusHashMap.put('S', TurningStatus.Slanted);
        turningStatusHashMap.put('N', TurningStatus.No);
        crossingStatusHashMap.put('L', CrossingStatus.LeftTop);
        crossingStatusHashMap.put('R', CrossingStatus.RightTop);
        crossingStatusHashMap.put('N', CrossingStatus.NoCross);

        //Read the input
        Scanner s = new Scanner(System.in);
        System.out.println("\nInput the Turning Rule (0~511)");
        int turningRuleInput = s.nextInt();
//        int turningRuleInput = 68;
        TurningRule turningRule = new TurningRule(turningRuleInput);

        System.out.println("\nInput the Crossing Rule (0~511)");
        int crossingRuleInput = s.nextInt();
//        int crossingRuleInput = 0;
        CrossingRule crossingRule = new CrossingRule(crossingRuleInput);

        System.out.println("\nInput the height of the grid");
        int height = s.nextInt();
//        int height = 18;

        System.out.println("\nInput the width of the grid (not less than 2)");
        int width = s.nextInt();
//        int width = 2;

        System.out.println("\nInput the initial configuration in the following form:");
        System.out.println("<Turning Left><Turning Right><Crossing> <Turning Left><Turning Right><Crossing>...");
        System.out.println("For the turning rules, please use U for Upright. S for Slanted. N for No Strand");
        System.out.println("For the crossing rules, please use N for NoCross. L for LeftTop. R for RightTop");
        String[] input = new String[width];
        for (int i = 0; i < width; i++) {
            input[i] = s.next();
//            input[i] = "SSR";
        }
//        input[1] = "SNN";
        BruteForceSearch.InitialConditionContainer initialConditionContainer = new BruteForceSearch.InitialConditionContainer(new HashMap<>());
        BruteForceSearch.DuplicateConditionContainer duplicateConditionContainer = new BruteForceSearch.DuplicateConditionContainer(new HashSet<>());

        Generator cellGenerator = new Generator(initialConditionContainer, duplicateConditionContainer, crossingRule, turningRule, input, width, height, turningStatusHashMap, crossingStatusHashMap);

        System.out.println("\nIf you want to decide whether the pattern holds together based on a full cycle enter 1. Otherwise enter 0.");

        int checkCycle = s.nextInt();
//        int checkCycle = 1;

        int cycleLength = cellGenerator.generateCell(checkCycle == 1);
        if (checkCycle == 1) {
            System.out.println("\nLength of the cycle is " + cycleLength);
        }

        ThreadHoldChecker threadHoldChecker = new ThreadHoldChecker(cellGenerator.getThreadsMap(), cellGenerator.getAvailableThreads());
        threadHoldChecker.stronglyConnectedCheck(true);

        System.out.println("\nDisplaying the graph in the pop up window. Closing the window will exit the program");

        cellGenerator.displayPattern();
//        for (int i = 0; i < 2 * width; i++) {
//            for (int j = 0; j < 2 * width; j++) {
//                System.out.print(threadsMap[i][j] + " ");
//            }
//            System.out.println();
//        }


    }


}
