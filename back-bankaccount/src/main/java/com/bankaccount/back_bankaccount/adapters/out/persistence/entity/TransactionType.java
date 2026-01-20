package com.bankaccount.back_bankaccount.adapters.out.persistence.entity;

import lombok.Getter;

/**
 * JPA enum for transaction types.
 * This is part of the infrastructure layer.
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
