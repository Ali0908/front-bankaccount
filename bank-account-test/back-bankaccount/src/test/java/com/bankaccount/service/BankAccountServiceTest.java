package com.bankaccount.service;

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
import com.bankaccount.back_bankaccount.exception.AccountNotFoundException;
import com.bankaccount.back_bankaccount.exception.InsufficientBalanceException;
import com.bankaccount.back_bankaccount.mapper.interfaces.IBankAccountMapper;
import com.bankaccount.back_bankaccount.model.BankAccountEntity;
import com.bankaccount.back_bankaccount.repository.IBankAcountRepository;
import com.bankaccount.back_bankaccount.service.BankAccountServiceImplementation;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {
	
    @InjectMocks
    private BankAccountServiceImplementation accountService;

    @Mock
    private IBankAcountRepository bankAccountRepository;

    @Mock
    private IBankAccountMapper bankAccountMapper;

    //given
    private BankAccountEntity bankAccountEntity = Mockito.mock(BankAccountEntity.class);
    private List<BankAccountEntity> bankAccountEntityList = Lists.newArrayList(bankAccountEntity);
    private BankAccountDto bankAccountDto = Mockito.mock(BankAccountDto.class);
    private List<BankAccountDto> bankAccountDtoList = Lists.newArrayList(bankAccountDto);

    //when
    @Test
    void when_getAllBankAccounts() {
        // when
        Mockito.when(bankAccountRepository.findAll()).thenReturn(bankAccountEntityList);
        Mockito.when(bankAccountMapper.toDtoList(bankAccountEntityList)).thenReturn(bankAccountDtoList);

        // call
        final List<BankAccountDto> result = accountService.getAllBankAccounts();

        // then
        Assertions.assertThat(result).isEqualTo(bankAccountDtoList);
        
    }

    @Test
    void should_deposit_money_and_update_balance() {
    // Given
    BankAccountEntity account = new BankAccountEntity();
    account.setAccountNumber("ACC-001");
    account.setBalance(100.0);
    
    BankAccountDto expectedDto = BankAccountDto.builder()
        .accountNumber("ACC-001")
        .balance(150.0)
        .build();
    
    Mockito.when(bankAccountRepository.findByAccountNumber("ACC-001"))
        .thenReturn(Optional.of(account));
    Mockito.when(bankAccountRepository.save(Mockito.any(BankAccountEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    Mockito.when(bankAccountMapper.toDto(Mockito.any(BankAccountEntity.class)))
        .thenReturn(expectedDto);

    // When
    BankAccountDto result = accountService.deposit("ACC-001", 50.0);

    // Then
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getBalance()).isEqualTo(150.0);
}

    @Test
    void should_throw_exception_when_account_not_found() {
        // Given
        Mockito.when(bankAccountRepository.findByAccountNumber("UNKNOWN"))
            .thenReturn(Optional.empty());

        // When / Then
        Assertions.assertThatThrownBy(() -> accountService.deposit("UNKNOWN", 50.0))
            .isInstanceOf(AccountNotFoundException.class)
            .hasMessageContaining("UNKNOWN");
    }

    @Test
    void should_withdraw_money_and_update_balance() {
        // Given
        BankAccountEntity account = new BankAccountEntity();
        account.setAccountNumber("ACC-001");
        account.setBalance(200.0);
        
        BankAccountDto expectedDto = BankAccountDto.builder()
            .accountNumber("ACC-001")
            .balance(150.0)
            .build();
        
        Mockito.when(bankAccountRepository.findByAccountNumber("ACC-001"))
            .thenReturn(Optional.of(account));
        Mockito.when(bankAccountRepository.save(Mockito.any(BankAccountEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(bankAccountMapper.toDto(Mockito.any(BankAccountEntity.class)))
            .thenReturn(expectedDto);

        // When
        BankAccountDto result = accountService.withdraw("ACC-001", 50.0);

        // Then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getBalance()).isEqualTo(150.0);
        Assertions.assertThat(account.getBalance()).isEqualTo(150.0);
    }

    @Test
    void should_throw_exception_when_withdrawing_from_unknown_account() {
        // Given
        Mockito.when(bankAccountRepository.findByAccountNumber("UNKNOWN"))
            .thenReturn(Optional.empty());

        // When / Then
        Assertions.assertThatThrownBy(() -> accountService.withdraw("UNKNOWN", 50.0))
            .isInstanceOf(AccountNotFoundException.class)
            .hasMessageContaining("UNKNOWN");
    }

    @Test
    void should_throw_exception_when_insufficient_balance() {
        // Given
        BankAccountEntity account = new BankAccountEntity();
        account.setAccountNumber("ACC-001");
        account.setBalance(100.0);
        
        Mockito.when(bankAccountRepository.findByAccountNumber("ACC-001"))
            .thenReturn(Optional.of(account));

        // When / Then
        Assertions.assertThatThrownBy(() -> accountService.withdraw("ACC-001", 150.0))
            .isInstanceOf(InsufficientBalanceException.class)
            .hasMessageContaining("Insufficient balance");
    }
}
