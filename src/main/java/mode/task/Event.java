package mode.task;

public class Event extends Task {
    protected final String from;
    protected final String to;

    public Event(String description, String from, String to) {
        super(description, false, false, false);
        this.from = from;
        this.to = to;
    }

    public Event(String description, boolean isCompleted, boolean isUrgent, boolean isImportant, String from, String to) {
        super(description, isCompleted, isUrgent, isImportant);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E] " + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}