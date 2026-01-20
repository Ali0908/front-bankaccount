package com.bankaccount.back_bankaccount.adapters.out.persistence.repository;

import com.bankaccount.back_bankaccount.adapters.out.persistence.entity.TransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA repository interface.
 * This is part of the infrastructure layer.
 */
public interface TransactionJpaRepository extends JpaRepository<TransactionJpaEntity, Long> {
    List<TransactionJpaEntity> findByAccountNumberAndTransactionDateAfterOrderByTransactionDateDesc(
            String accountNumber, 
            LocalDateTime date
    );
}
