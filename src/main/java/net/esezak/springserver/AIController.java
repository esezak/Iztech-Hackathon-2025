package net.esezak.springserver;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AIController {
    private static final String model = "deepseek-r1:latest";
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static HttpClient client = HttpClient.newHttpClient();
    private static HttpRequest request;
    public static void askAI(String prompt) throws IOException, InterruptedException {
        JSONObject json = JSONBUILDER(prompt);
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:11434/api/generate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(request.toString());
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());

    }
    private static JSONObject JSONBUILDER(String promt){
        JSONObject obj = new JSONObject();
        obj.put("model",model);
        obj.put("promt",promt);
        obj.put("stream",false);
        System.out.println(obj);
        return obj;
    }
    public static void aistreatest(){

    }
    public static void main(String[] args) throws IOException, InterruptedException {
        askAI("Hello");
    }
}
