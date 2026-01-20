package com.bankaccount.back_bankaccount.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Pure domain model for Transaction.
 * No infrastructure dependencies (JPA, Spring, etc.)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    private Long id;
    private String accountNumber;
    private LocalDateTime transactionDate;
    private TransactionType type;
    private Double amount;
    private Double balanceAfter;

    /**
     * Factory method to create a deposit transaction
     */
    public static Transaction createDeposit(String accountNumber, Double amount, Double balanceAfter) {
        return Transaction.builder()
                .accountNumber(accountNumber)
                .transactionDate(LocalDateTime.now())
                .type(TransactionType.DEPOSIT_CURRENT)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .build();
    }

    /**
     * Factory method to create a withdrawal transaction
     */
    public static Transaction createWithdrawal(String accountNumber, Double amount, Double balanceAfter) {
        return Transaction.builder()
                .accountNumber(accountNumber)
                .transactionDate(LocalDateTime.now())
                .type(TransactionType.WITHDRAWAL)
                .amount(-amount)  // Negative for withdrawals
                .balanceAfter(balanceAfter)
                .build();
    }

    /**
     * Factory method to create a savings deposit transaction
     */
    public static Transaction createSavingsDeposit(String accountNumber, Double amount, Double balanceAfter) {
        return Transaction.builder()
                .accountNumber(accountNumber)
                .transactionDate(LocalDateTime.now())
                .type(TransactionType.DEPOSIT_SAVINGS)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .build();
    }
}
