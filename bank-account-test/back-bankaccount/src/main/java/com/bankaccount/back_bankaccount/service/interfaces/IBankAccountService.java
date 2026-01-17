package com.bankaccount.back_bankaccount.service.interfaces;

import com.bankaccount.back_bankaccount.dto.BankAccountDto;
import java.util.List;

public interface IBankAccountService {

    /**
     * Retrieves all bank accounts.
     * @return A list of  {@link BankAccountDto} representing all bank accounts.
     */

    public List<BankAccountDto> getAllBankAccounts();

    /**
     * Deposits a specified amount into a bank account identified by its account number.
     * @param accountNumber The account number of the bank account.
     * @param amount The amount to deposit.
     * @return The updated {@link BankAccountDto} after the deposit.
     */
    BankAccountDto deposit(String accountNumber, Double amount);

    /**
     * Withdraws a specified amount from a bank account identified by its account number.
     * A withdrawal cannot be performed if the amount exceeds the available balance.
     * @param accountNumber The account number of the bank account.
     * @param amount The amount to withdraw.
     * @return The updated {@link BankAccountDto} after the withdrawal.
     * @throws AccountNotFoundException If the account is not found.
     * @throws com.bankaccount.back_bankaccount.exception.InsufficientBalanceException If the withdrawal amount exceeds the available balance.
     */
    BankAccountDto withdraw(String accountNumber, Double amount);

}
