package com.emopet.emopet_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO для ответа при сохранении настроения
 * Содержит сохранённое настроение и список новых достижений
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoodSaveResponseDto {

    /**
     * Сохранённое настроение
     */
    private MoodResponseDto mood;

    /**
     * Список новых разблокированных достижений
     */
    private List<AchievementDto> newAchievements;

    /**
     * Общее количество достижений пользователя
     */
    private Integer totalAchievements;

    /**
     * Текущая серия дней
     */
    private Integer currentStreak;
}