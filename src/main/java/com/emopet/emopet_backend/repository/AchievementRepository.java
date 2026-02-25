package com.emopet.emopet_backend.repository;

import com.emopet.emopet_backend.model.Achievement;
import com.emopet.emopet_backend.model.AchievementConditionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, String> {

    /**
     * Найти достижение по типу условия
     */
    Optional<Achievement> findByConditionType(AchievementConditionType conditionType);

    /**
     * Получить все достижения, отсортированные по ID
     */
    List<Achievement> findAllByOrderByIdAsc();
}