package com.preptracker.repository;

import com.preptracker.entity.PrepPlan;
import com.preptracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrepPlanRepository extends JpaRepository<PrepPlan, Long> {

    List<PrepPlan> findByUserOrderByCreatedAtDesc(User user);

    Optional<PrepPlan> findByIdAndUser(Long id, User user);
}
