import java.util.ArrayList;

public class Student {
    int studentID;
    ArrayList<Course> courses;
    float penalty;

    Student(int studentID) {
        this.studentID = studentID;
        courses = new ArrayList<>();
        penalty = 0;
    }

    void addCourse(Course course) {
        courses.add(course);
    }
}
