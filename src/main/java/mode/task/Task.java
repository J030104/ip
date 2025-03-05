package mode.task;

public abstract class Task {
    protected String description;
    protected boolean isCompleted;
    protected boolean isUrgent;
    protected boolean isImportant;

    public Task(String description, boolean isCompleted, boolean isUrgent, boolean isImportant) {
        this.description = description;
        this.isCompleted = isCompleted;
        this.isUrgent = isUrgent;
        this.isImportant = isImportant;
    }

    public String getDescription() {
        return description;
    }

    protected String getStatusString() {
        return "[" + (isCompleted ? "X" : " ") + "][" + (isUrgent ? "!" : " ") + "][" + (isImportant ? "I" : " ") + "] ";
    }

    @Override
    public String toString() {
        return getStatusString() + description;
    }
}