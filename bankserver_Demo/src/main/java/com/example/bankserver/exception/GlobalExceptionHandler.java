package com.example.bankserver.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // ========= 400: VALIDATION_FAILED =========

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest req
    ) {
        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> Map.of(
                        "field", err.getField(),
                        "message", err.getDefaultMessage()
                ))
                .toList();

        return problem(
                "Validation failed",
                400,
                "VALIDATION_FAILED",
                "Request validation failed",
                req.getRequestURI(),
                errors
        );
    }

    // ========= 400: MALFORMED_JSON =========

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleMalformedJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest req
    ) {
        return problem(
                "Malformed JSON",
                400,
                "MALFORMED_JSON",
                "Request body is not valid JSON",
                req.getRequestURI(),
                null
        );
    }

    // ========= 403: FORBIDDEN =========

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public Map<String, Object> handleForbidden(
            AccessDeniedException ex,
            HttpServletRequest req
    ) {
        return problem(
                "Forbidden",
                403,
                "FORBIDDEN",
                "You do not have permission to access this resource",
                req.getRequestURI(),
                null
        );
    }

    // ========= 401: UNAUTHORIZED =========

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public Map<String, Object> handleUnauthorized(
            AuthenticationException ex,
            HttpServletRequest req
    ) {
        return problem(
                "Unauthorized",
                401,
                "UNAUTHORIZED",
                "Authentication required",
                req.getRequestURI(),
                null
        );
    }

    // ========= 404: NOT_FOUND =========

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest req
    ) {
        return problem(
                "Not found",
                404,
                "NOT_FOUND",
                "Resource not found",
                req.getRequestURI(),
                null
        );
    }

    // ========= 422: INSUFFICIENT_FUNDS =========

    @ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public Map<String, Object> handleInsufficientFunds(
            InsufficientFundsException ex,
            HttpServletRequest req
    ) {
        String detail = String.format(
                "Account %s has balance %.2f, transaction would make it %.2f.",
                ex.getAccountId(),
                ex.getBalance(),
                ex.getBalance().subtract(ex.getAmount())
        );

        return problem(
                "Insufficient funds",
                422,
                "INSUFFICIENT_FUNDS",
                detail,
                req.getRequestURI(),
                List.of(Map.of(
                        "field", "amount",
                        "message", "must be ≤ available balance"
                ))
        );
    }

    // ========= 422: BUSINESS_RULE_VIOLATION =========

    @ExceptionHandler(BusinessRuleException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public Map<String, Object> handleBusinessRule(
            BusinessRuleException ex,
            HttpServletRequest req
    ) {
        return problem(
                "Business rule violation",
                422,
                ex.getCode(),
                ex.getMessage(),
                req.getRequestURI(),
                null
        );
    }

    // ========= 502: PAYMENT_PROCESSOR_ERROR =========

    @ExceptionHandler(PaymentProcessorException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ResponseBody
    public Map<String, Object> handlePaymentProcessor(
            PaymentProcessorException ex,
            HttpServletRequest req
    ) {
        return problem(
                "Payment processor error",
                502,
                ex.getCode(),
                "External payment failed",
                req.getRequestURI(),
                null
        );
    }

    // ========= 500: INTERNAL_ERROR =========

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, Object> handleGeneric(
            Exception ex,
            HttpServletRequest req
    ) {
        return problem(
                "Internal server error",
                500,
                "INTERNAL_ERROR",
                "An unexpected error occurred",
                req.getRequestURI(),
                null
        );
    }

    // ========= HELPER =========

    private Map<String, Object> problem(
            String title,
            int status,
            String code,
            String detail,
            String instance,
            List<Map<String, String>> errors
    ) {
        return Map.of(
                "type", "about:blank",
                "title", title,
                "status", status,
                "code", code,
                "detail", detail,
                "instance", instance,
                "timestamp", Instant.now().toString(),
                "errors", errors == null ? List.of() : errors
        );
    }
}