package mode.task;

public class Todo extends Task {
    public Todo(String description) {
        super(description, false, false, false);
    }

    public Todo(String description, boolean isCompleted, boolean isUrgent, boolean isImportant) {
        super(description, isCompleted, isUrgent, isImportant);
    }

    @Override
    public String toString() {
        return "[T] " + super.toString();
    }
}
