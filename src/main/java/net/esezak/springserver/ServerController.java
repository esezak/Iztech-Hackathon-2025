package net.esezak.springserver;

import jakarta.annotation.PostConstruct;
import net.esezak.datagen.DataGenThread;
import net.esezak.datagen.UsageData;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
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

    @PostConstruct
    public void startThread() {
        thread.start();
    }

    @GetMapping("/regression")
    public Map<String, Object> getRegression() {
        SimpleRegression regression = dataGenThread.getRegressionResult();
        Map<String, Object> response = new HashMap<>();
        response.put("a", regression.getSlope());
        response.put("b",regression.getIntercept());
        response.put("day-30",regression.predict(30));

        return response;
    }

    @GetMapping("/data")
    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> response = new ArrayList<>();

        for (UsageData data : dataGenThread.getUsageDataList()) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("day", data.getDay());
            entry.put("total", data.getCumulativeUsage());
            response.add(entry);
        }

        return response;
    }


    @PostMapping("/postpromt")
    public String postPromt(String promt) {
        return AIController.askAI(promt);
    }
}
