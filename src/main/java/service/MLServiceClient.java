package service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MLServiceClient {

    private final HttpClient httpClient;
    private final String baseUrl;

    public MLServiceClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
        this.baseUrl = "http://localhost:8000/api/v1/recommendations/";
    }

    /**
     * Fetches recommended product IDs for the given passenger.
     * Uses a simple regex to parse the JSON array to avoid adding heavy JSON dependencies.
     */
    public List<Integer> getRecommendations(String passengerId) {
        List<Integer> recommendedIds = new ArrayList<>();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + passengerId))
                    .timeout(Duration.ofSeconds(2))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String body = response.body();
                // Simple parsing for {"passenger_id":"12345","recommended_product_ids":[2,3]}
                Pattern pattern = Pattern.compile("\"recommended_product_ids\":\\s*\\[(.*?)\\]");
                Matcher matcher = pattern.matcher(body);
                if (matcher.find()) {
                    String arrayContent = matcher.group(1);
                    if (!arrayContent.trim().isEmpty()) {
                        String[] parts = arrayContent.split(",");
                        for (String p : parts) {
                            recommendedIds.add(Integer.parseInt(p.trim()));
                        }
                    }
                }
            } else {
                System.err.println("ML Service returned status code: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Could not reach ML Service. Reason: " + e.getMessage());
        }
        return recommendedIds;
    }
}
