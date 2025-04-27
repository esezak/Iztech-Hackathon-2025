package net.esezak.datagen;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGenThread implements Runnable {
    private static double cumulativeUsage = 0d;
    private static List<UsageData> usageData = new ArrayList<>();
    private boolean isDone = false;

    @Override
    public void run() {
        System.out.println("Thread STARTED");
        Random rd = new Random(System.currentTimeMillis());
        int hour = 1;
        int day = 1;
        int month = 4;
        int year = 2025;

        while (!isDone) {
            double usageHourly = 0.57869d;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            double usageRandom = rd.nextDouble(0.3068035, 0.4886725);
            int dice = rd.nextInt(0, 101);
            if (dice <= 30) {
                usageHourly += usageRandom * 1.3;
            } else if (dice <= 50) {
                usageHourly += usageRandom;
            } else if (dice <= 80) {
                usageHourly -= usageRandom * 1.3;
            } else {
                usageHourly -= usageRandom;
            }
            cumulativeUsage += usageHourly;
            usageData.add(new UsageData(cumulativeUsage, day, hour, month, year));

            hour++;
            if (hour == 25) {
                hour = 1;
                day++;
                if (day == 31) {
                    day = 1;
                    month++;
                    cumulativeUsage = 0;
                    usageData = new ArrayList<>();
                    if (month == 13) {
                        month = 1;
                        year++;
                    }
                }
            }
        }
    }

    public void stopThread() {
        isDone = true;
    }

    public String getUsageData() {
        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(usageData);
            System.out.println(json);
            System.out.println("size::" + usageData.size());
            return json;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<UsageData> getUsageDataList() {
        return usageData;
    }

    public SimpleRegression getRegressionResult() {
        SimpleRegression regression = new SimpleRegression();
        List<UsageData> usageDataList = getUsageDataList();
        if (!usageDataList.isEmpty()) {
            for (UsageData u : usageDataList) {
                if (u.getHour() == 24) {
                    System.out.println("Day:" + u.getDay() + "::Month:" + u.getMonth() + "::" + u.getCumulativeUsage());
                    regression.addData(u.getDay(), u.getCumulativeUsage());
                }
            }
            System.out.println("Regression value at day 30::::" + regression.predict(30));
            return regression;
        }
        return null;
    }
}

