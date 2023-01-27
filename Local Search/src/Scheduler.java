import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Scheduler {
    HashMap<String, Course> courses;
    ArrayList<Student> students;
    int totalStudents;
    int totalCourses;
    HashMap<String, Course> result;
    int heuristicC;
    float avgPen;
    int linearOrExp;

    Scheduler(HashMap<String, Course> courses, ArrayList<Student> students,
              int totalCourses, int n, int heuristicC, int linearOrExp) {
        this.courses = courses;
        this.students = students;
        this.totalStudents = n;
        this.totalCourses = totalCourses;
        result = new HashMap<>();
        this.heuristicC = heuristicC;
        this.linearOrExp = linearOrExp;
        avgPen = 0;
    }

    public void schedule() {
        if (result.size() == totalCourses) {
            return;
        }
        Course selected = selectCourseConstruct(heuristicC);
        courses.remove(selected.courseNo);
        boolean flag = false;
        ArrayList<Integer> flags = new ArrayList<>();
        int max = Integer.MIN_VALUE;
        for (Course course : selected.neighbors) {
            if (course.timeSlot != -1) {
                flag = true;
                flags.add(course.timeSlot);
                if (course.timeSlot > max) {
                    max = course.timeSlot;
                }
            }
            course.totalUncoloredNeighbors--;
            course.saturationDegree++;
        }
        if (flag) {
            for (int i = 1; i <= max; i++) {
                if (!flags.contains(i)) {
                    selected.timeSlot = i;
                    break;
                }
                selected.timeSlot = max + 1;
            }
        } else {
            selected.timeSlot = 1;
        }
        result.put(selected.courseNo, selected);
        schedule();
    }

    public void calculatePenalty() {
        if (linearOrExp == 0) {
            for (Student student : students) {
                student.courses.sort((o1, o2) -> o1.timeSlot - o2.timeSlot);
                student.penalty = 0;
                for (int i=0; i<student.courses.size()-1; i++) {
                    Course course = student.courses.get(i);
                    Course course1 = student.courses.get(i+1);
                    int diff = Math.abs(course.timeSlot - course1.timeSlot);
                    if (diff <= 5) {
                        student.penalty += 2 * (5 - diff);
                    }
                }
                avgPen += student.penalty;
            }
        } else {
            for (Student student : students) {
                student.penalty = 0;
                student.courses.sort((o1, o2) -> o1.timeSlot - o2.timeSlot);
                for (int i=0; i<student.courses.size()-1; i++) {
                    Course course = student.courses.get(i);
                    Course course1 = student.courses.get(i+1);
                    int diff = Math.abs(course.timeSlot - course1.timeSlot);
                    if (diff <= 5) {
                        student.penalty += 2 ^ (5 - diff);
                    }
                }
                avgPen += student.penalty;
            }
        }
        avgPen /= totalStudents;
    }

    private Course selectCourseConstruct(int heuristic) {
        if (heuristic == 1) {
            int max = Integer.MIN_VALUE;
            ArrayList<Course> maxes = new ArrayList<>();
            for (Course c : courses.values()) {
                if (c.totalConflicts > max) {
                    max = c.totalConflicts;
                }
            }
            for (Course c : courses.values()) {
                if (c.totalConflicts == max) {
                    maxes.add(c);
                }
            }
            return maxes.get((int) (Math.random() * maxes.size()));
        } else if (heuristic == 2) {
            int max = Integer.MIN_VALUE;
            for (Course c : courses.values()) {
                if (c.saturationDegree > max) {
                    max = c.saturationDegree;
                }
            }
            Course selected = null;
            int maxUncolored = Integer.MIN_VALUE;
            for (Course c : courses.values()) {
                if (c.saturationDegree == max) {
                    if (maxUncolored < c.totalUncoloredNeighbors) {
                        maxUncolored = c.totalUncoloredNeighbors;
                        selected = c;
                    }
                }
            }
            return selected;
        } else if (heuristic == 3) {
            int max = Integer.MIN_VALUE;
            Course selected = null;
            for (Course c : courses.values()) {
                if (c.enrolled > max) {
                    max = c.enrolled;
                    selected = c;
                }
            }
            return selected;
        } else if (heuristic == 4) {
            ArrayList<String> keys = new ArrayList<>(courses.keySet());
            return courses.get(keys.get((int) (Math.random() * keys.size())));
        }
        return null;
    }

    private Course getRandomCourse() {
        ArrayList<String> keys = new ArrayList<>(result.keySet());
        return result.get(keys.get((int) (Math.random() * keys.size())));
    }

    public void minimizePenalties() {
        int t = 1;
        int totalRuntime = 1000;
        boolean isOptimized = false;
        // Kempe-Chain
        while (true) {
            Course course = getRandomCourse();
            int timeSlot = course.timeSlot;
            int scndTime = 0;
            int max = Integer.MIN_VALUE;
            ArrayList<Course> kempeChain = new ArrayList<>();
            for (Course crs : course.neighbors) {
                int i = crs.timeSlot;
                ArrayList<Course> subgraph = new ArrayList<>();
                LinkedList<Course> queue = new LinkedList<>();
                queue.add(course);
                while (!queue.isEmpty()) {
                    Course c = queue.poll();
                    if (!subgraph.contains(c) && c.timeSlot == timeSlot || c.timeSlot == i) {
                        if (!subgraph.contains(c)) {
                            subgraph.add(c);
                            for (Course neighbor : c.neighbors) {
                                if (neighbor.timeSlot == i || neighbor.timeSlot == timeSlot) {
                                    queue.add(neighbor);
                                }
                            }
                        }
                    }
                }
                if (max < subgraph.size()) {
                    max = subgraph.size();
                    kempeChain = subgraph;
                    scndTime = i;
                }
            }
            interchangeColors(kempeChain, timeSlot, scndTime);
            float tempPen = avgPen;
            calculatePenalty();
            if (tempPen < avgPen) {
                interchangeColors(kempeChain, timeSlot, scndTime);
                avgPen = tempPen;
                if (t >= totalRuntime) break;
            }
            else {
                isOptimized = true;
            }

            t++;
        }
        System.out.println("After Kempe-Chain: " + avgPen);
        if (isOptimized) {
            // Pair swap
            int i = 1;
            while (true) {
                Course[] courses = new Course[2];
                selectRandomPair(courses);
                boolean flag = true;

                for (Course course : courses[0].neighbors) {
                    if (course.timeSlot == courses[1].timeSlot) {
                        flag = false;
                    }
                }
                if (flag) {
                    for (Course course : courses[1].neighbors) {
                        if (course.timeSlot == courses[0].timeSlot) {
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    int temp = courses[0].timeSlot;
                    courses[0].timeSlot = courses[1].timeSlot;
                    courses[1].timeSlot = temp;
                    float tempPen = avgPen;
                    calculatePenalty();
                    if (avgPen > tempPen) {
                        int temp1 = courses[0].timeSlot;
                        courses[0].timeSlot = courses[1].timeSlot;
                        courses[1].timeSlot = temp1;
                        avgPen = tempPen;
                        if (i >= totalRuntime) break;
                    }
                    i++;
                }
            }
            System.out.println("After Pair Swap: " + avgPen);
        }
    }

    public void printCorrectness() {
        boolean correct = true;
        for (Course c : result.values()) {
            for (Course course : c.neighbors) {
                if (c.timeSlot == course.timeSlot) {
                    correct = false;
                }
            }
        }
        if (correct) System.out.println("Solution is correct");
        else System.out.println("Solution is not correct");
    }

    private void interchangeColors(ArrayList<Course> kempeChain, int t1, int t2) {
        for (Course course : kempeChain) {
            if (course.timeSlot == t1)
                course.timeSlot = t2;
            else
                course.timeSlot = t1;
        }
    }

    private void selectRandomPair(Course[] courses) {
        while (true) {
            int index1 = (int) (Math.random() * result.size());
            int index2 = (int) (Math.random() * result.size());
            if (index1 != index2) {
                courses[0] = (Course) result.values().toArray()[index1];
                courses[1] = (Course) result.values().toArray()[index2];
                if (courses[0].timeSlot != courses[1].timeSlot) break;
                if (!courses[0].neighbors.contains(courses[1])) break;
            }
        }
    }

    public void maximumTimeSlot() {
        int max = Integer.MIN_VALUE;
        for (Course course : result.values()) {
            if (course.timeSlot > max) {
                max = course.timeSlot;
            }
        }
        System.out.println("Total number of days: " + max);
    }

    public void printSchedule() {
        int max = Integer.MIN_VALUE;
        for (Course c : result.values()) {
            System.out.println(c.courseNo + " " + c.timeSlot);
            if (c.timeSlot > max) {
                max = c.timeSlot;
            }
        }
        System.out.println("Total number of days: " + max);
    }
}
