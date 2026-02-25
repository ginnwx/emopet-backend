package com.emopet.emopet_backend.service;

import com.emopet.emopet_backend.model.Achievement;
import com.emopet.emopet_backend.model.UserAchievement;
import com.emopet.emopet_backend.model.AchievementConditionType;
import com.emopet.emopet_backend.repository.AchievementRepository;
import com.emopet.emopet_backend.repository.UserAchievementRepository;
import com.emopet.emopet_backend.repository.UserMoodRepository;
import com.emopet.emopet_backend.model.UserMood;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserMoodRepository userMoodRepository;

    /**
     * Получить все достижения с информацией о разблокировке для пользователя
     */
    @Transactional(readOnly = true)
    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAllByOrderByIdAsc();
    }

    /**
     * Получить разблокированные достижения пользователя
     */
    @Transactional(readOnly = true)
    public List<UserAchievement> getUserAchievements(Long userId) {
        return userAchievementRepository.findAllByUserIdWithAchievement(userId);
    }

    /**
     * Получить ID разблокированных достижений пользователя
     */
    @Transactional(readOnly = true)
    public Set<String> getUnlockedAchievementIds(Long userId) {
        return new HashSet<>(userAchievementRepository.findAchievementIdsByUserId(userId));
    }

    /**
     * Проверить и разблокировать новые достижения после сохранения настроения
     * Возвращает список НОВЫХ достижений
     */
    @Transactional
    public List<Achievement> checkAndUnlockAchievements(Long userId) {
        log.debug("Checking achievements for user {}", userId);

        // Получаем все настроения пользователя
        List<UserMood> allMoods = userMoodRepository.findByUserIdOrderByMoodDateDesc(userId);

        if (allMoods.isEmpty()) {
            return Collections.emptyList();
        }

        // Получаем уже разблокированные достижения
        Set<String> unlockedIds = getUnlockedAchievementIds(userId);

        // Получаем все достижения
        List<Achievement> allAchievements = achievementRepository.findAll();

        // Проверяем каждое достижение
        List<Achievement> newlyUnlocked = new ArrayList<>();

        for (Achievement achievement : allAchievements) {
            // Пропускаем уже разблокированные
            if (unlockedIds.contains(achievement.getId())) {
                continue;
            }

            // Проверяем условие
            if (checkCondition(achievement.getConditionType(), userId, allMoods)) {
                // Разблокируем достижение
                UserAchievement userAchievement = UserAchievement.builder()
                        .userId(userId)
                        .achievementId(achievement.getId())
                        .build();

                userAchievementRepository.save(userAchievement);
                newlyUnlocked.add(achievement);

                log.info("User {} unlocked achievement: {}", userId, achievement.getId());
            }
        }

        return newlyUnlocked;
    }

    /**
     * Проверка условия достижения
     */
    private boolean checkCondition(
            AchievementConditionType conditionType,
            Long userId,
            List<UserMood> allMoods
    ) {
        return switch (conditionType) {
            case FIRST_ENTRY -> checkFirstEntry(allMoods);
            case STREAK_3 -> checkStreak(allMoods, 3);
            case STREAK_7 -> checkStreak(allMoods, 7);
            case STREAK_30 -> checkStreak(allMoods, 30);
            case TOTAL_10 -> checkTotal(allMoods, 10);
            case TOTAL_30 -> checkTotal(allMoods, 30);
            case TOTAL_100 -> checkTotal(allMoods, 100);
            case PERFECT_WEEK -> checkPerfectWeek(allMoods);
            case PERFECT_MONTH -> checkPerfectMonth(allMoods);
            case ALL_MOODS_EXPLORED -> checkAllMoodsExplored(allMoods);
        };
    }

    /**
     * Проверка: первая запись
     */
    private boolean checkFirstEntry(List<UserMood> moods) {
        return !moods.isEmpty();
    }

    /**
     * Проверка: общее количество записей
     */
    private boolean checkTotal(List<UserMood> moods, int required) {
        return moods.size() >= required;
    }

    /**
     * Проверка: все эмоции использованы
     */
    private boolean checkAllMoodsExplored(List<UserMood> moods) {
        Set<Integer> uniqueMoods = moods.stream()
                .map(UserMood::getMoodValue)
                .collect(Collectors.toSet());

        return uniqueMoods.size() == 5 &&
                uniqueMoods.containsAll(Arrays.asList(1, 2, 3, 4, 5));
    }

    /**
     * Проверка: серия N дней подряд
     * ВАЖНО: Считаем от сегодня назад, проверяем непрерывность
     */
    private boolean checkStreak(List<UserMood> moods, int requiredStreak) {
        if (moods.size() < requiredStreak) {
            return false;
        }

        // Сортируем по дате (от новых к старым)
        List<UserMood> sortedMoods = moods.stream()
                .sorted(Comparator.comparing(UserMood::getMoodDate).reversed())
                .collect(Collectors.toList());

        // Проверяем текущую серию от сегодня/последней записи
        LocalDate expectedDate = sortedMoods.get(0).getMoodDate();
        int currentStreak = 0;

        for (UserMood mood : sortedMoods) {
            if (mood.getMoodDate().equals(expectedDate)) {
                currentStreak++;
                expectedDate = expectedDate.minusDays(1);

                if (currentStreak >= requiredStreak) {
                    return true;
                }
            } else {
                // Прерывание серии
                break;
            }
        }

        return false;
    }

    /**
     * Проверка: идеальная неделя (7 дней подряд без пропусков)
     */
    private boolean checkPerfectWeek(List<UserMood> moods) {
        return checkPerfectPeriod(moods, 7);
    }

    /**
     * Проверка: идеальный месяц (30 дней подряд без пропусков)
     */
    private boolean checkPerfectMonth(List<UserMood> moods) {
        return checkPerfectPeriod(moods, 30);
    }

    /**
     * Проверка: идеальный период (N дней подряд без пропусков)
     * Ищем любой период длиной N дней, где есть запись на каждый день
     */
    private boolean checkPerfectPeriod(List<UserMood> moods, int days) {
        if (moods.size() < days) {
            return false;
        }

        // Создаём Set дат для быстрого поиска
        Set<LocalDate> moodDates = moods.stream()
                .map(UserMood::getMoodDate)
                .collect(Collectors.toSet());

        // Сортируем даты
        List<LocalDate> sortedDates = new ArrayList<>(moodDates);
        sortedDates.sort(Comparator.reverseOrder());

        // Проверяем каждое окно размером days
        for (int i = 0; i <= sortedDates.size() - days; i++) {
            LocalDate startDate = sortedDates.get(i);
            LocalDate endDate = startDate.minusDays(days - 1);

            // Проверяем, есть ли запись на каждый день в диапазоне
            boolean isPerfect = true;
            LocalDate currentDate = startDate;

            for (int j = 0; j < days; j++) {
                if (!moodDates.contains(currentDate)) {
                    isPerfect = false;
                    break;
                }
                currentDate = currentDate.minusDays(1);
            }

            if (isPerfect) {
                return true;
            }
        }

        return false;
    }

    /**
     * Вычислить текущую серию дней пользователя
     * Используется для отображения в UI
     */
    @Transactional(readOnly = true)
    public int calculateCurrentStreak(Long userId) {
        List<UserMood> moods = userMoodRepository.findByUserIdOrderByMoodDateDesc(userId);

        if (moods.isEmpty()) {
            return 0;
        }

        // Проверяем, есть ли запись за сегодня или вчера
        LocalDate today = LocalDate.now();
        LocalDate latestDate = moods.get(0).getMoodDate();

        // Если последняя запись не сегодня и не вчера - серия прервана
        long daysSinceLastEntry = ChronoUnit.DAYS.between(latestDate, today);
        if (daysSinceLastEntry > 1) {
            return 0;
        }

        // Считаем серию
        int streak = 0;
        LocalDate expectedDate = latestDate;

        for (UserMood mood : moods) {
            if (mood.getMoodDate().equals(expectedDate)) {
                streak++;
                expectedDate = expectedDate.minusDays(1);
            } else {
                break;
            }
        }

        return streak;
    }

    /**
     * Получить статистику достижений пользователя
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAchievementStats(Long userId) {
        long totalAchievements = achievementRepository.count();
        long unlockedCount = userAchievementRepository.countByUserId(userId);
        int currentStreak = calculateCurrentStreak(userId);
        long totalEntries = userMoodRepository.countByUserId(userId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAchievements", totalAchievements);
        stats.put("unlockedAchievements", unlockedCount);
        stats.put("currentStreak", currentStreak);
        stats.put("totalEntries", totalEntries);
        stats.put("progress", totalAchievements > 0
                ? (double) unlockedCount / totalAchievements * 100
                : 0.0);

        return stats;
    }
}