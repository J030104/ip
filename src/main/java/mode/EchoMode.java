package mode;

import java.util.Scanner;

import eggo.OutputHandler;

/**
 * EchoMode is a mode where the chatbot repeats user input verbatim.
 * Users can exit this mode by typing "exit".
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
