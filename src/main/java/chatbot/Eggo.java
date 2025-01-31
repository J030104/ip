package chatbot;

import java.util.Scanner;

public class Eggo {

    public static void main(String[] args) {
        Chatbot chatbot = new Chatbot();
        Scanner scanner = new Scanner(System.in);
//        OutputHandler outputHandler = new OutputHandler();
        Lobby lobby = new Lobby();

        OutputHandler.greet();

        /**
         * Entry Point
         */
        lobby.start(scanner);
//        while (true) {
//            System.out.print("You: ");
//            String userInput = scanner.nextLine();
//            if ("bye".equals(userInput)) {
//                System.out.println("Goodbye!");
//                break;
//            }
//            System.out.println(chatbot.getResponse(userInput));
//        }

        /**
         * Cleanup
         */
        scanner.close();
    }
}
