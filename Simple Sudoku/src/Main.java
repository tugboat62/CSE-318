import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws IOException {
        int heuristic = 5;
        int forwardChecking = 1;
        String fileName = "data/d-10-09.txt";
        if (args.length == 3) {
            heuristic = Integer.parseInt(args[0]);
            forwardChecking = Integer.parseInt(args[1]);
            fileName = args[2];
        }
        System.out.println();
        System.out.println("Heuristic: " + heuristic);
        System.out.println("Forward Checking: " + forwardChecking);
        System.out.println("File: " + fileName);
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        HashMap<String, Assignment> assignments = new HashMap<>();
        int unassigned = 0;
        String line = br.readLine();
        int n;
        n = Integer.parseInt(line);
        int[][] mat = new int[n][n];
        for (int i = 0; i < n; i++) {
            line = br.readLine();
            line = line.trim();
            String[] p = line.split("\\s*,\\s+|\\t+");
            for (int j = 0; j < n; j++) {
                mat[i][j] = Integer.parseInt(p[j]);
                if (p[j].equals("0")) {
                    unassigned++;
                    assignments.put((new Pair(i,j)).toString(), new Assignment(i, j, 0, n));
                }
            }
        }
        br.close();

        for (Assignment a: assignments.values()) {
            a.forwardDegree++;
            for (int j = 0; j < n; j++) {
                if (j != a.col){
                    if(mat[a.row][j] != 0) {
                        a.remove(mat[a.row][j]);
                    } else {
                        a.forwardDegree++;
                    }
                }
                if (j != a.row) {
                    if (mat[j][a.col] != 0) {
                        a.remove(mat[j][a.col]);
                    } else {
                        a.forwardDegree++;
                    }
                }
            }
        }

        for (Assignment a: assignments.values()) {
            for (Assignment b: assignments.values()) {
                if(a.row != b.row && a.col == b.col) {
                    a.neighbors.add(b);
                }
                if(a.row == b.row && a.col != b.col) {
                    a.neighbors.add(b);
                }
            }
        }
//        for (Assignment a: assignments.values()) {
//            System.out.println(a.row + ", " + a.col + " -> domain length: " + a.domain.length);
//            System.out.println("Domain: "+ Arrays.toString(a.domain));
//            System.out.println("Forward Degree: " + a.forwardDegree);
//            System.out.println();
//        }

        BacktrackingAndForwardChecking backtracking = new BacktrackingAndForwardChecking(mat, n, unassigned, assignments, heuristic, forwardChecking);
        double start = System.currentTimeMillis();
        backtracking.backTrackingSearch();
        double end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) / 1000 + " seconds");
        System.out.println("Total Nodes: " + backtracking.nodes);
        System.out.println("Total backtracks: " + backtracking.backtracks + "\n");
    }
}
