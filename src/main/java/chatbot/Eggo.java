package chatbot;

import java.util.Scanner;

public class Eggo {
//        String logo = "  _____                        \n"
//                    + " | ____|  ____   ____    ____   \n"
//                    + " |  _|   / _  | / _  |  / __ \\  \n"
//                    + " | |___ | (_| || (_| | | ( _) |   \n"
//                    + " |_____| \\___,| \\___,|  \\ __ / \n"
//                    + "         |____/ |____/          \n";
    private final static String logo = " _____                         \n" +
            "| ____|   __ _    __ _    ___  \n" +
            "|  _|    / _` |  / _` |  / _ \\ \n" +
            "| |___  | (_| | | (_| | | (_) |\n" +
            "|_____|  \\__, |  \\__, |  \\___/ \n" +
            "         |___/   |___/         ";

    public static void main(String[] args) {
        Chatbot chatbot = new Chatbot();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Hello from\n" + logo);

        String greeting = "____________________________________________________________\n" +
                " Hello! I'm Eggo!\n" +
                " What can I do for you?\n" +
                "____________________________________________________________\n";
        System.out.println(greeting);

        /**
         * Entry Point
         */
        while (true) {
            System.out.print("You: ");
            String userInput = scanner.nextLine();
            if ("bye".equals(userInput)) {
                System.out.println("Goodbye!");
                break;
            }
            System.out.println(chatbot.getResponse(userInput));
        }

        /**
         * Cleanup
         */
        scanner.close();
    }
}
