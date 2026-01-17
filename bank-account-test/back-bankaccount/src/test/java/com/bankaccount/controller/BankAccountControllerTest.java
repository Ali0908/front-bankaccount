package com.bankaccount.controller;
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

import com.bankaccount.back_bankaccount.controller.BankAccountController;
import com.bankaccount.back_bankaccount.dto.BankAccountDto;
import com.bankaccount.back_bankaccount.dto.DepositRequestDto;
import com.bankaccount.back_bankaccount.dto.WithdrawRequestDto;
import com.bankaccount.back_bankaccount.exception.AccountNotFoundException;
import com.bankaccount.back_bankaccount.exception.InsufficientBalanceException;
import com.bankaccount.back_bankaccount.service.interfaces.IBankAccountService;



@ExtendWith(MockitoExtension.class)
class BankAccountControllerTest {

    @Mock
    private IBankAccountService bankAccountService;

    @InjectMocks
    private BankAccountController bankAccountController;

    // given
    private final BankAccountDto bankAccountDto = Mockito.mock(BankAccountDto.class);
    private final List<BankAccountDto> bankAccountDtoList = List.of(bankAccountDto);
    

    @Test
    void should_return_all_accounts_when_calling_getAllBankAccounts() {
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
        String accountNumber = "ACC-001";
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
        String accountNumber = "ACC-001";
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
        String accountNumber = "ACC-001";
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
}
