package com.bankaccount.back_bankaccount.adapters.in.rest.mapper;

import com.bankaccount.back_bankaccount.domain.model.Statement;
import com.bankaccount.back_bankaccount.domain.model.Transaction;
import com.bankaccount.back_bankaccount.dto.StatementDto;
import com.bankaccount.back_bankaccount.dto.TransactionDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper between domain Statement and REST DTO.
 * Part of the primary adapter (REST API).
 */
@Component
public class StatementDtoMapper {
    
    /**
     * Convert domain model to DTO
     */
    public StatementDto toDto(Statement domain) {
        if (domain == null) {
            return null;
        }
        
        List<TransactionDto> transactionDtos = domain.getTransactions().stream()
                .map(this::toTransactionDto)
                .collect(Collectors.toList());
        
        return StatementDto.builder()
                .accountNumber(domain.getAccountNumber())
                .accountType(domain.getAccountType())
                .currentBalance(domain.getCurrentBalance())
                .savingsBalance(domain.getSavingsBalance())
                .statementDate(domain.getStatementDate())
                .transactions(transactionDtos)
                .build();
    }
    
    /**
     * Convert domain Transaction to DTO
     */
    private TransactionDto toTransactionDto(Transaction transaction) {
        return TransactionDto.builder()
                .date(transaction.getTransactionDate())
                .type(transaction.getType().getLabel())
                .amount(transaction.getAmount())
                .balanceAfter(transaction.getBalanceAfter())
                .build();
    }
}
