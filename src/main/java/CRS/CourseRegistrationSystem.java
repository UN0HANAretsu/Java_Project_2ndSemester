package CRS;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;

public class CourseRegistrationSystem {

    static ArrayList<Student> students = new ArrayList<>();
    static ArrayList<Course> availableCourses = new ArrayList<>();

    static final String FILE = "students.txt";
    static final int MAX_COURSES = 10;

    static Student currentStudent;

    // ---------- COURSE ----------
    static class Course {
        String code, name;
        double credit;

        Course(String code, String name, double credit) {
            this.code = code;
            this.name = name;
            this.credit = credit;
        }

        public String toString() {
            return code + " - " + name + " (" + credit + " Credits)";
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
            String line = id + "," + name + "," + section + "," + semester + "," + year + "," + password;
            for (String c : registeredCourses) line += "," + c;
            return line;
        }

        static Student fromLine(String line) {
            String[] d = line.split(",");
            Student s = new Student(d[0], d[1], d[2], d[3], d[4], d[5]);
            for (int i = 6; i < d.length; i++) s.registeredCourses.add(d[i]);
            return s;
        }
    }

    // ---------- MAIN ----------
    public static void main(String[] args) {
        loadCourses();
        load();
        new LoginFrame();
    }

    // ---------- LOGIN FRAME ----------
    static class LoginFrame extends JFrame {

        LoginFrame() {
    setTitle("Course Registration System");
    setSize(1000, 500);
    setLayout(new GridBagLayout());

    // ---------- Buttons ----------
    JButton loginBtn = new JButton("Login");
    JButton signupBtn = new JButton("Sign Up");

    // Make buttons larger
    loginBtn.setPreferredSize(new Dimension(160, 45));
    signupBtn.setPreferredSize(new Dimension(160, 45));

    JPanel panel = new JPanel();
    panel.add(loginBtn);
    panel.add(signupBtn);

    // ---------- Main Panel with Welcome Text ----------
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

    JLabel welcomeLabel = new JLabel("Welcome to Starlings'S course registration System!");
    welcomeLabel.setFont(new Font("Arial", Font.BOLD, 25)); // larger font
    welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    mainPanel.add(welcomeLabel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // spacing
    panel.setAlignmentX(Component.CENTER_ALIGNMENT);
    mainPanel.add(panel);

    add(mainPanel);

    // ---------- Button Actions ----------
    loginBtn.addActionListener(e -> showLoginDialog());
    signupBtn.addActionListener(e -> showSignupDialog());

    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setVisible(true);
}

        // ---------- LOGIN ----------
        void showLoginDialog() {

            JTextField idField = new JTextField();
            JPasswordField passField = new JPasswordField();

            JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
            panel.add(new JLabel("Student ID:"));
            panel.add(idField);
            panel.add(new JLabel("Password:"));
            panel.add(passField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Login",
                    JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String id = idField.getText().trim();
                String pw = new String(passField.getPassword()).trim();

                if (id.isEmpty() || pw.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Fill all the fields.");
                    return;
                }

                Student s = find(id);

                if (s != null && s.password.equals(pw)) {
                    currentStudent = s;
                    new Dashboard();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid ID or Password.");
                }
            } else {
                return;
            }
        }

        // ---------- SIGNUP ----------
        void showSignupDialog() {

            JTextField nameField = new JTextField();
            JTextField idField = new JTextField();
            JTextField secField = new JTextField();
            JTextField semField = new JTextField();
            JTextField yrField = new JTextField();
            JPasswordField pwField = new JPasswordField();

            JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));

            panel.add(new JLabel("Name:"));
            panel.add(nameField);

            panel.add(new JLabel("ID:"));
            panel.add(idField);

            panel.add(new JLabel("Section:"));
            panel.add(secField);

            panel.add(new JLabel("Semester:"));
            panel.add(semField);

            panel.add(new JLabel("Year:"));
            panel.add(yrField);

            panel.add(new JLabel("Password:"));
            panel.add(pwField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Sign Up",
                    JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {

                String name = nameField.getText().trim();
                String id = idField.getText().trim();
                String sec = secField.getText().trim();
                String sem = semField.getText().trim();
                String yr = yrField.getText().trim();
                String pw = new String(pwField.getPassword()).trim();

                if (name.isEmpty() || id.isEmpty() || sec.isEmpty() ||
                        sem.isEmpty() || yr.isEmpty() || pw.isEmpty()) {

                    JOptionPane.showMessageDialog(this, "Fill all the fields.");
                    return;
                }

                if (find(id) != null) {
                    JOptionPane.showMessageDialog(this, "ID already exists.");
                    return;
                }

                Student s = new Student(id, name, sec, sem, yr, pw);
                students.add(s);
                save();

                JOptionPane.showMessageDialog(this, "Signup successful!");

                currentStudent = s;
                new Dashboard();
                dispose();

            } else {
                return;
            }
        }
    }

    // ---------- DASHBOARD ----------
    static class Dashboard extends JFrame {

        JTextArea area = new JTextArea();

        Dashboard() {
    setTitle("Menu");
    setSize(1000, 800);

    // Main panel with BorderLayout
    JPanel mainPanel = new JPanel(new BorderLayout());

    // ---------- Center Panel ----------
    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

    // Welcome label
    JLabel welcomeLabel = new JLabel("Welcome, " + currentStudent.name + "!");
    welcomeLabel.setFont(new Font("Arial", Font.BOLD, 25));
    welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // center horizontally

    centerPanel.add(Box.createVerticalGlue()); // pushes content to vertical center
    centerPanel.add(welcomeLabel);
    centerPanel.add(Box.createRigidArea(new Dimension(0, 20))); // space between welcome and buttons

    // Buttons
    JButton b1 = new JButton("View Profile");
    JButton b2 = new JButton("Edit Profile");
    JButton b3 = new JButton("Course List");
    JButton b4 = new JButton("Register");
    JButton b5 = new JButton("Drop");
    JButton b6 = new JButton("Logout");

    JButton[] buttons = {b1, b2, b3, b4, b5, b6};
    Dimension btnSize = new Dimension(160, 45);

    for (JButton b : buttons) {
        b.setMaximumSize(btnSize);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(b);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    centerPanel.add(Box.createVerticalGlue()); // pushes content to vertical center

    // ---------- Text Area ----------
    JTextArea area = new JTextArea();
    area.setEditable(false);

    mainPanel.add(centerPanel, BorderLayout.CENTER);
    mainPanel.add(new JScrollPane(area), BorderLayout.SOUTH);

    add(mainPanel);

    // ---------- Button Actions ----------
    b1.addActionListener(e -> viewProfile());
    b2.addActionListener(e -> editProfile());
    b3.addActionListener(e -> CoursesList());
    b4.addActionListener(e -> registerCourses());
    b5.addActionListener(e -> dropCourse());
    b6.addActionListener(e -> {
        save();
        new LoginFrame();
        dispose();
    });

    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setVisible(true);
}
        void viewProfile() {

    JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));

    panel.add(new JLabel("Name: " + currentStudent.name));
    panel.add(new JLabel("ID: " + currentStudent.id));
    panel.add(new JLabel("Section: " + currentStudent.section));
    panel.add(new JLabel("Semester: " + currentStudent.semester));
    panel.add(new JLabel("Year: " + currentStudent.year));

    panel.add(new JLabel("---------------------------"));

    panel.add(new JLabel("Registered Courses (" 
            + currentStudent.registeredCourses.size() + "/" + MAX_COURSES + "):"));

    if (currentStudent.registeredCourses.isEmpty()) {
        panel.add(new JLabel("No courses registered."));
    } else {
        for (String code : currentStudent.registeredCourses) {

            Course c = getCourse(code);

            if (c != null) {
                panel.add(new JLabel(c.code + " - " + c.name));
            } else {
                panel.add(new JLabel(code + " (Unknown Course)"));
            }
        }
    }

    JOptionPane.showMessageDialog(this, panel, "Profile",
            JOptionPane.INFORMATION_MESSAGE);
}

        void editProfile() {
    // Create text fields pre-filled with current values
    JTextField nameField = new JTextField(currentStudent.name);
    JTextField sectionField = new JTextField(currentStudent.section);
    JTextField semesterField = new JTextField(currentStudent.semester);
    JTextField yearField = new JTextField(currentStudent.year);
    JPasswordField passwordField = new JPasswordField(currentStudent.password);

    // Panel with GridLayout for labels and fields
    JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
    panel.add(new JLabel("Name:"));
    panel.add(nameField);
    panel.add(new JLabel("Section:"));
    panel.add(sectionField);
    panel.add(new JLabel("Semester:"));
    panel.add(semesterField);
    panel.add(new JLabel("Year:"));
    panel.add(yearField);
    panel.add(new JLabel("Password:"));
    panel.add(passwordField);

    int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Edit Profile",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
    );

    if (result == JOptionPane.OK_OPTION) {
        // Only update fields that are not empty
        String name = nameField.getText().trim();
        String section = sectionField.getText().trim();
        String semester = semesterField.getText().trim();
        String year = yearField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (!name.isEmpty()) currentStudent.name = name;
        if (!section.isEmpty()) currentStudent.section = section;
        if (!semester.isEmpty()) currentStudent.semester = semester;
        if (!year.isEmpty()) currentStudent.year = year;
        if (!password.isEmpty()) currentStudent.password = password;

        save();
        JOptionPane.showMessageDialog(this, "Profile updated!");
    }
}

        private void CoursesList() {

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    for (Course c : availableCourses) {
        panel.add(new JLabel(c.code + " - " + c.name + " (" + c.credit + " Credits)"));
    }

    JOptionPane.showMessageDialog(
            this,
            panel,
            "Course List",
            JOptionPane.INFORMATION_MESSAGE
    );
}
        void registerCourses() {
    String input = JOptionPane.showInputDialog(
            "Enter course code(s) to register (separate multiple codes with commas):"
    );

    if (input == null || input.trim().isEmpty()) return; // Cancel or empty

    String[] codes = input.split(","); // split by comma
    int addedCount = 0;

    for (String code : codes) {
        code = code.trim(); // remove extra spaces

        if (code.isEmpty()) continue;

        if (currentStudent.registeredCourses.size() >= MAX_COURSES) {
            JOptionPane.showMessageDialog(this, "Maximum " + MAX_COURSES + " courses allowed.");
            break;
        }

        Course c = getCourse(code);

        if (c == null) {
            JOptionPane.showMessageDialog(this, code + " not found.");
            continue;
        }

        if (currentStudent.registeredCourses.contains(code)) {
            JOptionPane.showMessageDialog(this, code + " already registered.");
            continue;
        }

        currentStudent.registeredCourses.add(code);
        addedCount++;
    }

    if (addedCount > 0) {
        save();
        JOptionPane.showMessageDialog(this, addedCount + " course(s) registered successfully!");
    }
}

        void dropCourse() {
    if (currentStudent.registeredCourses.isEmpty()) {
        JOptionPane.showMessageDialog(this, "You have no registered courses to drop.");
        return;
    }

    // Build a display string of registered courses
    StringBuilder sb = new StringBuilder("Registered Courses:\n");
    for (String code : currentStudent.registeredCourses) {
        Course c = getCourse(code);
        if (c != null) {
            sb.append(c.code).append(" - ").append(c.name).append("\n");
        } else {
            sb.append(code).append(" (Unknown Course)\n");
        }
    }
    sb.append("\nEnter the course code(s) to remove (comma-separated):");

    // Show the input dialog
    String input = JOptionPane.showInputDialog(this, sb.toString());
    if (input == null || input.trim().isEmpty()) return; // Cancel or empty

    String[] codesToRemove = input.split(",");
    int removedCount = 0;

    // Use a standard for-loop to remove courses safely
    for (String code : codesToRemove) {
        code = code.trim(); // remove spaces
        if (code.isEmpty()) continue;

        boolean found = false;
        for (int i = 0; i < currentStudent.registeredCourses.size(); i++) {
            if (currentStudent.registeredCourses.get(i).equalsIgnoreCase(code)) {
                currentStudent.registeredCourses.remove(i);
                removedCount++;
                found = true;
                break; // remove only one match
            }
        }
        if (!found) {
            JOptionPane.showMessageDialog(this, code + " not found or not registered.");
        }
    }

    if (removedCount > 0) {
        save();
        JOptionPane.showMessageDialog(this, removedCount + " course(s) removed successfully!");
    }
}
    }

    // ---------- FILE ----------
    static void save() {
        try (PrintWriter pw = new PrintWriter(FILE)) {
            for (Student s : students)
                pw.println(s.toLine());
        } catch (Exception e) {
            System.out.println("Error saving file.");
        }
    }

    static void load() {
        File f = new File(FILE);
        if (!f.exists()) return;

        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine())
                students.add(Student.fromLine(sc.nextLine()));
        } catch (Exception e) {
            System.out.println("Error loading file.");
        }
    }

    static Student find(String id) {
        for (Student s : students)
            if (s.id.equals(id)) return s;
        return null;
    }

    static Course getCourse(String code) {
        for (Course c : availableCourses)
            if (c.code.equalsIgnoreCase(code)) return c;
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