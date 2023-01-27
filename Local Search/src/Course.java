import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Course {
    String courseNo;
    ArrayList<Integer> students;
    int enrolled;
    int totalConflicts;
    int saturationDegree;
    int timeSlot;
    int totalUncoloredNeighbors;
    LinkedList<Course> neighbors;

    HashMap<String, ArrayList<Integer>> conflicts;


    Course(String Name, int enrolled) {
        this.courseNo = Name;
        this.enrolled = enrolled;
        students = new ArrayList<>();
        conflicts = new HashMap<>();
        totalConflicts = 0;
        saturationDegree = 0;
        timeSlot = -1;
        totalUncoloredNeighbors = 0;
        neighbors = new LinkedList<>();
    }

    void addStudent(int student) {
        students.add(student);
    }

    public void addConflict(String s, int stdID, Course c) {
        if (conflicts.containsKey(s)) {
            conflicts.get(s).add(stdID);
        } else {
            ArrayList<Integer> a = new ArrayList<>();
            neighbors.add(c);
            a.add(stdID);
            conflicts.put(s, a);
            totalConflicts++;
            totalUncoloredNeighbors++;
        }
    }
}
