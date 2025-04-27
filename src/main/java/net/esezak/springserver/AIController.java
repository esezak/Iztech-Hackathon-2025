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
//        String model = "deepseek-r1:latest";
        String model = "gemma3:12b";
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
        //String response = fullResponse.split("</think>")[1];
        //System.out.println("Summary:\n"+response);
//        System.out.println("Full response: " + fullResponse);
        return fullResponse;
    }

    public static void main(String[] args) throws Exception {
        String prompt = """
                You will receive data about a household electricity usage.
                1. Your first data is the threshold of the total allowed energy consumption of a given month.
                2. Your second data is the predicted projection based on previous the consumption in this month.
                3. Your third data is the current total power consumed for the current month.
                4. Your fourth data is the current day of the month.
                5. Your fifth data is an aggregate and represents the amount of change observed every day.
                6. The Data: 400, 421, 391, 15
                7. Aggregate: 13, 13, 14, 14, 14, 14, 24, 14, 14, 14, 24, 13, 13, 14, 14
                8. Your task is to determine if the prediction is bellow the max allowed energy consumption.
                9. If the prediction exceeds the threshold you should recommend solutions to reduce the overall consumption.
                10. If the prediction does not exceed the threshold then tell the user an encouraging quote.
                11. If you find any sudden change between two days notify the user by saying that they need to decrease power consumption.
                12. The possible solutions for decreasing power consumption and notifying of user must be itemized.
                13. Only write the solutions for reducing the power consumptions
                """;
        long start = System.currentTimeMillis();
//        askAI(prompt);
        trimResponse(askAI(prompt));
        long end = System.currentTimeMillis();
        System.out.println("Total time: " + (end - start) + "ms");
    }
    private static String trimResponse(String response) {
        String marker = "1.";
        int startIndex = response.indexOf(marker);
        if(startIndex != -1) {
            String sub = response.substring(startIndex);
            System.out.println(sub);
            return sub;
        }else{
            System.out.println("marker not found");
            return null;
        }

    }
}
