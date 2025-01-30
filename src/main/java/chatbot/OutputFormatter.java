package chatbot;

public class OutputFormatter {
    public String formatResponse(String response) {
        return "____________________________________________________________\n"
                + "Chatbot: " + response + "\n"
                + "____________________________________________________________";
    }
}
