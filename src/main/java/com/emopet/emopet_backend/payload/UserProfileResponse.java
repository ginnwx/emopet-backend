package com.emopet.emopet_backend.payload;

public class UserProfileResponse {
    private Long id;
    private String email;
    private int coins;
    private String username; // displayName

    public UserProfileResponse(Long id, String email, int coins, String username) {
        this.id = id;
        this.email = email;
        this.coins = coins;
        this.username = username;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public int getCoins() { return coins; }
    public String getUsername() { return username; }
}
