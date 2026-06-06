package com.preptracker.dto;

import com.preptracker.entity.Note;

import java.time.Instant;

public class NoteResponse {

    private Long id;
    private Long projectId;
    private String title;
    private String content;
    private String subject;
    private Instant createdAt;
    private Instant updatedAt;

    public static NoteResponse from(Note note) {
        NoteResponse response = new NoteResponse();
        response.setId(note.getId());
        response.setProjectId(note.getPrepPlan().getId());
        response.setTitle(note.getTitle());
        response.setContent(note.getContent());
        response.setSubject(note.getSubject());
        response.setCreatedAt(note.getCreatedAt());
        response.setUpdatedAt(note.getUpdatedAt());
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
