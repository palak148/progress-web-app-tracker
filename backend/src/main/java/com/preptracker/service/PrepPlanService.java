package com.preptracker.service;

import com.preptracker.dto.PrepPlanRequest;
import com.preptracker.dto.PrepPlanResponse;
import com.preptracker.entity.PrepPlan;
import com.preptracker.entity.User;
import com.preptracker.repository.FlashcardRepository;
import com.preptracker.repository.NoteRepository;
import com.preptracker.repository.PrepPlanRepository;
import com.preptracker.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrepPlanService {

    private final PrepPlanRepository prepPlanRepository;
    private final TaskRepository taskRepository;
    private final NoteRepository noteRepository;
    private final FlashcardRepository flashcardRepository;

    public PrepPlanService(PrepPlanRepository prepPlanRepository,
                           TaskRepository taskRepository,
                           NoteRepository noteRepository,
                           FlashcardRepository flashcardRepository) {
        this.prepPlanRepository = prepPlanRepository;
        this.taskRepository = taskRepository;
        this.noteRepository = noteRepository;
        this.flashcardRepository = flashcardRepository;
    }

    public List<PrepPlanResponse> listPlans(User user) {
        return prepPlanRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(PrepPlanResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public PrepPlanResponse createPlan(User user, PrepPlanRequest request) {
        PrepPlan plan = new PrepPlan();
        plan.setUser(user);
        plan.setName(request.getName());
        plan.setExamType(request.getExamType());
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());
        return PrepPlanResponse.from(prepPlanRepository.save(plan));
    }

    public PrepPlan getPlanForUser(User user, Long planId) {
        return prepPlanRepository.findByIdAndUser(planId, user)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    @Transactional
    public void deletePlan(User user, Long planId) {
        PrepPlan plan = getPlanForUser(user, planId);
        taskRepository.deleteByUserAndPrepPlan(user, plan);
        noteRepository.deleteByUserAndPrepPlan(user, plan);
        flashcardRepository.deleteByUserAndPrepPlan(user, plan);
        prepPlanRepository.delete(plan);
    }
}
