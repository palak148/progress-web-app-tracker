package com.preptracker.controller;

import com.preptracker.dto.HeatmapResponse;
import com.preptracker.dto.StreakResponse;
import com.preptracker.entity.PrepPlan;
import com.preptracker.entity.User;
import com.preptracker.security.SecurityUtils;
import com.preptracker.service.PrepPlanService;
import com.preptracker.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;
    private final PrepPlanService prepPlanService;
    private final SecurityUtils securityUtils;

    public StatsController(StatsService statsService,
                           PrepPlanService prepPlanService,
                           SecurityUtils securityUtils) {
        this.statsService = statsService;
        this.prepPlanService = prepPlanService;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/streak")
    public ResponseEntity<StreakResponse> getStreak(@RequestParam(required = false) Long projectId) {
        User user = securityUtils.getCurrentUser();
        PrepPlan plan = resolvePlan(user, projectId);
        return ResponseEntity.ok(statsService.getStreakStats(user, plan));
    }

    @GetMapping("/heatmap")
    public ResponseEntity<HeatmapResponse> getHeatmap(
            @RequestParam(defaultValue = "2026") int year,
            @RequestParam(required = false) Long projectId) {
        User user = securityUtils.getCurrentUser();
        PrepPlan plan = resolvePlan(user, projectId);
        return ResponseEntity.ok(statsService.getHeatmap(user, plan, year));
    }

    private PrepPlan resolvePlan(User user, Long projectId) {
        if (projectId == null) {
            return null;
        }
        return prepPlanService.getPlanForUser(user, projectId);
    }
}
