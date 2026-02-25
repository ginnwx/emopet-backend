package com.emopet.emopet_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO для ошибок API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDto {

    /**
     * Код ошибки (например, DATE_NOT_ALLOWED, VALIDATION_ERROR)
     */
    private String error;

    /**
     * Человекочитаемое сообщение
     */
    private String message;

    /**
     * Время ошибки
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * HTTP статус
     */
    private Integer status;

    /**
     * Путь запроса
     */
    private String path;

    /**
     * Дополнительные детали (опционально)
     */
    private Object details;

    /**
     * Конструктор для быстрого создания простой ошибки
     */
    public static ErrorResponseDto of(String error, String message) {
        return ErrorResponseDto.builder()
                .error(error)
                .message(message)
                .build();
    }

    /**
     * Конструктор с HTTP статусом
     */
    public static ErrorResponseDto of(String error, String message, int status) {
        return ErrorResponseDto.builder()
                .error(error)
                .message(message)
                .status(status)
                .build();
    }
}