package chatbot;

/**
 * Handles all chatbot outputs to the user.
 */
public class OutputHandler {

//        String logo = "  _____                        \n"
//                    + " | ____|  ____   ____    ____   \n"
//                    + " |  _|   / _  | / _  |  / __ \\  \n"
//                    + " | |___ | (_| || (_| | | ( _) |   \n"
//                    + " |_____| \\___,| \\___,|  \\ __ / \n"
//                    + "         |____/ |____/          \n";

    public final static String logo = " _____                         \n" +
            "| ____|   __ _    __ _    ___  \n" +
            "|  _|    / _` |  / _` |  / _ \\ \n" +
            "| |___  | (_| | | (_| | | (_) |\n" +
            "|_____|  \\__, |  \\__, |  \\___/ \n" +
            "         |___/   |___/         ";

    public final static String LINE_SEPARATOR = "_________________________________________________________________________\n";
    public final static String INDENT = "    ";

    public static void greet() {
        final String intro = "Hello from\n" + logo;
        print(intro);

        final String greeting = "Howdy! I'm Eggo!\n" +
                "What can I do for you?";
        print(greeting);
    }

    public static void printInfo(String message) {
        print(INDENT + "[INFO] " + message);
    }

    public static void printWarning(String message) {
        print(INDENT + "[WARNING] " + message);
    }

    public static void printError(String message) {
        print(INDENT + "[ERROR] " + message);
    }

//    public static void printPrompt(String prompt) {
//        print(prompt + " ");
//    }

    public static void print(String response) {
        String indentedResponse = INDENT + response.replace("\n", "\n" + INDENT);
        System.out.println(LINE_SEPARATOR
                + indentedResponse + "\n"
                + LINE_SEPARATOR);
    }
}
