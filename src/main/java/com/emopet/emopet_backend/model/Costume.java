// Costume.java
package com.emopet.emopet_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "costumes")
public class Costume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private int price;

    @Column(name = "image_suffix")
    private String imageSuffix; // "hat", "suit"

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Конструкторы
    public Costume() {}

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getImageSuffix() { return imageSuffix; }
    public void setImageSuffix(String imageSuffix) { this.imageSuffix = imageSuffix; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}