package chatbot;

import java.io.*;
import java.util.*;

/**
 * A mode that allows the user to add tasks and list them.
 */
public class ToDoMode implements Mode {
    private static final String FILE_PATH = "data/todo_list.txt";
    public static final String PROMPT = """
            Welcome to To-Do Mode!
            Type ANYTHING to add it to your list
            Type 'list' to view your tasks.
            Type 'mark [indices]' to mark tasks as done.
            Type 'unmark [indices]' to mark tasks as not done.
            Type 'modify [index] [new task]' to modify a task.
            Type 'delete [indices]' to remove tasks.
            Type 'urg [indices]' to mark tasks as urgent.
            Type 'noturg [indices]' to remove urgent mark.
            Type 'imp [indices]' to mark tasks as important.
            Type 'notimp [indices]' to remove important mark.
            Type 'exit' to return to the Lobby.""";

    private static class Task {
        String description;
        boolean isCompleted;
        boolean isUrgent;
        boolean isImportant;

        Task(String description, boolean isCompleted, boolean isUrgent, boolean isImportant) {
            this.description = description;
            this.isCompleted = isCompleted;
            this.isUrgent = isUrgent;
            this.isImportant = isImportant;
        }
    }

    private final List<Task> tasks;

    public ToDoMode() {
        tasks = new ArrayList<>();
        loadTasks(); // Load existing tasks from file
    }

    @Override
    public void start(Scanner scanner) {
        OutputHandler.print(PROMPT);

        label:
        while (true) {
            String input = scanner.nextLine().trim();
            String[] parts = input.split(" ", 2);
            String command = parts[0].toLowerCase();
            String arguments = parts.length > 1 ? parts[1].trim() : "";

            switch (command) {
            case "exit":
                OutputHandler.printInfo("Exiting ToDo Mode.");
                break label;
            case "list":
                listTasks();
                break;
            case "mark":
                markTasks(arguments);
                break;
            case "unmark":
                unmarkTasks(arguments);
                break;
            case "modify":
                modifyTask(arguments);
                break;
            case "delete":
                deleteTasks(arguments);
                break;
            case "urg":
                updateTaskUrgency(arguments, true, "marked as urgent");
                break;
            case "noturg":
                updateTaskUrgency(arguments, false, "removed urgent mark");
                break;
            case "imp":
                updateTaskImportance(arguments, true, "marked as important");
                break;
            case "notimp":
                updateTaskImportance(arguments, false, "removed important mark");
                break;
            default:
                addTask(input);
                break;
            }
        }
    }

    private void addTask(String task) {
        tasks.add(new Task(task, false, false, false)); // Default to not done
        saveTasks();
        OutputHandler.print("Added: " + task);
    }

    private void listTasks() {
        if (tasks.isEmpty()) {
            OutputHandler.print("Your task list is empty.");
            return;
        }

        StringBuilder output = new StringBuilder("Here are the tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            String status = (tasks.get(i).isCompleted ? "[X]" : "[ ]") + (tasks.get(i).isUrgent ? "[U]" : "[ ]") + (tasks.get(i).isImportant ? "[I]" : "[ ]");
            output.append(i + 1).append(". ").append(status).append(" ").append(tasks.get(i).description).append("\n");
        }
        OutputHandler.print(output.toString());
    }

    private void markTasks(String arguments) {
        updateTaskStatus(arguments, true, "marked as done");
    }

    private void unmarkTasks(String arguments) {
        updateTaskStatus(arguments, false, "marked as not done");
    }

    private void updateTaskUrgency(String arguments, boolean status, String message) {
        try {
            String[] parts = arguments.split(" ");
            List<Integer> indices = new ArrayList<>();
            for (String part : parts) {
                int index = Integer.parseInt(part) - 1;
                if (index >= 0 && index < tasks.size()) {
                    tasks.get(index).isUrgent = status;
                    indices.add(index + 1);
                }
            }
            if (!indices.isEmpty()) {
                saveTasks();
                OutputHandler.print("Successfully " + message + " tasks: " + indices);
            }
            else {
                OutputHandler.printError("Invalid task indices.");
            }
        } catch (Exception e) {
            OutputHandler.printError("Usage: urg/noturg [task numbers]");
        }
    }

    private void updateTaskStatus(String arguments, boolean status, String message) {
        try {
            String[] parts = arguments.split(" ");
            List<Integer> indices = new ArrayList<>();
            for (String part : parts) {
                int index = Integer.parseInt(part) - 1;
                if (index >= 0 && index < tasks.size()) {
                    tasks.get(index).isCompleted = status;
                    indices.add(index + 1);
                }
            }
            if (!indices.isEmpty()) {
                saveTasks();
                OutputHandler.print("Successfully " + message + " tasks: " + indices);
            }
            else {
                OutputHandler.printError("Invalid task indices.");
            }
        } catch (Exception e) {
            OutputHandler.printError("Usage: mark/unmark [task numbers]");
        }
    }

    private void updateTaskImportance(String arguments, boolean status, String message) {
        try {
            String[] parts = arguments.split(" ");
            List<Integer> indices = new ArrayList<>();
            for (String part : parts) {
                int index = Integer.parseInt(part) - 1;
                if (index >= 0 && index < tasks.size()) {
                    tasks.get(index).isImportant = status;
                    indices.add(index + 1);
                }
            }
            if (!indices.isEmpty()) {
                saveTasks();
                OutputHandler.print("Successfully " + message + " tasks: " + indices);
            }
            else {
                OutputHandler.printError("Invalid task indices.");
            }
        } catch (Exception e) {
            OutputHandler.printError("Usage: imp/notimp [task numbers]");
        }
    }

    private void modifyTask(String arguments) {
        try {
            String[] parts = arguments.split(" ", 2);
            int index = Integer.parseInt(parts[0]) - 1;
            if (index >= 0 && index < tasks.size()) {
                tasks.get(index).description = parts[1];
                saveTasks();
                OutputHandler.print("Task updated successfully.");
            }
            else {
                OutputHandler.printError("Invalid task index.");
            }
        } catch (Exception e) {
            OutputHandler.printError("Usage: modify [task number] [new task description]");
        }
    }

    private void deleteTasks(String arguments) {
        try {
            String[] parts = arguments.split(" ");
            List<Integer> indices = new ArrayList<>();
            for (String part : parts) {
                int index = Integer.parseInt(part) - 1;
                if (index >= 0 && index < tasks.size()) {
                    indices.add(index);
                }
            }
            if (!indices.isEmpty()) {
                indices.sort(Collections.reverseOrder());
                for (int index : indices) {
                    tasks.remove(index);
                }
                saveTasks();
                OutputHandler.print("Tasks deleted successfully.");
            }
            else {
                OutputHandler.printError("Invalid task indices.");
            }
        } catch (Exception e) {
            OutputHandler.printError("Usage: delete [task numbers]");
        }
    }

    private void loadTasks() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                boolean isCompleted = line.startsWith("[X]");
                boolean isUrgent = line.contains("[U]");
                boolean isImportant = line.contains("[I]");
                String task = line.replace("[X]", "").replace("[U]", "").replace("[I]", "").trim();
                tasks.add(new Task(task, isCompleted, isUrgent, isImportant));
            }
        } catch (IOException e) {
            OutputHandler.printError("Error loading tasks: " + e.getMessage());
        }
    }

    private void saveTasks() {
        File directory = new File("data");
        if (!directory.exists()) {
            if (!directory.mkdir()) {
                OutputHandler.printError("Failed to create directory: " + directory.getAbsolutePath());
                return;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Task task : tasks) {
                String status = (task.isCompleted ? "[X]" : "[ ]") + (task.isUrgent ? "[U]" : "[ ]") + (task.isImportant ? "[I]" : "[ ]");
                writer.write(status + " " + task.description);
                writer.newLine();
            }
        } catch (IOException e) {
            OutputHandler.printError("Error saving tasks: " + e.getMessage());
        }
    }
}
