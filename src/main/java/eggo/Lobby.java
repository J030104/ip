package eggo;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import exception.InvalidCommandException;

import mode.Mode;
import mode.TaskMode;
import mode.EchoMode;

/**
 * Represents the Eggo lobby where users can interact with and switch between different modes.
 * The lobby serves as the main entry point and management interface for available Eggo modes.
 */
public class Lobby {
    private final Map<String, Mode> modes; // Stores available Eggo modes
    private static String availableModes;

    /**
     * Initializes the Eggo lobby with different modes.
     * The available modes are stored in a HashMap for quick access.
     */
    public Lobby() {
        modes = new HashMap<>();
        modes.put("echo", new EchoMode()); // Register Echo Mode
        modes.put("task", new TaskMode()); // Register Task Mode
        availableModes = String.join(", ", modes.keySet());
    }

    /**
     * A nested static class responsible for generating predefined responses
     * to common user inputs and handling default responses for unrecognized inputs.
     */
    public static class ResponseGenerator {
        private static final Map<String, String> responseMap = new HashMap<>();

        // Initialize predefined responses
        static {
            responseMap.put("hello", "Hi! How can I assist you today?");
            responseMap.put("hi", "Hi! How can I assist you today?");
            responseMap.put("hey", "Hi! How can I assist you today?");
            responseMap.put("how are you?", "Hi! I'm great. Thanks for asking! How can I assist you today?");
            responseMap.put("what's your name?", "My name is Eggo! How can I assist you today?");
            responseMap.put("your name?", "My name is Eggo! How can I assist you today?");
        }

        /**
         * Retrieves the map containing predefined responses.
         *
         * @return A map where keys are user inputs and values are corresponding chatbot responses.
         */
        public static Map<String, String> getResponseMap() {
            return responseMap;
        }
    }

    /**
     * Starts the Eggo lobby, allowing users to select modes or exit the application.
     * The method runs an interactive loop where users can input commands.
     *
     * @param scanner Scanner object to read user input from the console.
     */
    public void start(Scanner scanner) {

        // Display the welcome message with available modes
        final String WELCOME = """
                Welcome to the Chatbot Lobby!
                Type a mode name to enter a mode.
                Available modes: %s
                
                Type 'help' for assistance.
                Type 'exit' to leave."""
                .formatted(availableModes);

        OutputHandler.printInfo(WELCOME);

        while (true) {
            try {
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("bye")) {
                    OutputHandler.printInfo("Exiting chatbot Eggo. Goodbye!");
                    break;
                } else if (input.equalsIgnoreCase("help")) {
                    HelpHandler.help();
                } else if (modes.containsKey(input)) {
                    // Switch to the selected mode
                    modes.get(input).start(scanner);
                    OutputHandler.printInfo("Returned to the Lobby.");
                } else {
                    // Handle invalid input
                    Map<String, String> responseMap = ResponseGenerator.getResponseMap();
                    resolveInputResponse(input, responseMap);
                }
            } catch (Exception e) {
                // The exception has been handled
            }
        }
    }

    /**
     * Resolves user input by checking against predefined responses.
     * If the input is not recognized, an InvalidCommandException is thrown.
     *
     * @param input The user input string to be checked.
     * @param responseMap A map of predefined responses.
     * @throws InvalidCommandException If the input is not found in the predefined response map.
     */
    public void resolveInputResponse(String input, Map<String, String> responseMap) throws InvalidCommandException {
        if (!responseMap.containsKey(input)) {
            OutputHandler.print("I didn't catch that. Could you try again?");
            throw new InvalidCommandException();
        }

        String response = responseMap.get(input.toLowerCase());
        OutputHandler.print(response);
    }

    private static class HelpHandler implements Helper {
        static final String HELP_MESSAGE = """
            Type a mode name to enter a mode.
            Available modes: %s
            Type 'exit' to leave."""
            .formatted(availableModes);

        public static void help() {
            OutputHandler.print(HELP_MESSAGE);
        }
    }
}
