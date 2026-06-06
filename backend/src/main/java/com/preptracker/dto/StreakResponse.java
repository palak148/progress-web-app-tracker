package com.preptracker.dto;

import java.util.Map;

public class StreakResponse {

    private int currentStreak;
    private int longestStreak;
    private int activeDaysThisMonth;
    private int completedTasksThisMonth;
    private int plannedTasksThisMonth;

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public int getActiveDaysThisMonth() {
        return activeDaysThisMonth;
    }

    public void setActiveDaysThisMonth(int activeDaysThisMonth) {
        this.activeDaysThisMonth = activeDaysThisMonth;
    }

    public int getCompletedTasksThisMonth() {
        return completedTasksThisMonth;
    }

    public void setCompletedTasksThisMonth(int completedTasksThisMonth) {
        this.completedTasksThisMonth = completedTasksThisMonth;
    }

    public int getPlannedTasksThisMonth() {
        return plannedTasksThisMonth;
    }

    public void setPlannedTasksThisMonth(int plannedTasksThisMonth) {
        this.plannedTasksThisMonth = plannedTasksThisMonth;
    }
}
