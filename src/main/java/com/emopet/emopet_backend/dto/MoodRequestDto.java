package com.emopet.emopet_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO для создания/обновления записи настроения
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoodRequestDto {

    /**
     * Дата настроения (формат: YYYY-MM-DD)
     */
    @NotNull(message = "Дата обязательна")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    /**
     * Значение настроения: 1-5
     */
    @NotNull(message = "Значение настроения обязательно")
    @Min(value = 1, message = "Минимальное значение: 1")
    @Max(value = 5, message = "Максимальное значение: 5")
    private Integer moodValue;

    /**
     * Заметка (опционально)
     */
    @Size(max = 5000, message = "Заметка не может быть длиннее 5000 символов")
    private String note;
}