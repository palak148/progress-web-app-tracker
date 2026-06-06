package com.preptracker.controller;

import com.preptracker.dto.FlashcardRequest;
import com.preptracker.dto.FlashcardResponse;
import com.preptracker.security.SecurityUtils;
import com.preptracker.service.FlashcardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/flashcards")
public class FlashcardController {

    private final FlashcardService flashcardService;
    private final SecurityUtils securityUtils;

    public FlashcardController(FlashcardService flashcardService, SecurityUtils securityUtils) {
        this.flashcardService = flashcardService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<List<FlashcardResponse>> listFlashcards(@RequestParam Long projectId) {
        return ResponseEntity.ok(flashcardService.listFlashcards(securityUtils.getCurrentUser(), projectId));
    }

    @PostMapping
    public ResponseEntity<FlashcardResponse> createFlashcard(@Valid @RequestBody FlashcardRequest request) {
        return ResponseEntity.ok(flashcardService.createFlashcard(securityUtils.getCurrentUser(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlashcardResponse> updateFlashcard(@PathVariable Long id,
                                                             @Valid @RequestBody FlashcardRequest request) {
        return ResponseEntity.ok(flashcardService.updateFlashcard(securityUtils.getCurrentUser(), id, request));
    }

    @PatchMapping("/{id}/mastered")
    public ResponseEntity<FlashcardResponse> toggleMastered(@PathVariable Long id) {
        return ResponseEntity.ok(flashcardService.toggleMastered(securityUtils.getCurrentUser(), id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlashcard(@PathVariable Long id) {
        flashcardService.deleteFlashcard(securityUtils.getCurrentUser(), id);
        return ResponseEntity.noContent().build();
    }
}
