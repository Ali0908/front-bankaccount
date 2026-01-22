package com.bankaccount.back_bankaccount.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BankAccountConstants {
    
    public static final String SLASH = "/";

    public static final String DEPOSIT_CURRENT_LABEL = "Dépôt sur compte courant";
    public static final String WITHDRAWAL_LABEL = "Retrait";
    public static final String DEPOSIT_SAVINGS_LABEL = "Dépôt sur livret d'épargne";

    public static final String ACCOUNT_NUMBER = "accountNumber";

    // Error messages
    public static final String INSUFFICIENT_BALANCE_ERROR = "Insufficient balance for withdrawal";
    public static final String OVERDRAFT_LIMIT_INVALID_ERROR = "Overdraft limit must be between 0 and 300";
    public static final String SAVINGS_OVERDRAFT_ERROR = "Savings accounts cannot have overdraft";
    public static final String SAVINGS_AT_CAPACITY_ERROR = "Savings account is at maximum capacity";

    // Account types
    public static final String ACCOUNT_TYPE_SAVINGS_AND_CURRENT = "Compte Courant + Livret d'épargne";
    public static final String ACCOUNT_TYPE_SAVINGS = "Livret d'épargne";
    public static final String ACCOUNT_TYPE_CURRENT = "Compte Courant";

    // Validation messages
    public static final String ACCOUNT_NUMBER_REQUIRED_MESSAGE = "Account number is required";
    public static final String AMOUNT_REQUIRED_MESSAGE = "Amount is required";
    public static final String AMOUNT_POSITIVE_MESSAGE = "Amount must be positive";

    // Exception messages
    public static final String ACCOUNT_NOT_FOUND_MESSAGE = "Account not found: ";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient balance. Available: %.2f, Requested: %.2f";

    // Error codes
    public static final String ERROR_CODE_INSUFFICIENT_BALANCE = "INSUFFICIENT_BALANCE";
    public static final String ERROR_CODE_ACCOUNT_NOT_FOUND = "ACCOUNT_NOT_FOUND";
    public static final String ERROR_CODE_INTERNAL_ERROR = "INTERNAL_ERROR";

    // Error messages for responses
    public static final String UNEXPECTED_ERROR_MESSAGE = "Une erreur inattendue s'est produite";
}