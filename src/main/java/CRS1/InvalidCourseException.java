
package CRS1;
public class InvalidCourseException extends Exception {

    public InvalidCourseException() {
        super("Invalid course code.");
    }

    public InvalidCourseException(String message) {
        super(message);
    }
}