package com.bankaccount.back_bankaccount.domain.ports.out;

import com.bankaccount.back_bankaccount.domain.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Output port for transaction persistence.
 * This is a secondary port that will be implemented by infrastructure adapters.
 */
public interface TransactionRepositoryPort {
    
    /**
     * Save a transaction
     */
    Transaction save(Transaction transaction);
    
    /**
     * Find transactions by account number after a specific date
     */
    List<Transaction> findByAccountNumberAndDateAfter(String accountNumber, LocalDateTime date);
}
