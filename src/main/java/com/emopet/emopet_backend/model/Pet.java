// Pet.java
package com.emopet.emopet_backend.model;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "pets")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name = "Котик";
    private int hunger = 80;
    private int health = 90;
    private int fun = 70;
    private String state = "cat_default";
    @Column(name = "sleep_until")
    private LocalDateTime sleepUntil;

    // 🎯 Связь с пользователем (Один пользователь - Один питомец)
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 🎯 Активный костюм
    @ManyToOne
    @JoinColumn(name = "equipped_costume_id")
    private Costume equippedCostume;

    // Конструкторы
    public Pet() {}

    public Pet(User user) {
        this.user = user;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getHunger() { return hunger; }
    public void setHunger(int hunger) { this.hunger = Math.min(100, Math.max(0, hunger)); }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = Math.min(100, Math.max(0, health)); }

    public int getFun() { return fun; }
    public void setFun(int fun) { this.fun = Math.min(100, Math.max(0, fun)); }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Costume getEquippedCostume() { return equippedCostume; }
    public void setEquippedCostume(Costume equippedCostume) { this.equippedCostume = equippedCostume; }

    public LocalDateTime getSleepUntil() {
        return sleepUntil;
    }

    public void setSleepUntil(LocalDateTime sleepUntil) {
        this.sleepUntil = sleepUntil;
    }

}