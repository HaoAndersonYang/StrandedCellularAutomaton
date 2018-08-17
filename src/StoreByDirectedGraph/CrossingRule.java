package StoreByDirectedGraph;

import java.util.HashMap;

public class CrossingRule {

    public HashMap<CrossingStatus, Integer> tagMap;
    public HashMap<CrossingStatus, Integer> offsetMap;
    public boolean[] crossingRule;

    public CrossingRule(int rule) {
        tagMap = new HashMap<>();
        offsetMap = new HashMap<>();
        String binaryString = Integer.toBinaryString(rule);
        tagMap.put(CrossingStatus.RightTop, 0);
        tagMap.put(CrossingStatus.NoCross, 1);
        tagMap.put(CrossingStatus.LeftTop, 2);
        offsetMap.put(CrossingStatus.RightTop, 2);
        offsetMap.put(CrossingStatus.NoCross, 1);
        offsetMap.put(CrossingStatus.LeftTop, 0);
        crossingRule = new boolean[9];
        for (int i = 0; i < binaryString.length(); i++) {
            crossingRule[i] = (binaryString.charAt(binaryString.length() - i - 1) == '1');
        }
        for (int i = binaryString.length(); i < 9; i++) {
            crossingRule[i] = false;
        }
    }


    public CrossingStatus generateStatus(CellNode target) {
        CellNode[] neighbors = target.getNeighbors();
        CrossingStatus left = neighbors[0].getCrossingStatus();
        CrossingStatus right = neighbors[1].getCrossingStatus();
        int tag = tagMap.get(left);
        int offset = offsetMap.get(right);
        int configure = tag * 3 + offset;
        if (crossingRule[configure]) {
            return CrossingStatus.RightTop;
        } else {
            return CrossingStatus.LeftTop;
        }
    }
}
