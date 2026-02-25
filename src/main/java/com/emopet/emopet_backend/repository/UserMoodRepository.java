package com.emopet.emopet_backend.repository;

import com.emopet.emopet_backend.model.UserMood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserMoodRepository extends JpaRepository<UserMood, Long> {

    /**
     * Найти настроение пользователя за конкретную дату
     */
    Optional<UserMood> findByUserIdAndMoodDate(Long userId, LocalDate moodDate);

    /**
     * Получить все настроения пользователя за период
     * Сортировка по дате (от старых к новым)
     */
    List<UserMood> findByUserIdAndMoodDateBetweenOrderByMoodDateAsc(
            Long userId,
            LocalDate fromDate,
            LocalDate toDate
    );

    /**
     * Получить все настроения пользователя, отсортированные по дате (убывание)
     */
    List<UserMood> findByUserIdOrderByMoodDateDesc(Long userId);

    /**
     * Подсчитать общее количество записей пользователя
     */
    long countByUserId(Long userId);

    /**
     * Получить уникальные значения настроений, которые использовал пользователь
     */
    @Query("SELECT DISTINCT um.moodValue FROM UserMood um WHERE um.userId = :userId")
    List<Integer> findDistinctMoodValuesByUserId(@Param("userId") Long userId);

    /**
     * Получить самую раннюю дату записи пользователя
     */
    @Query("SELECT MIN(um.moodDate) FROM UserMood um WHERE um.userId = :userId")
    Optional<LocalDate> findEarliestMoodDateByUserId(@Param("userId") Long userId);

    /**
     * Получить самую позднюю дату записи пользователя
     */
    @Query("SELECT MAX(um.moodDate) FROM UserMood um WHERE um.userId = :userId")
    Optional<LocalDate> findLatestMoodDateByUserId(@Param("userId") Long userId);

    /**
     * Получить последние N записей пользователя
     */
    List<UserMood> findTop7ByUserIdOrderByMoodDateDesc(Long userId);
    List<UserMood> findTop30ByUserIdOrderByMoodDateDesc(Long userId);

    /**
     * Проверить существование записи за дату
     */
    boolean existsByUserIdAndMoodDate(Long userId, LocalDate moodDate);

    /**
     * Удалить все записи пользователя (для тестов)
     */
    void deleteByUserId(Long userId);
}