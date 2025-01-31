package chatbot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A mode that allows the user to add tasks and list them.
 */
public class ToDoMode implements Mode {
    private static final String FILE_PATH = "data/todo_list.txt";
    public static final String PROMPT = "Welcome to To-Do Mode!\n" +
            "Type literally ANYTHING to add it to your list\n" +
            "Type 'list' to view your tasks.\n" +
            "Type 'exit' to return to the Lobby.";
    private List<String> tasks;

    public ToDoMode() {
        tasks = new ArrayList<>();
        loadTasks(); // Load existing tasks from file
    }

    @Override
    public void start(Scanner scanner) {
        OutputHandler.print(PROMPT);

        while (true) {
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                OutputHandler.printInfo("Exiting ToDo Mode.");
                break;
            } else if (input.equalsIgnoreCase("list")) {
                listTasks();
            } else {
                addTask(input);
            }
        }
    }

    /**
     * Adds a task and saves it to the file.
     */
    private void addTask(String task) {
        tasks.add(task);
        saveTasks(); // Persist changes
        OutputHandler.print("Added: " + task);
    }

    /**
     * Displays all tasks.
     */
    private void listTasks() {
        if (tasks.isEmpty()) {
            OutputHandler.print("Your task list is empty.");
        } else {
            StringBuilder listOutput = new StringBuilder();
            for (int i = 0; i < tasks.size(); i++) {
                listOutput.append((i + 1)).append(". ").append(tasks.get(i)).append("\n");
            }
            OutputHandler.print(listOutput.toString());
        }
    }

    /**
     * Loads tasks from the file.
     */
    private void loadTasks() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return; // No saved tasks
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String task;
            while ((task = reader.readLine()) != null) {
                tasks.add(task);
            }
        } catch (IOException e) {
            OutputHandler.printError("Error loading tasks: " + e.getMessage());
        }
    }

    /**
     * Saves tasks to the file.
     */
    private void saveTasks() {
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdir(); // Create "data" folder if it doesn't exist
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String task : tasks) {
                writer.write(task);
                writer.newLine();
            }
        } catch (IOException e) {
            OutputHandler.printError("Error saving tasks: " + e.getMessage());
        }
    }
}
