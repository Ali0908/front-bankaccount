package com.bankaccount.back_bankaccount.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatementDto {
    private String accountNumber;
    private String accountType;
    private Double currentBalance;
    private Double savingsBalance;
    private LocalDateTime statementDate;
    private List<TransactionDto> transactions;
}