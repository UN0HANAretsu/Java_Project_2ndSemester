package C_R_S;

import java.util.*;

public class Student {

    String id, name, section, semester, year, password;
    ArrayList<String> registeredCourses = new ArrayList<>();

    public Student(String id, String name, String section, String semester, String year, String password) {
        this.id = id;
        this.name = name;
        this.section = section;
        this.semester = semester;
        this.year = year;
        this.password = password;
    }

    // save to file
    public String toLine() {
        String line = id + "," + name + "," + section + "," + semester + "," + year + "," + password;

        for (String c : registeredCourses)
            line += "," + c;

        return line;
    }

    // load from file
    public static Student fromLine(String line) {
        String[] d = line.split(",");

        Student s = new Student(d[0], d[1], d[2], d[3], d[4], d[5]);

        for (int i = 6; i < d.length; i++)
            s.registeredCourses.add(d[i]);

        return s;
    }
}