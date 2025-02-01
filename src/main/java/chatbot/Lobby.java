package chatbot;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import chatbot.InputHandler;
import chatbot.OutputHandler;

/**
 * The Lobby class manages different chatbot modes and provides a central interface.
 * Users can select modes, exit the chatbot, or request help.
 *
 * Features:
 * - Registers available chatbot modes.
 * - Handles user input to switch modes.
 * - Provides user feedback and guidance.
 *
 * Usage:
 * Create an instance of `Lobby` and call `start(scanner)` to begin.
 */
public class Lobby {
    private final Map<String, Mode> modes; // Stores available chatbot modes

    /**
     * Initializes the chatbot lobby with different modes.
     */
    public Lobby() {
        modes = new HashMap<>();
        modes.put("echo", new EchoMode()); // Register Echo Mode
        modes.put("todo", new ToDoMode()); // Register To-Do Mode
    }

    /**
     * Starts the chatbot lobby, allowing users to choose modes or exit.
     *
     * @param scanner Scanner object to read user input.
     */
    public void start(Scanner scanner) {

        // Generate a list of available modes dynamically
        String availableModes = String.join(", ", modes.keySet());

        // Display the welcome message with available modes
        final String welcome = """
                Welcome to the Chatbot Lobby!
                Type a mode name to enter a mode.
                
                Available modes: %s
                Type 'help' for assistance. (Currently Unavailable)"""
                .formatted(availableModes);

        OutputHandler.printInfo(welcome);

        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();

            // Process input before handling commands
            input = InputHandler.processInput(input);

            if (input.equals("exit")) {
                OutputHandler.printInfo("Exiting chatbot Eggo. Goodbye!");
                break;
            }
            else if (input.equals("help")) {
                // Placeholder for help system (unavailable for now)
                OutputHandler.printInfo("Help is currently unavailable.");
            }
            else if (modes.containsKey(input)) {
                // Switch to the selected mode
                modes.get(input).start(scanner);
                OutputHandler.printInfo("Returned to the Lobby.");
            }
            else {
                // Handle invalid input
                OutputHandler.printInfo("Unknown command. Type 'help' for available commands.");
            }
        }
    }
}
