package com.preptracker.dto;

import com.preptracker.entity.Flashcard;

import java.time.Instant;

public class FlashcardResponse {

    private Long id;
    private Long projectId;
    private String front;
    private String back;
    private String subject;
    private boolean mastered;
    private Instant createdAt;

    public static FlashcardResponse from(Flashcard card) {
        FlashcardResponse response = new FlashcardResponse();
        response.setId(card.getId());
        response.setProjectId(card.getPrepPlan().getId());
        response.setFront(card.getFront());
        response.setBack(card.getBack());
        response.setSubject(card.getSubject());
        response.setMastered(card.isMastered());
        response.setCreatedAt(card.getCreatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getFront() {
        return front;
    }

    public void setFront(String front) {
        this.front = front;
    }

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isMastered() {
        return mastered;
    }

    public void setMastered(boolean mastered) {
        this.mastered = mastered;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
