package com.preptracker.service;

import com.preptracker.dto.FlashcardRequest;
import com.preptracker.dto.FlashcardResponse;
import com.preptracker.entity.Flashcard;
import com.preptracker.entity.PrepPlan;
import com.preptracker.entity.User;
import com.preptracker.repository.FlashcardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlashcardService {

    private final FlashcardRepository flashcardRepository;
    private final PrepPlanService prepPlanService;

    public FlashcardService(FlashcardRepository flashcardRepository, PrepPlanService prepPlanService) {
        this.flashcardRepository = flashcardRepository;
        this.prepPlanService = prepPlanService;
    }

    public List<FlashcardResponse> listFlashcards(User user, Long projectId) {
        PrepPlan plan = prepPlanService.getPlanForUser(user, projectId);
        return flashcardRepository.findByUserAndPrepPlanOrderByCreatedAtDesc(user, plan).stream()
                .map(FlashcardResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public FlashcardResponse createFlashcard(User user, FlashcardRequest request) {
        PrepPlan plan = prepPlanService.getPlanForUser(user, request.getProjectId());

        Flashcard card = new Flashcard();
        card.setUser(user);
        card.setPrepPlan(plan);
        card.setFront(request.getFront());
        card.setBack(request.getBack());
        card.setSubject(request.getSubject());

        return FlashcardResponse.from(flashcardRepository.save(card));
    }

    @Transactional
    public FlashcardResponse updateFlashcard(User user, Long cardId, FlashcardRequest request) {
        Flashcard card = flashcardRepository.findByIdAndUser(cardId, user)
                .orElseThrow(() -> new IllegalArgumentException("Flashcard not found"));

        if (request.getFront() != null && !request.getFront().isBlank()) {
            card.setFront(request.getFront());
        }
        if (request.getBack() != null && !request.getBack().isBlank()) {
            card.setBack(request.getBack());
        }
        card.setSubject(request.getSubject());

        return FlashcardResponse.from(flashcardRepository.save(card));
    }

    @Transactional
    public FlashcardResponse toggleMastered(User user, Long cardId) {
        Flashcard card = flashcardRepository.findByIdAndUser(cardId, user)
                .orElseThrow(() -> new IllegalArgumentException("Flashcard not found"));
        card.setMastered(!card.isMastered());
        return FlashcardResponse.from(flashcardRepository.save(card));
    }

    @Transactional
    public void deleteFlashcard(User user, Long cardId) {
        Flashcard card = flashcardRepository.findByIdAndUser(cardId, user)
                .orElseThrow(() -> new IllegalArgumentException("Flashcard not found"));
        flashcardRepository.delete(card);
    }
}
