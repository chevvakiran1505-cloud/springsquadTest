package com.example.bankserver.exception;

public class PaymentProcessorException extends RuntimeException {

    private final String code;

    public PaymentProcessorException(String message) {
        super(message);
        this.code = "PAYMENT_PROCESSOR_ERROR";
    }

    public PaymentProcessorException(String message, Throwable cause) {
        super(message, cause);
        this.code = "PAYMENT_PROCESSOR_ERROR";
    }

    public String getCode() {
        return code;
    }
}