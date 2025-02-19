package exception;

public class CorruptedTaskEntryException extends RuntimeException {
    public CorruptedTaskEntryException(String message) {
        super(message);
    }
}
