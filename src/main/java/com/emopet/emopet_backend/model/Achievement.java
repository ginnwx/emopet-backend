package com.emopet.emopet_backend.model;

import com.emopet.emopet_backend.model.AchievementConditionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity для справочника достижений
 * Это статический справочник, который заполняется при инициализации БД
 */
@Entity
@Table(name = "achievements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Achievement {

    /**
     * ID достижения (строковый, например "first_entry")
     */
    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * Эмодзи-иконка достижения
     */
    @Column(name = "emoji", nullable = false, length = 10)
    private String emoji;

    /**
     * Тип условия для разблокировки
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type", nullable = false, length = 50)
    private AchievementConditionType conditionType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}