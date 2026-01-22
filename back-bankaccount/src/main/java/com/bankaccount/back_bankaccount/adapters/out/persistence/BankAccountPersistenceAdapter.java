package com.bankaccount.back_bankaccount.adapters.out.persistence;

import com.bankaccount.back_bankaccount.adapters.out.persistence.entity.BankAccountJpaEntity;
import com.bankaccount.back_bankaccount.adapters.out.persistence.mapper.BankAccountJpaMapper;
import com.bankaccount.back_bankaccount.adapters.out.persistence.repository.BankAccountJpaRepository;
import com.bankaccount.back_bankaccount.domain.model.BankAccount;
import com.bankaccount.back_bankaccount.domain.ports.out.BankAccountRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the BankAccountRepositoryPort.
 * This bridges the domain layer with the JPA infrastructure.
 * Part of the secondary adapters (infrastructure).
 */
@Component
@RequiredArgsConstructor
public class BankAccountPersistenceAdapter implements BankAccountRepositoryPort {
    
    private final BankAccountJpaRepository jpaRepository;
    private final BankAccountJpaMapper mapper;

    @Override
    public List<BankAccount> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<BankAccount> findByAccountNumber(String accountNumber) {
        return jpaRepository.findByAccountNumber(accountNumber)
                .map(mapper::toDomain);
    }

    @Override
    public BankAccount save(BankAccount account) {
        BankAccountJpaEntity entity = mapper.toEntity(account);
        BankAccountJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
