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

/**
 * A mode that allows the user to add tasks and list them.
 */
public class ToDoMode implements Mode {
    private static final String FILE_PATH = "data/todo_list.txt";
    public static final String PROMPT = "Welcome to To-Do Mode!\n" +
            "Type ANYTHING to add it to your list\n" +
            "Type 'list' to view your tasks.\n" +
            "Type 'mark [indices]' to mark tasks as done.\n" +
            "Type 'unmark [indices]' to mark tasks as not done.\n" +
            "Type 'modify [index] [new task]' to modify a task.\n" +
            "Type 'delete [indices]' to remove tasks.\n" +
            "Type 'exit' to return to the Lobby.";

    private static class Task {
        String description;
        boolean isCompleted;

        Task(String description, boolean isCompleted) {
            this.description = description;
            this.isCompleted = isCompleted;
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

        while (true) {
            String input = scanner.nextLine().trim();
            String[] parts = input.split(" ", 2);
            String command = parts[0].toLowerCase();
            String arguments = parts.length > 1 ? parts[1].trim() : "";

            if (command.equals("exit")) {
                OutputHandler.printInfo("Exiting ToDo Mode.");
                break;
            } else if (command.equals("list")) {
                listTasks();
            } else if (command.equals("mark")) {
                markTasks(arguments);
            } else if (command.equals("unmark")) {
                unmarkTasks(arguments);
            } else if (command.equals("modify")) {
                modifyTask(arguments);
            } else if (command.equals("delete")) {
                deleteTasks(arguments);
            } else {
                addTask(input);
            }
        }
    }

    private void addTask(String task) {
        tasks.add(new Task(task, false)); // Default to not done
        saveTasks();
        OutputHandler.print("Added: " + task);
    }

    private void listTasks() {
        if (tasks.isEmpty()) {
            OutputHandler.print("Your task list is empty.");
        } else {
            String output = "Here are the tasks in your list:\n";
            for (int i = 0; i < tasks.size(); i++) {
                String status = tasks.get(i).isCompleted ? "[X]" : "[ ]";
                output += (i + 1) + ". " + status + " " + tasks.get(i).description + "\n";
            }
            OutputHandler.print(output);
        }
    }

    private void markTasks(String arguments) {
        updateTaskStatus(arguments, true, "marked as done");
    }

    private void unmarkTasks(String arguments) {
        updateTaskStatus(arguments, false, "marked as not done");
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
            } else {
                OutputHandler.printError("Invalid task indices.");
            }
        } catch (Exception e) {
            OutputHandler.printError("Usage: mark/unmark [task numbers]");
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
            } else {
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
            } else {
                OutputHandler.printError("Invalid task indices.");
            }
        } catch (Exception e) {
            OutputHandler.printError("Usage: delete [task numbers]");
        }
    }

    private void loadTasks() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                boolean isCompleted = line.startsWith("[X]");
                String task = line.substring(4);
                tasks.add(new Task(task, isCompleted));
            }
        } catch (IOException e) {
            OutputHandler.printError("Error loading tasks: " + e.getMessage());
        }
    }

    private void saveTasks() {
        File directory = new File("data");
        if (!directory.exists()) directory.mkdir();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Task task : tasks) {
                String status = task.isCompleted ? "[X]" : "[ ]";
                writer.write(status + " " + task.description);
                writer.newLine();
            }
        } catch (IOException e) {
            OutputHandler.printError("Error saving tasks: " + e.getMessage());
        }
    }
}
