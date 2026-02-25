package com.emopet.emopet_backend.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    // 🎯 Геттеры и сеттеры
    private String email;
    private String password;

}