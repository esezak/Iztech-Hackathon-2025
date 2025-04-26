package net.esezak.datagen;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Random;

public class DataGenThread implements Runnable {
    private static ArrayList<UsageData> usageData = new ArrayList<>();
    private boolean isDone = false;
    @Override
    public void run() {
        Random rd = new Random(System.currentTimeMillis());
        int hour = 0;
        int day = 27;
        int month = 4;
        int year = 2025;
        while (!isDone) {
            try {
                Thread.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            usageData.add(new UsageData(rd.nextDouble(0, 0.121), day, hour, month, year, rd.nextInt(0, 1)));
            hour++;
            if (hour == 24) {
                hour = 0;
                day++;
                if (day == 31) {
                    day = 1;
                    month++;
                    if (month == 13) {
                        month = 1;
                        year++;
                    }
                }
            }
        }
    }

    public void stopThread(){
        isDone = true;
    }

    public static ArrayList<UsageData> getUsageData() {
        //ObjectMapper mapper = new ObjectMapper();
        //String json = mapper.writeValueAsString();
        return usageData;
    }
}
class UsageData{
    private double usage = 0.57869d;
    private int hour;
    private int day;
    private int month;
    private int year;

    public UsageData(double usage, int day, int hour, int month, int year, int dice) {
        if(dice==0)this.usage = this.usage + usage;
        else this.usage = this.usage - usage;
        this.hour = hour;
        this.day = day;
        this.month = month;
        this.year = year;
    }
}
