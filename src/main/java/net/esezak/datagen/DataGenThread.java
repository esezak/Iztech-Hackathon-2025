package net.esezak.datagen;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Random;

public class DataGenThread implements Runnable {
    private static double cumulativeUsage = 0d;
    private static ArrayList<UsageData> usageData = new ArrayList<>();
    private boolean isDone = false;

    public static void main(String[] args) {
        DataGenThread t = new DataGenThread();
        Thread thread = new Thread(t);
        try{
            thread.start();
            Thread.sleep(5000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        Random rd = new Random(System.currentTimeMillis());
        int hour = 1;
        int day = 1;
        int month = 4;
        int year = 2025;

        double usageHourly = 0.57869d;

        while (!isDone) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            double usageRandom = rd.nextDouble(0.04629, 0.115738);
            int dice = rd.nextInt(0, 2);
            if (dice == 0) {
                usageHourly += usageRandom;
            } else {
                usageHourly -= usageRandom;
            }
            cumulativeUsage += usageHourly;
            usageData.add(new UsageData(cumulativeUsage, day, hour, month, year));

            hour++;
            if (hour == 25) {
                hour = 0;
                day++;
                if (day == 31) {
                    day = 1;
                    month++;
                    cumulativeUsage = 0;
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
            System.out.println("size:::"+ usageData.size());
            return json;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

//    public SimpleRegression getRegressionResult(){
//        String jsonArrayString = getUsageData();
//        SimpleRegression regression = new SimpleRegression();
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            List<UsageData> usageDataList = mapper.readValue(jsonArrayString, new TypeReference<>() {});
//            if (!usageDataList.isEmpty()) {
//                int month = usageDataList.getFirst().getMonth();
//                for (UsageData u : usageDataList) {
//                    if (u.getHour() == 24 && u.getMonth() == month && u.getDay() < 20) {
//                        System.out.println(u.getCumulativeUsage());
//                        regression.addData(u.getDay(), u.getCumulativeUsage());
//                        System.out.println(u.getDay());
//                    }
//                }
//            }
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println("aaaaaaa::::"+regression.predict(30));
//        return null;
//    }

}

class UsageData {
    private double cumulativeUsage;
    private int hour;
    private int day;
    private int month;
    private int year;

    public UsageData(){}

    public UsageData(double cumulativeUsage, int day, int hour, int month, int year) {
        this.cumulativeUsage = cumulativeUsage;
        this.hour = hour;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public double getCumulativeUsage() {
        return cumulativeUsage;
    }

    public void setCumulativeUsage(double cumulativeUsage) {
        this.cumulativeUsage = cumulativeUsage;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
