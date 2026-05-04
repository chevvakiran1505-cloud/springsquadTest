package com.example.demobanking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

// @RestControllerAdvice is a composed annotation: @ControllerAdvice + @ResponseBody.
// Every method in this class writes directly to the response body as JSON.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // TODO 15: Handle MethodArgumentNotValidException.
    // This exception is thrown when @Valid fails on a @RequestBody parameter.
    // Steps:
    //   1. Extract all field errors from ex.getBindingResult().getFieldErrors()
    //   2. Map each to an ErrorResponse.FieldError using field.getField() and
    //      field.getDefaultMessage()
    //   3. Build and return a ResponseEntity with status 400 and an ErrorResponse
    //      whose message is "Validation failed" and whose errors list contains the
    //      field errors from step 2.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(f -> new ErrorResponse.FieldError(f.getField(), f.getDefaultMessage()))
                .toList();

        ErrorResponse body = new ErrorResponse(
                "VALIDATION_ERROR",
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                fieldErrors,
                Instant.now()
        );

        return ResponseEntity.badRequest().body(body);
    }

    // TODO 16: Handle AccessDeniedException.
    // This exception is thrown by Spring Security when @PreAuthorize denies access.
    // Return 403 Forbidden with status "ACCESS_DENIED" and message
    // "You do not have permission to perform this action."
    // Use ErrorResponse.of() for the body.
    //
    // IMPORTANT: For this handler to intercept AccessDeniedException, you must
    // configure Spring Security not to handle it first. You will do this in Task 4.3.

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of("ACCESS_DENIED", 403,
                        "You do not have permission to perform this action."));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // Log the full exception internally so it is available in application logs
        // and monitoring tools, but never expose it to the caller.
        System.err.println("Unhandled exception: " + ex.getMessage());
      //  ex.printStackTrace();

        return ResponseEntity.internalServerError()
                .body(ErrorResponse.of("INTERNAL_ERROR", 500,
                        "An unexpected error occurred."));
    }
}
