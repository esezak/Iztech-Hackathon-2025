package net.esezak.springserver;

import jakarta.annotation.PostConstruct;
import net.esezak.datagen.DataGenThread;
import net.esezak.datagen.UsageData;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class ServerController {
    private DataGenThread dataGenThread = new DataGenThread();
    private Thread thread = new Thread(dataGenThread);
    private double savedThreshold = 0;
    private double regressionPrediction = 0;

    @PostMapping("/threshold")
    public void receiveThreshold(@RequestBody ThresholdRequest thresholdRequest) {
        System.out.println("Threshold-----------------:" + thresholdRequest.getValue());
        this.savedThreshold = thresholdRequest.getValue();
        System.out.println("Received threshold: " + savedThreshold);
    }

    @GetMapping("/threshold")
    public double getThreshold() {
        return savedThreshold;
    }

    @PostConstruct
    public void startThread() {
        thread.start();
    }

    @GetMapping("/trigger")
    public void generateAiResponse(){
        JSONObject data = new JSONObject();
        data.put("threshold", savedThreshold);
        data.put("prediction", regressionPrediction);
        ArrayList<Double> cumulativeData = new ArrayList<>();
        for (UsageData u : dataGenThread.getUsageDataList()) {
            if (u.getHour() == 24) {
                cumulativeData.add(u.getCumulativeUsage());
            }
        }
        data.put("currentConsumption",cumulativeData);
        AIController.askAI(data.toString());
    }

    @GetMapping("/regression")
    public Map<String, Object> getRegression() {
        SimpleRegression regression = dataGenThread.getRegressionResult();
        Map<String, Object> response = new HashMap<>();
        response.put("a", regression.getSlope());
        response.put("b",regression.getIntercept());

        regressionPrediction = regression.predict(30);
        response.put("day-30",regressionPrediction);

        return response;
    }

    @GetMapping("/data")
    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> response = new ArrayList<>();

        for (UsageData data : dataGenThread.getUsageDataList()) {
            if (data.getHour() == 24) {
                Map<String, Object> entry = new HashMap<>();
                entry.put("day", data.getDay());
                entry.put("total", data.getCumulativeUsage());
                response.add(entry);
            }
        }

        return response;
    }

    @PostMapping("/postpromt")
    public String postPromt(String promt) {
        return AIController.askAI(promt);
    }
}

class ThresholdRequest {
    private Double value;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}