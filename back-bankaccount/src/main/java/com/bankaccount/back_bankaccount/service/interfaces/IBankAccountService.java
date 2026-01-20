package com.bankaccount.back_bankaccount.service.interfaces;

import com.bankaccount.back_bankaccount.dto.BankAccountDto;
import com.bankaccount.back_bankaccount.dto.StatementDto;
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
     * A withdrawal cannot be performed if the final balance exceeds the overdraft limit.
     * @param accountNumber The account number of the bank account.
     * @param amount The amount to withdraw.
     * @return The updated {@link BankAccountDto} after the withdrawal.
     * @throws AccountNotFoundException If the account is not found.
     * @throws com.bankaccount.back_bankaccount.exception.InsufficientBalanceException If the withdrawal amount exceeds the available balance with overdraft.
     */
    BankAccountDto withdraw(String accountNumber, Double amount);

    /**
     * Sets the overdraft limit for a bank account (max 300€).
     * @param accountNumber The account number of the bank account.
     * @param overdraftLimit The overdraft limit (0-300).
     * @return The updated {@link BankAccountDto} with the new overdraft limit.
     * @throws AccountNotFoundException If the account is not found.
     * @throws IllegalArgumentException If overdraft limit is not between 0 and 300.
     */
    BankAccountDto setOverdraftLimit(String accountNumber, Double overdraftLimit);

    /**
     * Deposits money to the savings account of a bank account.
     * The deposit will be partial if it exceeds the savings limit (22950€ - Livret A).
     * @param accountNumber The account number of the bank account.
     * @param amount The amount to deposit.
     * @return The updated {@link BankAccountDto} with the new savings balance.
     * @throws AccountNotFoundException If the account is not found.
     * @throws IllegalArgumentException If the savings account is at maximum capacity.
     */
    BankAccountDto depositToSavings(String accountNumber, Double amount);

    /**
     * Get account statement for the last 30 days.
     * Returns the account type, current balances, and all transactions in antéchronological order.
     * @param accountNumber The account number.
     * @return StatementDto with account info and transaction history.
     * @throws AccountNotFoundException If the account is not found.
     */
    StatementDto getStatement(String accountNumber);

}