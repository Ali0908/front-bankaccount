package com.bankaccount.back_bankaccount.controller.paths;

import com.bankaccount.constants.BankAccountConstants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ResourcePath {

    public static final String PATH_BANK_ACCOUNT = BankAccountConstants.SLASH + "bank-accounts";

    public static final String PATH_CASH_DEPOSIT = BankAccountConstants.SLASH + "cash-deposit";

    public static final String PATH_CASH_WITHDRAWAL = BankAccountConstants.SLASH + "cash-withdrawal";


    
}
