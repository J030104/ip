package chatbot;

import java.util.Scanner;

public class Eggo {

    public static void main(String[] args) {
        Chatbot chatbot = new Chatbot();
        Scanner scanner = new Scanner(System.in);
        Lobby lobby = new Lobby();

        OutputHandler.greet();

        /**
         * Entry Point
         */
        lobby.start(scanner);

        /**
         * Cleanup
         */
        scanner.close();
    }
}
