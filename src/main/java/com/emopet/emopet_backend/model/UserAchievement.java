package com.emopet.emopet_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity для достижений, полученных пользователем
 */
@Entity
@Table(
        name = "user_achievements",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_user_achievement",
                        columnNames = {"user_id", "achievement_id"}
                )
        },
        indexes = {
                @Index(name = "idx_user_achievements_user_id", columnList = "user_id"),
                @Index(name = "idx_user_achievements_achievement_id", columnList = "achievement_id")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * ID достижения из справочника
     */
    @Column(name = "achievement_id", nullable = false, length = 50)
    private String achievementId;

    /**
     * Время разблокировки достижения
     */
    @CreationTimestamp
    @Column(name = "unlocked_at", nullable = false, updatable = false)
    private LocalDateTime unlockedAt;

    /**
     * Связь с Achievement (для удобства)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id", insertable = false, updatable = false)
    private Achievement achievement;
}