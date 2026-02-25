package com.emopet.emopet_backend.repository;

import com.emopet.emopet_backend.model.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

    /**
     * Получить все достижения пользователя
     */
    @Query("SELECT ua FROM UserAchievement ua " +
            "JOIN FETCH ua.achievement " +
            "WHERE ua.userId = :userId " +
            "ORDER BY ua.unlockedAt DESC")
    List<UserAchievement> findAllByUserIdWithAchievement(@Param("userId") Long userId);

    /**
     * Проверить, есть ли у пользователя конкретное достижение
     */
    boolean existsByUserIdAndAchievementId(Long userId, String achievementId);

    /**
     * Найти достижение пользователя по ID достижения
     */
    Optional<UserAchievement> findByUserIdAndAchievementId(Long userId, String achievementId);

    /**
     * Получить ID всех разблокированных достижений пользователя
     */
    @Query("SELECT ua.achievementId FROM UserAchievement ua WHERE ua.userId = :userId")
    List<String> findAchievementIdsByUserId(@Param("userId") Long userId);

    /**
     * Подсчитать количество достижений пользователя
     */
    long countByUserId(Long userId);

    /**
     * Удалить все достижения пользователя (для тестов)
     */
    void deleteByUserId(Long userId);
}