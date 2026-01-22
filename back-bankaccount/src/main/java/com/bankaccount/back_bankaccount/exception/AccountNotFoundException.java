package com.bankaccount.back_bankaccount.exception;

import com.bankaccount.back_bankaccount.constants.BankAccountConstants;

/**
 * Exception thrown when a bank account is not found
 */
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String accountNumber) {
        super(BankAccountConstants.ACCOUNT_NOT_FOUND_MESSAGE + accountNumber);
    }

    public AccountNotFoundException(String accountNumber, Throwable cause) {
        super(BankAccountConstants.ACCOUNT_NOT_FOUND_MESSAGE + accountNumber, cause);
    }
}
