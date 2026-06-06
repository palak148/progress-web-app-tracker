package com.preptracker.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class FlashcardRequest {

    @NotNull
    private Long projectId;

    @NotBlank
    private String front;

    @NotBlank
    private String back;

    private String subject;

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
}
