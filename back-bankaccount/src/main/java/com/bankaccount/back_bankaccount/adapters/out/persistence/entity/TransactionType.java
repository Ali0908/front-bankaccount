package com.bankaccount.back_bankaccount.adapters.out.persistence.entity;

import com.bankaccount.back_bankaccount.constants.BankAccountConstants;
import lombok.Getter;

/**
 * JPA enum for transaction types.
 * This is part of the infrastructure layer.
 */
@Getter
public enum TransactionType {
    DEPOSIT_CURRENT(BankAccountConstants.DEPOSIT_CURRENT_LABEL),
    WITHDRAWAL("Retrait"),
    DEPOSIT_SAVINGS(BankAccountConstants.DEPOSIT_SAVINGS_LABEL);

    private final String label;

    TransactionType(String label) {
        this.label = label;
    }
}
