package com.emopet.emopet_backend.exceptions;

/**
 * Исключение для случаев, когда дата не разрешена для редактирования
 */
public class DateNotAllowedException extends RuntimeException {

    private final String errorCode;

    public DateNotAllowedException(String message) {
        super(message);
        this.errorCode = "DATE_NOT_ALLOWED";
    }

    public DateNotAllowedException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}