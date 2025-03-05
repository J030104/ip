package mode;

import java.util.Scanner;

import eggo.OutputHandler;

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
     */
    @Override
    public void start(Scanner scanner) {
        OutputHandler.printInfo(PROMPT);

        while (true) {
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                OutputHandler.printInfo("Exiting Echo Mode.");
                break;
            }
            OutputHandler.print(input);
        }
    }
}
