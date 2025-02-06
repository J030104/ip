package chatbot;

public class InputHandler {
    public static String processInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "empty";
        }
        return input.trim().toLowerCase();  // Normalize input
    }
}
