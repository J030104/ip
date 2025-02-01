package chatbot;

import java.util.Scanner;

import chatbot.OutputHandler;

/**
 * The EchoMode class allows users to enter a mode where the chatbot repeats
 * whatever the user types. This mode continues running until the user exits.
 *
 * Features:
 * - Repeats user input verbatim.
 * - Allows users to exit by typing "exit".
 *
 * Usage:
 * Call `start(scanner)` to begin echoing user input.
 */
public class EchoMode implements Mode {

    /**
     * Message displayed when the user enters Echo Mode.
     */
    private static final String PROMPT = """
            You have entered Echo Mode! Type something, and I'll repeat it.
            Press 'Ctrl + Q' (Currently Unavailable, type 'exit' instead) to return to the lobby.""";

    /**
     * Starts Echo Mode and repeats user input until the user exits.
     *
     * @param scanner Scanner object to read user input.
     */
    @Override
    public void start(Scanner scanner) {
        OutputHandler.print(PROMPT);

        while (true) {
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                OutputHandler.printInfo("Exiting Echo Mode.");
                break;
            }
            OutputHandler.print(input);
        }
    }

    // Future extension: Implement stop() if needed in the future.
    // @Override
    // public void stop(Scanner scanner) {}
}
