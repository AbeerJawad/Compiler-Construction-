import java.util.ArrayList;
import java.util.List;

public class ErrorHandler {
    private final List<String> errors = new ArrayList<>();

    //line number and error
    public void addError(int lineNumber, String message) {
        errors.add("Error on line " + lineNumber + ": " + message);
    }

    //checks for errors
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void printErrors() {
        if (errors.isEmpty()) {
            System.out.println("No errors found.");
            return;
        }
        System.out.println("===== Errors Detected =====");
        for (String error : errors) {
            System.out.println(error);
        }
        System.out.println("==========================");
    }
}
