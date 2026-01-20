package com.bankaccount.back_bankaccount.exception;

public class SavingsAccountOverdraftException extends RuntimeException {
    
    private static final String DEFAULT_MESSAGE = "Savings accounts cannot have overdraft";
    
    public SavingsAccountOverdraftException() {
        super(DEFAULT_MESSAGE);
    }
    
    public SavingsAccountOverdraftException(String message) {
        super(message);
    }
    
    public SavingsAccountOverdraftException(String message, Throwable cause) {
        super(message, cause);
    }
}
