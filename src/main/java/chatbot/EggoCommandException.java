package chatbot;

import chatbot.OutputHandler;

public class EggoCommandException extends Exception {
    public EggoCommandException() {
        OutputHandler.print("I didn't catch that. Could you try again?");
//        OutputHandler.printError("EggoCommandException: Type 'help' for available commands.");
    }
}
