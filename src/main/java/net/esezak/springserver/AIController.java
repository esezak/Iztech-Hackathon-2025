package net.esezak.springserver;

import org.apache.http.HttpConnection;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AIController {
    public static String askAI(String prompt) {
        String model = "deepseek-r1:latest";
        String parameter = "{ \"temperature\": 0.0, \"top_p\": 1.0, \"max_tokens\": 50, \"stop\": [\"<think></think>\"] }";
        String fullResponse = "";
        try {
            // Set up an HTTP POST request
            URL url = new URL("http://localhost:11434/api/generate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Create request JSON
            JSONObject requestJson = new JSONObject();
            requestJson.put("model", model);
            requestJson.put("prompt", prompt);
            requestJson.put("stream", false);
            requestJson.put("parameter", parameter);

            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestJson.toString().getBytes());
            }

            // Get response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String responseLine = br.readLine();
                JSONObject responseJson = new JSONObject(responseLine);
                fullResponse = responseJson.getString("response");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        String response = fullResponse.split("</think>")[1];
        System.out.println("Summary:\n"+response);
        //System.out.println("Full response: " + fullResponse);
        return response;
    }

    public static void main(String[] args) throws Exception {
        String prompt = """
                You will receive data about a household electricity usage.
                1. Your first data is the threshold of the total allowed energy consumption of a given month.
                2. Your second data is the predicted projection based on previous the consumption in this month.
                3. Your third data is the current total power consumed for the current month.
                4. Your fourth data is the current day of the month.
                5. The Data: 600,850,300,10
                6. Your task is to determine if the prediction is bellow the max allowed energy consumption.
                7. If the prediction exceeds the threshold you should recommend solutions to reduce the overall consumption.
                8. If the prediction does not exceed the threshold then tell the user an encouraging quote.
                9. MOST IMPORTANT: Your answer must be less than 100 words and simple to understand.
                """;
        long start = System.currentTimeMillis();
        askAI(prompt);
        long end = System.currentTimeMillis();
        System.out.println("Total time: " + (end - start) + "ms");
    }
}
