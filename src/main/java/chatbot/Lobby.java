package chatbot;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Represents a chatbot lobby where users can interact with and switch between different modes.
 * The lobby serves as the main entry point and management interface for available chatbot modes.
 */
public class Lobby {
    private final Map<String, Mode> modes; // Stores available chatbot modes

    /**
     * Initializes the chatbot lobby with different modes.
     */
    public Lobby() {
        modes = new HashMap<>();
        modes.put("echo", new EchoMode()); // Register Echo Mode
        modes.put("task", new TaskMode()); // Register Task Mode
    }

    
    /**
     * Features:
     * - Predefined responses for common input cases.
     * - Default response for unrecognized inputs.
     */
    public static class ResponseGenerator {
        private static final Map<String, String> responseMap = new HashMap<>();

        static {
            responseMap.put("empty", "I didn't catch that. Could you try again?");
            responseMap.put("hello", "Hi! How can I assist you today?");
        }

        public static Map<String, String> getResponseMap() {
            return responseMap;
        }
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
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("bye")) {
                OutputHandler.printInfo("Exiting chatbot Eggo. Goodbye!");
                break;
            }
            else if (input.equalsIgnoreCase("help")) {
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
                Map<String, String> responseMap = ResponseGenerator.getResponseMap();
                if (!responseMap.containsKey(input)) {
                    input = "empty";
                }
                String response = responseMap.get(input);
                OutputHandler.print(response);
                OutputHandler.printInfo("Type 'help' for available commands.");
            }
        }
    }
}
