package com.emopet.emopet_backend.model;

/**
 * Типы условий для разблокировки достижений
 */
public enum AchievementConditionType {
    FIRST_ENTRY("Первая запись"),
    STREAK_3("Серия 3 дня"),
    STREAK_7("Серия 7 дней"),
    STREAK_30("Серия 30 дней"),
    TOTAL_10("10 записей всего"),
    TOTAL_30("30 записей всего"),
    TOTAL_100("100 записей всего"),
    PERFECT_WEEK("Идеальная неделя"),
    PERFECT_MONTH("Идеальный месяц"),
    ALL_MOODS_EXPLORED("Все эмоции использованы");

    private final String description;

    AchievementConditionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}