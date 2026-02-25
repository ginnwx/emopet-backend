package com.emopet.emopet_backend.service;

import com.emopet.emopet_backend.exceptions.DateNotAllowedException;
import com.emopet.emopet_backend.model.Achievement;
import com.emopet.emopet_backend.model.UserMood;
import com.emopet.emopet_backend.repository.UserMoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с записями настроения
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MoodService {

    private final UserMoodRepository moodRepository;
    private final AchievementService achievementService;

    /**
     * Сохранить или обновить запись настроения
     * Возвращает tuple: (mood, newAchievements)
     */
    @Transactional
    public MoodSaveResult upsertMood(Long userId, LocalDate date, Integer moodValue, String note) {
        // ВАЛИДАЦИЯ: проверяем дату
        validateDate(date);

        log.debug("Upserting mood for user {} on date {}", userId, date);

        // Проверяем, существует ли уже запись
        Optional<UserMood> existingMood = moodRepository.findByUserIdAndMoodDate(userId, date);

        UserMood mood;
        if (existingMood.isPresent()) {
            // Обновление существующей записи
            mood = existingMood.get();
            mood.setMoodValue(moodValue);
            mood.setNote(note);
            log.debug("Updating existing mood entry for user {}", userId);
        } else {
            // Создание новой записи
            mood = UserMood.builder()
                    .userId(userId)
                    .moodDate(date)
                    .moodValue(moodValue)
                    .note(note)
                    .build();
            log.debug("Creating new mood entry for user {}", userId);
        }

        // Сохраняем
        mood = moodRepository.save(mood);

        // Проверяем достижения
        List<Achievement> newAchievements = achievementService.checkAndUnlockAchievements(userId);

        if (!newAchievements.isEmpty()) {
            log.info("User {} unlocked {} new achievements", userId, newAchievements.size());
        }

        return new MoodSaveResult(mood, newAchievements);
    }

    /**
     * Валидация даты настроения
     * Правила:
     * - Можно сохранять ТОЛЬКО за сегодня
     * - Прошлые даты - запрещены
     * - Будущие даты - запрещены
     */
    private void validateDate(LocalDate date) {
        LocalDate today = LocalDate.now();

        if (date.isBefore(today)) {
            throw new DateNotAllowedException(
                    "Можно редактировать только сегодняшний день. Прошлые дни доступны только для просмотра."
            );
        }

        if (date.isAfter(today)) {
            throw new DateNotAllowedException(
                    "Нельзя отмечать настроение на будущее"
            );
        }

        // date.equals(today) - OK
    }

    /**
     * Получить настроения пользователя за период
     */
    @Transactional(readOnly = true)
    public List<UserMood> getMoodsByPeriod(Long userId, LocalDate fromDate, LocalDate toDate) {
        log.debug("Fetching moods for user {} from {} to {}", userId, fromDate, toDate);

        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("fromDate не может быть позже toDate");
        }

        return moodRepository.findByUserIdAndMoodDateBetweenOrderByMoodDateAsc(
                userId,
                fromDate,
                toDate
        );
    }

    /**
     * Получить конкретное настроение
     */
    @Transactional(readOnly = true)
    public Optional<UserMood> getMoodByDate(Long userId, LocalDate date) {
        return moodRepository.findByUserIdAndMoodDate(userId, date);
    }

    /**
     * Получить все настроения пользователя
     */
    @Transactional(readOnly = true)
    public List<UserMood> getAllMoods(Long userId) {
        return moodRepository.findByUserIdOrderByMoodDateDesc(userId);
    }

    /**
     * Получить последние N настроений
     */
    @Transactional(readOnly = true)
    public List<UserMood> getRecentMoods(Long userId, int limit) {
        if (limit == 7) {
            return moodRepository.findTop7ByUserIdOrderByMoodDateDesc(userId);
        } else if (limit == 30) {
            return moodRepository.findTop30ByUserIdOrderByMoodDateDesc(userId);
        } else {
            return moodRepository.findByUserIdOrderByMoodDateDesc(userId).stream()
                    .limit(limit)
                    .toList();
        }
    }

    /**
     * Удалить настроение (только для сегодняшнего дня)
     */
    @Transactional
    public void deleteMood(Long userId, LocalDate date) {
        validateDate(date);

        Optional<UserMood> mood = moodRepository.findByUserIdAndMoodDate(userId, date);
        mood.ifPresent(moodRepository::delete);

        log.info("Deleted mood for user {} on date {}", userId, date);
    }

    /**
     * Результат сохранения настроения
     */
    public record MoodSaveResult(
            UserMood mood,
            List<Achievement> newAchievements
    ) {}
}