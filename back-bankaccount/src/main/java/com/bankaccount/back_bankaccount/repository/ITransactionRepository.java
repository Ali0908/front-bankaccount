package com.bankaccount.back_bankaccount.repository;

import com.bankaccount.back_bankaccount.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ITransactionRepository extends JpaRepository<TransactionEntity, Long> {
    
    /**
     * Find all transactions for an account after a specific date, ordered by date descending (most recent first)
     * @param accountNumber The account number
     * @param startDate The start date (transactions after this date)
     * @return List of transactions in antéchronological order
     */
    List<TransactionEntity> findByAccountNumberAndTransactionDateAfterOrderByTransactionDateDesc(
        String accountNumber, 
        LocalDateTime startDate
    );
}