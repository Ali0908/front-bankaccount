package com.bankaccount.back_bankaccount.adapters.out.persistence.mapper;

import com.bankaccount.back_bankaccount.adapters.out.persistence.entity.TransactionJpaEntity;
import com.bankaccount.back_bankaccount.domain.model.Transaction;
import com.bankaccount.back_bankaccount.domain.model.TransactionType;
import org.springframework.stereotype.Component;

/**
 * Mapper between domain Transaction and JPA entity.
 * Part of the infrastructure layer.
 */
@Component
public class TransactionJpaMapper {
    
    /**
     * Convert JPA entity to domain model
     */
    public Transaction toDomain(TransactionJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Transaction.builder()
                .id(entity.getId())
                .accountNumber(entity.getAccountNumber())
                .transactionDate(entity.getTransactionDate())
                .type(mapType(entity.getType()))
                .amount(entity.getAmount())
                .balanceAfter(entity.getBalanceAfter())
                .build();
    }
    
    /**
     * Convert domain model to JPA entity
     */
    public TransactionJpaEntity toEntity(Transaction domain) {
        if (domain == null) {
            return null;
        }
        
        return TransactionJpaEntity.builder()
                .id(domain.getId())
                .accountNumber(domain.getAccountNumber())
                .transactionDate(domain.getTransactionDate())
                .type(mapType(domain.getType()))
                .amount(domain.getAmount())
                .balanceAfter(domain.getBalanceAfter())
                .build();
    }
    
    /**
     * Map domain TransactionType to JPA TransactionType
     */
    private TransactionType mapType(com.bankaccount.back_bankaccount.adapters.out.persistence.entity.TransactionType entityType) {
        if (entityType == null) {
            return null;
        }
        return TransactionType.valueOf(entityType.name());
    }
    
    /**
     * Map JPA TransactionType to domain TransactionType
     */
    private com.bankaccount.back_bankaccount.adapters.out.persistence.entity.TransactionType mapType(TransactionType domainType) {
        if (domainType == null) {
            return null;
        }
        return com.bankaccount.back_bankaccount.adapters.out.persistence.entity.TransactionType.valueOf(domainType.name());
    }
}
