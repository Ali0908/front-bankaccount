package com.bankaccount.back_bankaccount.exception;

import com.bankaccount.back_bankaccount.constants.BankAccountConstants;

/**
 * Exception thrown when a withdrawal cannot be performed due to insufficient balance
 */
public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(Double availableBalance, Double requestedAmount) {
        super(String.format(BankAccountConstants.INSUFFICIENT_BALANCE_MESSAGE, 
            availableBalance, requestedAmount));
    }

    public InsufficientBalanceException(Double availableBalance, Double requestedAmount, Throwable cause) {
        super(String.format(BankAccountConstants.INSUFFICIENT_BALANCE_MESSAGE, 
            availableBalance, requestedAmount), cause);
    }
}
