package clientflow.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Component
public class OpenAiClient {

    @Value("${openai.api-key}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String chat(String systemPrompt, String userMessage) {
        try {
            String body = objectMapper.writeValueAsString(Map.of(
                    "model", "openai/gpt-oss-20b",
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", userMessage)
                    ),
                    "temperature", 0.7
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            JsonNode root = objectMapper.readTree(response.body());

            // Check for API errors
            if (root.has("error")) {
                String errorMessage = root.path("error").path("message").asText();
                throw new RuntimeException("OpenAI error: " + errorMessage);
            }

            JsonNode choices = root.path("choices");
            if (choices.isEmpty()) {
                throw new RuntimeException("No response from OpenAI");
            }

            return choices.get(0).path("message").path("content").asText();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("OpenAI API call failed: " + e.getMessage());
        }
    }
}