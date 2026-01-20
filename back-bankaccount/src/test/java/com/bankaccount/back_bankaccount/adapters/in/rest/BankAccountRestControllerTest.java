package com.bankaccount.back_bankaccount.adapters.in.rest;

import com.bankaccount.back_bankaccount.adapters.in.rest.mapper.BankAccountDtoMapper;
import com.bankaccount.back_bankaccount.adapters.in.rest.mapper.StatementDtoMapper;
import com.bankaccount.back_bankaccount.domain.model.BankAccount;
import com.bankaccount.back_bankaccount.domain.model.Statement;
import com.bankaccount.back_bankaccount.domain.ports.in.*;
import com.bankaccount.back_bankaccount.dto.BankAccountDto;
import com.bankaccount.back_bankaccount.dto.DepositRequestDto;
import com.bankaccount.back_bankaccount.dto.OverdraftRequestDto;
import com.bankaccount.back_bankaccount.dto.StatementDto;
import com.bankaccount.back_bankaccount.dto.WithdrawRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BankAccountRestController (Primary Adapter).
 * Tests the REST API layer.
 */
@ExtendWith(MockitoExtension.class)
class BankAccountRestControllerTest {

    private static final String ACC_001 = "ACC-001";

    @InjectMocks
    private BankAccountRestController controller;

    @Mock
    private GetAllAccountsUseCase getAllAccountsUseCase;

    @Mock
    private DepositMoneyUseCase depositMoneyUseCase;

    @Mock
    private WithdrawMoneyUseCase withdrawMoneyUseCase;

    @Mock
    private SetOverdraftLimitUseCase setOverdraftLimitUseCase;

    @Mock
    private DepositToSavingsUseCase depositToSavingsUseCase;

    @Mock
    private GetStatementUseCase getStatementUseCase;

    @Mock
    private BankAccountDtoMapper accountMapper;

    @Mock
    private StatementDtoMapper statementMapper;

    private BankAccount account;
    private BankAccountDto accountDto;

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

        accountDto = BankAccountDto.builder()
                .id(1L)
                .accountNumber(ACC_001)
                .balance(100.0)
                .overdraftLimit(50.0)
                .savingsBalance(0.0)
                .savingsDepositLimit(22950.0)
                .build();
    }

    @Test
    void should_get_all_accounts() {
        // Given
        List<BankAccount> accounts = List.of(account);
        List<BankAccountDto> dtos = List.of(accountDto);

        when(getAllAccountsUseCase.getAllAccounts()).thenReturn(accounts);
        when(accountMapper.toDtoList(accounts)).thenReturn(dtos);

        // When
        ResponseEntity<List<BankAccountDto>> response = controller.getAllBankAccounts();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(getAllAccountsUseCase).getAllAccounts();
        verify(accountMapper).toDtoList(accounts);
    }

    @Test
    void should_deposit_money() {
        // Given
        DepositRequestDto request = DepositRequestDto.builder()
                .accountNumber(ACC_001)
                .amount(50.0)
                .build();

        BankAccount updatedAccount = BankAccount.builder()
                .accountNumber(ACC_001)
                .balance(150.0)
                .build();

        BankAccountDto updatedDto = BankAccountDto.builder()
                .accountNumber(ACC_001)
                .balance(150.0)
                .build();

        when(depositMoneyUseCase.deposit(ACC_001, 50.0)).thenReturn(updatedAccount);
        when(accountMapper.toDto(updatedAccount)).thenReturn(updatedDto);

        // When
        ResponseEntity<BankAccountDto> response = controller.deposit(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getBalance()).isEqualTo(150.0);
        verify(depositMoneyUseCase).deposit(ACC_001, 50.0);
    }

    @Test
    void should_withdraw_money() {
        // Given
        WithdrawRequestDto request = WithdrawRequestDto.builder()
                .accountNumber(ACC_001)
                .amount(30.0)
                .build();

        BankAccount updatedAccount = BankAccount.builder()
                .accountNumber(ACC_001)
                .balance(70.0)
                .build();

        BankAccountDto updatedDto = BankAccountDto.builder()
                .accountNumber(ACC_001)
                .balance(70.0)
                .build();

        when(withdrawMoneyUseCase.withdraw(ACC_001, 30.0)).thenReturn(updatedAccount);
        when(accountMapper.toDto(updatedAccount)).thenReturn(updatedDto);

        // When
        ResponseEntity<BankAccountDto> response = controller.withdraw(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getBalance()).isEqualTo(70.0);
        verify(withdrawMoneyUseCase).withdraw(ACC_001, 30.0);
    }

    @Test
    void should_set_overdraft() {
        // Given
        OverdraftRequestDto request = OverdraftRequestDto.builder()
                .accountNumber(ACC_001)
                .overdraftLimit(200.0)
                .build();

        BankAccount updatedAccount = BankAccount.builder()
                .accountNumber(ACC_001)
                .overdraftLimit(200.0)
                .build();

        BankAccountDto updatedDto = BankAccountDto.builder()
                .accountNumber(ACC_001)
                .overdraftLimit(200.0)
                .build();

        when(setOverdraftLimitUseCase.setOverdraftLimit(ACC_001, 200.0)).thenReturn(updatedAccount);
        when(accountMapper.toDto(updatedAccount)).thenReturn(updatedDto);

        // When
        ResponseEntity<BankAccountDto> response = controller.setOverdraft(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getOverdraftLimit()).isEqualTo(200.0);
        verify(setOverdraftLimitUseCase).setOverdraftLimit(ACC_001, 200.0);
    }

    @Test
    void should_deposit_to_savings() {
        // Given
        DepositRequestDto request = DepositRequestDto.builder()
                .accountNumber(ACC_001)
                .amount(1000.0)
                .build();

        BankAccount updatedAccount = BankAccount.builder()
                .accountNumber(ACC_001)
                .savingsBalance(1000.0)
                .build();

        BankAccountDto updatedDto = BankAccountDto.builder()
                .accountNumber(ACC_001)
                .savingsBalance(1000.0)
                .build();

        when(depositToSavingsUseCase.depositToSavings(ACC_001, 1000.0)).thenReturn(updatedAccount);
        when(accountMapper.toDto(updatedAccount)).thenReturn(updatedDto);

        // When
        ResponseEntity<BankAccountDto> response = controller.depositToSavings(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getSavingsBalance()).isEqualTo(1000.0);
        verify(depositToSavingsUseCase).depositToSavings(ACC_001, 1000.0);
    }

    @Test
    void should_get_statement() {
        // Given
        Statement statement = Statement.builder()
                .accountNumber(ACC_001)
                .accountType("Compte Courant")
                .currentBalance(100.0)
                .savingsBalance(0.0)
                .statementDate(LocalDateTime.now())
                .transactions(List.of())
                .build();

        StatementDto statementDto = StatementDto.builder()
                .accountNumber(ACC_001)
                .accountType("Compte Courant")
                .currentBalance(100.0)
                .savingsBalance(0.0)
                .statementDate(LocalDateTime.now())
                .transactions(List.of())
                .build();

        when(getStatementUseCase.getStatement(ACC_001)).thenReturn(statement);
        when(statementMapper.toDto(statement)).thenReturn(statementDto);

        // When
        ResponseEntity<StatementDto> response = controller.getStatement(ACC_001);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getAccountNumber()).isEqualTo(ACC_001);
        verify(getStatementUseCase).getStatement(ACC_001);
    }
}
