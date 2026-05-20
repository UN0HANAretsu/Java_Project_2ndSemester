package C_R_S;

import java.util.*;
import java.io.*;

public class CourseRegistrationSystem {

    static ArrayList<Student> students = new ArrayList<>();
    static ArrayList<Course> availableCourses = new ArrayList<>();
    static final String FILE = "students.txt";
    static final int MAX_COURSES = 10;
    static Student currentStudent;
    static Scanner scanner = new Scanner(System.in);

    // ---------- COURSE ----------
    static class Course {
        String code, name;
        double credit;

        Course(String code, String name, double credit) {
            this.code = code;
            this.name = name;
            this.credit = credit;
        }

        @Override
        public String toString() {
            return String.format("%-10s | %-40s | %.1f Credits", code, name, credit);
        }
    }

    // ---------- STUDENT ----------
    static class Student {
        String id, name, section, semester, year, password;
        ArrayList<String> registeredCourses = new ArrayList<>();

        Student(String id, String name, String section, String semester, String year, String password) {
            this.id = id;
            this.name = name;
            this.section = section;
            this.semester = semester;
            this.year = year;
            this.password = password;
        }

        String toLine() {
            StringBuilder line = new StringBuilder(id + "," + name + "," + section + "," + semester + "," + year + "," + password);
            for (String c : registeredCourses) line.append(",").append(c);
            return line.toString();
        }

        static Student fromLine(String line) {
            String[] d = line.split(",");
            if (d.length < 6) return null;
            Student s = new Student(d[0], d[1], d[2], d[3], d[4], d[5]);
            for (int i = 6; i < d.length; i++) s.registeredCourses.add(d[i]);
            return s;
        }
    }

    // ---------- MAIN CONTROL FLOW ----------
    public static void main(String[] args) {
        loadCourses();
        load();

        while (true) {
            System.out.println("\n=== Welcome to Starlings's Course Registration System ===");
            System.out.println("1. Login");
            System.out.println("2. Sign Up");
            System.out.println("3. Exit");
            System.out.print("Select an option: ");
            
            String choice = scanner.nextLine();

            switch (choice) {
                case "1": login(); break;
                case "2": signup(); break;
                case "3": 
                    save();
                    System.out.println("Data saved. Goodbye!");
                    System.exit(0);
                default: System.out.println("Invalid choice.");
            }
        }
    }

    // ---------- AUTHENTICATION ----------
    static void login() {
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String pw = scanner.nextLine().trim();

        Student s = find(id);
        if (s != null && s.password.equals(pw)) {
            currentStudent = s;
            dashboard();
        } else {
            System.out.println("Invalid ID or Password.");
        }
    }

    static void signup() {
        System.out.print("Name: "); String name = scanner.nextLine();
        System.out.print("ID: "); String id = scanner.nextLine();
        System.out.print("Section: "); String sec = scanner.nextLine();
        System.out.print("Semester: "); String sem = scanner.nextLine();
        System.out.print("Year: "); String yr = scanner.nextLine();
        System.out.print("Password: "); String pw = scanner.nextLine();

        if (name.isEmpty() || id.isEmpty() || pw.isEmpty()) {
            System.out.println("Error: Name, ID, and Password are required.");
            return;
        }

        if (find(id) != null) {
            System.out.println("Error: ID already exists.");
            return;
        }

        Student s = new Student(id, name, sec, sem, yr, pw);
        students.add(s);
        save();
        System.out.println("Signup successful!");
        currentStudent = s;
        dashboard();
    }

    // ---------- DASHBOARD ----------
    static void dashboard() {
        while (currentStudent != null) {
            System.out.println("\n--- Dashboard: Welcome, " + currentStudent.name + " ---");
            System.out.println("1. View Profile");
            System.out.println("2. Edit Profile");
            System.out.println("3. Course List");
            System.out.println("4. Register Courses");
            System.out.println("5. Drop Course");
            System.out.println("6. Logout");
            System.out.print("Select: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> viewProfile();
                case "2" -> editProfile();
                case "3" -> showCourseList();
                case "4" -> registerCourses();
                case "5" -> dropCourse();
                case "6" -> { 
                    save();
                    currentStudent = null; 
                    System.out.println("Logged out.");
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    static void viewProfile() {
        System.out.println("\n--- My Profile ---");
        System.out.println("Name: " + currentStudent.name);
        System.out.println("ID: " + currentStudent.id);
        System.out.println("Section: " + currentStudent.section);
        System.out.println("Semester: " + currentStudent.semester);
        System.out.println("Year: " + currentStudent.year);
        System.out.println("Registered Courses (" + currentStudent.registeredCourses.size() + "/" + MAX_COURSES + "):");
        
        if (currentStudent.registeredCourses.isEmpty()) {
            System.out.println("- No courses registered.");
        } else {
            for (String code : currentStudent.registeredCourses) {
                Course c = getCourse(code);
                System.out.println("  > " + (c != null ? c : code));
            }
        }
    }

    static void editProfile() {
        System.out.println("\n--- Edit Profile (Press Enter to keep current value) ---");
        
        // Edit Name
        System.out.print("New Name [" + currentStudent.name + "]: ");
        String name = scanner.nextLine();
        if (!name.trim().isEmpty()) currentStudent.name = name;

        // Edit Section
        System.out.print("New Section [" + currentStudent.section + "]: ");
        String sec = scanner.nextLine();
        if (!sec.trim().isEmpty()) currentStudent.section = sec;

        // Edit Semester (New Field)
        System.out.print("New Semester [" + currentStudent.semester + "]: ");
        String sem = scanner.nextLine();
        if (!sem.trim().isEmpty()) currentStudent.semester = sem;

        // Edit Year (New Field)
        System.out.print("New Year [" + currentStudent.year + "]: ");
        String yr = scanner.nextLine();
        if (!yr.trim().isEmpty()) currentStudent.year = yr;

        // Edit Password
        System.out.print("New Password: ");
        String pw = scanner.nextLine();
        if (!pw.trim().isEmpty()) currentStudent.password = pw;

        save(); // Save changes to students.txt
        System.out.println("Profile updated successfully!");
    }

    static void showCourseList() {
        System.out.println("\n--- Available Courses ---");
        for (Course c : availableCourses) {
            System.out.println(c);
        }
    }

    static void registerCourses() {
        System.out.print("Enter course codes to register (comma-separated): ");
        String input = scanner.nextLine();
        String[] codes = input.split(",");

        for (String code : codes) {
            code = code.trim().toUpperCase();
            if (code.isEmpty()) continue;

            if (currentStudent.registeredCourses.size() >= MAX_COURSES) {
                System.out.println("Limit reached. Cannot add " + code);
                break;
            }

            Course c = getCourse(code);
            if (c == null) {
                System.out.println("Error: " + code + " not found.");
            } else if (currentStudent.registeredCourses.contains(code)) {
                System.out.println("Error: Already registered for " + code);
            } else {
                currentStudent.registeredCourses.add(code);
                System.out.println("Registered: " + code);
            }
        }
        save();
    }

    static void dropCourse() {
        if (currentStudent.registeredCourses.isEmpty()) {
            System.out.println("No courses to drop.");
            return;
        }
        System.out.println("Your courses: " + currentStudent.registeredCourses);
        System.out.print("Enter course code to drop: ");
        String code = scanner.nextLine().trim().toUpperCase();

        if (currentStudent.registeredCourses.remove(code)) {
            System.out.println("Dropped successfully.");
            save();
        } else {
            System.out.println("Course not found in your list.");
        }
    }

    // ---------- HELPER METHODS ----------
    static void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (Student s : students) pw.println(s.toLine());
        } catch (IOException e) {
            System.err.println("Error saving data.");
        }
    }

    static void load() {
        File f = new File(FILE);
        if (!f.exists()) return;
        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                Student s = Student.fromLine(sc.nextLine());
                if (s != null) students.add(s);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found.");
        }
    }

    static Student find(String id) {
        for (Student s : students) if (s.id.equals(id)) return s;
        return null;
    }

    static Course getCourse(String code) {
        for (Course c : availableCourses) if (c.code.equalsIgnoreCase(code)) return c;
        return null;
    }

    static void loadCourses() {
        availableCourses.add(new Course("CSE101","Structured Programming Language",3.0));
        availableCourses.add(new Course("CSE102","Structured Programming Language Lab",1.5));
        availableCourses.add(new Course("CSE103","Data Structures",3.0));
        availableCourses.add(new Course("CSE104","Algorithm",3.0));
        availableCourses.add(new Course("CSE105","Object Oriented Programming Language",3.0));
        availableCourses.add(new Course("CSE106","Object Oriented Programming Language Lab",1.5));
        availableCourses.add(new Course("CSE107","Discrete Mathematics",3.0));
        availableCourses.add(new Course("CSE108","Machine Learning",3.0));
        availableCourses.add(new Course("MAT101","Calculus: Differential Equations",3.0));
        availableCourses.add(new Course("MAT102","Ordinary and Partial Differential Equations",3.0));
        availableCourses.add(new Course("EEE101","Fundamentals of Electrical Engineering",3.0));
        availableCourses.add(new Course("EEE102","Fundamentals of Electrical Engineering Lab",1.5));
        availableCourses.add(new Course("ME101","Engineering Drawing",2.0));
        availableCourses.add(new Course("MAT105","Mathematics",3.0));
        availableCourses.add(new Course("GED101","English",3.0));
        availableCourses.add(new Course("GED102","English Lab",1.5));
        availableCourses.add(new Course("GED103","Bangladesh Studies",3.0));
        availableCourses.add(new Course("CHEM101","Engineering Chemistry",3.0));
        availableCourses.add(new Course("PHY101","Engineering Physics",3.0));
        availableCourses.add(new Course("PHY102","Engineering Physics Lab",1.5));
    }
}
