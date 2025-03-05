package exception;

import eggo.OutputHandler;

public class EggoCommandException extends Exception {
    public EggoCommandException() {
        OutputHandler.print("I didn't catch that. Could you try again?");
    }
}
