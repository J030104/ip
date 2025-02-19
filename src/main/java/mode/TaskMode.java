package mode;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

import eggo.OutputHandler;
import eggo.Helper;

import exception.CorruptedTaskEntryException;
import exception.DirectoryCreationException;
import exception.InvalidCommandException;
import exception.InvalidTaskFormatException;
import exception.TaskNotFoundException;

/**
 * This class
 * - Allows the user to add tasks, list them, rename and delete.
 * - Allows the tasks to be marked as done. (urgent and important tags are currently Unavailable).
 */
public class TaskMode implements Mode {
    private static final String FILE_PATH = "data/task_list.txt";
    public static final String WELCOME_MESSAGE = """
            ========== Welcome to Task Mode! ==========\n\n""";
    public static final String PROMPT = """
            Type 'list' to view your tasks.
            Type 'todo [description]' to add a to-do.
            Type 'deadline [description] /by [time]' to add a deadline.
            Type 'event [description] /from [start time] /to [end time]' to add an event.
            Type 'mark [indices]' to mark tasks as done.
            Type 'unmark [indices]' to mark tasks as not done.
            Type 'rename [index] [new task name]' to rename a task).
            Type 'delete [indices]' to remove tasks.
            Type 'urg [indices]' to mark tasks as urgent.
            Type 'noturg [indices]' to remove urgent mark.
            Type 'imp [indices]' to mark tasks as important.
            Type 'notimp [indices]' to remove important mark.
            Type 'help' to view the help menu.
            Type 'exit' to return to the Lobby.""";

    private abstract static class Task {
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

        public abstract String toString();
    }

    private static class Todo extends Task {
        public Todo(String description) {
            super(description, false, false, false);
        }

        public Todo(String description, boolean isCompleted, boolean isUrgent, boolean isImportant) {
            super(description, isCompleted, isUrgent, isImportant);
        }

        @Override
        public String toString() {
            return "[T][" + (isCompleted ? "X" : " ") + "][" + (isUrgent ? "!" : " ") + "][" + (isImportant ? "I" : " ") + "] " + description;
        }
    }

    private static class Deadline extends Task {
        private final String by;

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
            return "[D][" + (isCompleted ? "X" : " ") + "][" + (isUrgent ? "!" : " ") + "][" + (isImportant ? "I" : " ") + "] " + description + " (by: " + by + ")";
        }
    }

    private static class Event extends Task {
        private final String from;
        private final String to;

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
            return "[E]["+ (isCompleted ? "X" : " ") + "][" + (isUrgent ? "!" : " ") + "][" + (isImportant ? "I" : " ") + "] " + description + " (from: " + from + " to: " + to + ")";
        }
    }

    private final List<Task> tasks;

    public TaskMode() {
        tasks = new ArrayList<>();
        loadTasks(); // Load existing tasks from file
    }

    @Override
    public void start(Scanner scanner) {
        OutputHandler.printInfo(WELCOME_MESSAGE + PROMPT);

        while (true) {
            try {
                String input = scanner.nextLine().trim();
                String[] parts = input.split(" ", 2); // To be examined
                String command = parts[0].toLowerCase();
                String arguments = parts.length > 1 ? parts[1].trim() : "";

                switch (command) {
                    case "exit" -> {
                        OutputHandler.printInfo("Exiting Task Mode.");
                        return;
                    }
                    case "todo" -> addTodo(arguments);
                    case "deadline" -> addDeadline(arguments);
                    case "event" -> addEvent(arguments);
                    case "list" -> listTasks();
                    case "mark" -> updateTasksDone(arguments, true);
                    case "unmark" -> updateTasksDone(arguments, false);
                    case "urg" -> updateTaskUrgency(arguments, true, "marked as urgent");
                    case "noturg" -> updateTaskUrgency(arguments, false, "removed urgent mark");
                    case "imp" -> updateTaskImportance(arguments, true, "marked as important");
                    case "notimp" -> updateTaskImportance(arguments, false, "removed important mark");
                    case "rename" -> renameTask(arguments);
                    case "delete" -> deleteTasks(arguments);
                    case "help" -> HelpHandler.help();
                    default -> OutputHandler.printWarning("Unknown command: " + command);
                }
            } catch (Exception e) {
                OutputHandler.printError("Exception Caught: " + e.getClass() + "\n" + e.getMessage());
            }
        }
    }


    private static class HelpHandler implements Helper {
        public static final String DELETE_DETAIL = "\n\n\"delete [description]\" can only remove one task at a time.";

        public static void help() {
            OutputHandler.print(PROMPT + DELETE_DETAIL);
        }
    }

    /**
     * Adds a new Todo to the list.
     */
    private void addTodo(String description) throws InvalidTaskFormatException {
        if (description.isEmpty()) {
            throw new InvalidTaskFormatException("Usage: todo [description]");
        }
        tasks.add(new Todo(description));
        saveTasks();
        OutputHandler.printInfo("Added: " + description);
    }

    /**
     * Adds a new Deadline to the list.
     */
    private void addDeadline(String arguments) throws InvalidTaskFormatException {
        String[] parts = arguments.split(" /by ", 2);
        if (parts.length < 2) {
            throw new InvalidTaskFormatException("Usage: deadline [description] /by [time]");
        }
        tasks.add(new Deadline(parts[0], parts[1]));
        saveTasks();
        OutputHandler.printInfo("Added: " + parts[0] + " (by: " + parts[1] + ")");
    }

    /**
     * Adds a new Event to the list.
     */
    private void addEvent(String arguments) throws InvalidTaskFormatException {
        String[] parts = arguments.split(" /from | /to ", 3);
        if (parts.length < 3) {
            throw new InvalidTaskFormatException("Usage: event [description] /from [start time] /to [end time]");
        }
        tasks.add(new Event(parts[0], parts[1], parts[2]));
        saveTasks();
        OutputHandler.printInfo("Added: " + parts[0] + " (from: " + parts[1] + " to: " + parts[2] + ")");
    }

    /**
     * Lists all tasks with their statuses.
     */
    private void listTasks() {
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
    private void updateTasksDone(String arguments, boolean status) throws TaskNotFoundException, InvalidCommandException {
        updateTaskField(arguments, task -> { task.isCompleted = status; return null; }, "marked as done");
    }

    /**
     * Updates task urgency status.
     */
    private void updateTaskUrgency(String arguments, boolean status, String message) throws TaskNotFoundException, InvalidCommandException {
        updateTaskField(arguments, task -> { task.isUrgent = status; return null; }, message);
    }

    /**
     * Updates task importance status.
     */
    private void updateTaskImportance(String arguments, boolean status, String message) throws TaskNotFoundException, InvalidCommandException {
        updateTaskField(arguments, task -> { task.isImportant = status; return null; }, message);
    }

    /**
     * Updates a specified field of tasks based on indices provided in the arguments.
     *
     * @param arguments   The string containing space-separated task indices.
     * @param fieldSetter A lambda function to update the task field.
     * @param successMsg  The message to display on successful update.
     */
    private void updateTaskField(String arguments, Function<Task, Void> fieldSetter, String successMsg) throws TaskNotFoundException, InvalidCommandException {
        List<Integer> indices = parseTaskIndices(arguments);

        for (int index : indices) {
            fieldSetter.apply(tasks.get(index));
        }

        OutputHandler.printInfo("Successfully " + successMsg + " tasks: " + arguments + ".");
        saveTasks();
    }

    /**
     * Modifies a task's description.
     */
    private void renameTask(String arguments) throws InvalidTaskFormatException, TaskNotFoundException, InvalidCommandException {
        try {
            String[] parts = arguments.split(" ", 2);
            if (parts.length < 2) {
                throw new InvalidTaskFormatException("Usage: rename [task number] [new description]");
            }

            int index = Integer.parseInt(parts[0]) - 1;
            validateIndex(index);

            tasks.get(index).description = parts[1];
            saveTasks();
            OutputHandler.printInfo("Task updated successfully.");
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid task number format. Use numbers only.", e);
        }
    }

    /**
     * Deletes tasks by index.
     */
    private void deleteTasks(String arguments) throws InvalidCommandException {
        try {
            List<Integer> indices = new ArrayList<>();
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
                    break; // Only remove one matching task --> Should be documented in the help system
                }
            }
            if (taskToRemove != null) {
                tasks.remove(taskToRemove);
            } else {
                throw new InvalidCommandException("Task not found: " + arguments);
            }
        }

        saveTasks();
        OutputHandler.printInfo("Tasks deleted successfully.");
    }

    /**
     * Parses space-separated task indices and validates them.
     */
    private List<Integer> parseTaskIndices(String arguments) throws TaskNotFoundException, InvalidCommandException {
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
            throw new InvalidCommandException("Invalid number format. Proceeding to search task item...", e);
        }

        return indices;
    }

    /**
     * Validates that a task index is within bounds.
     */
    private void validateIndex(int index) throws TaskNotFoundException {
        if (index < 0 || index >= tasks.size()) {
            throw new TaskNotFoundException("Task number out of range.");
        }
    }

    /**
     * Saves tasks to a file in a structured format.
     */
    private void saveTasks() {
        File directory = new File("data");
        try {
            if (!directory.exists() && !directory.mkdir()) {
                throw new DirectoryCreationException("Failed to create directory: " + directory.getAbsolutePath());
            }
        } catch (DirectoryCreationException e) {
            OutputHandler.printError(e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Task task : tasks) {
                StringBuilder line = new StringBuilder();

                // Task type
                if (task instanceof Todo) {
                    line.append("T|");
                } else if (task instanceof Deadline) {
                    line.append("D|");
                } else if (task instanceof Event) {
                    line.append("E|");
                }

                // Task properties
                line.append(task.isCompleted ? "1|" : "0|")
                        .append(task.isUrgent ? "1|" : "0|")
                        .append(task.isImportant ? "1|" : "0|")
                        .append(task.description).append("|");

                // Additional fields for Deadlines and Events
                if (task instanceof Deadline deadline) {
                    line.append(deadline.by);
                } else if (task instanceof Event event) {
                    line.append(event.from).append("|").append(event.to);
                }

                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            OutputHandler.printError("Error saving tasks: " + e.getMessage());
        }
    }

    /**
     * Loads tasks from a file.
     */
    private void loadTasks() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return; // No file, no tasks to load
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split("\\|"); // To correctly escape the pipe

                    if (parts.length < 5) {
                        throw new CorruptedTaskEntryException("Skipping corrupted task entry: " + line);
                    }

                    String type = parts[0];
                    boolean isCompleted = parts[1].equals("1");
                    boolean isUrgent = parts[2].equals("1");
                    boolean isImportant = parts[3].equals("1");
                    String description = parts[4];

                    switch (type) {
                    case "T" -> tasks.add(new Todo(description, isCompleted, isUrgent, isImportant));
                    case "D" -> {
                        if (parts.length < 6) {
                            throw new CorruptedTaskEntryException("Malformed deadline entry: " + line);
                        }
                        tasks.add(new Deadline(description, isCompleted, isUrgent, isImportant, parts[5]));
                    }
                    case "E" -> {
                        if (parts.length < 7) {
                            throw new CorruptedTaskEntryException("Malformed event entry: " + line);
                        }
                        tasks.add(new Event(description, isCompleted, isUrgent, isImportant, parts[5], parts[6]));
                    }
                    default -> throw new CorruptedTaskEntryException("Unknown task type: " + type);
                    }
                } catch (CorruptedTaskEntryException e) {
                    OutputHandler.printError(e.getMessage());
                }
            }
        } catch (IOException e) {
            OutputHandler.printError("Error loading tasks: " + e.getMessage());
        }
    }
}
