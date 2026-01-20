package com.bankaccount.back_bankaccount.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Pure domain model for account statement.
 * Aggregates account information and transaction history.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Statement {
    
    private String accountNumber;
    private String accountType;
    private Double currentBalance;
    private Double savingsBalance;
    private LocalDateTime statementDate;
    private List<Transaction> transactions;
}
