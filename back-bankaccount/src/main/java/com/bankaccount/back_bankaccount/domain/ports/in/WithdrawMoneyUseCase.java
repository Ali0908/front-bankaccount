package com.bankaccount.back_bankaccount.domain.ports.in;

import com.bankaccount.back_bankaccount.domain.model.BankAccount;

/**
 * Input port for withdrawing money from current account.
 * This is a use case interface (primary port).
 */
public interface WithdrawMoneyUseCase {
    BankAccount withdraw(String accountNumber, Double amount);
}
