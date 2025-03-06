package mode.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import eggo.OutputHandler;

import exception.InvalidCommandException;
import exception.InvalidTaskFormatException;
import exception.TaskNotFoundException;

public class TaskManager {

    protected static List<Task> tasks = new ArrayList<>();

    static {
        tasks = new ArrayList<>();
        TaskStorage.loadTasks(); // Load existing tasks from file
    }

    public static List<Task> getTasks() {
        return tasks;
    }

    /**
     * Adds a new Todo to the list.
     */
    public static void addTodo(String description) throws InvalidTaskFormatException {
        if (description.isEmpty()) {
            throw new InvalidTaskFormatException("Usage: todo [description]");
        }
        tasks.add(new Todo(description));
        TaskStorage.saveTasks();
        OutputHandler.printInfo("Added: " + description);
    }

    /**
     * Adds a new Deadline to the list.
     */
    public static void addDeadline(String arguments) throws InvalidTaskFormatException {
        String[] parts = arguments.split(" /by ", 2);
        if (parts.length < 2) {
            throw new InvalidTaskFormatException("Usage: deadline [description] /by [time]");
        }
        tasks.add(new Deadline(parts[0], parts[1]));
        TaskStorage.saveTasks();
        OutputHandler.printInfo("Added: " + parts[0] + " (by: " + parts[1] + ")");
    }

    /**
     * Adds a new Event to the list.
     */
    public static void addEvent(String arguments) throws InvalidTaskFormatException {
        String[] parts = arguments.split(" /from | /to ", 3);
        if (parts.length < 3) {
            throw new InvalidTaskFormatException("Usage: event [description] /from [start time] /to [end time]");
        }
        tasks.add(new Event(parts[0], parts[1], parts[2]));
        TaskStorage.saveTasks();
        OutputHandler.printInfo("Added: " + parts[0] + " (from: " + parts[1] + " to: " + parts[2] + ")");
    }

    /**
     * Lists all tasks with their statuses.
     */
    public static void listTasks() {
        if (tasks.isEmpty()) {
            OutputHandler.printInfo("Your task list is empty.");
            return;
        }

        StringBuilder output = new StringBuilder("Here are the tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            output.append(i + 1).append(". ").append(tasks.get(i)).append("\n");
        }

        output.append("\n").append("Total: ").append(tasks.size()).append(" task(s).");
        OutputHandler.print(output.toString());
    }

    /**
     * Marks tasks as completed or not.
     */
    public static void updateTasksDone(String arguments, boolean status) throws TaskNotFoundException, InvalidCommandException {
        updateTaskField(arguments, task -> { task.isCompleted = status; return null; }, "marked as done");
    }

    /**
     * Updates task urgency status.
     */
    public static void updateTaskUrgency(String arguments, boolean status, String message) throws TaskNotFoundException, InvalidCommandException {
        updateTaskField(arguments, task -> { task.isUrgent = status; return null; }, message);
    }

    /**
     * Updates task importance status.
     */
    public static void updateTaskImportance(String arguments, boolean status, String message) throws TaskNotFoundException, InvalidCommandException {
        updateTaskField(arguments, task -> { task.isImportant = status; return null; }, message);
    }

    /**
     * Updates a specified field of tasks based on indices provided in the arguments.
     *
     * @param arguments   The string containing space-separated task indices.
     * @param fieldSetter A lambda function to update the task field.
     * @param successMsg  The message to display on successful update.
     */
    private static void updateTaskField(String arguments, Function<Task, Void> fieldSetter, String successMsg) throws TaskNotFoundException, InvalidCommandException {
        List<Integer> indices = parseTaskIndices(arguments);

        for (int index : indices) {
            fieldSetter.apply(tasks.get(index));
        }

        OutputHandler.printInfo("Successfully " + successMsg + " tasks: " + arguments + ".");
        TaskStorage.saveTasks();
    }

    public static void findTask(String arguments) throws TaskNotFoundException, InvalidCommandException {
        if (arguments.isEmpty()) {
            throw new InvalidTaskFormatException("Usage: find [keyword]");
        }

        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase().contains(arguments.toLowerCase())) {
                matchingTasks.add(task);
            }
        }

        if (matchingTasks.isEmpty()) {
            OutputHandler.printInfo("No matching tasks found for: " + arguments);
            return;
        }

        StringBuilder output = new StringBuilder("Here are the matching tasks in your list:\n");
        for (int i = 0; i < matchingTasks.size(); i++) {
            output.append(i + 1).append(". ").append(matchingTasks.get(i)).append("\n");
        }

        OutputHandler.print(output.toString());
    }

    /**
     * Modifies a task's description.
     */
    public static void renameTask(String arguments) throws InvalidTaskFormatException, TaskNotFoundException, InvalidCommandException {
        try {
            String[] parts = arguments.split(" ", 2);
            if (parts.length < 2) {
                throw new InvalidTaskFormatException("Usage: rename [task number] [new description]");
            }

            int index = Integer.parseInt(parts[0]) - 1;
            validateIndex(index);

            tasks.get(index).description = parts[1];
            TaskStorage.saveTasks();
            OutputHandler.printInfo("Task updated successfully.");
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid task number format. Use numbers only.", e);
        }
    }

    /**
     * Deletes tasks by index while also support delete by name.
     */
    public static void deleteTasks(String arguments) throws InvalidCommandException {
        try {
            List<Integer> indices;
            indices = parseTaskIndices(arguments);
            // Reverse sort ensures we delete from the back to avoid shifting issues
            indices.sort(Collections.reverseOrder());
            for (int index : indices) {
                tasks.remove(index);
            }
        } catch (InvalidCommandException | TaskNotFoundException e) {
            // If parsing fails, assume arguments is a task description
            Task taskToRemove = null;
            for (Task task : tasks) {
                if (task.getDescription().equals(arguments)) {
                    taskToRemove = task;
                    break; // Only remove one matching task
                }
            }
            if (taskToRemove != null) {
                tasks.remove(taskToRemove);
            } else {
                throw new InvalidCommandException("Task not found: " + arguments);
            }
        }

        TaskStorage.saveTasks();
        OutputHandler.printInfo("Tasks deleted successfully.");
    }

    /**
     * Parses space-separated TaskStorage indices and validates them.
     */
    private static List<Integer> parseTaskIndices(String arguments) throws TaskNotFoundException, InvalidCommandException {
        // Have to take care of the delimiter issue here
        List<Integer> indices = new ArrayList<>();
        try {
            String[] parts = arguments.split(" ");
            for (String part : parts) {
                int index = Integer.parseInt(part) - 1;
                validateIndex(index);
                indices.add(index);
            }
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid number format. Proceeding to search TaskStorage item...", e);
        }

        return indices;
    }

    /**
     * Validates that a TaskStorage index is within bounds.
     */
    private static void validateIndex(int index) throws TaskNotFoundException {
        if (index < 0 || index >= tasks.size()) {
            throw new TaskNotFoundException("Task number out of range.");
        }
    }
}
