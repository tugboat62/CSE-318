public class Pair {
    int row;
    int col;
    Pair(int r, int c) {
        row = r;
        col = c;
    }

    @Override
    public String toString() {
        return row+ " " + col;
    }
}
