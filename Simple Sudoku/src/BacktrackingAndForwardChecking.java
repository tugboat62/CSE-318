import java.util.*;

public class BacktrackingAndForwardChecking {

    int[][] mat;
    ArrayList<Assignment> solutions;
    HashMap<String, Assignment> unassignedMap;
    int unassigned;
    long backtracks = 0;
    long nodes = 0;
    int assigned = 0;
    int size;
    int heuristic;
    int forwardChecking;
    boolean failure = false;

    public BacktrackingAndForwardChecking(int[][] mat, int size, int unassigned, HashMap<String,
            Assignment> assignments, int heuristic, int forwardChecking) {
        this.mat = mat;
        solutions = new ArrayList<>();
        unassignedMap = assignments;
        this.forwardChecking = forwardChecking;
        this.unassigned = unassigned;
        this.size = size;
        this.heuristic = heuristic;
    }

    public void backTrackingSearch() {
//        System.out.println("Total unassigned: " + unassigned);
        ArrayList<Assignment> result = BackTrack();
        int[][] m = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(mat[i], 0, m[i], 0, size);
        }
        for (Assignment a : result) {
            m[a.row][a.col] = a.value;
        }
        if (assigned == unassigned) {
            if (check(m)) {
                System.out.println("Correct solution");
            } else System.out.println("Incorrect solution");
        } else {
            System.out.println("No solution found");
        }
        //printMat(m);
    }

    public boolean check(int[][] m) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (m[i][j] == 0) {
                    return false;
                }
                for (int k = 0; k < size; k++) {
                    if (k != j && m[i][k] == m[i][j]) {
                        return false;
                    }
                    if (k != i && m[k][j] == m[i][j]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void printMat(int[][] m) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(m[i][j] + " ");
            }
            System.out.println();
        }
    }

    private ArrayList<Assignment> BackTrack() {
        failure = false;
        if (solutions.size() == unassigned) {
            return solutions;
        }
        Assignment var = selectUnassignedVariable();
//        System.out.println("Selected: " + var.row + ", " + var.col);
        if (var.domain.length == 0) {
            backtracks++;
        }
        for (int i : orderDomainValues(var)) {
            nodes++;
            var.value = i;
            assigned++;
            solutions.add(var);
            ArrayList<Assignment> removed = new ArrayList<>();
            if (forwardChecking == 1) {
                boolean inf = Inference(var, removed);
//                    System.out.println("Inference: " + inf + " " + var.row + ", " + var.col + " -> " + var.value);
                if (inf) {
                    unassignedMap.remove((new Pair(var.row, var.col)).toString());
                    ArrayList<Assignment> result = BackTrack();
                    if (!failure) {
//                            System.out.println("Assigned: " + var.row + ", " + var.col + " -> " + var.value);
//                            System.out.println("Till now assigned: " + assigned);
                        return result;
                    }
                    unassignedMap.put((new Pair(var.row, var.col)).toString(), var);
                } else {
                    backtracks++;
                }
                for (Assignment a : var.neighbors) {
                    a.neighbors.add(var);
                    a.forwardDegree++;
                }
                for (Assignment a : removed) {
                    a.add(var.value);
                }

            } else {
                failure = false;
                Pair p = new Pair(var.row, var.col);
                unassignedMap.remove(p.toString());
                for (Assignment a : var.neighbors) {
                    a.neighbors.remove(var);
                    a.forwardDegree--;
                    int c = a.remove(var.value);
                    if (c > 0) {
                        removed.add(a);
                    }
                    if (a.domain.length == 0) {
                        failure = true;
                    }
                }
                ArrayList<Assignment> result = BackTrack();
                if (!failure) {
//                        System.out.println("Assigned: " + var.row + ", " + var.col + " -> " + var.value);
//                        System.out.println("Till now assigned: " + assigned);
//                    System.out.println("Result returning from " + var.row + ", " + var.col + " -> " + var.value);
                    return result;
                }
//                System.out.println("Backtrack from: " + var.row + ", " + var.col + " -> " + var.value);
                for (Assignment a : var.neighbors) {
                    a.neighbors.add(var);
                    a.forwardDegree++;
                }
                for (Assignment a : removed) {
                    a.add(var.value);
                }
                unassignedMap.put((new Pair(var.row, var.col)).toString(), var);
            }
            var.value = 0;
            assigned--;
            solutions.remove(var);
        }
        failure = true;
        return solutions;
    }

    private int[] orderDomainValues(Assignment assignment) {
        randomArray(assignment.domain);
        return assignment.domain;
    }

    private boolean Inference(Assignment var, ArrayList<Assignment> removed) {
        for (Assignment a : var.neighbors) {
            int i = a.remove(var.value);
            if (i > 0) {
                removed.add(a);
            }
            a.forwardDegree--;
            a.neighbors.remove(var);
        }
        for (Assignment a : var.neighbors) {
            if (a.domain.length == 0) {
                failure = true;
                return false;
            }
        }
        return true;
    }

    private Assignment selectUnassignedVariable() {
        if (heuristic == 1) {
            Pair p = null;
            int min = Integer.MAX_VALUE;
            for (Assignment a : unassignedMap.values()) {
                if (a.domain.length < min) {
                    min = a.domain.length;
                    p = new Pair(a.row, a.col);
                }
            }
            return unassignedMap.get(p.toString());
        } else if (heuristic == 2) {
            int max = -1;
            Pair p = null;
            for (Assignment a : unassignedMap.values()) {
                if (a.forwardDegree > max) {
                    max = a.forwardDegree;
                    p = new Pair(a.row, a.col);
                }
            }
            return unassignedMap.get(p.toString());
        } else if (heuristic == 3) {
            ArrayList<Pair> hashCodes = new ArrayList<>();
            int min = Integer.MAX_VALUE;
            for (Assignment a : unassignedMap.values()) {
                if (a.domain.length < min) {
                    min = a.domain.length;
                }
            }

            for (Assignment a : unassignedMap.values()) {
                if (a.domain.length == min) {
                    hashCodes.add(new Pair(a.row, a.col));
                }
            }

            int max = -1;
            Pair p = hashCodes.get(0);
            if (unassignedMap.size() == 1) return unassignedMap.get(p.toString());
            for (Pair pair : hashCodes) {
                Assignment a = unassignedMap.get(pair.toString());
                if (a.forwardDegree > max) {
                    max = a.forwardDegree;
                    p = pair;
                }
            }
            return unassignedMap.get(p.toString());
        } else if (heuristic == 4) {
            Pair p = null;
            float min = 10000;
            for (Assignment a : unassignedMap.values()) {
                float flag = (float) a.domain.length / (float) a.forwardDegree;
                if (flag < min) {
                    min = flag;
                    p = new Pair(a.row, a.col);
                }
            }
            if (p == null) {
                p = new Pair(unassignedMap.values().iterator().next().row, unassignedMap.values().iterator().next().col);
            }
            return unassignedMap.get(p.toString());
        } else {
            try {
                Set<String> hashCodes = unassignedMap.keySet();
                return unassignedMap.get(hashCodes.toArray()[new Random().nextInt(hashCodes.size())].toString());
            } catch (NullPointerException e) {
                System.out.println("Exception caught at heuristic 5 or higher");
                return null;
            }
        }
    }

    private void randomArray(int[] array) {
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            int index = random.nextInt(array.length);
            int temp = array[i];
            array[i] = array[index];
            array[index] = temp;
        }
    }
}
