import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class Main {
    public static int cnt = 0;
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("input.txt"));
        if (args.length > 0) {
            br = new BufferedReader(new FileReader(args[0]));
        }
        String line = br.readLine();
        int n, s = 0, t = 0;
        n = Integer.parseInt(line);
        int[][] mat = new int[n][n];
        int[] check = new int[n * n];
        for (int i = 0; i < n; i++) {
            line = br.readLine();
            line = line.trim();
            String[] p = line.split("\\s+|\\t+");
            for (int j = 0; j < n; j++) {
                if (p[j].equals("0")) {
                    mat[i][j] = 0;
                    check[i * n + j] = 0;
                    s = i;
                    t = j;
                } else {
                    mat[i][j] = Integer.parseInt(p[j]);
                    check[i * n + j] = Integer.parseInt(p[j]);
                }
            }
        }

        br.close();

        int inv = 0;
        for (int i = 0; i < n * n; i++) {
            for (int j = i + 1; j < n * n; j++)
                if (check[i] > 0 && check[j] > 0 && check[i] > check[j])
                    inv++;
        }

        boolean solvable = true;
        if (n % 2 == 1) {
            if (inv % 2 == 1)
                solvable = false;
        } else {
            int pos = n - s;

            if (pos % 2 == 1) {
                if (inv % 2 != 0)
                    solvable = false;
            } else {
                if (inv % 2 != 1)
                    solvable = false;
            }
        }

        if (!solvable) {
            System.out.println("No solution");
        } else {
            Comparator<Node> customComparator = Comparator.comparingInt(o -> o.getPriority() + o.getMoves());

            PriorityQueue<Node> pq1 = new PriorityQueue<>(customComparator);
            PriorityQueue<Node> pq2 = new PriorityQueue<>(customComparator);
            HashSet<NodeMini> visited = new HashSet<>();
            pq1.add(new Node(s, t, mat, calcHamming(mat, s, t, n), 0, null));
            pq2.add(new Node(s, t, mat, calcManhattan(mat, s, t, n), 0, null));
            Node result;


            System.out.println("Generating solution using A* with Hamming distance heuristic...");
            System.out.println();
            System.out.println("Initial state:");
            printMat(mat);
            double hamTime, manTime;
            int hamCnt = 0, manCnt = 0;


            double start = System.currentTimeMillis();
            while (true) {
                Node node1 = pq1.poll();
                if (node1.getPriority() == 0) {
                    System.out.println("Total moves: " + node1.getMoves());
                    System.out.println();
                    result = node1;
                    break;
                }
                visited.add(new NodeMini(node1.getMat()));
                makeNodes(node1, pq1, n, visited, 1);
//                System.out.println("States generated: " + cnt);
            }
            double end = System.currentTimeMillis();
            hamTime = (end - start) / 1000;
            hamCnt = cnt;
            printSolution(result, n);

            cnt = 0;
            visited.clear();
            System.out.println("Generating solution using A* with Manhattan distance heuristic...");
            System.out.println();
            System.out.println("Initial state:");
            printMat(mat);
            start = System.currentTimeMillis();
            while (true) {
                Node node2 = pq2.poll();
                if (node2.getPriority() == 0) {
                    System.out.println("Total moves: " + node2.getMoves());
                    System.out.println();
                    result = node2;
                    break;
                }
                visited.add(new NodeMini(node2.mat));
                makeNodes(node2, pq2, n, visited, 0);
            }
            end = System.currentTimeMillis();
            manTime = (end - start) / 1000;
            manCnt = cnt;
            printSolution(result, n);

            System.out.println("Hamming distance heuristic time: " + hamTime + " seconds");
            System.out.println("Manhattan distance heuristic time: " + manTime + " seconds");

            System.out.println("Hamming distance heuristic states generated: " + hamCnt);
            System.out.println("Manhattan distance heuristic states generated: " + manCnt);
        }
    }

    private static void printSolution(Node st, int n) {
        Node result = st;
        LinkedList<Node> states = new LinkedList<>();
        while (result.parent != null) {
            states.addFirst(result);
            result = result.parent;
        }
        for (Node state : states) {
            System.out.println(state.getMove());
            printMat(state.getMat());
        }
        System.out.println();
        System.out.println();
    }

    private static int calcHamming(int[][] mat, int s, int t, int n) {
        int hamming = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (mat[i][j] != 0) {
                    if (i * n + j != mat[i][j] - 1) {
                        hamming++;
                    }
                }
            }
        }

        return hamming;
    }

    private static int calcManhattan(int[][] mat, int s, int t, int n) {
        int manhattan = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (mat[i][j] != 0) {
                    int row = (mat[i][j] - 1) / n;
                    int column = (mat[i][j] - 1) % n;
                    manhattan += Math.abs(row - i) + Math.abs(column - j);
                }
            }
        }

        return manhattan;
    }

    public static void makeNodes(Node n, PriorityQueue<Node> pq, int l, HashSet<NodeMini> visited, int choice) {
        int s = n.row;
        int t = n.column;
        if (s < l - 1) {
            addNode(s+1, t, s, t, l, pq, choice, visited, n);
        }
        if (s > 0) {
            addNode(s-1, t, s, t, l, pq, choice, visited, n);
        }
        if (t < l - 1) {
            addNode(s, t+1, s, t, l, pq, choice, visited, n);
        }
        if (t > 0) {
            addNode(s, t-1, s, t, l, pq, choice, visited, n);
        }
    }

    public static void swap(int[][] mat, int s, int t, int i, int j) {
        int temp = mat[s][t];
        mat[s][t] = mat[i][j];
        mat[i][j] = temp;
    }

    public static void printMat(int[][] mat) {
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat.length; j++) {
                System.out.print(mat[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static int hamCond(int[][] mat, int s, int t, int l) {
        if (s * l + t != mat[s][t] - 1) {
            return 1;
        }
        return 0;
    }

    public static int manCond(int[][] mat, int s, int t, int l) {
        int row = (mat[s][t] - 1) / l;
        int column = (mat[s][t] - 1) % l;
        return Math.abs(row - s) + Math.abs(column - t);
    }

    public static void addNode(int i, int j, int s, int t, int l, PriorityQueue<Node> pq,
                               int choice, HashSet<NodeMini> visited, Node parent) {
        int[][] pmat = parent.getMat();
        int mat[][] = new int[l][l];
        for (int k = 0; k < l; k++) {
            for (int m = 0; m < l; m++) {
                mat[k][m] = pmat[k][m];
            }
        }
        int p = parent.priority;
        if (choice == 1) {
            p -= hamCond(mat, i, j, l);
        } else {
            p -= manCond(mat, i, j, l);
        }
        swap(mat, s, t, i, j);
        if (!visited.contains(new NodeMini(mat))) {
            if (choice == 1) {
                p += hamCond(mat, s, t, l);
            } else {
                p += manCond(mat, s, t, l);
            }
            cnt++;
            Node node = new Node(i, j, mat, p, parent.moves + 1, parent);
            node.setMove(mat[s][t] + " is moved from (" + i + "," + j + ") to (" + s + "," + t + ")");
            pq.add(node);
        }
    }
}
