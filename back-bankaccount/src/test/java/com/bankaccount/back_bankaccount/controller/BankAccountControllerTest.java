package com.bankaccount.back_bankaccount.controller;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bankaccount.back_bankaccount.dto.BankAccountDto;
import com.bankaccount.back_bankaccount.dto.DepositRequestDto;
import com.bankaccount.back_bankaccount.dto.OverdraftRequestDto;
import com.bankaccount.back_bankaccount.dto.StatementDto;
import com.bankaccount.back_bankaccount.dto.WithdrawRequestDto;
import com.bankaccount.back_bankaccount.exception.AccountNotFoundException;
import com.bankaccount.back_bankaccount.exception.InsufficientBalanceException;
import com.bankaccount.back_bankaccount.exception.SavingsAccountOverdraftException;
import com.bankaccount.back_bankaccount.service.interfaces.IBankAccountService;



@ExtendWith(MockitoExtension.class)
class BankAccountControllerTest {

    @Mock
    private IBankAccountService bankAccountService;

    @InjectMocks
    private BankAccountController bankAccountController;

    private static final String ACC_001 = "ACC-001";

    // given
    private final BankAccountDto bankAccountDto = Mockito.mock(BankAccountDto.class);
    private final List<BankAccountDto> bankAccountDtoList = List.of(bankAccountDto);
    

    @Test
    void shouldReturnAllAccountsWhenCallingGetAllBankAccounts() {
        // when
        Mockito.when(bankAccountService.getAllBankAccounts()).thenReturn(bankAccountDtoList);

        // call
        final ResponseEntity<List<BankAccountDto>> response = bankAccountController.getAllBankAccounts();

        // then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isEqualTo(bankAccountDtoList);
    }

    @Test
    void should_deposit_money_and_return_updated_account() {
        // given
        String accountNumber = ACC_001;
        Double amount = 100.0;
        DepositRequestDto request = DepositRequestDto.builder()
            .accountNumber(accountNumber)
            .amount(amount)
            .build();
        
        BankAccountDto updatedAccount = BankAccountDto.builder()
            .accountNumber(accountNumber)
            .balance(200.0)
            .build();

        // when
        Mockito.when(bankAccountService.deposit(accountNumber, amount))
            .thenReturn(updatedAccount);

        // call
        final ResponseEntity<BankAccountDto> response = bankAccountController.deposit(request);

        // then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isEqualTo(updatedAccount);
        Assertions.assertThat(response.getBody().getBalance()).isEqualTo(200.0);
    }

    @Test
    void should_return_not_found_when_depositing_to_unknown_account() {
        // given
        String accountNumber = "UNKNOWN";
        Double amount = 50.0;
        DepositRequestDto request = DepositRequestDto.builder()
            .accountNumber(accountNumber)
            .amount(amount)
            .build();

        // when
        Mockito.when(bankAccountService.deposit(accountNumber, amount))
            .thenThrow(new AccountNotFoundException(accountNumber));

        // then
        Assertions.assertThatThrownBy(() -> bankAccountController.deposit(request))
            .isInstanceOf(AccountNotFoundException.class)
            .hasMessageContaining(accountNumber);
    }

    @Test
    void should_withdraw_money_and_return_updated_account() {
        // given
        String accountNumber = ACC_001;
        Double amount = 50.0;
        WithdrawRequestDto request = WithdrawRequestDto.builder()
            .accountNumber(accountNumber)
            .amount(amount)
            .build();
        
        BankAccountDto updatedAccount = BankAccountDto.builder()
            .accountNumber(accountNumber)
            .balance(150.0)
            .build();

        // when
        Mockito.when(bankAccountService.withdraw(accountNumber, amount))
            .thenReturn(updatedAccount);

        // call
        final ResponseEntity<BankAccountDto> response = bankAccountController.withdraw(request);

        // then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isEqualTo(updatedAccount);
        Assertions.assertThat(response.getBody().getBalance()).isEqualTo(150.0);
    }

    @Test
    void should_return_not_found_when_withdrawing_from_unknown_account() {
        // given
        String accountNumber = "UNKNOWN";
        Double amount = 50.0;
        WithdrawRequestDto request = WithdrawRequestDto.builder()
            .accountNumber(accountNumber)
            .amount(amount)
            .build();

        // when
        Mockito.when(bankAccountService.withdraw(accountNumber, amount))
            .thenThrow(new AccountNotFoundException(accountNumber));

        // then
        Assertions.assertThatThrownBy(() -> bankAccountController.withdraw(request))
            .isInstanceOf(AccountNotFoundException.class)
            .hasMessageContaining(accountNumber);
    }

    @Test
    void should_throw_insufficient_balance_when_withdrawing_more_than_available() {
        // given
        String accountNumber = ACC_001;
        Double amount = 500.0;
        WithdrawRequestDto request = WithdrawRequestDto.builder()
            .accountNumber(accountNumber)
            .amount(amount)
            .build();

        // when
        Mockito.when(bankAccountService.withdraw(accountNumber, amount))
            .thenThrow(new InsufficientBalanceException(200.0, 500.0));

        // then
        Assertions.assertThatThrownBy(() -> bankAccountController.withdraw(request))
            .isInstanceOf(InsufficientBalanceException.class)
            .hasMessageContaining("Insufficient balance");
    }

    @Test
    void should_set_overdraft_limit_and_return_updated_account() {
        // given
        String accountNumber = ACC_001;
        Double overdraftLimit = 500.0;
        OverdraftRequestDto request = OverdraftRequestDto.builder()
            .accountNumber(accountNumber)
            .overdraftLimit(overdraftLimit)
            .build();
        
        BankAccountDto updatedAccount = BankAccountDto.builder()
            .accountNumber(accountNumber)
            .balance(100.0)
            .overdraftLimit(overdraftLimit)
            .build();

        // when
        Mockito.when(bankAccountService.setOverdraftLimit(accountNumber, overdraftLimit))
            .thenReturn(updatedAccount);

        // call
        final ResponseEntity<BankAccountDto> response = bankAccountController.setOverdraftLimit(request);

        // then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isEqualTo(updatedAccount);
        Assertions.assertThat(response.getBody().getOverdraftLimit()).isEqualTo(500.0);
    }

    @Test
    void should_return_not_found_when_setting_overdraft_on_unknown_account() {
        // given
        String accountNumber = "UNKNOWN";
        Double overdraftLimit = 300.0;
        OverdraftRequestDto request = OverdraftRequestDto.builder()
            .accountNumber(accountNumber)
            .overdraftLimit(overdraftLimit)
            .build();

        // when
        Mockito.when(bankAccountService.setOverdraftLimit(accountNumber, overdraftLimit))
            .thenThrow(new AccountNotFoundException(accountNumber));

        // then
        Assertions.assertThatThrownBy(() -> bankAccountController.setOverdraftLimit(request))
            .isInstanceOf(AccountNotFoundException.class)
            .hasMessageContaining(accountNumber);
    }

    // ========================================
    // Feature 3: Savings Account Tests
    // ========================================

    @Test
    void should_deposit_to_savings_and_return_updated_account() {
        // given
        String accountNumber = "SAV-001";
        Double amount = 500.0;
        DepositRequestDto request = DepositRequestDto.builder()
            .accountNumber(accountNumber)
            .amount(amount)
            .build();
        
        BankAccountDto updatedAccount = BankAccountDto.builder()
            .accountNumber(accountNumber)
            .savingsBalance(1500.0)
            .savingsDepositLimit(22950.0)
            .build();

        // when
        Mockito.when(bankAccountService.depositToSavings(accountNumber, amount))
            .thenReturn(updatedAccount);

        // call
        final ResponseEntity<BankAccountDto> response = bankAccountController.depositToSavings(request);

        // then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isEqualTo(updatedAccount);
        Assertions.assertThat(response.getBody().getSavingsBalance()).isEqualTo(1500.0);
    }

    @Test
    void should_return_not_found_when_depositing_to_unknown_savings_account() {
        // given
        String accountNumber = "UNKNOWN";
        Double amount = 100.0;
        DepositRequestDto request = DepositRequestDto.builder()
            .accountNumber(accountNumber)
            .amount(amount)
            .build();

        // when
        Mockito.when(bankAccountService.depositToSavings(accountNumber, amount))
            .thenThrow(new AccountNotFoundException(accountNumber));

        // then
        Assertions.assertThatThrownBy(() -> bankAccountController.depositToSavings(request))
            .isInstanceOf(AccountNotFoundException.class)
            .hasMessageContaining(accountNumber);
    }

    @Test
    void should_throw_exception_when_savings_account_at_limit() {
        // given
        String accountNumber = "SAV-001";
        Double amount = 100.0;
        DepositRequestDto request = DepositRequestDto.builder()
            .accountNumber(accountNumber)
            .amount(amount)
            .build();

        // when
        Mockito.when(bankAccountService.depositToSavings(accountNumber, amount))
            .thenThrow(new IllegalArgumentException("Savings account is at maximum capacity"));

        // then
        Assertions.assertThatThrownBy(() -> bankAccountController.depositToSavings(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("maximum capacity");
    }

    @Test
    void should_deposit_partial_amount_when_exceeding_savings_limit() {
        // given
        String accountNumber = "SAV-001";
        Double amount = 100.0;
        DepositRequestDto request = DepositRequestDto.builder()
            .accountNumber(accountNumber)
            .amount(amount)
            .build();
        
        BankAccountDto updatedAccount = BankAccountDto.builder()
            .accountNumber(accountNumber)
            .savingsBalance(22950.0)
            .savingsDepositLimit(22950.0)
            .build();

        // when - only partial amount deposited to reach the limit
        Mockito.when(bankAccountService.depositToSavings(accountNumber, amount))
            .thenReturn(updatedAccount);

        // call
        final ResponseEntity<BankAccountDto> response = bankAccountController.depositToSavings(request);

        // then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getSavingsBalance()).isEqualTo(22950.0);
    }

    @Test
    void should_throw_exception_when_setting_overdraft_on_savings_account() {
        // given
        String accountNumber = "SAV-001";
        Double overdraftLimit = 100.0;
        OverdraftRequestDto request = OverdraftRequestDto.builder()
            .accountNumber(accountNumber)
            .overdraftLimit(overdraftLimit)
            .build();

        // when
        Mockito.when(bankAccountService.setOverdraftLimit(accountNumber, overdraftLimit))
            .thenThrow(new SavingsAccountOverdraftException());

        // then
        Assertions.assertThatThrownBy(() -> bankAccountController.setOverdraftLimit(request))
            .isInstanceOf(SavingsAccountOverdraftException.class)
            .hasMessageContaining("Savings accounts cannot have overdraft");
    }

    // ========================================
    // Feature 4: Statement (Relevé de compte)
    // ========================================

    @Test
    void should_get_statement_for_account() {
        // given
        String accountNumber = ACC_001;
        StatementDto expectedStatement = StatementDto.builder()
            .accountNumber(accountNumber)
            .accountType("Compte Courant")
            .currentBalance(500.0)
            .savingsBalance(0.0)
            .transactions(List.of())
            .build();

        // when
        Mockito.when(bankAccountService.getStatement(accountNumber))
            .thenReturn(expectedStatement);

        // call
        final ResponseEntity<StatementDto> response = bankAccountController.getStatement(accountNumber);

        // then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isEqualTo(expectedStatement);
        Assertions.assertThat(response.getBody().getAccountType()).isEqualTo("Compte Courant");
        Assertions.assertThat(response.getBody().getCurrentBalance()).isEqualTo(500.0);
    }

    @Test
    void should_get_statement_for_savings_account() {
        // given
        String accountNumber = "SAV-001";
        StatementDto expectedStatement = StatementDto.builder()
            .accountNumber(accountNumber)
            .accountType("Livret d'épargne")
            .currentBalance(0.0)
            .savingsBalance(5000.0)
            .transactions(List.of())
            .build();

        // when
        Mockito.when(bankAccountService.getStatement(accountNumber))
            .thenReturn(expectedStatement);

        // call
        final ResponseEntity<StatementDto> response = bankAccountController.getStatement(accountNumber);

        // then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getAccountType()).isEqualTo("Livret d'épargne");
        Assertions.assertThat(response.getBody().getSavingsBalance()).isEqualTo(5000.0);
    }

    @Test
    void should_get_statement_with_both_balances() {
        // given
        String accountNumber = "ACC-002";
        StatementDto expectedStatement = StatementDto.builder()
            .accountNumber(accountNumber)
            .accountType("Compte Courant + Livret d'épargne")
            .currentBalance(2000.0)
            .savingsBalance(3000.0)
            .transactions(List.of())
            .build();

        // when
        Mockito.when(bankAccountService.getStatement(accountNumber))
            .thenReturn(expectedStatement);

        // call
        final ResponseEntity<StatementDto> response = bankAccountController.getStatement(accountNumber);

        // then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getAccountType()).isEqualTo("Compte Courant + Livret d'épargne");
        Assertions.assertThat(response.getBody().getCurrentBalance()).isEqualTo(2000.0);
        Assertions.assertThat(response.getBody().getSavingsBalance()).isEqualTo(3000.0);
    }

    @Test
    void should_return_not_found_when_getting_statement_for_unknown_account() {
        // given
        String accountNumber = "UNKNOWN";

        // when
        Mockito.when(bankAccountService.getStatement(accountNumber))
            .thenThrow(new AccountNotFoundException(accountNumber));

        // then
        Assertions.assertThatThrownBy(() -> bankAccountController.getStatement(accountNumber))
            .isInstanceOf(AccountNotFoundException.class)
            .hasMessageContaining(accountNumber);
    }
}