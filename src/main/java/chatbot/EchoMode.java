package chatbot;

import java.util.Scanner;
import chatbot.OutputHandler;

public class EchoMode implements Mode {

    private static final String PROMPT = "You have entered Echo Mode! Type something, and I'll repeat it.\n" +
            "Press 'Ctrl + Q' (Currently Unavailable, type 'exit') to return to the lobby.";

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

//    @Override
//    public void stop(Scanner scanner) {
//
//    }
}
