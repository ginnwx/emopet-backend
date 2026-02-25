package com.emopet.emopet_backend.controller;

import com.emopet.emopet_backend.model.User;
import com.emopet.emopet_backend.payload.UpdateUsernameRequest;
import com.emopet.emopet_backend.payload.UserProfileResponse;
import com.emopet.emopet_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        User user = userService.findByEmail(principal.getName());

        return ResponseEntity.ok(new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getCoins(),
                user.getDisplayName()
        ));
    }

    @PutMapping("/username")
    public ResponseEntity<UserProfileResponse> updateUsername(@RequestBody UpdateUsernameRequest req,
                                                              Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        String username = req.getUsername();
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userService.findByEmail(principal.getName());
        user.setDisplayName(username.trim());
        userService.save(user);

        return ResponseEntity.ok(new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getCoins(),
                user.getDisplayName()
        ));
    }
}
