package eggo;

import java.util.Scanner;

/**
 * The main class for the chatbot application.
 * This class initializes the chatbot, handles user input, and starts the lobby.
 */
public class Eggo {

    /**
     * The entry point of the chatbot application.
     */
    public static void main(String[] args) {
        // Create a Scanner object to handle user input
        Scanner scanner = new Scanner(System.in);

        // Initialize the lobby where users can choose modes
        Lobby lobby = new Lobby();

        // Display the initial greeting message
        OutputHandler.greet();

        /**
         * Entry Point
         * Starts the chatbot lobby where users can select different modes.
         */
        lobby.start(scanner);

        /**
         * Cleanup
         * Close the scanner to free up resources.
         */
        scanner.close();
    }
}
