import java.util.ArrayList;

public class Assignment {
    public int[] domain;
    int row;
    int col;
    int value;
    int forwardDegree;
    ArrayList<Assignment> neighbors;

    public Assignment(int row, int col, int value, int size) {
        this.row = row;
        this.col = col;
        this.value = value;
        this.forwardDegree = 0;
        this.neighbors = new ArrayList<>();
        domain = new int[size];
        for (int i = 1; i <= size; i++) {
            domain[i - 1] = i;
        }
    }

    public int remove(int i) {
        int cnt = 0;
        for (int k = 0; k < domain.length; k++) {
            if (domain[k] == i) {
                domain[k] = 0;
                cnt++;
                break;
            }
        }
        int[] temp = new int[domain.length - cnt];
        for (int k = 0, j = 0; k < domain.length; k++) {
            if (domain[k] != 0) {
                temp[j++] = domain[k];
            }
        }
        domain = temp;
        return cnt;
    }

    public void add(int i) {
        int[] temp = new int[domain.length + 1];
        for (int k = 0; k < domain.length; k++) {
            temp[k] = domain[k];
        }
        temp[domain.length] = i;
        domain = temp;
    }

}
