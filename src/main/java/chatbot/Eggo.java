package chatbot;

import java.util.Scanner;

//import Archive.Chatbot;

/**
 * The main class for the chatbot application.
 * This class initializes the chatbot, handles user input, and starts the lobby.
 */
public class Eggo {

    /**
     * The entry point of the chatbot application.
     *
     * @param args Command-line arguments (currently not used in this application).
     */
    public static void main(String[] args) {
        // Initialize chatbot instance
//        Chatbot chatbot = new Chatbot();

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
