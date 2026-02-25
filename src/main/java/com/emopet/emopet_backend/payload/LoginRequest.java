package com.emopet.emopet_backend.payload;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    // 🎯 Геттеры и сеттеры
    private String email;
    private String password;

}