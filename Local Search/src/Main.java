import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws IOException {
        String stDataFile = "data/tre-s-92.stu";
        String crsDataFile = "data/tre-s-92.crs";
        int heuristicC = 2;                             // 1 -> Largest degree, 2 -> Saturation degree,
                                                        // 3 -> Largest enrollment, 4 -> Random ordering

        int linearOrExp = 1;                            // 0 for linear, 1 for exponential

        if (args.length == 4) {
            stDataFile = args[0];
            crsDataFile = args[1];
            heuristicC = Integer.parseInt(args[2]);
            linearOrExp = Integer.parseInt(args[3]);
        }

        HashMap<String, Course> courses = new HashMap<>();
        ArrayList<Student> students = new ArrayList<>();
        BufferedReader st = new BufferedReader(new FileReader(stDataFile));
        BufferedReader crs = new BufferedReader(new FileReader(crsDataFile));

        String line = crs.readLine();
        while (line != null) {
            String[] s = line.split("\\s+|\\t+");
            int n = Integer.parseInt(s[1]);
            String name = s[0];
            Course c = new Course(name, n);
            courses.put(name, c);
            line = crs.readLine();
        }

        line = st.readLine();
        int stdID = 0;
        while (line != null) {
            stdID++;
            String[] s = line.split("\\s+|\\t+");
            Student student = new Student(stdID);
            for (int i = 0; i < s.length; i++) {
                Course c = courses.get(s[i]);
                student.addCourse(c);
                c.addStudent(stdID);
                if (s.length > 1) {
                    for (int j = i+1; j < s.length; j++) {
                        Course c2 = courses.get(s[j]);
                        c.addConflict(s[j], stdID, c2);
                        c2.addConflict(s[i], stdID, c);
                    }
                }
            }
            students.add(student);
            line = st.readLine();
        }

        st.close();
        crs.close();

//        for (Course c: courses.values()) {
//            System.out.println("course: " + c.courseNo + " enrolled: " + c.enrolled + " students: " + c.students.size());
//            for (String course: c.conflicts.keySet()) {
//                ArrayList<Integer> a = c.conflicts.get(course);
//                System.out.println("With course: " + course + " total conflicts: " + a.size());
//            }
//            System.out.println();
//        }

        System.out.println();
        System.out.println("File name: " + stDataFile);
        if (linearOrExp == 0) {
            System.out.println("With linear penalty strategy");
        } else {
            System.out.println("With exponential penalty strategy");
        }
        Scheduler scheduler = new Scheduler(courses, students, courses.size(), stdID, heuristicC, linearOrExp);
        scheduler.schedule();
        scheduler.printCorrectness();
        scheduler.maximumTimeSlot();
        scheduler.calculatePenalty();
        System.out.println("Initial avg Penalty with heuristic " + heuristicC + ": " + scheduler.avgPen);
        scheduler.minimizePenalties();
        scheduler.printCorrectness();
        System.out.println();
    }
}
