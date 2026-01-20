package com.bankaccount.back_bankaccount.adapters.out.persistence;

import com.bankaccount.back_bankaccount.adapters.out.persistence.entity.TransactionJpaEntity;
import com.bankaccount.back_bankaccount.adapters.out.persistence.mapper.TransactionJpaMapper;
import com.bankaccount.back_bankaccount.adapters.out.persistence.repository.TransactionJpaRepository;
import com.bankaccount.back_bankaccount.domain.model.Transaction;
import com.bankaccount.back_bankaccount.domain.ports.out.TransactionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapter implementing the TransactionRepositoryPort.
 * This bridges the domain layer with the JPA infrastructure.
 * Part of the secondary adapters (infrastructure).
 */
@Component
@RequiredArgsConstructor
public class TransactionPersistenceAdapter implements TransactionRepositoryPort {
    
    private final TransactionJpaRepository jpaRepository;
    private final TransactionJpaMapper mapper;

    @Override
    public Transaction save(Transaction transaction) {
        TransactionJpaEntity entity = mapper.toEntity(transaction);
        TransactionJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<Transaction> findByAccountNumberAndDateAfter(String accountNumber, LocalDateTime date) {
        return jpaRepository.findByAccountNumberAndTransactionDateAfterOrderByTransactionDateDesc(accountNumber, date)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
