package net.esezak.springserver;

import org.apache.http.HttpConnection;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;

public class AIController {
    private static String parsejson(String json, String prompt) {
        JSONObject jsonObject = new JSONObject(json);
        String temp =
                prompt = prompt.replace("<threshold>", String.valueOf(jsonObject.getDouble("threshold")));
        prompt = prompt.replace("<prediction>", String.valueOf(jsonObject.getDouble("prediction")));
        prompt = prompt.replace("<aggregate>", jsonObject.getString("aggregate"));
        return prompt;
    }

    public static String askAI(String json) {
//        String model = "gemma3:12b";
        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey("")
                .build();
        String fullResponse = "";
        String prompt = """
                You will receive data about a household electricity usage.
                1. Your first data is the threshold of the total allowed energy consumption of a given month.
                2. Your second data is the predicted projection based on previous the consumption in this month.
                5. Your third data is an aggregate and represents the daily electricity consumption.
                6. The Data: <threshold>,<prediction>
                7. Aggregate: <aggregate>
                8. Your task is to determine if the prediction is bellow the max allowed energy consumption.
                9. If the prediction exceeds the threshold you should recommend solutions to reduce the overall consumption.
                10. If you find any sudden change between two days notify the user by saying that they need to decrease power consumption.
                11. The possible solutions for decreasing power consumption and notifying of user must be itemized.
                """;

        prompt = parsejson(json, prompt);
//        System.out.println("Changed Prompt::" + prompt + "\n");
//        try {
//
//            URL url = new URL("http://localhost:11434/api/generate");
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/json");
//            conn.setDoOutput(true);
//
//            JSONObject requestJson = new JSONObject();
//            requestJson.put("model", model);
//            requestJson.put("prompt", prompt);
//            requestJson.put("stream", false);
//
//            try (OutputStream os = conn.getOutputStream()) {
//                os.write(requestJson.toString().getBytes());
//            }
//
//            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
//                String responseLine = br.readLine();
//                JSONObject responseJson = new JSONObject(responseLine);
//                fullResponse = responseJson.getString("response");
//            }
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        ResponseCreateParams params = ResponseCreateParams.builder()
                .input(prompt)
                .model(ChatModel.GPT_4_1)
                .build();

        Response response = client.responses().create(params);

        String answer = response.output()
                .get(0)
                .message().toString();

        System.out.println("Answer: " + trimResponse(answer));
        return trimResponse(answer);
    }

    public static void main(String[] args) throws Exception {

        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();
        System.out.println("Total time: " + (end - start) + "ms");
    }

    private static String trimResponse(String response) {
        String marker = "text=";
        int startIndex = response.indexOf(marker);
        if (startIndex != -1) {
            String sub = response.substring(startIndex+marker.length());
            System.out.println(sub);
            return sub;
        } else {
            System.out.println("marker not found");
            return null;
        }

    }
}
