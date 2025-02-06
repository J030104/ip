package chatbot;

import java.util.Scanner;

import chatbot.OutputHandler;

/**
 * This class
 * - Repeats user input verbatim.
 */
public class EchoMode implements Mode {

    /**
     * Message displayed when the user enters Echo Mode.
     */
    private static final String PROMPT = """
            You have entered Echo Mode! Type something, and I'll repeat it.
            Press 'Ctrl + Q' (Currently Unavailable, type 'exit' or 'bye' instead) to return to the lobby.""";

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
