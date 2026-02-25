package com.emopet.emopet_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity для хранения записей настроения пользователя
 * Один пользователь может иметь только одну запись на дату
 */
@Entity
@Table(
        name = "user_moods",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_user_mood_date",
                        columnNames = {"user_id", "mood_date"}
                )
        },
        indexes = {
                @Index(name = "idx_user_moods_user_id", columnList = "user_id"),
                @Index(name = "idx_user_moods_mood_date", columnList = "mood_date"),
                @Index(name = "idx_user_moods_user_date", columnList = "user_id,mood_date")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Дата настроения (только дата, без времени)
     * LocalDate автоматически маппится на SQL DATE
     */
    @Column(name = "mood_date", nullable = false)
    private LocalDate moodDate;

    /**
     * Значение настроения: 1-5
     * 1 = 😫 (Ужасно)
     * 2 = 😢 (Грустно)
     * 3 = 😐 (Так себе)
     * 4 = 🙂 (Норм)
     * 5 = 😄 (Счастье)
     */
    @Column(name = "mood_value", nullable = false)
    private Integer moodValue;

    /**
     * Заметка пользователя (опционально)
     */
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Валидация значения настроения
     */
    @PrePersist
    @PreUpdate
    private void validate() {
        if (moodValue < 1 || moodValue > 5) {
            throw new IllegalArgumentException(
                    "Mood value must be between 1 and 5, got: " + moodValue
            );
        }
    }
}