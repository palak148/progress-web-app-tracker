package com.preptracker.service;

import com.preptracker.dto.TaskRequest;
import com.preptracker.dto.TaskResponse;
import com.preptracker.entity.PrepPlan;
import com.preptracker.entity.Task;
import com.preptracker.entity.TaskStatus;
import com.preptracker.entity.User;
import com.preptracker.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final PrepPlanService prepPlanService;

    public TaskService(TaskRepository taskRepository, PrepPlanService prepPlanService) {
        this.taskRepository = taskRepository;
        this.prepPlanService = prepPlanService;
    }

    public List<TaskResponse> getTasksForDate(User user, LocalDate date, Long planId) {
        PrepPlan plan = resolvePlan(user, planId);
        List<Task> tasks = plan != null
                ? taskRepository.findByUserAndPrepPlanAndPlannedDateOrderByCreatedAtAsc(user, plan, date)
                : taskRepository.findByUserAndPlannedDateOrderByCreatedAtAsc(user, date);
        return tasks.stream().map(TaskResponse::from).collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksInRange(User user, LocalDate start, LocalDate end, Long planId) {
        PrepPlan plan = resolvePlan(user, planId);
        List<Task> tasks = taskRepository.findByUserAndPlannedDateBetweenOrderByPlannedDateAsc(user, start, end);
        if (plan != null) {
            tasks = tasks.stream()
                    .filter(t -> t.getPrepPlan() != null && t.getPrepPlan().getId().equals(plan.getId()))
                    .collect(Collectors.toList());
        }
        return tasks.stream().map(TaskResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public TaskResponse createTask(User user, TaskRequest request) {
        Task task = new Task();
        task.setUser(user);
        task.setTitle(request.getTitle());
        task.setSubject(request.getSubject());
        task.setPlannedDate(request.getPlannedDate());
        task.setEstimatedMinutes(request.getEstimatedMinutes());

        if (request.getPrepPlanId() != null) {
            PrepPlan plan = prepPlanService.getPlanForUser(user, request.getPrepPlanId());
            task.setPrepPlan(plan);
        }

        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse completeTask(User user, Long taskId) {
        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(Instant.now());
        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse uncompleteTask(User user, Long taskId) {
        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        task.setStatus(TaskStatus.PENDING);
        task.setCompletedAt(null);
        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(User user, Long taskId) {
        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        taskRepository.delete(task);
    }

    private PrepPlan resolvePlan(User user, Long planId) {
        if (planId == null) {
            return null;
        }
        return prepPlanService.getPlanForUser(user, planId);
    }
}
