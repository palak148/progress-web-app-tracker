package com.preptracker.dto;

import com.preptracker.entity.Task;
import com.preptracker.entity.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;

public class TaskResponse {

    private Long id;
    private Long prepPlanId;
    private String title;
    private String subject;
    private LocalDate plannedDate;
    private Integer estimatedMinutes;
    private TaskStatus status;
    private Instant completedAt;
    private Instant createdAt;

    public static TaskResponse from(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setPrepPlanId(task.getPrepPlan() != null ? task.getPrepPlan().getId() : null);
        response.setTitle(task.getTitle());
        response.setSubject(task.getSubject());
        response.setPlannedDate(task.getPlannedDate());
        response.setEstimatedMinutes(task.getEstimatedMinutes());
        response.setStatus(task.getStatus());
        response.setCompletedAt(task.getCompletedAt());
        response.setCreatedAt(task.getCreatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrepPlanId() {
        return prepPlanId;
    }

    public void setPrepPlanId(Long prepPlanId) {
        this.prepPlanId = prepPlanId;
    }

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

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
