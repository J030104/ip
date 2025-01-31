package chatbot;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import chatbot.InputHandler;
import chatbot.OutputHandler;

/**
 * Enables different modes (functionalities)
 */

public class Lobby {
    private Map<String, Mode> modes;

    public Lobby() {
        modes = new HashMap<>();
        modes.put("echo", new EchoMode()); // Register Echo Mode
        modes.put("todo", new ToDoMode());
//        modes.put("", new Mode());
    }

    public void start(Scanner scanner) {
        // *** Standardize the output method.

        // Generate a list of available modes dynamically
        String availableModes = String.join(", ", modes.keySet());

        // Standardize the output method.
        final String welcome = "Welcome to the Chatbot Lobby!\n" +
                "Type a mode name to enter a mode.\n" +
                "Available modes: " + availableModes + "\n" +
                "Type 'help' for assistance. (Currently Unavailable)";

        OutputHandler.print(welcome);

        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();

            // Everything here is lower-cased
            input = InputHandler.processInput(input);

            if (input.equals("exit")) {
                OutputHandler.print("Exiting chatbot Eggo. Goodbye!");
                break;
            } else if (input.equals("help")) {
//                OutputHandler.printInfo(
//                HelpSystem.showHelp("lobby");
            } else if (modes.containsKey(input)) {
                modes.get(input).start(scanner);
                OutputHandler.printInfo("Returned to the Lobby.");
            } else {
                OutputHandler.printInfo("Unknown command. Type 'help' for available commands.");
            }
        }
    }
}
