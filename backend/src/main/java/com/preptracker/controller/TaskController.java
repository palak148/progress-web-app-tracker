package com.preptracker.controller;

import com.preptracker.dto.TaskRequest;
import com.preptracker.dto.TaskResponse;
import com.preptracker.entity.User;
import com.preptracker.security.SecurityUtils;
import com.preptracker.service.TaskService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final SecurityUtils securityUtils;

    public TaskController(TaskService taskService, SecurityUtils securityUtils) {
        this.taskService = taskService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) Long projectId) {

        User user = securityUtils.getCurrentUser();

        if (date != null) {
            return ResponseEntity.ok(taskService.getTasksForDate(user, date, projectId));
        }
        if (start != null && end != null) {
            return ResponseEntity.ok(taskService.getTasksInRange(user, start, end, projectId));
        }
        return ResponseEntity.ok(taskService.getTasksForDate(user, LocalDate.now(), projectId));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.createTask(securityUtils.getCurrentUser(), request));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.completeTask(securityUtils.getCurrentUser(), id));
    }

    @PatchMapping("/{id}/uncomplete")
    public ResponseEntity<TaskResponse> uncompleteTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.uncompleteTask(securityUtils.getCurrentUser(), id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(securityUtils.getCurrentUser(), id);
        return ResponseEntity.noContent().build();
    }
}
