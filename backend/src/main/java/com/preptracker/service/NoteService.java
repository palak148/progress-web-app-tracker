package com.preptracker.service;

import com.preptracker.dto.NoteRequest;
import com.preptracker.dto.NoteResponse;
import com.preptracker.entity.Note;
import com.preptracker.entity.PrepPlan;
import com.preptracker.entity.User;
import com.preptracker.repository.NoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final PrepPlanService prepPlanService;

    public NoteService(NoteRepository noteRepository, PrepPlanService prepPlanService) {
        this.noteRepository = noteRepository;
        this.prepPlanService = prepPlanService;
    }

    public List<NoteResponse> listNotes(User user, Long projectId) {
        PrepPlan plan = prepPlanService.getPlanForUser(user, projectId);
        return noteRepository.findByUserAndPrepPlanOrderByUpdatedAtDesc(user, plan).stream()
                .map(NoteResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public NoteResponse createNote(User user, NoteRequest request) {
        PrepPlan plan = prepPlanService.getPlanForUser(user, request.getProjectId());

        Note note = new Note();
        note.setUser(user);
        note.setPrepPlan(plan);
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setSubject(request.getSubject());

        return NoteResponse.from(noteRepository.save(note));
    }

    @Transactional
    public NoteResponse updateNote(User user, Long noteId, NoteRequest request) {
        Note note = noteRepository.findByIdAndUser(noteId, user)
                .orElseThrow(() -> new IllegalArgumentException("Note not found"));

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            note.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            note.setContent(request.getContent());
        }
        note.setSubject(request.getSubject());
        note.setUpdatedAt(Instant.now());

        return NoteResponse.from(noteRepository.save(note));
    }

    @Transactional
    public void deleteNote(User user, Long noteId) {
        Note note = noteRepository.findByIdAndUser(noteId, user)
                .orElseThrow(() -> new IllegalArgumentException("Note not found"));
        noteRepository.delete(note);
    }
}
