package com.bankaccount.back_bankaccount.adapters.in.rest;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankaccount.back_bankaccount.adapters.in.rest.mapper.BankAccountDtoMapper;
import com.bankaccount.back_bankaccount.adapters.in.rest.mapper.StatementDtoMapper;
import com.bankaccount.back_bankaccount.constants.BankAccountConstants;
import com.bankaccount.back_bankaccount.constants.ResourcePath;
import com.bankaccount.back_bankaccount.domain.model.BankAccount;
import com.bankaccount.back_bankaccount.domain.model.Statement;
import com.bankaccount.back_bankaccount.domain.ports.in.*;
import com.bankaccount.back_bankaccount.dto.BankAccountDto;
import com.bankaccount.back_bankaccount.dto.DepositRequestDto;
import com.bankaccount.back_bankaccount.dto.OverdraftRequestDto;
import com.bankaccount.back_bankaccount.dto.StatementDto;
import com.bankaccount.back_bankaccount.dto.WithdrawRequestDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller - Primary Adapter.
 * Implements the REST API and calls use cases through ports.
 * Part of the adapters/in layer (primary adapters).
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ResourcePath.PATH_BANK_ACCOUNT)
public class BankAccountRestController {
    
    private final GetAllAccountsUseCase getAllAccountsUseCase;
    private final DepositMoneyUseCase depositMoneyUseCase;
    private final WithdrawMoneyUseCase withdrawMoneyUseCase;
    private final SetOverdraftLimitUseCase setOverdraftLimitUseCase;
    private final DepositToSavingsUseCase depositToSavingsUseCase;
    private final GetStatementUseCase getStatementUseCase;
    
    private final BankAccountDtoMapper accountMapper;
    private final StatementDtoMapper statementMapper;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BankAccountDto>> getAllBankAccounts() {
        List<BankAccount> accounts = getAllAccountsUseCase.getAllAccounts();
        return ResponseEntity.ok(accountMapper.toDtoList(accounts));
    }

    @PostMapping(value = ResourcePath.PATH_CASH_DEPOSIT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BankAccountDto> deposit(@Valid @RequestBody DepositRequestDto request) {
        log.info("Deposit request: {} amount {} on account {}", 
            request.getAmount(), request.getAccountNumber());
        
        BankAccount account = depositMoneyUseCase.deposit(
            request.getAccountNumber(), 
            request.getAmount()
        );
        
        return ResponseEntity.ok(accountMapper.toDto(account));
    }

    @PostMapping(value = ResourcePath.PATH_CASH_WITHDRAWAL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BankAccountDto> withdraw(@Valid @RequestBody WithdrawRequestDto request) {
        log.info("Withdrawal request: {} amount {} from account {}", 
            request.getAmount(), request.getAccountNumber());
        
        BankAccount account = withdrawMoneyUseCase.withdraw(
            request.getAccountNumber(), 
            request.getAmount()
        );
        
        return ResponseEntity.ok(accountMapper.toDto(account));
    }

    @PostMapping(value = ResourcePath.PATH_OVERDRAFT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BankAccountDto> setOverdraft(@Valid @RequestBody OverdraftRequestDto request) {
        log.info("Set overdraft request: {} limit for account {}", 
            request.getOverdraftLimit(), request.getAccountNumber());
        
        BankAccount account = setOverdraftLimitUseCase.setOverdraftLimit(
            request.getAccountNumber(), 
            request.getOverdraftLimit()
        );
        
        return ResponseEntity.ok(accountMapper.toDto(account));
    }

    @PostMapping(value = ResourcePath.PATH_SAVINGS_DEPOSIT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BankAccountDto> depositToSavings(@Valid @RequestBody DepositRequestDto request) {
        log.info("Deposit to savings request: {} amount {} on account {}", 
            request.getAmount(), request.getAccountNumber());
        
        BankAccount account = depositToSavingsUseCase.depositToSavings(
            request.getAccountNumber(), 
            request.getAmount()
        );
        
        return ResponseEntity.ok(accountMapper.toDto(account));
    }

    @GetMapping(value = ResourcePath.PATH_STATEMENT_BY_ACCOUNT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StatementDto> getStatement(@PathVariable(BankAccountConstants.ACCOUNT_NUMBER) String accountNumber) {
        log.info("Get statement request for account {}", accountNumber);
        
        Statement statement = getStatementUseCase.getStatement(accountNumber);
        
        return ResponseEntity.ok(statementMapper.toDto(statement));
    }
}
