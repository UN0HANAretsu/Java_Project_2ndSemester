package CRS1;

import javax.swing.*;//UI (buttons, windows)
import java.awt.*;//layout, fonts, graphics
import java.util.*;//ArrayList, Scanner
import java.io.*;//file read/write

public class CourseRegistrationSystem {

    static ArrayList<Student> students = new ArrayList<>(); //All registered students stored here
    static ArrayList<Course> availableCourses = new ArrayList<>();//All courses list

    static final String FILE = "students.txt"; //file name for saving data
    static final int MAX_COURSES = 10;

    static Student currentStudent; //currently logged-in student stored here

    // ---------- COURSE ---------- //course code course name credit store thake
    static class Course { //ekta course k kivabe represent korba, seta define kora hocche.
        String code, name; //variables - Ei 3 ta variable course er information store kore:
        double credit;

        Course(String code, String name, double credit) { //new Course object bananor time e value set kore
            this.code = code; // variable er shathe parameter connect kortese
            this.name = name;
            this.credit = credit;
        }

        public String toString() { // Course object ke readable string e convert kore
            return code + " - " + name + " (" + credit + " Credits)";
        }
    }

    // ---------- STUDENT ---------- // “Student data structure + file save/load converter”
    static class Student {
        String id, name, section, semester, year, password; //student er basic profile data
        ArrayList<String> registeredCourses = new ArrayList<>(); // registered couurses list

        Student(String id, String name, String section, String semester, String year, String password) { // constructor value set korar jonno
            this.id = id;
            this.name = name;
            this.section = section;
            this.semester = semester;
            this.year = year;
            this.password = password;
        }

        String toLine() { //text line e convert kora
            String line = id + "," + name + "," + section + "," + semester + "," + year + "," + password;
            for (String c : registeredCourses) line += "," + c;
            return line;
        }

        static Student fromLine(String line) {
            String[] d = line.split(","); //comma diye data break
            Student s = new Student(d[0], d[1], d[2], d[3], d[4], d[5]); //first 6 fields = student info
            for (int i = 6; i < d.length; i++) s.registeredCourses.add(d[i]); //index 6 theke last porjonto course codes
            return s; // return object
        }
    }

    // ---------- MAIN ----------
    public static void main(String[] args) {
        loadCourses();
        load(); // students.txt file read
        new LoginFrame();// UI start
    }

    // ---------- LOGIN FRAME ----------
    static class LoginFrame extends JFrame { // this is a window

        LoginFrame() {
    setTitle("Course Registration System");  // window design setup
    setSize(1000, 500); //height width
    setLayout(new GridBagLayout());

    // ---------- Buttons ----------
    JButton loginBtn = new JButton("Login");   //adds login button
    JButton signupBtn = new JButton("Sign Up");

    // Make buttons larger
    loginBtn.setPreferredSize(new Dimension(160, 45)); // buttons size
    signupBtn.setPreferredSize(new Dimension(160, 45));

//Ei 3 line basically Swing UI te button gulo ekta container e organize kore
    
    JPanel panel = new JPanel(); // Ekta empty container (box) create hocche.
    panel.add(loginBtn); //loginBtn ke panel er moddhe rakha hocche
    panel.add(signupBtn); //second button o same panel e add hocche

    // ---------- Main Panel with Welcome Text ----------
    JPanel mainPanel = new JPanel(); //Ekta main container banano hocche
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); //layout set (UI elements gulo vertical direction e arrange hobe (top → bottom)

    JLabel welcomeLabel = new JLabel("Welcome to Starlings'S course registration System!"); //welcome level create
    welcomeLabel.setFont(new Font("Arial", Font.BOLD, 25)); // larger font
    welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    mainPanel.add(welcomeLabel); //mainPanel er moddhe welcome text add hocche
    mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // spacing - 20px vertical gap create kore
    panel.setAlignmentX(Component.CENTER_ALIGNMENT);
    mainPanel.add(panel);

    add(mainPanel);

    // ---------- Button Actions ----------
    loginBtn.addActionListener(e -> showLoginDialog()); //Login button e click korle showLoginDialog() function run hobe.
    signupBtn.addActionListener(e -> showSignupDialog());//signup button click → signup dialog open

    setDefaultCloseOperation(EXIT_ON_CLOSE); //window close button (❌) click korle program fully exit hoy
    setLocationRelativeTo(null); //window screen er center e open hobe
    setVisible(true);// ei line chara UI show hobe na
}

        // ---------- LOGIN ---------- //user login verify kora”.
        void showLoginDialog() { //ekta function jeta login popup show kore

  // 2 ta input box create hocche
            JTextField idField = new JTextField(); //Student ID input
            JPasswordField passField = new JPasswordField();//Password input (hidden text)

            JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5)); //2 rows, 2 columns form (5,5 means horizontal + vertical spacing between cells)
            
           //form fill kora hocche
            panel.add(new JLabel("Student ID:"));
            panel.add(idField);
            panel.add(new JLabel("Password:"));
            panel.add(passField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Login",
                    JOptionPane.OK_CANCEL_OPTION); // ekta popup window open jekhane login, id , pass , ok and cancel show kore

            if (result == JOptionPane.OK_OPTION) { //user OK click korse kina check
                String id = idField.getText().trim(); //user input extract kora hocche extra space remove kore
                String pw = new String(passField.getPassword()).trim();

                if (id.isEmpty() || pw.isEmpty()) { //empty check (jodi field empty hoy → error)
                    JOptionPane.showMessageDialog(this, "Fill all the fields.");
                    return;
                }

                Student s = find(id); // system e oi ID er student search kora hocche

                if (s != null && s.password.equals(pw)) { // 2 ta condition:  student exist korte hob password match korte hobe
                    currentStudent = s; // success login (logged in student save kora hocche globally)
                    new Dashboard(); //next window open (main menu)
                    dispose(); //login window close kore dey
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid ID or Password."); // login fail hole or wrong input hole error message popup
                }
            } else {
                return; //user cancel korle function exit
            }
        }

        // ---------- SIGNUP ----------
        void showSignupDialog() { // sighnup page open korar function

            JTextField nameField = new JTextField(); //input feilds create
            JTextField idField = new JTextField();
            JTextField secField = new JTextField();
            JTextField semField = new JTextField();
            JTextField yrField = new JTextField();
            JPasswordField pwField = new JPasswordField();

            JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5)); // form layout panel - 6 rows × 2 columns form

            panel.add(new JLabel("Name:")); ///UI form fill kora hocche
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
                    JOptionPane.OK_CANCEL_OPTION); // ekta popup open hoy with form + OK/Cancel

            if (result == JOptionPane.OK_OPTION) { // ok checked

                String name = nameField.getText().trim(); // user input theke dtata extract kora hochhe , extra space remove kore
                String id = idField.getText().trim();
                String sec = secField.getText().trim();
                String sem = semField.getText().trim();
                String yr = yrField.getText().trim();
                String pw = new String(pwField.getPassword()).trim();

                if (name.isEmpty() || id.isEmpty() || sec.isEmpty() || // kono field empty thakle error
                        sem.isEmpty() || yr.isEmpty() || pw.isEmpty()) {

                    JOptionPane.showMessageDialog(this, "Fill all the fields.");
                    return;
                }

                if (find(id) != null) { //same ID already exist korle signup block
                    JOptionPane.showMessageDialog(this, "ID already exists.");
                    return;
                }

                Student s = new Student(id, name, sec, sem, yr, pw);// new student object create hocche
                students.add(s); //in-memory database e add
                save();

                JOptionPane.showMessageDialog(this, "Signup successful!");// user ke confirm kora message 

                currentStudent = s; //auto login after sighnup
                new Dashboard();
                dispose(); // signup window close

            } else {
                return; //user cancel korle function stop
            }
        }
    }

    // ---------- DASHBOARD ---------- //(main menu screen after login)
    static class Dashboard extends JFrame { 

        JTextArea area = new JTextArea();

        Dashboard() { //eta run hole full dashboard UI build hoy
    setTitle("Menu"); //window title bar e "Menu" show hobe
    setSize(1000, 800); // window width = 1000px, height = 800px

    // Main panel with BorderLayout
    JPanel mainPanel = new JPanel(new BorderLayout());

    // ---------- Center Panel ----------
    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS)); //vertical UI layout (top → bottom)

    // Welcome label
    JLabel welcomeLabel = new JLabel("Welcome, " + currentStudent.name + "!"); // welcpme label
    welcomeLabel.setFont(new Font("Arial", Font.BOLD, 25)); // font styling - big bold heading
    welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // center horizontally

    centerPanel.add(Box.createVerticalGlue()); // pushes content to vertical center
    centerPanel.add(welcomeLabel);
    centerPanel.add(Box.createRigidArea(new Dimension(0, 20))); // space between welcome and buttons

    // Buttons
    JButton b1 = new JButton("View Profile"); // dashboard menu options
    JButton b2 = new JButton("Edit Profile");
    JButton b3 = new JButton("Course List");
    JButton b4 = new JButton("Register");
    JButton b5 = new JButton("Drop");
    JButton b6 = new JButton("Logout");

    JButton[] buttons = {b1, b2, b3, b4, b5, b6}; //loop kore same styling apply korar jonno shobgulo buttons e
    Dimension btnSize = new Dimension(160, 45); // all buttons same size hobe

    for (JButton b : buttons) { // each button e apply hobe
        b.setMaximumSize(btnSize); //size set
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(b);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15))); // buttons er moddhe gap
    }

    centerPanel.add(Box.createVerticalGlue()); // pushes content to vertical center

    // ---------- Text Area ----------
    JTextArea area = new JTextArea(); //ekta text box create hocche (output/log dekhar jonno)
    area.setEditable(false);

    mainPanel.add(centerPanel, BorderLayout.CENTER); //centerPanel (buttons + welcome) middle e place hocche
    mainPanel.add(new JScrollPane(area), BorderLayout.SOUTH);
//text area ke scrollable banানো hocche
    add(mainPanel);

    // ---------- Button Actions ----------
    b1.addActionListener(e -> viewProfile()); //click - profile open
    b2.addActionListener(e -> editProfile());
    b3.addActionListener(e -> CoursesList());
    b4.addActionListener(e -> registerCourses());
    b5.addActionListener(e -> dropCourse());
    b6.addActionListener(e -> {
        save();
        new LoginFrame();
        dispose();
    });

    setDefaultCloseOperation(EXIT_ON_CLOSE); //click korle program close
    setLocationRelativeTo(null);
    setVisible(true);
}
        void viewProfile() { //student details show kore popup e

    JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5)); //vertical list format

    panel.add(new JLabel("Name: " + currentStudent.name)); //basic info show
    panel.add(new JLabel("ID: " + currentStudent.id));
    panel.add(new JLabel("Section: " + currentStudent.section));
    panel.add(new JLabel("Semester: " + currentStudent.semester));
    panel.add(new JLabel("Year: " + currentStudent.year));

    panel.add(new JLabel("---------------------------"));

    panel.add(new JLabel("Registered Courses (" 
            + currentStudent.registeredCourses.size() + "/" + MAX_COURSES + "):")); //course count

    if (currentStudent.registeredCourses.isEmpty()) {
        panel.add(new JLabel("No courses registered.")); // if no course
    } else {
        for (String code : currentStudent.registeredCourses) { //each course code process

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

        void editProfile() { //user data change system
    // Create text fields pre-filled with current values
    JTextField nameField = new JTextField(currentStudent.name); //old value already loaded
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

        if (!name.isEmpty()) currentStudent.name = name; // blank input ignore
        if (!section.isEmpty()) currentStudent.section = section;
        if (!semester.isEmpty()) currentStudent.semester = semester;
        if (!year.isEmpty()) currentStudent.year = year;
        if (!password.isEmpty()) currentStudent.password = password;

        save(); //file update
        JOptionPane.showMessageDialog(this, "Profile updated!");
    }
}

        private void CoursesList() { //all available courses show

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    for (Course c : availableCourses) { //loop all courses
        panel.add(new JLabel(c.code + " - " + c.name + " (" + c.credit + " Credits)"));
    }

    JOptionPane.showMessageDialog(
            this,
            panel,
            "Course List",
            JOptionPane.INFORMATION_MESSAGE
    );
}
        void registerCourses() {  //register courses

    String input = JOptionPane.showInputDialog(
            "Enter course code(s) to register (comma-separated):"
    );

    if (input == null || input.trim().isEmpty()) return;

    String[] codes = input.split(","); //multiple courses handle
    int addedCount = 0;

    for (String code : codes) {
        code = code.trim();
        if (code.isEmpty()) continue;

        try { //exception handling start

            // 1. MAX LIMIT CHECK
            if (currentStudent.registeredCourses.size() >= MAX_COURSES) {
                throw new CourseLimitException(
                        "Maximum " + MAX_COURSES + " courses allowed." // max limit check
                );
            }

            // 2. DUPLICATE CHECK
            if (currentStudent.registeredCourses.contains(code)) { // duplicate check
                JOptionPane.showMessageDialog(this,
                        code + " already registered.");
                continue;
            }

            // 3. COURSE VALIDATION
            Course c = getCourse(code); //validation( invalid course reject)
            if (c == null) {
                throw new InvalidCourseException(
                        code + " is not a valid course."
                );
            }

            // 4. ADD COURSE
            currentStudent.registeredCourses.add(code); //add coureses
            addedCount++; 

        } catch (CourseLimitException e) { //exception handling
            JOptionPane.showMessageDialog(this, e.getMessage());
            break; // stop further processing

        } catch (InvalidCourseException e) { //exception handling
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // SAVE ONLY IF SUCCESS
    if (addedCount > 0) { // save only if success
        save();
        JOptionPane.showMessageDialog(this,
                addedCount + " course(s) registered successfully!");
    }
}

        void dropCourse() {  //drop course optionn
    if (currentStudent.registeredCourses.isEmpty()) { // if empty - return
        JOptionPane.showMessageDialog(this, "You have no registered courses to drop.");
        return;
    }

    // Build a display string of registered courses
    StringBuilder sb = new StringBuilder("Registered Courses:\n"); //course list show
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

    String[] codesToRemove = input.split(","); // split input
    int removedCount = 0;

    // Use a standard for-loop to remove courses safely
    for (String code : codesToRemove) {
        code = code.trim(); // remove spaces
        if (code.isEmpty()) continue;

        boolean found = false; // initially dhore nicchi: “course ta paoa jabe na
        for (int i = 0; i < currentStudent.registeredCourses.size(); i++) { // loop  start (student er sob registered course ek ek kore check korbe)
            if (currentStudent.registeredCourses.get(i).equalsIgnoreCase(code)) { ///user je course drop korte chay (code), seta match hocche kina chec
                currentStudent.registeredCourses.remove(i); //matched course list theke delete hoye jacche
                removedCount++; //koyta course remove holo seta track kora hocche
                found = true; //course pawa geche confirm kora hocche


                break; // remove only one match
            }
        }
        if (!found) { //jodi loop er moddhe course na paoa jay
            JOptionPane.showMessageDialog(this, code + " not found or not registered.");
        }
    }

    if (removedCount > 0) { //jodi course remove hoy 
        save(); //update data file e write hoy
        JOptionPane.showMessageDialog(this, removedCount + " course(s) removed successfully!"); //success message
    }
}
    }

    // ---------- FILE ----------
    static void save() { //memory te thaka student list ke file e save kore
        try (PrintWriter pw = new PrintWriter(FILE)) { //file e write kore
            for (Student s : students)//every student one by one
                pw.println(s.toLine()); //Student object → text line → file e save
        } catch (Exception e) {//error handlling (file error hole message show kore)
            System.out.println("Error saving file.");
        }
    }

    static void load() { //file theke data read kore memory te load kore
        File f = new File(FILE);
        if (!f.exists()) return; //file na thakle exit (no crash)

        try (Scanner sc = new Scanner(f)) { //file read kore line by line
            while (sc.hasNextLine())///file er shob line read
                students.add(Student.fromLine(sc.nextLine()));
        } catch (Exception e) {
            System.out.println("Error loading file.");
        }
    }

    static Student find(String id) { //ID diye student search kore
        for (Student s : students)
            if (s.id.equals(id)) return s; //ID match hole return
        return null;
    }

    static Course getCourse(String code) {
        for (Course c : availableCourses)
            if (c.code.equalsIgnoreCase(code)) return c;
        return null;
    }

    static void loadCourses() { //system start hole sob course memory te load kore( full university course list memory te ready hoy)
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
    }
}