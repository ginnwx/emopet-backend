package com.emopet.emopet_backend.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {
    // 🎯 Геттеры и сеттеры
    private String token;
    private String type = "Bearer";

    public JwtResponse(String token) {
        this.token = token;
    }

}