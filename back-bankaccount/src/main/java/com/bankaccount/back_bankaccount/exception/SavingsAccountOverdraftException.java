package com.bankaccount.back_bankaccount.exception;

import com.bankaccount.back_bankaccount.constants.BankAccountConstants;

public class SavingsAccountOverdraftException extends RuntimeException {
    
    public SavingsAccountOverdraftException() {
        super(BankAccountConstants.SAVINGS_OVERDRAFT_ERROR);
    }
    
    public SavingsAccountOverdraftException(String message) {
        super(message);
    }
    
    public SavingsAccountOverdraftException(String message, Throwable cause) {
        super(message, cause);
    }
}
