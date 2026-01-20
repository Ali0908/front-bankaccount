package com.bankaccount.back_bankaccount.adapters.out.persistence.repository;

import com.bankaccount.back_bankaccount.adapters.out.persistence.entity.BankAccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository interface.
 * This is part of the infrastructure layer.
 */
public interface BankAccountJpaRepository extends JpaRepository<BankAccountJpaEntity, Long> {
    Optional<BankAccountJpaEntity> findByAccountNumber(String accountNumber);
}
