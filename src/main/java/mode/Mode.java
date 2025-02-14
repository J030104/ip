package mode;

import java.util.Scanner;

/**
 * Represents a mode in the chatbot system.
 * Each mode provides a unique functionality and follows a standard structure.
 *
 * Implementing classes should define the behavior of `start(Scanner scanner)`,
 * which is responsible for handling user interactions within that mode.
 */
public interface Mode {

    /**
     * Starts the mode and manages user interactions.
     * Implementing classes should handle user input and define mode-specific behaviors.
     *
     * @param scanner Scanner object to read user input.
     */
    void start(Scanner scanner);

    // Future extension:
    // void stop(Scanner scanner); // Optional: Define behavior for stopping a mode.
}
