package com.preptracker.controller;

import com.preptracker.dto.PrepPlanRequest;
import com.preptracker.dto.PrepPlanResponse;
import com.preptracker.security.SecurityUtils;
import com.preptracker.service.PrepPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class PrepPlanController {

    private final PrepPlanService prepPlanService;
    private final SecurityUtils securityUtils;

    public PrepPlanController(PrepPlanService prepPlanService, SecurityUtils securityUtils) {
        this.prepPlanService = prepPlanService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<List<PrepPlanResponse>> listPlans() {
        return ResponseEntity.ok(prepPlanService.listPlans(securityUtils.getCurrentUser()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrepPlanResponse> getPlan(@PathVariable Long id) {
        return ResponseEntity.ok(PrepPlanResponse.from(
                prepPlanService.getPlanForUser(securityUtils.getCurrentUser(), id)));
    }

    @PostMapping
    public ResponseEntity<PrepPlanResponse> createPlan(@Valid @RequestBody PrepPlanRequest request) {
        return ResponseEntity.ok(prepPlanService.createPlan(securityUtils.getCurrentUser(), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        prepPlanService.deletePlan(securityUtils.getCurrentUser(), id);
        return ResponseEntity.noContent().build();
    }
}
