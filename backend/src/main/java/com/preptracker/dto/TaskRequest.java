package com.preptracker.dto;

import com.preptracker.entity.TaskStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class TaskRequest {

    @NotBlank
    private String title;

    private String subject;

    @NotNull
    private LocalDate plannedDate;

    private Integer estimatedMinutes;

    private Long prepPlanId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDate getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(LocalDate plannedDate) {
        this.plannedDate = plannedDate;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public Long getPrepPlanId() {
        return prepPlanId;
    }

    public void setPrepPlanId(Long prepPlanId) {
        this.prepPlanId = prepPlanId;
    }
}
