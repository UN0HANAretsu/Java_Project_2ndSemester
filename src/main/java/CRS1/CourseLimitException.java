
package CRS1;

public class CourseLimitException extends Exception {

    public CourseLimitException() {
        super("Maximum course limit exceeded.");
    }

    public CourseLimitException(String message) {
        super(message);
    }
}