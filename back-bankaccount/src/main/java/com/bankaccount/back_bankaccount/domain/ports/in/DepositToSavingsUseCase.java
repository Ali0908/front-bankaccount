package com.bankaccount.back_bankaccount.domain.ports.in;

import com.bankaccount.back_bankaccount.domain.model.BankAccount;

/**
 * Input port for depositing money to savings account.
 * This is a use case interface (primary port).
 */
public interface DepositToSavingsUseCase {
    BankAccount depositToSavings(String accountNumber, Double amount);
}
