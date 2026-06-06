package com.preptracker.repository;

import com.preptracker.entity.PrepPlan;
import com.preptracker.entity.Task;
import com.preptracker.entity.TaskStatus;
import com.preptracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByIdAndUser(Long id, User user);

    List<Task> findByUserAndPlannedDateOrderByCreatedAtAsc(User user, LocalDate plannedDate);

    List<Task> findByUserAndPrepPlanAndPlannedDateOrderByCreatedAtAsc(User user, PrepPlan prepPlan, LocalDate plannedDate);

    List<Task> findByUserAndPlannedDateBetweenOrderByPlannedDateAsc(User user, LocalDate start, LocalDate end);

    List<Task> findByUserAndStatusAndCompletedAtIsNotNullOrderByCompletedAtAsc(User user, TaskStatus status);

    List<Task> findByUserAndPrepPlanAndStatusAndCompletedAtIsNotNullOrderByCompletedAtAsc(
            User user, PrepPlan prepPlan, TaskStatus status);

    long countByUserAndPlannedDateBetween(User user, LocalDate start, LocalDate end);

    long countByUserAndPrepPlanAndPlannedDateBetween(User user, PrepPlan prepPlan, LocalDate start, LocalDate end);

    long countByUserAndStatusAndCompletedAtBetween(User user, TaskStatus status, Instant start, Instant end);

    long countByUserAndPrepPlanAndStatusAndCompletedAtBetween(
            User user, PrepPlan prepPlan, TaskStatus status, Instant start, Instant end);

    void deleteByUserAndPrepPlan(User user, PrepPlan prepPlan);
}
