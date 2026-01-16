package Ai;

public class ApiKeys {
    public static String getOpenAiApiKey() {
        String key = System.getenv("OPENAI_API_KEY");
        if (key == null || key.isBlank()) {
            throw new RuntimeException("OPENAI_API_KEY environment variable not set");
        }
        return key;
    }
}
