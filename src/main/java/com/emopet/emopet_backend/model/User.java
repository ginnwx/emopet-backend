// User.java
package com.emopet.emopet_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import com.emopet.emopet_backend.model.Role;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // отображаемое имя в приложении
    @Column(nullable = false)
    private String username = "Друг";

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    private int coins = 100;

    public User() {}

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // ===================== SECURITY =====================

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority(role.name())
        );
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    /**
     * ВАЖНО:
     * Spring Security считает username = email, поэтому здесь возвращаем email.
     * Но чтобы в JSON не улетал email как "username" — помечаем JsonIgnore.
     */
    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    // ===================== JSON/API =====================

    /**
     * Это поле будет приходить во Flutter как user.username = "Друг/Ginny"
     */
    @JsonProperty("username")
    public String getDisplayName() {
        return username;
    }

    /**
     * Обновление отображаемого имени
     */
    public void setDisplayName(String displayName) {
        this.username = displayName;
    }
    //РОЛЬ
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_USER;
    // ===================== GETTERS/SETTERS =====================

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }
}
