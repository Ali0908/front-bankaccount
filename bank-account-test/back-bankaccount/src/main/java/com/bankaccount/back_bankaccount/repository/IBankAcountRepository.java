package com.bankaccount.back_bankaccount.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bankaccount.back_bankaccount.model.BankAccountEntity;

@Repository
public interface IBankAcountRepository extends JpaRepository<BankAccountEntity, Long> {

    Optional<BankAccountEntity> findByAccountNumber(String accountNumber);
    
}
