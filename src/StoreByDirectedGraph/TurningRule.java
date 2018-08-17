package StoreByDirectedGraph;

import java.util.HashMap;

public class TurningRule {

    public HashMap<TurningStatus, Integer> statusMap;
    public boolean[] turningRule;

    public TurningRule(int rule) {
        statusMap = new HashMap<>();
        statusMap.put(TurningStatus.Slanted, 2);
        statusMap.put(TurningStatus.No, 1);
        statusMap.put(TurningStatus.Upright, 0);
        String binaryString = Integer.toBinaryString(rule);
        turningRule = new boolean[9];
        for (int i = 0; i < binaryString.length(); i++) {
            turningRule[i] = (binaryString.charAt(binaryString.length() - i - 1) == '1');
        }
        for (int i = binaryString.length(); i < 9; i++) {
            turningRule[i] = false;
        }
//        for (int i = 0; i < turningRule.length; i++) {
//            System.out.println(turningRule[i]);
//        }
    }

    /*
    Generate the turning status for the target cell.
    Due to the turning rule is 8 bit and each bit corresponds to a neighbor composition,
    the leftNeighbor's right status is treated as "tag" and the rightNeighbor's left status is treated as "offset".
     */
    public TurningStatus[] generateStatus(CellNode target) {
        TurningStatus[] result = new TurningStatus[2];
        CellNode[] neighbors = target.getNeighbors();
        TurningStatus left;
        TurningStatus right;

        if (neighbors[0].getTurningStatus()[0] == TurningStatus.Slanted) {
            left = neighbors[0].getTurningStatus()[0];
        } else if (neighbors[0].getTurningStatus()[1] == TurningStatus.Upright) {
            left = neighbors[0].getTurningStatus()[1];
        } else {
            left = TurningStatus.No;
        }
        if (neighbors[1].getTurningStatus()[1] == TurningStatus.Slanted) {
            right = neighbors[1].getTurningStatus()[1];
        } else if (neighbors[1].getTurningStatus()[1] == TurningStatus.Upright) {
            right = neighbors[1].getTurningStatus()[0];
        } else {
            right = TurningStatus.No;
        }
        int tag = statusMap.get(left);
        int offset = statusMap.get(right);
        int configure = tag * 3 + offset;
        //The current configure result in slanted
        if (turningRule[configure]) {
            //Check if left status is No
            if (tag != 1) {
                result[0] = TurningStatus.Slanted;
            } else {
                result[0] = TurningStatus.No;
            }
            //Check if right status is No
            if (offset != 1) {
                result[1] = TurningStatus.Slanted;
            } else {
                result[1] = TurningStatus.No;
            }
        } else {
            //Check if left status is No
            if (tag != 1) {
                result[0] = TurningStatus.Upright;
            } else {
                result[0] = TurningStatus.No;
            }
            //Check if right status is No
            if (offset != 1) {
                result[1] = TurningStatus.Upright;
            } else {
                result[1] = TurningStatus.No;
            }
        }
        return result;
    }
}
