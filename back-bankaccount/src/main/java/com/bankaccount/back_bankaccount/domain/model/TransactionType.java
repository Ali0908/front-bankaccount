package com.bankaccount.back_bankaccount.domain.model;

import lombok.Getter;

/**
 * Domain enum for transaction types.
 * Contains business logic for transaction type labels.
 */
@Getter
public enum TransactionType {
    DEPOSIT_CURRENT("Dépôt compte courant"),
    WITHDRAWAL("Retrait"),
    DEPOSIT_SAVINGS("Dépôt livret d'épargne");

    private final String label;

    TransactionType(String label) {
        this.label = label;
    }
}
