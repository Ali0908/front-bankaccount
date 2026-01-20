package com.bankaccount.back_bankaccount.domain.ports.in;

import com.bankaccount.back_bankaccount.domain.model.BankAccount;

import java.util.List;

/**
 * Input port for retrieving all bank accounts.
 * This is a use case interface (primary port).
 */
public interface GetAllAccountsUseCase {
    List<BankAccount> getAllAccounts();
}
