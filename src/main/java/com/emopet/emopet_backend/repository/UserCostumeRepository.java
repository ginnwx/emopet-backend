package com.emopet.emopet_backend.repository;

import com.emopet.emopet_backend.model.User;
import com.emopet.emopet_backend.model.UserCostume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserCostumeRepository extends JpaRepository<UserCostume, Long> {

    // 🎯 Находит все костюмы пользователя
    List<UserCostume> findByUser(User user);

    // ✅ ПРАВИЛЬНЫЙ ВАРИАНТ 1: Используем связь через costume.id
    @Query("SELECT uc FROM UserCostume uc WHERE uc.user = :user AND uc.costume.id = :costumeId")
    Optional<UserCostume> findByUserAndCostumeId(@Param("user") User user, @Param("costumeId") Long costumeId);

    // ✅ ПРАВИЛЬНЫЙ ВАРИАНТ 2: Spring Data JPA синтаксис
    Optional<UserCostume> findByUserAndCostume_Id(User user, Long costumeId);

    // 🎯 Находит надетые костюмы пользователя
    List<UserCostume> findByUserAndEquippedTrue(User user);

    // 🎯 Находит все надетые костюмы (для проверки)
    List<UserCostume> findByEquippedTrue();
}