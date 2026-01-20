package com.bankaccount.back_bankaccount.domain.ports.in;

import com.bankaccount.back_bankaccount.domain.model.BankAccount;

/**
 * Input port for setting overdraft limit.
 * This is a use case interface (primary port).
 */
public interface SetOverdraftLimitUseCase {
    BankAccount setOverdraftLimit(String accountNumber, Double overdraftLimit);
}
