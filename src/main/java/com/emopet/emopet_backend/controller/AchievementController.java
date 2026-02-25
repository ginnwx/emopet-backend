package com.emopet.emopet_backend.controller;

import com.emopet.emopet_backend.dto.EntityMapper;
import com.emopet.emopet_backend.dto.AchievementDto;
import com.emopet.emopet_backend.model.Achievement;
import com.emopet.emopet_backend.model.UserAchievement;
import com.emopet.emopet_backend.service.AchievementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * REST Controller для работы с достижениями
 */
@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
@Slf4j
public class AchievementController {

    private final AchievementService achievementService;
    private final EntityMapper mapper;

    /**
     * Получить все достижения с информацией о разблокировке
     * GET /api/achievements
     *
     * Возвращает:
     * - Список всех достижений
     * - Для каждого: разблокировано или нет
     * - Время разблокировки (если разблокировано)
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllAchievements(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = extractUserId(userDetails);

        log.info("Fetching achievements for user {}", userId);

        // Получаем все достижения
        List<Achievement> allAchievements = achievementService.getAllAchievements();

        // Получаем разблокированные достижения пользователя
        List<UserAchievement> userAchievements = achievementService.getUserAchievements(userId);

        // Создаём Map для быстрого поиска
        Map<String, UserAchievement> userAchievementMap = userAchievements.stream()
                .collect(Collectors.toMap(
                        UserAchievement::getAchievementId,
                        ua -> ua
                ));

        // Преобразуем в DTO
        List<AchievementDto> achievementDtos = allAchievements.stream()
                .map(achievement -> {
                    UserAchievement userAchievement = userAchievementMap.get(achievement.getId());
                    if (userAchievement != null) {
                        // Разблокировано
                        return mapper.toAchievementDto(achievement, userAchievement);
                    } else {
                        // Заблокировано
                        return mapper.toAchievementDto(achievement);
                    }
                })
                .collect(Collectors.toList());

        // Получаем статистику
        Map<String, Object> stats = achievementService.getAchievementStats(userId);

        // Формируем ответ
        Map<String, Object> response = new HashMap<>();
        response.put("achievements", achievementDtos);
        response.put("stats", stats);

        return ResponseEntity.ok(response);
    }

    /**
     * Получить только разблокированные достижения
     * GET /api/achievements/unlocked
     */
    @GetMapping("/unlocked")
    public ResponseEntity<List<AchievementDto>> getUnlockedAchievements(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = extractUserId(userDetails);

        log.info("Fetching unlocked achievements for user {}", userId);

        List<UserAchievement> userAchievements = achievementService.getUserAchievements(userId);

        List<AchievementDto> response = userAchievements.stream()
                .map(ua -> mapper.toAchievementDto(ua.getAchievement(), ua))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Получить статистику достижений
     * GET /api/achievements/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAchievementStats(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = extractUserId(userDetails);

        log.info("Fetching achievement stats for user {}", userId);

        Map<String, Object> stats = achievementService.getAchievementStats(userId);

        return ResponseEntity.ok(stats);
    }

    /**
     * Извлечь userId из UserDetails
     */
    private Long extractUserId(UserDetails userDetails) {
        // TODO: Адаптировать под вашу реализацию JWT
        return 1L; // Заглушка
    }
}