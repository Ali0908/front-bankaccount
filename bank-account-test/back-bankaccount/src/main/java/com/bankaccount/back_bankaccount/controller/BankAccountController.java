package com.bankaccount.back_bankaccount.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankaccount.back_bankaccount.controller.paths.ResourcePath;
import com.bankaccount.back_bankaccount.dto.BankAccountDto;
import com.bankaccount.back_bankaccount.dto.DepositRequestDto;
import com.bankaccount.back_bankaccount.dto.WithdrawRequestDto;
import com.bankaccount.back_bankaccount.service.interfaces.IBankAccountService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ResourcePath.PATH_BANK_ACCOUNT)
public class BankAccountController {
    
    private final IBankAccountService bankAccountService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BankAccountDto>> getAllBankAccounts() {
        return ResponseEntity.ok(bankAccountService.getAllBankAccounts());
    }

    @PostMapping(value = ResourcePath.PATH_CASH_DEPOSIT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BankAccountDto> deposit(@Valid @RequestBody DepositRequestDto request) {
        log.info("Deposit request: {} amount {} on account {}", 
            request.getAmount(), request.getAccountNumber());
        BankAccountDto result = bankAccountService.deposit(request.getAccountNumber(), request.getAmount());
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = ResourcePath.PATH_CASH_WITHDRAWAL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BankAccountDto> withdraw(@Valid @RequestBody WithdrawRequestDto request) {
        log.info("Withdrawal request: {} amount {} from account {}", 
            request.getAmount(), request.getAccountNumber());
        BankAccountDto result = bankAccountService.withdraw(request.getAccountNumber(), request.getAmount());
        return ResponseEntity.ok(result);
    }
}
