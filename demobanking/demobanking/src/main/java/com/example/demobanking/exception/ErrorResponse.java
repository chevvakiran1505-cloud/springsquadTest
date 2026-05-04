package com.example.demobanking.exception;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        String status,
        int code,
        String message,
        List<FieldError> errors,
        Instant timestamp
) {
    public record FieldError(String field, String message) {}

    // Convenience factory for single-message errors with no field list.
    public static ErrorResponse of(String status, int code, String message) {
        return new ErrorResponse(status, code, message, List.of(), Instant.now());
    }
}