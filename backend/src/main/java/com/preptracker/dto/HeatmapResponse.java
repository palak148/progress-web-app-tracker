package com.preptracker.dto;

import java.util.Map;

public class HeatmapResponse {

    private int year;
    private Map<String, Integer> days;

    public HeatmapResponse() {
    }

    public HeatmapResponse(int year, Map<String, Integer> days) {
        this.year = year;
        this.days = days;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Map<String, Integer> getDays() {
        return days;
    }

    public void setDays(Map<String, Integer> days) {
        this.days = days;
    }
}
