package com.emopet.emopet_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO для информации о достижении
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchievementDto {

    private String id;

    private String title;

    private String description;

    private String emoji;

    private String conditionType;

    /**
     * Разблокировано ли достижение для пользователя
     */
    private Boolean isUnlocked;

    /**
     * Время разблокировки (если разблокировано)
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime unlockedAt;
}