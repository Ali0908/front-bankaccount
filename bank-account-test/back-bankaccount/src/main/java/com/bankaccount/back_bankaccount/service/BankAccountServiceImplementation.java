package com.bankaccount.back_bankaccount.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bankaccount.back_bankaccount.dto.BankAccountDto;
import com.bankaccount.back_bankaccount.exception.AccountNotFoundException;
import com.bankaccount.back_bankaccount.exception.InsufficientBalanceException;
import com.bankaccount.back_bankaccount.mapper.interfaces.IBankAccountMapper;
import com.bankaccount.back_bankaccount.model.BankAccountEntity;
import com.bankaccount.back_bankaccount.repository.IBankAcountRepository;
import com.bankaccount.back_bankaccount.service.interfaces.IBankAccountService;

@Service
public class BankAccountServiceImplementation implements IBankAccountService {

    private final IBankAcountRepository bankAccountRepository;
    private final IBankAccountMapper bankAccountMapper;

    public BankAccountServiceImplementation(IBankAcountRepository bankAccountRepository, 
                                        IBankAccountMapper bankAccountMapper) {
        this.bankAccountRepository = bankAccountRepository;
        this.bankAccountMapper = bankAccountMapper;
    }

    @Override
    public List<BankAccountDto> getAllBankAccounts() {
        return this.bankAccountMapper.toDtoList(this.bankAccountRepository.findAll());
    }

    @Override
    public BankAccountDto deposit(String accountNumber, Double amount) {
        BankAccountEntity account = bankAccountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        
        account.setBalance(account.getBalance() + amount);
        BankAccountEntity saved = bankAccountRepository.save(account);
        
        return bankAccountMapper.toDto(saved);
    }

    @Override
    public BankAccountDto withdraw(String accountNumber, Double amount) {
        BankAccountEntity account = bankAccountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        
        // Rule: withdrawal cannot be performed if amount exceeds available balance
        if (amount > account.getBalance()) {
            throw new InsufficientBalanceException(account.getBalance(), amount);
        }
        
        account.setBalance(account.getBalance() - amount);
        BankAccountEntity saved = bankAccountRepository.save(account);
        
        return bankAccountMapper.toDto(saved);
    }

    
}