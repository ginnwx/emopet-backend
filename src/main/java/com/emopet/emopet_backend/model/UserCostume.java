// UserCostume.java - ПОЛНАЯ ВЕРСИЯ
package com.emopet.emopet_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_costumes")
public class UserCostume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "costume_id")
    private Costume costume;

    @Column(name = "is_equipped")
    private boolean equipped;

    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt;

    // Конструкторы
    public UserCostume() {}

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Costume getCostume() { return costume; }
    public void setCostume(Costume costume) { this.costume = costume; }

    public boolean isEquipped() { return equipped; }
    public void setEquipped(boolean equipped) { this.equipped = equipped; }

    public LocalDateTime getPurchasedAt() { return purchasedAt; }
    public void setPurchasedAt(LocalDateTime purchasedAt) { this.purchasedAt = purchasedAt; }
}