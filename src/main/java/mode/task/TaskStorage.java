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

public class TaskStorage {

    private static final String FILE_PATH = "data/task_list.txt";

    private static final List<Task> tasks = TaskManager.getTasks();

    /**
     * Saves tasks to a file in a structured format.
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
     * Loads tasks from a file.
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
                        throw new CorruptedTaskEntryException("Skipping corrupted TaskManager entry: " + line);
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
                    default -> throw new CorruptedTaskEntryException("Unknown TaskManager type: " + type);
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
