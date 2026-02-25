package com.emopet.emopet_backend.dto;

import com.emopet.emopet_backend.model.Role;

public record AdminUserResponse(
        Long id,
        String email,
        String username,
        int coins,
        Role role
) {}