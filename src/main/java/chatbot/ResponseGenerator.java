package chatbot;

import java.util.HashMap;
import java.util.Map;

public class ResponseGenerator {
    private Map<String, String> responseMap;
//    private

    public ResponseGenerator() {
        responseMap = new HashMap<>();
        responseMap.put("empty", "I didn't catch that. Could you try again?");
        responseMap.put("hello", "Hi! How can I assist you today?");
//        responseMap.put("bye", "Goodbye! Have a great day!");
    }

    public String generateResponse(String input) {
        return responseMap.getOrDefault(input, "I'm not sure how to respond to that.");
    }
}
