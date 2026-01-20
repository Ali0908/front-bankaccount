package com.bankaccount.back_bankaccount.domain.model;

import com.bankaccount.back_bankaccount.exception.InsufficientBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for BankAccount domain model.
 * No infrastructure dependencies - pure domain logic testing.
 */
class BankAccountTest {

    private BankAccount account;

    @BeforeEach
    void setUp() {
        account = BankAccount.builder()
                .id(1L)
                .accountNumber("ACC-001")
                .balance(100.0)
                .overdraftLimit(50.0)
                .savingsBalance(0.0)
                .savingsDepositLimit(22950.0)
                .build();
    }

    @Test
    void should_allow_withdrawal_within_balance() {
        // When
        boolean canWithdraw = account.canWithdraw(80.0);
        
        // Then
        assertThat(canWithdraw).isTrue();
    }

    @Test
    void should_allow_withdrawal_with_overdraft() {
        // When
        boolean canWithdraw = account.canWithdraw(130.0); // 100 + 50 overdraft = 150 max
        
        // Then
        assertThat(canWithdraw).isTrue();
    }

    @Test
    void should_not_allow_withdrawal_exceeding_overdraft() {
        // When
        boolean canWithdraw = account.canWithdraw(200.0);
        
        // Then
        assertThat(canWithdraw).isFalse();
    }

    @Test
    void should_perform_deposit() {
        // When
        account.deposit(50.0);
        
        // Then
        assertThat(account.getBalance()).isEqualTo(150.0);
    }

    @Test
    void should_perform_withdrawal() {
        // When
        account.withdraw(30.0);
        
        // Then
        assertThat(account.getBalance()).isEqualTo(70.0);
    }

    @Test
    void should_throw_exception_when_withdrawal_exceeds_limit() {
        // When & Then
        assertThatThrownBy(() -> account.withdraw(200.0))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient balance");
    }

    @Test
    void should_set_overdraft_limit() {
        // When
        account.setOverdraft(200.0);
        
        // Then
        assertThat(account.getOverdraftLimit()).isEqualTo(200.0);
    }

    @Test
    void should_not_allow_overdraft_above_300() {
        // When & Then
        assertThatThrownBy(() -> account.setOverdraft(350.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("between 0 and 300");
    }

    @Test
    void should_not_allow_overdraft_for_savings_account() {
        // Given
        BankAccount savingsAccount = BankAccount.builder()
                .accountNumber("SAV-001")
                .balance(100.0)
                .overdraftLimit(0.0)
                .build();
        
        // When & Then
        assertThatThrownBy(() -> savingsAccount.setOverdraft(100.0))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Savings accounts cannot have overdraft");
    }

    @Test
    void should_identify_savings_account() {
        // Given
        BankAccount savingsAccount = BankAccount.builder()
                .accountNumber("SAV-001")
                .build();
        
        // Then
        assertThat(savingsAccount.isSavingsAccount()).isTrue();
        assertThat(account.isSavingsAccount()).isFalse();
    }

    @Test
    void should_deposit_to_savings() {
        // When
        Double deposited = account.depositToSavings(1000.0);
        
        // Then
        assertThat(deposited).isEqualTo(1000.0);
        assertThat(account.getSavingsBalance()).isEqualTo(1000.0);
    }

    @Test
    void should_deposit_partial_amount_when_exceeds_savings_limit() {
        // Given
        account.setSavingsBalance(22900.0); // Near limit
        
        // When
        Double deposited = account.depositToSavings(100.0); // Only 50€ available
        
        // Then
        assertThat(deposited).isEqualTo(50.0);
        assertThat(account.getSavingsBalance()).isEqualTo(22950.0);
    }

    @Test
    void should_throw_exception_when_savings_at_capacity() {
        // Given
        account.setSavingsBalance(22950.0);
        
        // When & Then
        assertThatThrownBy(() -> account.depositToSavings(100.0))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("maximum capacity");
    }

    @Test
    void should_determine_account_type_current_only() {
        // Then
        assertThat(account.getAccountType()).isEqualTo("Compte Courant");
    }

    @Test
    void should_determine_account_type_with_savings() {
        // Given
        account.setSavingsBalance(1000.0);
        
        // Then
        assertThat(account.getAccountType()).isEqualTo("Compte Courant + Livret d'épargne");
    }

    @Test
    void should_determine_account_type_savings_only() {
        // Given
        account.setBalance(0.0);
        account.setSavingsBalance(1000.0);
        
        // Then
        assertThat(account.getAccountType()).isEqualTo("Livret d'épargne");
    }
}
