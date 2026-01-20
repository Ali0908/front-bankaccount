package com.bankaccount.back_bankaccount.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

/**
 * JPA Entity for bank account persistence.
 * This is part of the infrastructure layer (secondary adapter).
 */
@Data
@Entity
@Table(name = "bank_account", uniqueConstraints = @UniqueConstraint(columnNames = {"id"}))
public class BankAccountJpaEntity {
    
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_account_id_seq")
    @SequenceGenerator(name = "bank_account_id_seq", sequenceName = "bank_account_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "balance", nullable = false)
    private Double balance;

    @Column(name = "overdraft_limit", nullable = false)
    private Double overdraftLimit = 0.0;

    @Column(name = "savings_balance", nullable = false)
    private Double savingsBalance = 0.0;

    @Column(name = "savings_deposit_limit", nullable = false)
    private Double savingsDepositLimit = 22950.0; // Livret A limit
}
