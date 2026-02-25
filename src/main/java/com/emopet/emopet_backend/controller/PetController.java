package com.emopet.emopet_backend.controller;

import com.emopet.emopet_backend.model.*;
import com.emopet.emopet_backend.repository.*;
import com.emopet.emopet_backend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/pet")
public class PetController {

    @Autowired private UserRepository userRepository;
    @Autowired private PetRepository petRepository;
    @Autowired private CostumeRepository costumeRepository;
    @Autowired private UserCostumeRepository userCostumeRepository;
    @Autowired private JwtUtils jwtUtils;

    @Scheduled(fixedRate = 300000) // 5 минут
    public void decreasePetStats() {
        try {
            System.out.println("🔄 Автоматическое уменьшение показателей питомцев");

            List<Pet> allPets = petRepository.findAll();
            int updatedCount = 0;

            for (Pet pet : allPets) {

                if (pet.getSleepUntil() != null &&
                        pet.getSleepUntil().isAfter(LocalDateTime.now())) {
                    continue;
                }

                int newHunger = Math.max(0, pet.getHunger() - 10);
                int newFun = Math.max(0, pet.getFun() - 10);
                int newHealth = Math.max(0, pet.getHealth() - 5);

                pet.setHunger(newHunger);
                pet.setFun(newFun);
                pet.setHealth(newHealth);

                if (newHunger <= 10) {
                    pet.setState("cat_hungry");
                } else if (newFun <= 10) {
                    pet.setState("cat_sad");
                } else if (newHealth <= 20) {
                    pet.setState("cat_sad");
                }

                petRepository.save(pet);
                updatedCount++;
            }

            System.out.println("✅ Показатели обновлены для " + updatedCount + " питомцев");

        } catch (Exception e) {
            System.out.println("❌ Ошибка при автоматическом обновлении показателей: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 🎯 Получить все данные питомца (состояние + костюмы) - ЕДИНСТВЕННЫЙ МЕТОД!
    @GetMapping
    public ResponseEntity<?> getPetData(HttpServletRequest request) {
        try {
            System.out.println("🔐 [GET /api/pet] Начало запроса");

            User user = getUserFromToken(request);
            if (user == null) {
                System.out.println("❌ [GET /api/pet] User not found from token");
                return ResponseEntity.badRequest().body("User not found");
            }

            System.out.println("✅ [GET /api/pet] User найден: " + user.getEmail());

            // ✅ АВТОМАТИЧЕСКОЕ СОЗДАНИЕ ПИТОМЦА ЕСЛИ ЕГО НЕТ
            Optional<Pet> petOpt = petRepository.findByUser(user);
            Pet pet;

            boolean sleeping = false;

            if (petOpt.isEmpty()) {
                System.out.println("🎯 [GET /api/pet] Создаем нового питомца для пользователя: " + user.getEmail());

                pet = new Pet();
                pet.setUser(user);
                pet.setName("Котик");
                pet.setHunger(80);
                pet.setHealth(90);
                pet.setFun(70);
                pet.setState("cat_default");

                pet = petRepository.save(pet);

                System.out.println("✅ [GET /api/pet] Новый питомец создан: " + pet.getName());

            } else {
                pet = petOpt.get();

                sleeping = pet.getSleepUntil() != null &&
                        pet.getSleepUntil().isAfter(LocalDateTime.now());
// ⏰ Если время сна прошло — будим кота
                if (pet.getSleepUntil() != null &&
                        pet.getSleepUntil().isBefore(LocalDateTime.now())) {

                    pet.setSleepUntil(null);
                    pet.setState("cat_default");
                    petRepository.save(pet);
                }

                System.out.println("✅ [GET /api/pet] Pet найден: " + pet.getName());
            }


            List<UserCostume> userCostumes = userCostumeRepository.findByUser(user);
            System.out.println("🎭 [GET /api/pet] Костюмов у пользователя: " + userCostumes.size());

            Map<String, Object> response = new HashMap<>();
            response.put("hunger", pet.getHunger());
            response.put("health", pet.getHealth());
            response.put("fun", pet.getFun());
            response.put("sleeping", sleeping);
            response.put("sleepUntil",
                    pet.getSleepUntil() != null
                            ? pet.getSleepUntil().toString() + "Z"
                            : null
            );
            response.put("coins", user.getCoins());
            response.put("petState", pet.getState());
            response.put("petName", pet.getName());
            // ✅ USER (чтобы Flutter всегда видел имя)
            response.put("userId", user.getId());
            response.put("userEmail", user.getEmail());

// важно: в твоём Flutter парсере было userName, поэтому кладём и его,
// + кладём username для новых форматов (на будущее)
            response.put("userName", user.getDisplayName());
            response.put("username", user.getDisplayName());


            // Активный костюм
            if (pet.getEquippedCostume() != null) {
                System.out.println("👑 [GET /api/pet] Активный костюм: " + pet.getEquippedCostume().getName());
                Map<String, Object> equippedCostumeMap = new HashMap<>();
                equippedCostumeMap.put("id", pet.getEquippedCostume().getId());
                equippedCostumeMap.put("name", pet.getEquippedCostume().getName());
                equippedCostumeMap.put("imageSuffix", pet.getEquippedCostume().getImageSuffix());
                response.put("equippedCostume", equippedCostumeMap);
            } else {
                System.out.println("👑 [GET /api/pet] Нет активного костюма");
                response.put("equippedCostume", null);
            }

            // Список всех костюмов
            List<Costume> allAvailableCostumes = costumeRepository.findAll();
            System.out.println("🛍️ [GET /api/pet] Всего костюмов в магазине: " + allAvailableCostumes.size());

            Pet finalPet = pet;
            List<Map<String, Object>> allCostumes = allAvailableCostumes.stream()
                    .map(costume -> {
                        boolean owned = userCostumes.stream()
                                .anyMatch(uc -> uc.getCostume().getId().equals(costume.getId()));
                        boolean equipped = finalPet.getEquippedCostume() != null &&
                                finalPet.getEquippedCostume().getId().equals(costume.getId());

                        Map<String, Object> costumeMap = new HashMap<>();
                        costumeMap.put("id", costume.getId());
                        costumeMap.put("name", costume.getName());
                        costumeMap.put("price", costume.getPrice());
                        costumeMap.put("imageSuffix", costume.getImageSuffix());
                        costumeMap.put("owned", owned);
                        costumeMap.put("equipped", equipped);
                        return costumeMap;
                    })
                    .collect(Collectors.toList());

            response.put("costumes", allCostumes);

            System.out.println("✅ [GET /api/pet] Успешный ответ: " + response.keySet());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("💥 [GET /api/pet] Ошибка: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // 🎯 Выполнить действие с питомцем
    @PostMapping("/action")
    public ResponseEntity<?> performAction(
            HttpServletRequest request,
            @RequestBody Map<String, String> actionRequest) {

        try {
            System.out.println("🎮 [POST /api/pet/action] Начало запроса: " + actionRequest);

            User user = getUserFromToken(request);
            if (user == null) {
                System.out.println("❌ [POST /api/pet/action] User not found from token");
                return ResponseEntity.badRequest().body("User not found");
            }

            Pet pet = petRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Pet not found"));

//            // 🔒 Блок действий если кот спит
//            if (pet.getSleepUntil() != null &&
//                    pet.getSleepUntil().isAfter(LocalDateTime.now())) {
//
//                String action = actionRequest.get("action");
//
//                // Разрешаем только повторный sleep (если вдруг нажали)
//                if (!"sleep".equals(action)) {
//                    return ResponseEntity.badRequest().body(
//                            Map.of(
//                                    "success", false,
//                                    "message", "Кот спит 😴"
//                            )
//                    );
//                }
//            }
            if (pet.getSleepUntil() != null && pet.getSleepUntil().isAfter(LocalDateTime.now())) {
                String action = actionRequest.get("action");

                // Разрешаем sleep и wake, остальное блокируем
                if (!"sleep".equals(action) && !"wake".equals(action)) {
                    return ResponseEntity.badRequest().body(
                            Map.of("success", false, "message", "Кот спит 😴")
                    );
                }
            }



            String action = actionRequest.get("action");
            String message = "";

            System.out.println("🎯 [POST /api/pet/action] Действие: " + action);

            switch (action) {
                case "feed":
                    pet.setHunger(Math.min(100, pet.getHunger() + 40));
                    pet.setState("cat_eating");
                    message = "Покормлен!";
                    break;
                case "play":
                    pet.setFun(Math.min(100, pet.getFun() + 40));
                    pet.setState("cat_playing");
                    message = "Поиграли!";
                    break;
                case "pet":
                    pet.setHealth(Math.min(100, pet.getHealth() + 25));
                    pet.setFun(Math.min(100, pet.getFun() + 15));
                    pet.setState("cat_petting");
                    message = "Поглажен!";
                    break;
                case "drink":
                    pet.setHealth(Math.min(100, pet.getHealth() + 20));
                    pet.setState("cat_drinking");
                    message = "Напоен!";
                    break;
                case "sleep":
                    if (pet.getSleepUntil() != null &&
                            pet.getSleepUntil().isAfter(LocalDateTime.now())) {

                        return ResponseEntity.badRequest().body("Pet already sleeping");
                    }
                    pet.setSleepUntil(LocalDateTime.now(ZoneOffset.UTC).plusHours(9));
                    pet.setState("cat_sleeping");
                    message = "Котик уснул на 9 часов";
                    break;
                case "wake":
                    pet.setSleepUntil(null);
                    pet.setState("cat_default");
                    message = "Котик проснулся!";
                    break;

                default:
                    System.out.println("❌ [POST /api/pet/action] Неизвестное действие: " + action);
                    return ResponseEntity.badRequest().body("Unknown action: " + action);
            }

            // 🎯 ПОСЛЕ ДЕЙСТВИЯ ПРОВЕРЯЕМ ТОЛЬКО КРИТИЧЕСКИЕ СОСТОЯНИЯ
            if (pet.getHunger() <= 10) {
                pet.setState("cat_hungry");
            } else if (pet.getFun() <= 10) {
                pet.setState("cat_sad");
            } else if (pet.getHealth() <= 20) {
                pet.setState("cat_sad");
            }

            petRepository.save(pet);
            System.out.println("✅ [POST /api/pet/action] Действие выполнено: " + message);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", message);
            response.put("newHunger", pet.getHunger());
            response.put("newHealth", pet.getHealth());
            response.put("newFun", pet.getFun());
            response.put("newPetState", pet.getState());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("💥 [POST /api/pet/action] Ошибка: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // 🎯 Купить костюм
    @PostMapping("/costumes/buy")
    @Transactional
    public ResponseEntity<?> buyCostume(
            HttpServletRequest request,
            @RequestBody Map<String, Long> requestBody) {

        try {
            System.out.println("🛍️ [POST /api/pet/costumes/buy] Начало запроса: " + requestBody);

            User user = getUserFromToken(request);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }

            Long costumeId = requestBody.get("costumeId");
            Optional<Costume> costumeOpt = costumeRepository.findById(costumeId);

            if (costumeOpt.isEmpty()) {
                System.out.println("❌ [POST /api/pet/costumes/buy] Костюм не найден: " + costumeId);
                return ResponseEntity.badRequest().body("Costume not found");
            }

            Costume costume = costumeOpt.get();
            System.out.println("🎯 [POST /api/pet/costumes/buy] Покупка костюма: " + costume.getName());

            // ✅ ИСПОЛЬЗУЕМ ПРАВИЛЬНЫЙ МЕТОД
            Optional<UserCostume> existingCostume = userCostumeRepository.findByUserAndCostume_Id(user, costumeId);

            if (existingCostume.isPresent()) {
                System.out.println("❌ [POST /api/pet/costumes/buy] Костюм уже куплен: " + costume.getName());
                return ResponseEntity.badRequest().body("Costume already owned");
            }

            if (user.getCoins() < costume.getPrice()) {
                System.out.println("❌ [POST /api/pet/costumes/buy] Недостаточно монет. Нужно: " + costume.getPrice() + ", есть: " + user.getCoins());
                return ResponseEntity.badRequest().body("Not enough coins");
            }

            // Обновляем баланс
            user.setCoins(user.getCoins() - costume.getPrice());
            userRepository.save(user);

            // Создаем запись о покупке
            UserCostume userCostume = new UserCostume();
            userCostume.setUser(user);
            userCostume.setCostume(costume);
            userCostume.setEquipped(false);
            userCostume.setPurchasedAt(LocalDateTime.now());

            userCostumeRepository.save(userCostume);

            System.out.println("✅ [POST /api/pet/costumes/buy] Костюм куплен: " + costume.getName() + ", новый баланс: " + user.getCoins());

            Map<String, Object> costumeResponse = new HashMap<>();
            costumeResponse.put("id", costume.getId());
            costumeResponse.put("name", costume.getName());
            costumeResponse.put("imageSuffix", costume.getImageSuffix());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Костюм куплен: " + costume.getName());
            response.put("newCoins", user.getCoins());
            response.put("costume", costumeResponse);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("💥 [POST /api/pet/costumes/buy] Ошибка: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // 🎯 Надеть костюм
    @PostMapping("/costumes/equip")
    public ResponseEntity<?> equipCostume(
            HttpServletRequest request,
            @RequestBody Map<String, Long> requestBody) {

        try {
            System.out.println("👑 [POST /api/pet/costumes/equip] Начало запроса: " + requestBody);

            User user = getUserFromToken(request);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }

            Pet pet = petRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Pet not found"));

            Long costumeId = requestBody.get("costumeId");

            Optional<UserCostume> userCostumeOpt = userCostumeRepository.findByUserAndCostumeId(user, costumeId);
            if (userCostumeOpt.isEmpty()) {
                System.out.println("❌ [POST /api/pet/costumes/equip] Костюм не принадлежит пользователю: " + costumeId);
                return ResponseEntity.badRequest().body("Costume not owned");
            }

            UserCostume userCostume = userCostumeOpt.get();
            Costume costume = userCostume.getCostume();

            pet.setEquippedCostume(costume);
            petRepository.save(pet);

            System.out.println("✅ [POST /api/pet/costumes/equip] Костюм надет: " + costume.getName());

            Map<String, Object> equippedCostumeResponse = new HashMap<>();
            equippedCostumeResponse.put("id", costume.getId());
            equippedCostumeResponse.put("name", costume.getName());
            equippedCostumeResponse.put("imageSuffix", costume.getImageSuffix());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Надет костюм: " + costume.getName());
            response.put("equippedCostume", equippedCostumeResponse);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("💥 [POST /api/pet/costumes/equip] Ошибка: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    @PostMapping("/costumes/unequip")
    public ResponseEntity<?> unequipCostume(HttpServletRequest request) {
        try {
            User user = getUserFromToken(request);
            Pet pet = petRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Pet not found"));

            pet.setEquippedCostume(null);
            petRepository.save(pet);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Костюм снят"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
    private User getUserFromToken(HttpServletRequest request) {
        String token = extractToken(request);
        System.out.println("🔐 [getUserFromToken] Token получен: " + (token != null ? token.substring(0, 20) + "..." : "null"));

        if (token != null && jwtUtils.validateJwtToken(token)) {
            String email = jwtUtils.getUserNameFromJwtToken(token);
            System.out.println("📧 [getUserFromToken] Email из токена: " + email);

            User user = userRepository.findByEmail(email).orElse(null);
            System.out.println("👤 [getUserFromToken] User найден: " + (user != null ? user.getEmail() : "null"));
            return user;
        } else {
            System.out.println("❌ [getUserFromToken] Token невалиден или null");
        }
        return null;
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        System.out.println("📨 [extractToken] Authorization header: " + header);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            System.out.println("✅ [extractToken] Token извлечен: " + token.substring(0, Math.min(20, token.length())) + "...");
            return token;
        }
        System.out.println("❌ [extractToken] Нет Bearer token в header");
        return null;
    }
}