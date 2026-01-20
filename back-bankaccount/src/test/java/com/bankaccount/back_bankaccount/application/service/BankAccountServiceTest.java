package com.bankaccount.back_bankaccount.application.service;

import com.bankaccount.back_bankaccount.domain.model.BankAccount;
import com.bankaccount.back_bankaccount.domain.model.Statement;
import com.bankaccount.back_bankaccount.domain.model.Transaction;
import com.bankaccount.back_bankaccount.domain.ports.out.BankAccountRepositoryPort;
import com.bankaccount.back_bankaccount.domain.ports.out.TransactionRepositoryPort;
import com.bankaccount.back_bankaccount.exception.AccountNotFoundException;
import com.bankaccount.back_bankaccount.exception.InsufficientBalanceException;
import com.bankaccount.back_bankaccount.exception.SavingsAccountOverdraftException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BankAccountService (Application Layer).
 * Tests use case implementations with mocked ports.
 */
@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    private static final String ACC_001 = "ACC-001";
    private static final String SAV_001 = "SAV-001";
    private static final String UNKNOWN_ACCOUNT = "UNKNOWN";

    @InjectMocks
    private BankAccountService service;

    @Mock
    private BankAccountRepositoryPort accountRepository;

    @Mock
    private TransactionRepositoryPort transactionRepository;

    private BankAccount account;

    @BeforeEach
    void setUp() {
        account = BankAccount.builder()
                .id(1L)
                .accountNumber(ACC_001)
                .balance(100.0)
                .overdraftLimit(50.0)
                .savingsBalance(0.0)
                .savingsDepositLimit(22950.0)
                .build();
    }

    // ========== GET ALL ACCOUNTS ==========

    @Test
    void should_get_all_accounts() {
        // Given
        List<BankAccount> accounts = List.of(account);
        when(accountRepository.findAll()).thenReturn(accounts);

        // When
        List<BankAccount> result = service.getAllAccounts();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAccountNumber()).isEqualTo(ACC_001);
        verify(accountRepository).findAll();
    }

    // ========== DEPOSIT ==========

    @Test
    void should_deposit_money_and_record_transaction() {
        // Given
        when(accountRepository.findByAccountNumber(ACC_001))
                .thenReturn(Optional.of(account));
        when(accountRepository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        BankAccount result = service.deposit(ACC_001, 50.0);

        // Then
        assertThat(result.getBalance()).isEqualTo(150.0);
        verify(accountRepository).save(any(BankAccount.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void should_throw_exception_when_account_not_found_for_deposit() {
        // Given
        when(accountRepository.findByAccountNumber(UNKNOWN_ACCOUNT))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.deposit(UNKNOWN_ACCOUNT, 50.0))
                .isInstanceOf(AccountNotFoundException.class);
    }

    // ========== WITHDRAW ==========

    @Test
    void should_withdraw_money_within_balance() {
        // Given
        when(accountRepository.findByAccountNumber(ACC_001))
                .thenReturn(Optional.of(account));
        when(accountRepository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        BankAccount result = service.withdraw(ACC_001, 30.0);

        // Then
        assertThat(result.getBalance()).isEqualTo(70.0);
        verify(accountRepository).save(any(BankAccount.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void should_withdraw_with_overdraft() {
        // Given
        when(accountRepository.findByAccountNumber(ACC_001))
                .thenReturn(Optional.of(account));
        when(accountRepository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        BankAccount result = service.withdraw(ACC_001, 130.0); // 100 + 50 overdraft

        // Then
        assertThat(result.getBalance()).isEqualTo(-30.0);
    }

    @Test
    void should_throw_exception_when_withdrawal_exceeds_overdraft() {
        // Given
        when(accountRepository.findByAccountNumber(ACC_001))
                .thenReturn(Optional.of(account));

        // When & Then
        assertThatThrownBy(() -> service.withdraw(ACC_001, 200.0))
                .isInstanceOf(InsufficientBalanceException.class);
    }

    // ========== SET OVERDRAFT ==========

    @Test
    void should_set_overdraft_limit() {
        // Given
        when(accountRepository.findByAccountNumber(ACC_001))
                .thenReturn(Optional.of(account));
        when(accountRepository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        BankAccount result = service.setOverdraftLimit(ACC_001, 200.0);

        // Then
        assertThat(result.getOverdraftLimit()).isEqualTo(200.0);
        verify(accountRepository).save(any(BankAccount.class));
    }

    @Test
    void should_throw_exception_when_overdraft_invalid() {
        // Given
        when(accountRepository.findByAccountNumber(ACC_001))
                .thenReturn(Optional.of(account));

        // When & Then
        assertThatThrownBy(() -> service.setOverdraftLimit(ACC_001, 500.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_exception_when_setting_overdraft_for_savings_account() {
        // Given
        BankAccount savingsAccount = BankAccount.builder()
                .accountNumber(SAV_001)
                .balance(100.0)
                .build();
        
        when(accountRepository.findByAccountNumber(SAV_001))
                .thenReturn(Optional.of(savingsAccount));

        // When & Then
        assertThatThrownBy(() -> service.setOverdraftLimit(SAV_001, 100.0))
                .isInstanceOf(SavingsAccountOverdraftException.class);
    }

    // ========== SAVINGS DEPOSIT ==========

    @Test
    void should_deposit_to_savings() {
        // Given
        when(accountRepository.findByAccountNumber(ACC_001))
                .thenReturn(Optional.of(account));
        when(accountRepository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        BankAccount result = service.depositToSavings(ACC_001, 1000.0);

        // Then
        assertThat(result.getSavingsBalance()).isEqualTo(1000.0);
        verify(accountRepository).save(any(BankAccount.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void should_deposit_partial_amount_when_exceeds_limit() {
        // Given
        account.setSavingsBalance(22900.0);
        when(accountRepository.findByAccountNumber(ACC_001))
                .thenReturn(Optional.of(account));
        when(accountRepository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        BankAccount result = service.depositToSavings(ACC_001, 100.0);

        // Then
        assertThat(result.getSavingsBalance()).isEqualTo(22950.0); // Only 50€ deposited
    }

    // ========== GET STATEMENT ==========

    @Test
    void should_get_account_statement() {
        // Given
        when(accountRepository.findByAccountNumber(ACC_001))
                .thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountNumberAndDateAfter(eq(ACC_001), any(LocalDateTime.class)))
                .thenReturn(List.of());

        // When
        Statement result = service.getStatement(ACC_001);

        // Then
        assertThat(result.getAccountNumber()).isEqualTo(ACC_001);
        assertThat(result.getCurrentBalance()).isEqualTo(100.0);
        assertThat(result.getAccountType()).isEqualTo("Compte Courant");
        verify(transactionRepository).findByAccountNumberAndDateAfter(eq(ACC_001), any(LocalDateTime.class));
    }
}
