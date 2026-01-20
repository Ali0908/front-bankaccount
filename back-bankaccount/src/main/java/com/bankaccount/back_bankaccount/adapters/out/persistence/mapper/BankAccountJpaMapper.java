package com.bankaccount.back_bankaccount.adapters.out.persistence.mapper;

import com.bankaccount.back_bankaccount.adapters.out.persistence.entity.BankAccountJpaEntity;
import com.bankaccount.back_bankaccount.domain.model.BankAccount;
import org.springframework.stereotype.Component;

/**
 * Mapper between domain BankAccount and JPA entity.
 * Part of the infrastructure layer.
 */
@Component
public class BankAccountJpaMapper {
    
    /**
     * Convert JPA entity to domain model
     */
    public BankAccount toDomain(BankAccountJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return BankAccount.builder()
                .id(entity.getId())
                .accountNumber(entity.getAccountNumber())
                .balance(entity.getBalance())
                .overdraftLimit(entity.getOverdraftLimit())
                .savingsBalance(entity.getSavingsBalance())
                .savingsDepositLimit(entity.getSavingsDepositLimit())
                .build();
    }
    
    /**
     * Convert domain model to JPA entity
     */
    public BankAccountJpaEntity toEntity(BankAccount domain) {
        if (domain == null) {
            return null;
        }
        
        BankAccountJpaEntity entity = new BankAccountJpaEntity();
        entity.setId(domain.getId());
        entity.setAccountNumber(domain.getAccountNumber());
        entity.setBalance(domain.getBalance());
        entity.setOverdraftLimit(domain.getOverdraftLimit());
        entity.setSavingsBalance(domain.getSavingsBalance());
        entity.setSavingsDepositLimit(domain.getSavingsDepositLimit());
        
        return entity;
    }
}
