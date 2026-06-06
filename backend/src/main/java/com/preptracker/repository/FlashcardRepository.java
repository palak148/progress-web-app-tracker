package com.preptracker.repository;

import com.preptracker.entity.Flashcard;
import com.preptracker.entity.PrepPlan;
import com.preptracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {

    List<Flashcard> findByUserAndPrepPlanOrderByCreatedAtDesc(User user, PrepPlan prepPlan);

    Optional<Flashcard> findByIdAndUser(Long id, User user);

    void deleteByUserAndPrepPlan(User user, PrepPlan prepPlan);
}
