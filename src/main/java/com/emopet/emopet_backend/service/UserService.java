package com.emopet.emopet_backend.service;

import com.emopet.emopet_backend.model.User;
import com.emopet.emopet_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // ✅ правильное название (то, что ты реально используешь)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ если где-то в проекте уже дергается findByUsername(email)
    // оставим, но по факту он тоже ищет по email
    public User findByUsername(String email) {
        return findByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
