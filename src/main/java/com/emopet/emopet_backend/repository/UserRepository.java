// UserRepository.java - проверь что есть
package com.emopet.emopet_backend.repository;

import com.emopet.emopet_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 🎯 Находит пользователя по email
    Optional<User> findByEmail(String email);

    // 🎯 Проверяет существует ли пользователь с таким email
    Boolean existsByEmail(String email);


}