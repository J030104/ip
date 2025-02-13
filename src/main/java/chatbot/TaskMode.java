package chatbot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Consumer;

/**
 * This class
 * - Allows the user to add tasks, list them, rename and delete.
 * - Allows the tasks to be marked as done. (urgent and important tags are currently Unavailable).
 */
public class TaskMode implements Mode {
    private static final String FILE_PATH = "data/task_list.txt";
    public static final String PROMPT = """
            ========== Welcome to Task Mode! ==========
            
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
            Type 'exit' to return to the Lobby.""";

    private abstract static class Task {
        protected String description;
        protected boolean isCompleted;
        protected boolean isUrgent;
        protected boolean isImportant;

        Task(String description, boolean isCompleted, boolean isUrgent, boolean isImportant) {
            this.description = description;
            this.isCompleted = isCompleted;
            this.isUrgent = isUrgent;
            this.isImportant = isImportant;
        }

        public abstract String toString();
    }

    private static class Todo extends Task {
        public Todo(String description) {
            super(description, false, false, false);
        }

        @Override
        public String toString() {
            return "[T][" + (isCompleted ? "X" : " ") + "] " + description;
        }
    }

    private static class Deadline extends Task {
        private final String by;

        public Deadline(String description, String by) {
            super(description, false, false, false);
            this.by = by;
        }

        @Override
        public String toString() {
            return "[D][" + (isCompleted ? "X" : " ") + "] " + description + " (by: " + by + ")";
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

        @Override
        public String toString() {
            return "[E][" + (isCompleted ? "X" : " ") + "] " + description + " (from: " + from + " to: " + to + ")";
        }
    }

    private final List<Task> tasks;

    public TaskMode() {
        tasks = new ArrayList<>();
//        loadTasks(); // Load existing tasks from file
    }

    @Override
    public void start(Scanner scanner) {
        OutputHandler.print(PROMPT);

//        label:
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
                        //                break label;
                    }
                    case "todo" -> addTodo(arguments);
                    case "deadline" -> addDeadline(arguments);
                    case "event" -> addEvent(arguments);
                    case "list" -> listTasks();
                    case "mark" -> markTasks(arguments);
                    case "unmark" -> unmarkTasks(arguments);
                    case "rename" -> renameTask(arguments);
                    case "delete" -> deleteTasks(arguments);
                    case "urg" -> updateTaskUrgency(arguments, true, "marked as urgent");
                    case "noturg" -> updateTaskUrgency(arguments, false, "removed urgent mark");
                    case "imp" -> updateTaskImportance(arguments, true, "marked as important");
                    case "notimp" -> updateTaskImportance(arguments, false, "removed important mark");
                    default -> OutputHandler.printWarning("Unknown command: " + command);
                }
            } catch (Exception e) {
                OutputHandler.printError("Exception Caught.\n" + e.getMessage());
            }
        }
    }

    /**
     * Adds a new Todo to the list.
     */
    private void addTodo(String description) throws InvalidCommandException {
        if (description.isEmpty()) {
            throw new InvalidCommandException("Usage: todo [description]");
        }
        tasks.add(new Todo(description));
//        saveTasks();
        OutputHandler.print("Added: " + description);
    }

    /**
     * Adds a new Deadline to the list.
     */
    private void addDeadline(String arguments) throws InvalidCommandException {
        String[] parts = arguments.split(" /by ", 2);
        if (parts.length < 2) {
            throw new InvalidCommandException("Usage: deadline [description] /by [time]");
        }
        tasks.add(new Deadline(parts[0], parts[1]));
//        saveTasks();
        OutputHandler.print("Added: " + parts[0] + " (by: " + parts[1] + ")");
    }

    /**
     * Adds a new Event to the list.
     */
    private void addEvent(String arguments) throws InvalidCommandException {
        String[] parts = arguments.split(" /from | /to ", 3);
        if (parts.length < 3) {
            throw new InvalidCommandException("Usage: event [description] /from [start time] /to [end time]");
        }
        tasks.add(new Event(parts[0], parts[1], parts[2]));
//        saveTasks();
        OutputHandler.print("Added: " + parts[0] + " (from: " + parts[1] + " to: " + parts[2] + ")");
    }

    /**
     * Lists all tasks with their statuses.
     */
    private void listTasks() {
        if (tasks.isEmpty()) {
            OutputHandler.print("Your task list is empty.");
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
    private void markTasks(String arguments) throws InvalidCommandException {
        updateTaskField(arguments, task -> { task.isCompleted = true; return null; }, "marked as done");
    }

    private void unmarkTasks(String arguments) throws InvalidCommandException {
        updateTaskField(arguments, task -> { task.isCompleted = false; return null; }, "marked as not done");
    }

    /**
     * Updates task urgency status.
     */
    private void updateTaskUrgency(String arguments, boolean status, String message) throws InvalidCommandException {
        updateTaskField(arguments, task -> { task.isUrgent = status; return null; }, message);
    }

    /**
     * Updates task importance status.
     */
    private void updateTaskImportance(String arguments, boolean status, String message) throws InvalidCommandException {
        updateTaskField(arguments, task -> { task.isImportant = status; return null; }, message);
    }

    /**
     * Updates a specified field of tasks based on indices provided in the arguments.
     *
     * @param arguments   The string containing space-separated task indices.
     * @param fieldSetter A lambda function to update the task field.
     * @param successMsg  The message to display on successful update.
     */
    private void updateTaskField(String arguments, Function<Task, Void> fieldSetter, String successMsg) throws InvalidCommandException {
        List<Integer> indices = parseTaskIndices(arguments);
        if (indices.isEmpty()) {
            OutputHandler.printError("Invalid task indices.");
            return;
        }

        for (int index : indices) {
            fieldSetter.apply(tasks.get(index));
        }

        OutputHandler.print("Successfully " + successMsg + " tasks: " + indices);
    }

    /**
     * Modifies a task's description.
     */
    private void renameTask(String arguments) throws InvalidCommandException{
        try {
            String[] parts = arguments.split(" ", 2);
            if (parts.length < 2) {
                throw new InvalidCommandException("Usage: rename [task number] [new description]");
            }

            int index = Integer.parseInt(parts[0]) - 1;
            validateIndex(index);

            tasks.get(index).description = parts[1];
            OutputHandler.print("Task updated successfully.");
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid task number format. Use numbers only.", e);
        }
    }

    /**
     * Deletes tasks by index.
     */
    private void deleteTasks(String arguments) throws InvalidCommandException {
        List<Integer> indices = parseTaskIndices(arguments);
        if (indices.isEmpty()) {
            OutputHandler.printError("Invalid task indices.");
            return;
        }

        // Reverse sort ensures we delete from the back to avoid shifting issues
        indices.sort(Collections.reverseOrder());
        for (int index : indices) {
            tasks.remove(index);
        }

        OutputHandler.print("Tasks deleted successfully.");
    }

    /**
     * Parses space-separated task indices and validates them.
     */
    private List<Integer> parseTaskIndices(String arguments) throws InvalidCommandException {
        List<Integer> indices = new ArrayList<>();
        try {
            String[] parts = arguments.split(" ");
            for (String part : parts) {
                int index = Integer.parseInt(part) - 1;
                validateIndex(index);
                indices.add(index);
            }
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid task number format. Use numbers only.", e);
        }
        return indices;
    }

    /**
     * Validates that a task index is within bounds.
     */
    private void validateIndex(int index) throws InvalidCommandException {
        if (index < 0 || index >= tasks.size()) {
            throw new InvalidCommandException("Task number out of range.");
        }
    }

    /**
     * Loads tasks from a file.
     */
//    private void loadTasks() {
//        File file = new File(FILE_PATH);
//        if (!file.exists()) {
//            return;
//        }
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                boolean isCompleted = line.startsWith("[X]");
//                boolean isUrgent = line.contains("[U]");
//                boolean isImportant = line.contains("[I]");
//                String task = line.replace("[X]", "").replace("[U]", "").replace("[I]", "").trim();
//                tasks.add(new Task(task, isCompleted, isUrgent, isImportant));
//            }
//        } catch (IOException e) {
//            OutputHandler.printError("Error loading tasks: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Saves tasks to a file.
//     */
//    private void saveTasks() {
//        File directory = new File("data");
//        if (!directory.exists()) {
//            if (!directory.mkdir()) {
//                OutputHandler.printError("Failed to create directory: " + directory.getAbsolutePath());
//                return;
//            }
//        }
//
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
//            for (Task task : tasks) {
//                String status = (task.isCompleted ? "[X]" : "[ ]") + (task.isUrgent ? "[U]" : "[ ]") + (task.isImportant ? "[I]" : "[ ]");
//                writer.write(status + " " + task.description);
//                writer.newLine();
//            }
//        } catch (IOException e) {
//            OutputHandler.printError("Error saving tasks: " + e.getMessage());
//        }
//    }
}
