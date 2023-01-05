import java.util.ArrayList;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        ArrayList<Assignment> assignments = new ArrayList<>();
        Assignment a = new Assignment(1, 2, 3, 4);
        assignments.add(a);
        Assignment b = a;
        b.row = 7;
        assignments.remove(b);
        int[] arr = {1, 2, 3, 4};
        int[] arr2;
        arr2 = arr;
        arr2[0] = 7;
        System.out.println(Arrays.toString(arr));
    }
}
