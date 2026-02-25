package com.emopet.emopet_backend.controller;

import com.emopet.emopet_backend.dto.AdminUserResponse;
import com.emopet.emopet_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<AdminUserResponse> getAllUsers() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminEmail = auth.getName();

        log.info("Admin {} requested full user list", adminEmail);

        List<AdminUserResponse> users = userRepository.findAll().stream()
                .map(u -> new AdminUserResponse(
                        u.getId(),
                        u.getEmail(),
                        u.getDisplayName(),
                        u.getCoins(),
                        u.getRole()
                ))
                .toList();

        log.info("Admin {} received {} users", adminEmail, users.size());

        return users;
    }
}