package com.preptracker.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class NoteRequest {

    @NotNull
    private Long projectId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String subject;

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
}
