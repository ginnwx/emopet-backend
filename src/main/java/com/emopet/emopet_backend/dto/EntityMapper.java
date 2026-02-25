package com.emopet.emopet_backend.dto;

import com.emopet.emopet_backend.dto.AchievementDto;
import com.emopet.emopet_backend.dto.MoodResponseDto;
import com.emopet.emopet_backend.model.Achievement;
import com.emopet.emopet_backend.model.UserAchievement;
import com.emopet.emopet_backend.model.UserMood;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Mapper для преобразования Entity в DTO
 */
@Component
public class EntityMapper {

    /**
     * UserMood -> MoodResponseDto
     */
    public MoodResponseDto toMoodResponseDto(UserMood mood) {
        return MoodResponseDto.builder()
                .id(mood.getId())
                .date(mood.getMoodDate())
                .moodValue(mood.getMoodValue())
                .note(mood.getNote())
                .createdAt(mood.getCreatedAt())
                .updatedAt(mood.getUpdatedAt())
                .build();
    }

    /**
     * Achievement -> AchievementDto (без информации о разблокировке)
     */
    public AchievementDto toAchievementDto(Achievement achievement) {
        return AchievementDto.builder()
                .id(achievement.getId())
                .title(achievement.getTitle())
                .description(achievement.getDescription())
                .emoji(achievement.getEmoji())
                .conditionType(achievement.getConditionType().name())
                .isUnlocked(false)
                .unlockedAt(null)
                .build();
    }

    /**
     * Achievement -> AchievementDto (с проверкой разблокировки)
     */
    public AchievementDto toAchievementDto(Achievement achievement, Set<String> unlockedIds) {
        AchievementDto dto = toAchievementDto(achievement);
        dto.setIsUnlocked(unlockedIds.contains(achievement.getId()));
        return dto;
    }

    /**
     * Achievement + UserAchievement -> AchievementDto (полная информация)
     */
    public AchievementDto toAchievementDto(Achievement achievement, UserAchievement userAchievement) {
        return AchievementDto.builder()
                .id(achievement.getId())
                .title(achievement.getTitle())
                .description(achievement.getDescription())
                .emoji(achievement.getEmoji())
                .conditionType(achievement.getConditionType().name())
                .isUnlocked(true)
                .unlockedAt(userAchievement != null ? userAchievement.getUnlockedAt() : null)
                .build();
    }
}