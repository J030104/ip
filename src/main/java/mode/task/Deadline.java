package mode.task;

public class Deadline extends Task {
    protected final String by;

    public Deadline(String description, String by) {
        super(description, false, false, false);
        this.by = by;
    }

    public Deadline(String description, boolean isCompleted, boolean isUrgent, boolean isImportant, String by) {
        super(description, isCompleted, isUrgent, isImportant);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D] " + super.toString() + " (by: " + by + ")";
    }
}