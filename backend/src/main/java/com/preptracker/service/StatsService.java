package com.preptracker.service;

import com.preptracker.dto.HeatmapResponse;
import com.preptracker.dto.StreakResponse;
import com.preptracker.entity.PrepPlan;
import com.preptracker.entity.Task;
import com.preptracker.entity.TaskStatus;
import com.preptracker.entity.User;
import com.preptracker.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final TaskRepository taskRepository;

    public StatsService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public StreakResponse getStreakStats(User user, PrepPlan plan) {
        List<LocalDate> activeDays = getActiveDays(user, plan);
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());

        StreakResponse response = new StreakResponse();
        response.setCurrentStreak(calculateCurrentStreak(activeDays, today));
        response.setLongestStreak(calculateLongestStreak(activeDays));
        response.setActiveDaysThisMonth((int) activeDays.stream()
                .filter(d -> !d.isBefore(monthStart) && !d.isAfter(monthEnd))
                .count());

        if (plan != null) {
            response.setPlannedTasksThisMonth(
                    (int) taskRepository.countByUserAndPrepPlanAndPlannedDateBetween(user, plan, monthStart, monthEnd));
            response.setCompletedTasksThisMonth(
                    (int) taskRepository.countByUserAndPrepPlanAndStatusAndCompletedAtBetween(
                            user, plan, TaskStatus.COMPLETED,
                            monthStart.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                            monthEnd.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        } else {
            response.setPlannedTasksThisMonth(
                    (int) taskRepository.countByUserAndPlannedDateBetween(user, monthStart, monthEnd));
            response.setCompletedTasksThisMonth(
                    (int) taskRepository.countByUserAndStatusAndCompletedAtBetween(
                            user, TaskStatus.COMPLETED,
                            monthStart.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                            monthEnd.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        return response;
    }

    public HeatmapResponse getHeatmap(User user, PrepPlan plan, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        Map<String, Integer> days = new LinkedHashMap<>();
        List<Task> completed = plan != null
                ? taskRepository.findByUserAndPrepPlanAndStatusAndCompletedAtIsNotNullOrderByCompletedAtAsc(
                        user, plan, TaskStatus.COMPLETED)
                : taskRepository.findByUserAndStatusAndCompletedAtIsNotNullOrderByCompletedAtAsc(
                        user, TaskStatus.COMPLETED);

        Map<LocalDate, Long> counts = completed.stream()
                .map(task -> task.getCompletedAt().atZone(ZoneId.systemDefault()).toLocalDate())
                .filter(date -> !date.isBefore(start) && !date.isAfter(end))
                .collect(Collectors.groupingBy(d -> d, Collectors.counting()));

        counts.forEach((date, count) -> days.put(date.toString(), count.intValue()));

        return new HeatmapResponse(year, days);
    }

    private List<LocalDate> getActiveDays(User user, PrepPlan plan) {
        Set<LocalDate> days = new TreeSet<>();
        List<Task> completed = plan != null
                ? taskRepository.findByUserAndPrepPlanAndStatusAndCompletedAtIsNotNullOrderByCompletedAtAsc(
                        user, plan, TaskStatus.COMPLETED)
                : taskRepository.findByUserAndStatusAndCompletedAtIsNotNullOrderByCompletedAtAsc(
                        user, TaskStatus.COMPLETED);

        for (Task task : completed) {
            if (task.getCompletedAt() != null) {
                days.add(task.getCompletedAt().atZone(ZoneId.systemDefault()).toLocalDate());
            }
        }
        return days.stream().collect(Collectors.toList());
    }

    private int calculateCurrentStreak(List<LocalDate> activeDays, LocalDate today) {
        if (activeDays.isEmpty()) {
            return 0;
        }

        Set<LocalDate> daySet = new TreeSet<>(activeDays);
        int streak = 0;
        LocalDate cursor = daySet.contains(today) ? today : today.minusDays(1);

        while (daySet.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    private int calculateLongestStreak(List<LocalDate> activeDays) {
        if (activeDays.isEmpty()) {
            return 0;
        }

        int longest = 1;
        int current = 1;

        for (int i = 1; i < activeDays.size(); i++) {
            long gap = ChronoUnit.DAYS.between(activeDays.get(i - 1), activeDays.get(i));
            if (gap == 1) {
                current++;
                longest = Math.max(longest, current);
            } else if (gap > 1) {
                current = 1;
            }
        }
        return longest;
    }
}
