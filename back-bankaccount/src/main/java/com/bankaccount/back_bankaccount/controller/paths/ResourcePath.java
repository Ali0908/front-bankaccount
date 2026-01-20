package com.bankaccount.back_bankaccount.controller.paths;

import com.bankaccount.back_bankaccount.constants.BankAccountConstants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ResourcePath {


    public static final String PATH_BANK_ACCOUNT = BankAccountConstants.SLASH + "bank-accounts";

    public static final String PATH_CASH_DEPOSIT = BankAccountConstants.SLASH + "cash-deposit";

    public static final String PATH_CASH_WITHDRAWAL = BankAccountConstants.SLASH + "cash-withdrawal";

    public static final String PATH_OVERDRAFT = BankAccountConstants.SLASH + "overdraft";

    public static final String PATH_SAVINGS_DEPOSIT = BankAccountConstants.SLASH + "savings-deposit";

    public static final String PATH_STATEMENT = BankAccountConstants.SLASH + "statement";

}