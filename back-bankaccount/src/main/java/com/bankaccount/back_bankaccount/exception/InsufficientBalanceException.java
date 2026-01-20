package com.bankaccount.back_bankaccount.exception;

/**
 * Exception thrown when a withdrawal cannot be performed due to insufficient balance
 */
public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(Double availableBalance, Double requestedAmount) {
        super(String.format("Insufficient balance. Available: %.2f, Requested: %.2f", 
            availableBalance, requestedAmount));
    }

    public InsufficientBalanceException(Double availableBalance, Double requestedAmount, Throwable cause) {
        super(String.format("Insufficient balance. Available: %.2f, Requested: %.2f", 
            availableBalance, requestedAmount), cause);
    }
}
