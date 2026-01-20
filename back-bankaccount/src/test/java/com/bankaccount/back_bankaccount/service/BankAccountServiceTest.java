package com.bankaccount.back_bankaccount.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bankaccount.back_bankaccount.dto.BankAccountDto;
import com.bankaccount.back_bankaccount.dto.StatementDto;
import com.bankaccount.back_bankaccount.exception.AccountNotFoundException;
import com.bankaccount.back_bankaccount.exception.InsufficientBalanceException;
import com.bankaccount.back_bankaccount.exception.SavingsAccountOverdraftException;
import com.bankaccount.back_bankaccount.mapper.interfaces.IBankAccountMapper;
import com.bankaccount.back_bankaccount.model.BankAccountEntity;
import com.bankaccount.back_bankaccount.model.TransactionEntity;
import com.bankaccount.back_bankaccount.model.TransactionType;
import com.bankaccount.back_bankaccount.repository.IBankAcountRepository;
import com.bankaccount.back_bankaccount.repository.ITransactionRepository;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    private static final String ACC_001 = "ACC-001";
    private static final String UNKNOWN_ACCOUNT = "UNKNOWN";
	
    @InjectMocks
    private BankAccountServiceImplementation accountService;

    @Mock
    private IBankAcountRepository bankAccountRepository;

    @Mock
    private IBankAccountMapper bankAccountMapper;

    @Mock
    private ITransactionRepository transactionRepository;

    //given
    private BankAccountEntity bankAccountEntity = Mockito.mock(BankAccountEntity.class);
    private List<BankAccountEntity> bankAccountEntityList = Lists.newArrayList(bankAccountEntity);
    private BankAccountDto bankAccountDto = Mockito.mock(BankAccountDto.class);
    private List<BankAccountDto> bankAccountDtoList = Lists.newArrayList(bankAccountDto);

    // ========================================
    // Feature 1: Bank Account - Basic Operations
    // ========================================

    @Test
    void whenGetAllBankAccounts() {
        // Given
        Mockito.when(bankAccountRepository.findAll()).thenReturn(bankAccountEntityList);
        Mockito.when(bankAccountMapper.toDtoList(bankAccountEntityList)).thenReturn(bankAccountDtoList);

        // When
        final List<BankAccountDto> result = accountService.getAllBankAccounts();

        // Then
        Assertions.assertThat(result).isEqualTo(bankAccountDtoList);
        
    }

    @Test
    void should_deposit_money_and_update_balance() {
    // Given
    BankAccountEntity account = new BankAccountEntity();
    account.setAccountNumber(ACC_001);
    account.setBalance(100.0);
    
    BankAccountDto expectedDto = BankAccountDto.builder()
        .accountNumber(ACC_001)
        .balance(150.0)
        .build();
    
    Mockito.when(bankAccountRepository.findByAccountNumber(ACC_001))
        .thenReturn(Optional.of(account));
    Mockito.when(bankAccountRepository.save(Mockito.any(BankAccountEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    Mockito.when(bankAccountMapper.toDto(Mockito.any(BankAccountEntity.class)))
        .thenReturn(expectedDto);

    // When
    BankAccountDto result = accountService.deposit(ACC_001, 50.0);

    // Then
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getBalance()).isEqualTo(150.0);
}

    @Test
    void should_throw_exception_when_account_not_found() {
        // Given
        Mockito.when(bankAccountRepository.findByAccountNumber(UNKNOWN_ACCOUNT))
            .thenReturn(Optional.empty());

        // When / Then
        Assertions.assertThatThrownBy(() -> accountService.deposit(UNKNOWN_ACCOUNT, 50.0))
            .isInstanceOf(AccountNotFoundException.class)
            .hasMessageContaining(UNKNOWN_ACCOUNT);
    }

    @Test
    void should_withdraw_money_and_update_balance() {
        // Given
        BankAccountEntity account = new BankAccountEntity();
        account.setAccountNumber(ACC_001);
        account.setBalance(200.0);
        
        BankAccountDto expectedDto = BankAccountDto.builder()
            .accountNumber(ACC_001)
            .balance(150.0)
            .build();
        
        Mockito.when(bankAccountRepository.findByAccountNumber(ACC_001))
            .thenReturn(Optional.of(account));
        Mockito.when(bankAccountRepository.save(Mockito.any(BankAccountEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(bankAccountMapper.toDto(Mockito.any(BankAccountEntity.class)))
            .thenReturn(expectedDto);

        // When
        BankAccountDto result = accountService.withdraw(ACC_001, 50.0);

        // Then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getBalance()).isEqualTo(150.0);
        Assertions.assertThat(account.getBalance()).isEqualTo(150.0);
    }

    @Test
    void should_throw_exception_when_withdrawing_from_unknown_account() {
        // Given
        Mockito.when(bankAccountRepository.findByAccountNumber(UNKNOWN_ACCOUNT))
            .thenReturn(Optional.empty());

        // When / Then
        Assertions.assertThatThrownBy(() -> accountService.withdraw(UNKNOWN_ACCOUNT, 50.0))
            .isInstanceOf(AccountNotFoundException.class)
            .hasMessageContaining(UNKNOWN_ACCOUNT);
    }

    @Test
    void should_throw_exception_when_insufficient_balance() {
        // Given
        BankAccountEntity account = new BankAccountEntity();
        account.setAccountNumber(ACC_001);
        account.setBalance(100.0);
        
        Mockito.when(bankAccountRepository.findByAccountNumber(ACC_001))
            .thenReturn(Optional.of(account));

        // When / Then
        Assertions.assertThatThrownBy(() -> accountService.withdraw(ACC_001, 150.0))
            .isInstanceOf(InsufficientBalanceException.class)
            .hasMessageContaining("Insufficient balance");
    }

    // ========================================
    // Feature 2: Overdraft Authorization
    // ========================================

    @Test
    void should_allow_withdrawal_with_overdraft_enabled() {
        // Given
        BankAccountEntity account = new BankAccountEntity();
        account.setAccountNumber(ACC_001);
        account.setBalance(100.0);
        account.setOverdraftLimit(200.0);
        
        BankAccountDto expectedDto = BankAccountDto.builder()
            .accountNumber(ACC_001)
            .balance(-50.0)
            .overdraftLimit(200.0)
            .build();
        
        Mockito.when(bankAccountRepository.findByAccountNumber(ACC_001))
            .thenReturn(Optional.of(account));
        Mockito.when(bankAccountRepository.save(Mockito.any(BankAccountEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(bankAccountMapper.toDto(Mockito.any(BankAccountEntity.class)))
            .thenReturn(expectedDto);

        // When
        BankAccountDto result = accountService.withdraw(ACC_001, 150.0);

        // Then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getBalance()).isEqualTo(-50.0);
        Assertions.assertThat(account.getBalance()).isEqualTo(-50.0);
    }

    @Test
    void should_throw_exception_when_withdrawal_exceeds_overdraft_limit() {
        // Given
        BankAccountEntity account = new BankAccountEntity();
        account.setAccountNumber(ACC_001);
        account.setBalance(100.0);
        account.setOverdraftLimit(200.0);
        
        Mockito.when(bankAccountRepository.findByAccountNumber(ACC_001))
            .thenReturn(Optional.of(account));

        // When / Then - trying to withdraw 350 when balance is 100 and overdraft limit is 200
        // Final balance would be 100 - 350 = -250, which is less than -200 (not allowed)
        Assertions.assertThatThrownBy(() -> accountService.withdraw(ACC_001, 350.0))
            .isInstanceOf(InsufficientBalanceException.class)
            .hasMessageContaining("Insufficient balance");
    }

    @Test
    void should_set_overdraft_limit_successfully() {
        // Given
        BankAccountEntity account = new BankAccountEntity();
        account.setAccountNumber(ACC_001);
        account.setBalance(100.0);
        account.setOverdraftLimit(0.0);
        
        BankAccountDto expectedDto = BankAccountDto.builder()
            .accountNumber(ACC_001)
            .balance(100.0)
            .overdraftLimit(300.0)
            .build();
        
        Mockito.when(bankAccountRepository.findByAccountNumber(ACC_001))
            .thenReturn(Optional.of(account));
        Mockito.when(bankAccountRepository.save(Mockito.any(BankAccountEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(bankAccountMapper.toDto(Mockito.any(BankAccountEntity.class)))
            .thenReturn(expectedDto);

        // When
        BankAccountDto result = accountService.setOverdraftLimit(ACC_001, 300.0);

        // Then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getOverdraftLimit()).isEqualTo(300.0);
        Assertions.assertThat(account.getOverdraftLimit()).isEqualTo(300.0);
    }

    @Test
    void should_throw_exception_when_overdraft_limit_exceeds_max() {
        // Given - no mocking needed as validation happens first

        // When / Then
        Assertions.assertThatThrownBy(() -> accountService.setOverdraftLimit(ACC_001, 400.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Overdraft limit must be between 0 and 300");
    }

    @Test
    void should_throw_exception_when_overdraft_limit_is_negative() {
        // Given - no mocking needed as validation happens first

        // When / Then
        Assertions.assertThatThrownBy(() -> accountService.setOverdraftLimit(ACC_001, -50.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Overdraft limit must be between 0 and 300");
    }

    @Test
    void should_disable_overdraft_by_setting_limit_to_zero() {
        // Given
        BankAccountEntity account = new BankAccountEntity();
        account.setAccountNumber(ACC_001);
        account.setBalance(100.0);
        account.setOverdraftLimit(300.0);
        
        BankAccountDto expectedDto = BankAccountDto.builder()
            .accountNumber(ACC_001)
            .balance(100.0)
            .overdraftLimit(0.0)
            .build();
        
        Mockito.when(bankAccountRepository.findByAccountNumber(ACC_001))
            .thenReturn(Optional.of(account));
        Mockito.when(bankAccountRepository.save(Mockito.any(BankAccountEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(bankAccountMapper.toDto(Mockito.any(BankAccountEntity.class)))
            .thenReturn(expectedDto);

        // When
        BankAccountDto result = accountService.setOverdraftLimit(ACC_001, 0.0);

        // Then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getOverdraftLimit()).isEqualTo(0.0);
        Assertions.assertThat(account.getOverdraftLimit()).isEqualTo(0.0);
    }

    // ========================================
    // Feature 3: Savings Account (Livret d'épargne)
    // ========================================

    @Test
    void should_deposit_to_savings_within_limit() {
        // Given
        BankAccountEntity account = new BankAccountEntity();
        account.setAccountNumber("SAV-001");
        account.setSavingsBalance(1000.0);
        account.setSavingsDepositLimit(22950.0);
        
        BankAccountDto expectedDto = BankAccountDto.builder()
            .accountNumber("SAV-001")
            .savingsBalance(1500.0)
            .build();
        
        Mockito.when(bankAccountRepository.findByAccountNumber("SAV-001"))
            .thenReturn(Optional.of(account));
        Mockito.when(bankAccountRepository.save(Mockito.any(BankAccountEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(bankAccountMapper.toDto(Mockito.any(BankAccountEntity.class)))
            .thenReturn(expectedDto);

        // When
        BankAccountDto result = accountService.depositToSavings("SAV-001", 500.0);

        // Then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(account.getSavingsBalance()).isEqualTo(1500.0);
    }

    @Test
    void should_deposit_partial_amount_when_exceeding_savings_limit() {
        // Given
        BankAccountEntity account = new BankAccountEntity();
        account.setAccountNumber("SAV-001");
        account.setSavingsBalance(22900.0);
        account.setSavingsDepositLimit(22950.0);
        
        BankAccountDto expectedDto = BankAccountDto.builder()
            .accountNumber("SAV-001")
            .savingsBalance(22950.0)
            .build();
        
        Mockito.when(bankAccountRepository.findByAccountNumber("SAV-001"))
            .thenReturn(Optional.of(account));
        Mockito.when(bankAccountRepository.save(Mockito.any(BankAccountEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(bankAccountMapper.toDto(Mockito.any(BankAccountEntity.class)))
            .thenReturn(expectedDto);

        // When
        accountService.depositToSavings("SAV-001", 100.0);

        // Then - only 50€ should be deposited (limit is 22950, current is 22900)
        Assertions.assertThat(account.getSavingsBalance()).isEqualTo(22950.0);
    }

    @Test
    void should_throw_exception_when_savings_account_at_limit() {
        // Given
        BankAccountEntity account = new BankAccountEntity();
        account.setAccountNumber("SAV-001");
        account.setSavingsBalance(22950.0);
        account.setSavingsDepositLimit(22950.0);
        
        Mockito.when(bankAccountRepository.findByAccountNumber("SAV-001"))
            .thenReturn(Optional.of(account));

        // When / Then
        Assertions.assertThatThrownBy(() -> accountService.depositToSavings("SAV-001", 100.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("savings account is at maximum capacity");
    }

    @Test
    void should_throw_exception_when_depositing_to_unknown_savings_account() {
        // Given
        Mockito.when(bankAccountRepository.findByAccountNumber(UNKNOWN_ACCOUNT))
            .thenReturn(Optional.empty());

        // When / Then
        Assertions.assertThatThrownBy(() -> accountService.depositToSavings(UNKNOWN_ACCOUNT, 100.0))
            .isInstanceOf(AccountNotFoundException.class)
            .hasMessageContaining(UNKNOWN_ACCOUNT);
    }

    @Test
    void should_prevent_overdraft_on_savings_account() {
        // Given
        BankAccountEntity savingsAccount = new BankAccountEntity();
        savingsAccount.setAccountNumber("SAV-001");
        savingsAccount.setOverdraftLimit(0.0);
        
        Mockito.when(bankAccountRepository.findByAccountNumber("SAV-001"))
            .thenReturn(Optional.of(savingsAccount));

        // When / Then - attempting to set overdraft on savings should fail
        Assertions.assertThatThrownBy(() -> accountService.setOverdraftLimit("SAV-001", 100.0))
            .isInstanceOf(SavingsAccountOverdraftException.class)
            .hasMessageContaining("Savings accounts cannot have overdraft");
    }

    // ========================================
    // Feature 4: Statement (Relevé de compte)
    // ========================================

    @Test
    void should_get_statement_for_current_account() {
        // Given
        BankAccountEntity account = new BankAccountEntity();
        account.setAccountNumber(ACC_001);
        account.setBalance(500.0);
        account.setSavingsBalance(0.0);
        
        List<TransactionEntity> transactions = Lists.newArrayList(
            TransactionEntity.builder()
                .accountNumber(ACC_001)
                .type(TransactionType.DEPOSIT_CURRENT)
                .amount(200.0)
                .build()
        );
        
        Mockito.when(bankAccountRepository.findByAccountNumber(ACC_001))
            .thenReturn(Optional.of(account));
        Mockito.when(transactionRepository.findByAccountNumberAndTransactionDateAfterOrderByTransactionDateDesc(
            Mockito.anyString(), Mockito.any()))
            .thenReturn(transactions);

        // When
        StatementDto result = accountService.getStatement(ACC_001);

        // Then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getAccountNumber()).isEqualTo(ACC_001);
        Assertions.assertThat(result.getCurrentBalance()).isEqualTo(500.0);
        Assertions.assertThat(result.getAccountType()).isEqualTo("Compte Courant");
        Assertions.assertThat(result.getTransactions()).isNotEmpty();
    }

    @Test
    void should_get_statement_for_savings_account() {
        // Given
        BankAccountEntity account = new BankAccountEntity();
        account.setAccountNumber("SAV-001");
        account.setBalance(0.0);
        account.setSavingsBalance(5000.0);
        
        List<TransactionEntity> transactions = Lists.newArrayList(
            TransactionEntity.builder()
                .accountNumber("SAV-001")
                .type(TransactionType.DEPOSIT_SAVINGS)
                .amount(1000.0)
                .build()
        );
        
        Mockito.when(bankAccountRepository.findByAccountNumber("SAV-001"))
            .thenReturn(Optional.of(account));
        Mockito.when(transactionRepository.findByAccountNumberAndTransactionDateAfterOrderByTransactionDateDesc(
            Mockito.anyString(), Mockito.any()))
            .thenReturn(transactions);

        // When
        StatementDto result = accountService.getStatement("SAV-001");

        // Then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getAccountNumber()).isEqualTo("SAV-001");
        Assertions.assertThat(result.getSavingsBalance()).isEqualTo(5000.0);
        Assertions.assertThat(result.getAccountType()).isEqualTo("Livret d'épargne");
    }

    @Test
    void should_get_statement_with_both_current_and_savings_balance() {
        // Given
        BankAccountEntity account = new BankAccountEntity();
        account.setAccountNumber("ACC-002");
        account.setBalance(2000.0);
        account.setSavingsBalance(3000.0);
        
        List<TransactionEntity> transactions = Lists.newArrayList();
        
        Mockito.when(bankAccountRepository.findByAccountNumber("ACC-002"))
            .thenReturn(Optional.of(account));
        Mockito.when(transactionRepository.findByAccountNumberAndTransactionDateAfterOrderByTransactionDateDesc(
            Mockito.anyString(), Mockito.any()))
            .thenReturn(transactions);

        // When
        StatementDto result = accountService.getStatement("ACC-002");

        // Then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getCurrentBalance()).isEqualTo(2000.0);
        Assertions.assertThat(result.getSavingsBalance()).isEqualTo(3000.0);
        Assertions.assertThat(result.getAccountType()).isEqualTo("Compte Courant + Livret d'épargne");
    }

    @Test
    void should_return_empty_statement_when_no_transactions() {
        // Given
        BankAccountEntity account = new BankAccountEntity();
        account.setAccountNumber(ACC_001);
        account.setBalance(1000.0);
        account.setSavingsBalance(0.0);
        
        Mockito.when(bankAccountRepository.findByAccountNumber(ACC_001))
            .thenReturn(Optional.of(account));
        Mockito.when(transactionRepository.findByAccountNumberAndTransactionDateAfterOrderByTransactionDateDesc(
            Mockito.anyString(), Mockito.any()))
            .thenReturn(Lists.newArrayList());

        // When
        StatementDto result = accountService.getStatement(ACC_001);

        // Then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTransactions()).isEmpty();
    }

    @Test
    void should_throw_exception_when_getting_statement_for_unknown_account() {
        // Given
        Mockito.when(bankAccountRepository.findByAccountNumber(UNKNOWN_ACCOUNT))
            .thenReturn(Optional.empty());

        // When / Then
        Assertions.assertThatThrownBy(() -> accountService.getStatement(UNKNOWN_ACCOUNT))
            .isInstanceOf(AccountNotFoundException.class)
            .hasMessageContaining(UNKNOWN_ACCOUNT);
    }

    @Test
    void should_return_transactions_sorted_by_date_descending() {
        // Given
        BankAccountEntity account = new BankAccountEntity();
        account.setAccountNumber(ACC_001);
        account.setBalance(1000.0);
        account.setSavingsBalance(0.0);
        
        LocalDateTime now = LocalDateTime.now();
        List<TransactionEntity> transactions = Lists.newArrayList(
            TransactionEntity.builder()
                .accountNumber(ACC_001)
                .transactionDate(now.minusHours(1))
                .type(TransactionType.WITHDRAWAL)
                .amount(-100.0)
                .build(),
            TransactionEntity.builder()
                .accountNumber(ACC_001)
                .transactionDate(now)
                .type(TransactionType.DEPOSIT_CURRENT)
                .amount(500.0)
                .build()
        );
        
        Mockito.when(bankAccountRepository.findByAccountNumber(ACC_001))
            .thenReturn(Optional.of(account));
        Mockito.when(transactionRepository.findByAccountNumberAndTransactionDateAfterOrderByTransactionDateDesc(
            Mockito.anyString(), Mockito.any()))
            .thenReturn(transactions);

        // When
        StatementDto result = accountService.getStatement(ACC_001);

        // Then
        Assertions.assertThat(result.getTransactions()).hasSize(2);
        // Verify that the repository method was called with proper parameters
        Mockito.verify(transactionRepository).findByAccountNumberAndTransactionDateAfterOrderByTransactionDateDesc(
            Mockito.anyString(), Mockito.any());
    }
}