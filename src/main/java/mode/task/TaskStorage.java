package mode.task;

import java.util.List;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import eggo.OutputHandler;

import exception.CorruptedTaskEntryException;
import exception.DirectoryCreationException;

/**
 * Manages the storage and retrieval of tasks from a file.
 * <p>
 * This class handles saving tasks to a structured file format and loading them back
 * into the application when it starts. It ensures task persistence between sessions.
 * </p>
 */
public class TaskStorage {

    private static final String FILE_PATH = "data/task_list.txt";

    private static final List<Task> tasks = TaskManager.getTasks();

    /**
     * Saves the current task list to a file.
     * <p>
     * The tasks are stored in a structured format where each task type (Todo, Deadline, Event)
     * is represented by a specific identifier, followed by task properties such as completion
     * status, urgency, importance, and additional task-specific details.
     * </p>
     * <p>
     * If the storage directory does not exist, it will attempt to create it.
     * In case of any errors, they will be logged but will not interrupt the program execution.
     * </p>
     */
    public static void saveTasks() {
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
     * Loads tasks from the stored file into the application's task list.
     * <p>
     * Reads the task file line by line and reconstructs tasks based on their stored format.
     * If the file does not exist, no tasks are loaded. The method also performs basic validation
     * to detect and skip corrupted or malformed entries, logging appropriate error messages.
     * </p>
     */
    public static void loadTasks() {
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
