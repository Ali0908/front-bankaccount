package com.bankaccount.back_bankaccount.exception;

/**
 * Exception thrown when a bank account is not found
 */
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String accountNumber) {
        super("Account not found: " + accountNumber);
    }

    public AccountNotFoundException(String accountNumber, Throwable cause) {
        super("Account not found: " + accountNumber, cause);
    }
}
