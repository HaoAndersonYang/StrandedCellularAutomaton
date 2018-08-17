package StoreByDirectedGraph;

import java.util.ArrayList;

/**
 * Implemented Kosaraju's algorithm
 * Credit to https://en.wikipedia.org/wiki/Kosaraju%27s_algorithm
 */
public class ThreadHoldChecker {

    int[] visited;
    int[] assigned;
    ArrayList<Integer> connectivityChecklist;
    ArrayList[] componentsList;
    int[][] threadsMap;
    int[] availableThreads;

    public ThreadHoldChecker(int[][] threadsMap, int[] availableThreads) {
        this.threadsMap = threadsMap;
        this.availableThreads = availableThreads;
    }


    public boolean stronglyConnectedCheck(boolean displayOutput) {
        int size = availableThreads.length;
        connectivityChecklist = new ArrayList<>();
        componentsList = new ArrayList[size];
        visited = new int[size];
        assigned = new int[size];
        for (int i = 0; i < size; i++) {
            if (availableThreads[i] != 0) {
                //Perform DFS on the node
                visit(i);
            }
        }
        for (int node : connectivityChecklist) {
            assign(node, node);
        }
        int componentsCount = 0;
        for (int i = 0; i < size; i++) {
            if (componentsList[i] != null && availableThreads[i] != 0) {
                componentsCount++;
            }
        }
        if (componentsCount != 1) {
            if (displayOutput) {
                System.out.println("The pattern does not hold together and it has " + componentsCount + " separated parts.");
                System.out.println("The components are:");
                for (int i = 0; i < size; i++) {
                    if (componentsList[i] != null) {
                        for (Object threads : componentsList[i]) {
                            if (availableThreads[(int) threads] != 0) {
                                System.out.print("Thread#" + threads + " ");
                            }
                        }
                        System.out.println();
                    }
                }
            }
            return false;
        } else {
            if (displayOutput) {
                System.out.println("The pattern holds together");
            }
            return true;
        }
    }

    public void visit(int node) {
        if (visited[node] == 0) {
            //Mark as visited
            visited[node] = 1;
            //DFS
            for (int i = 0; i < availableThreads.length; i++) {
                if (threadsMap[node][i] == 1) {
                    visit(i);
                }
            }
            //Prepend the node
            connectivityChecklist.add(0, node);
        }
    }

    public void assign(int node, int root) {
        if (assigned[node] == 0) {
            assigned[node] = 1;
            if (componentsList[root] == null) {
                componentsList[root] = new ArrayList<Integer>();
            }
            componentsList[root].add(node);
            for (int i = 0; i < availableThreads.length; i++) {
                //Reverse Graph DFS
                if (threadsMap[i][node] == 1) {
                    assign(i, root);
                }
            }
        }
    }

}