package net.esezak.datagen;

public class UsageData {
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
