package com.bankaccount.back_bankaccount.model;

public enum TransactionType {
    DEPOSIT_CURRENT("Dépôt sur compte courant"),
    DEPOSIT_SAVINGS("Dépôt sur livret d'épargne"),
    WITHDRAWAL("Retrait");

    private final String label;

    TransactionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}