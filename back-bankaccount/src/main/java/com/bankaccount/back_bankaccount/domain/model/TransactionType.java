package com.bankaccount.back_bankaccount.domain.model;

import com.bankaccount.back_bankaccount.constants.BankAccountConstants;
import lombok.Getter;

/**
 * Domain enum for transaction types.
 * Contains business logic for transaction type labels.
 */
@Getter
public enum TransactionType {
    DEPOSIT_CURRENT(BankAccountConstants.DEPOSIT_CURRENT_LABEL),
    WITHDRAWAL(BankAccountConstants.WITHDRAWAL_LABEL),
    DEPOSIT_SAVINGS(BankAccountConstants.DEPOSIT_SAVINGS_LABEL);

    private final String label;

    TransactionType(String label) {
        this.label = label;
    }
}
