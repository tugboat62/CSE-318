public class NodeMini {
    int[][] mat;
    int size;

    NodeMini(int[][] mat) {
        this.mat = mat;
        size = mat.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeMini that = (NodeMini) o;
        return toString().equals(that.toString());
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                str.append(mat[i][j]).append(" ");
            }
            str.append("\n");
        }
        return str.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
