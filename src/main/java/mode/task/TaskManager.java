package mode.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import eggo.OutputHandler;

import exception.InvalidCommandException;
import exception.InvalidTaskFormatException;
import exception.TaskNotFoundException;

/**
 * Manages the task list, including adding, updating, deleting, searching, and listing tasks.
 * <p>
 * This class serves as the central handler for all task-related operations.
 * It interacts with {@link TaskStorage} to persist task data and ensures task-related commands
 * are executed correctly.
 * </p>
 */
public class TaskManager {

    protected static List<Task> tasks;

    static {
        tasks = new ArrayList<>();
        TaskStorage.loadTasks(); // Load existing tasks from file
    }

    public static List<Task> getTasks() {
        return tasks;
    }

    /**
     * Adds a new Todo task to the list.
     *
     * @param description Description of the task
     * @throws InvalidTaskFormatException If the description is empty
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
     * Adds a new Deadline task with a specific due date.
     *
     * @param arguments Description and due date of the deadline task in the format:
     *                  "description /by yyyy-MM-dd HHmm" (Best practice)
     * @throws InvalidTaskFormatException If the format is incorrect
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
     * Adds a new Event task with a specific start and end time.
     *
     * @param arguments Description, start time, and end time in the format:
     *                  "description /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm" (Best practice)
     * @throws InvalidTaskFormatException If the format is incorrect
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
     * Lists all tasks currently stored.
     * <p>
     * Outputs tasks in a numbered format, including their type, status, and description.
     * </p>
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
     * Updates task done status.
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


    /**
     * Searches for tasks containing a given keyword in their description.
     *
     * @param arguments The keyword to search for
     * @throws InvalidTaskFormatException If the search query is empty
     * @throws TaskNotFoundException If no matching tasks are found
     */
    public static void findTask(String arguments) throws InvalidTaskFormatException, TaskNotFoundException {
        if (arguments.isEmpty()) {
            throw new InvalidTaskFormatException("Usage: find [keyword] or find /type [todo|deadline|event]");
        }

        List<Task> matchingTasks;

        if (arguments.startsWith("/type ")) {
            String type = extractTaskType(arguments);
            matchingTasks = findTasksByType(type);
        } else {
            matchingTasks = findTasksByKeyword(arguments);
        }

        printMatchingTasks(matchingTasks, arguments);
    }

    // Extracts the task type from the command
    private static String extractTaskType(String arguments) {
        return arguments.substring(6).trim().toLowerCase();
    }

    // Finds tasks by keyword search
    private static List<Task> findTasksByKeyword(String keyword) {
        List<Task> results = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(task);
            }
        }
        return results;
    }

    // Finds tasks by type (todo, deadline, event)
    private static List<Task> findTasksByType(String type) {
        List<Task> results = new ArrayList<>();
        for (Task task : tasks) {
            if ((type.equals("todo") && task instanceof Todo) ||
                (type.equals("deadline") && task instanceof Deadline) ||
                (type.equals("event") && task instanceof Event)) {
                results.add(task);
            }
        }
        return results;
    }

    // Prints the list of matching tasks
    private static void printMatchingTasks(List<Task> tasks, String searchCriteria) throws TaskNotFoundException {
        if (tasks.isEmpty()) {
            throw new TaskNotFoundException("No matching tasks found for: " + searchCriteria);
        }

        StringBuilder output = new StringBuilder("Here are the matching tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            output.append(i + 1).append(". ").append(tasks.get(i)).append("\n");
        }

        OutputHandler.print(output.toString());
    }

    /**
     * Renames a task based on its index.
     *
     * @param arguments Task index followed by the new description
     * @throws InvalidTaskFormatException If the format is incorrect
     * @throws TaskNotFoundException If the task index is invalid
     * @throws InvalidCommandException If the input is improperly formatted
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
     * Deletes tasks by index or by matching description.
     *
     * @param arguments Task indices or task description to delete
     * @throws InvalidCommandException If the task is not found
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
     * Parses space-separated task indices from the provided string and validates them.
     * <p>
     * This method splits the input string into individual indices, converts them to integers,
     * and ensures they are within valid bounds. If an index is out of range, a
     * {@link TaskNotFoundException} is thrown. If the input contains non-numeric values,
     * an {@link InvalidCommandException} is thrown.
     * </p>
     *
     * @param arguments A space-separated string of task indices (e.g., "1 3 5").
     * @return A list of valid integer indices corresponding to the task list.
     * @throws TaskNotFoundException If any index is out of bounds.
     * @throws InvalidCommandException If the input format is incorrect (e.g., contains non-numeric values).
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
     * Validates that a task index is within bounds.
     *
     * @param index The index of the task to validate
     * @throws TaskNotFoundException If the index is out of range
     */
    private static void validateIndex(int index) throws TaskNotFoundException {
        if (index < 0 || index >= tasks.size()) {
            throw new TaskNotFoundException("Task number out of range.");
        }
    }
}
