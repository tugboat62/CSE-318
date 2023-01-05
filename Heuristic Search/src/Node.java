public class Node {
    int row, column, priority, moves;
    int[][] mat;
    String move = "No move";
    Node parent;

    Node(int i, int j, int[][] mat, int p, int m, Node parent) {
        row = i;
        column = j;
        int l = mat.length;
        this.mat = new int[l][l];
        for (int k=0; k<l; k++) {
            for (int h=0; h<l; h++) {
                this.mat[k][h] = mat[k][h];
            }
        }

        priority = p;
        this.parent = parent;
        moves = m;
    }

    public int[][] getMat() {
        return mat;
    }
    public int getPriority() {
        return priority;
    }

    public int getMoves() {
        return moves;
    }

    public void setMoves(int m) {
        moves = m;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }
}
