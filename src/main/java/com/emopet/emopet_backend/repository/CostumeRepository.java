// CostumeRepository.java
package com.emopet.emopet_backend.repository;

import com.emopet.emopet_backend.model.Costume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CostumeRepository extends JpaRepository<Costume, Long> {

    // 🎯 Находит все костюмы (уже есть в JpaRepository)
    List<Costume> findAll();

    // 🎯 Находит костюм по ID (уже есть в JpaRepository)
    Optional<Costume> findById(Long id);

    // 🎯 Находит костюмы по типу (если добавим поле type)
    // List<Costume> findByType(String type);
}