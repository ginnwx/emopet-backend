package com.emopet.emopet_backend.service;

import com.emopet.emopet_backend.model.Pet;
import com.emopet.emopet_backend.model.User;
import com.emopet.emopet_backend.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    public Optional<Pet> getPetByUser(User user) {
        return petRepository.findByUser(user);
    }

    public Optional<Pet> getPetByUserId(Long userId) {
        return petRepository.findByUserId(userId);
    }

    public Pet save(Pet pet) {
        return petRepository.save(pet);
    }

    // Метод для выполнения действия (например, покормить)
    public Pet feedPet(Pet pet) {
        pet.setHunger(Math.min(pet.getHunger() + 30, 100));
        return petRepository.save(pet);
    }
    public Pet putPetToSleep(Pet pet) {

        // Уже спит — ничего не делаем
        if (pet.getSleepUntil() != null &&
                pet.getSleepUntil().isAfter(LocalDateTime.now())) {
            return pet;
        }

        pet.setSleepUntil(LocalDateTime.now().plusHours(9));
        pet.setState("cat_sleep"); // если есть отдельная анимация сна

        return petRepository.save(pet);
    }
    public boolean isPetSleeping(Pet pet) {
        return pet.getSleepUntil() != null &&
                pet.getSleepUntil().isAfter(LocalDateTime.now());
    }

    // Аналогично play, drink, sleep фи т.д.
}