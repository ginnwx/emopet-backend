// PetRepository.java
package com.emopet.emopet_backend.repository;

import com.emopet.emopet_backend.model.Pet;
import com.emopet.emopet_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    // 🎯 Находит питомца по пользователю
    Optional<Pet> findByUser(User user);

    // 🎯 Находит питомца по ID пользователя
    Optional<Pet> findByUserId(Long userId);
}