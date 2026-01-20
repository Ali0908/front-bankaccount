package com.bankaccount.back_bankaccount.domain.ports.in;

import com.bankaccount.back_bankaccount.domain.model.BankAccount;

/**
 * Input port for depositing money to current account.
 * This is a use case interface (primary port).
 */
public interface DepositMoneyUseCase {
    BankAccount deposit(String accountNumber, Double amount);
}
