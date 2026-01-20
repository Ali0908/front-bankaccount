package com.bankaccount.back_bankaccount.domain.ports.out;

import com.bankaccount.back_bankaccount.domain.model.BankAccount;

import java.util.List;
import java.util.Optional;

/**
 * Output port for bank account persistence.
 * This is a secondary port that will be implemented by infrastructure adapters.
 */
public interface BankAccountRepositoryPort {
    
    /**
     * Find all bank accounts
     */
    List<BankAccount> findAll();
    
    /**
     * Find bank account by account number
     */
    Optional<BankAccount> findByAccountNumber(String accountNumber);
    
    /**
     * Save or update a bank account
     */
    BankAccount save(BankAccount account);
}
