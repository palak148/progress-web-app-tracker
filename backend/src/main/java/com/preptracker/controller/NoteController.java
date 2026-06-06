package com.preptracker.controller;

import com.preptracker.dto.NoteRequest;
import com.preptracker.dto.NoteResponse;
import com.preptracker.security.SecurityUtils;
import com.preptracker.service.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;
    private final SecurityUtils securityUtils;

    public NoteController(NoteService noteService, SecurityUtils securityUtils) {
        this.noteService = noteService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> listNotes(@RequestParam Long projectId) {
        return ResponseEntity.ok(noteService.listNotes(securityUtils.getCurrentUser(), projectId));
    }

    @PostMapping
    public ResponseEntity<NoteResponse> createNote(@Valid @RequestBody NoteRequest request) {
        return ResponseEntity.ok(noteService.createNote(securityUtils.getCurrentUser(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> updateNote(@PathVariable Long id,
                                                   @Valid @RequestBody NoteRequest request) {
        return ResponseEntity.ok(noteService.updateNote(securityUtils.getCurrentUser(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        noteService.deleteNote(securityUtils.getCurrentUser(), id);
        return ResponseEntity.noContent().build();
    }
}
