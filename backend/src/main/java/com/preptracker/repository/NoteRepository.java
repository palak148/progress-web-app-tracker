package com.preptracker.repository;

import com.preptracker.entity.Note;
import com.preptracker.entity.PrepPlan;
import com.preptracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUserAndPrepPlanOrderByUpdatedAtDesc(User user, PrepPlan prepPlan);

    Optional<Note> findByIdAndUser(Long id, User user);

    void deleteByUserAndPrepPlan(User user, PrepPlan prepPlan);
}
