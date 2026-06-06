package com.preptracker.dto;

import com.preptracker.entity.PrepPlan;

import java.time.Instant;
import java.time.LocalDate;

public class PrepPlanResponse {

    private Long id;
    private String name;
    private String examType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Instant createdAt;

    public static PrepPlanResponse from(PrepPlan plan) {
        PrepPlanResponse response = new PrepPlanResponse();
        response.setId(plan.getId());
        response.setName(plan.getName());
        response.setExamType(plan.getExamType());
        response.setStartDate(plan.getStartDate());
        response.setEndDate(plan.getEndDate());
        response.setCreatedAt(plan.getCreatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
