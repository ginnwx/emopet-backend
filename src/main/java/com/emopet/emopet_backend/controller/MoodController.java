package com.emopet.emopet_backend.controller;

import com.emopet.emopet_backend.exceptions.DateNotAllowedException;
import com.emopet.emopet_backend.model.User;
import com.emopet.emopet_backend.model.UserMood;
import com.emopet.emopet_backend.payload.MoodUpsertRequest;
import com.emopet.emopet_backend.repository.UserRepository;
import com.emopet.emopet_backend.service.MoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/moods")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MoodController {

    private final MoodService moodService;
    private final UserRepository userRepository;

    // ===== USER =====

    private Long getUserId(Principal principal) {

        if (principal == null) {
            throw new RuntimeException("Unauthorized");
        }

        String email = principal.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getId();
    }

    // ===== SAVE MOOD =====

    @PostMapping
    public ResponseEntity<?> saveMood(
            @RequestBody MoodUpsertRequest request,
            Principal principal
    ) {

        try {
            Long userId = getUserId(principal);

            var result = moodService.upsertMood(
                    userId,
                    LocalDate.parse(request.getDate()),
                    request.getMood(),
                    request.getNote()
            );

            return ResponseEntity.ok(result);


        } catch (DateNotAllowedException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // ===== HISTORY =====

    @GetMapping
    public ResponseEntity<List<UserMood>> getMoodsByPeriod(
            @RequestParam String from,
            @RequestParam String to,
            Principal principal
    ) {

        Long userId = getUserId(principal);

        List<UserMood> moods = moodService.getMoodsByPeriod(
                userId,
                LocalDate.parse(from),
                LocalDate.parse(to)
        );

        return ResponseEntity.ok(moods);
    }

    // ===== ONE DAY =====

    @GetMapping("/{date}")
    public ResponseEntity<?> getMoodByDate(
            @PathVariable String date,
            Principal principal
    ) {

        Long userId = getUserId(principal);

        return moodService.getMoodByDate(userId, LocalDate.parse(date))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ===== DELETE TODAY =====

    @DeleteMapping("/{date}")
    public ResponseEntity<?> deleteMood(
            @PathVariable String date,
            Principal principal
    ) {

        try {
            Long userId = getUserId(principal);

            moodService.deleteMood(userId, LocalDate.parse(date));

            return ResponseEntity.noContent().build();

        } catch (DateNotAllowedException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}
