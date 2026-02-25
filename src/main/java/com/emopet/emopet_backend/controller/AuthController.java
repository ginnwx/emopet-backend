package com.emopet.emopet_backend.controller;

import com.emopet.emopet_backend.model.User;
import com.emopet.emopet_backend.model.Pet;
import com.emopet.emopet_backend.payload.LoginRequest;
import com.emopet.emopet_backend.payload.RegisterRequest;
import com.emopet.emopet_backend.payload.JwtResponse;
import com.emopet.emopet_backend.repository.UserRepository;
import com.emopet.emopet_backend.repository.PetRepository;
import com.emopet.emopet_backend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*") // ✅ разрешаем запросы с Flutter
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepository;
    @Autowired private PetRepository petRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        log.info("New user registration attempt: {}", registerRequest.getEmail());
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return new ResponseEntity<>("Email уже используется!", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userRepository.save(user);

        // создаём питомца
        Pet pet = new Pet();
        pet.setUser(user);
        pet.setName("Котик");
        pet.setHunger(80);
        pet.setHealth(90);
        pet.setFun(70);
        pet.setState("cat_default");
        petRepository.save(pet);

        log.info("User successfully registered: {}", user.getEmail());
        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);

    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        log.info("Login attempt for email: {}", loginRequest.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        String jwt = jwtUtils.generateJwtToken(authentication);

        log.info("User successfully authenticated: {}", loginRequest.getEmail());

        return ResponseEntity.ok(new JwtResponse(jwt));
    }
}