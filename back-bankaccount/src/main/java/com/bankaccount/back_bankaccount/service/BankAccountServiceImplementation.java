package com.bankaccount.back_bankaccount.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bankaccount.back_bankaccount.dto.BankAccountDto;
import com.bankaccount.back_bankaccount.dto.StatementDto;
import com.bankaccount.back_bankaccount.dto.TransactionDto;
import com.bankaccount.back_bankaccount.exception.AccountNotFoundException;
import com.bankaccount.back_bankaccount.exception.InsufficientBalanceException;
import com.bankaccount.back_bankaccount.exception.SavingsAccountOverdraftException;
import com.bankaccount.back_bankaccount.mapper.interfaces.IBankAccountMapper;
import com.bankaccount.back_bankaccount.model.BankAccountEntity;
import com.bankaccount.back_bankaccount.model.TransactionEntity;
import com.bankaccount.back_bankaccount.model.TransactionType;
import com.bankaccount.back_bankaccount.repository.IBankAcountRepository;
import com.bankaccount.back_bankaccount.repository.ITransactionRepository;
import com.bankaccount.back_bankaccount.service.interfaces.IBankAccountService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImplementation implements IBankAccountService {

    private final IBankAcountRepository bankAccountRepository;
    private final IBankAccountMapper bankAccountMapper;
    private final ITransactionRepository transactionRepository;

    @Override
    public List<BankAccountDto> getAllBankAccounts() {
        return this.bankAccountMapper.toDtoList(this.bankAccountRepository.findAll());
    }

    @Override
    public BankAccountDto deposit(String accountNumber, Double amount) {
        BankAccountEntity account = bankAccountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        
        Double newBalance = account.getBalance() + amount;
        account.setBalance(newBalance);
        BankAccountEntity saved = bankAccountRepository.save(account);
        
        // Record transaction
        TransactionEntity transaction = TransactionEntity.builder()
            .accountNumber(accountNumber)
            .transactionDate(LocalDateTime.now())
            .type(TransactionType.DEPOSIT_CURRENT)
            .amount(amount)
            .balanceAfter(newBalance)
            .build();
        transactionRepository.save(transaction);
        
        return bankAccountMapper.toDto(saved);
    }

    @Override
    public BankAccountDto withdraw(String accountNumber, Double amount) {
        BankAccountEntity account = bankAccountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        
        Double finalBalance = account.getBalance() - amount;
        Double minAllowedBalance = -account.getOverdraftLimit();
        
        // Rule: withdrawal cannot be performed if final balance exceeds overdraft limit
        if (finalBalance < minAllowedBalance) {
            throw new InsufficientBalanceException(account.getBalance(), amount);
        }
        
        account.setBalance(finalBalance);
        BankAccountEntity saved = bankAccountRepository.save(account);
        
        // Record transaction (negative amount for withdrawal)
        TransactionEntity transaction = TransactionEntity.builder()
            .accountNumber(accountNumber)
            .transactionDate(LocalDateTime.now())
            .type(TransactionType.WITHDRAWAL)
            .amount(-amount)
            .balanceAfter(finalBalance)
            .build();
        transactionRepository.save(transaction);
        
        return bankAccountMapper.toDto(saved);
    }

    @Override
    public BankAccountDto setOverdraftLimit(String accountNumber, Double overdraftLimit) {
        if (overdraftLimit < 0 || overdraftLimit > 300) {
            throw new IllegalArgumentException("Overdraft limit must be between 0 and 300");
        }
        
        BankAccountEntity account = bankAccountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        
        // Savings accounts must not have overdraft
        boolean isSavingsByParam = accountNumber != null && accountNumber.toUpperCase().startsWith("SAV-");
        String entityAccountNumber = account.getAccountNumber();
        boolean isSavingsByEntity = entityAccountNumber != null && entityAccountNumber.toUpperCase().startsWith("SAV-");
        if (isSavingsByParam || isSavingsByEntity) {
            throw new SavingsAccountOverdraftException();
        }
        
        account.setOverdraftLimit(overdraftLimit);
        BankAccountEntity saved = bankAccountRepository.save(account);
        
        return bankAccountMapper.toDto(saved);
    }

    @Override
    public BankAccountDto depositToSavings(String accountNumber, Double amount) {
        BankAccountEntity account = bankAccountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        
        Double savingsLimit = account.getSavingsDepositLimit();
        Double currentSavings = account.getSavingsBalance() != null ? account.getSavingsBalance() : 0.0;
        Double availableSpace = savingsLimit - currentSavings;
        
        // If savings account is already at limit, throw exception
        if (availableSpace <= 0) {
            throw new IllegalArgumentException("The savings account is at maximum capacity (limit: " + savingsLimit + "€)");
        }
        
        // Deposit partial amount if exceeds limit
        Double depositAmount = Math.min(amount, availableSpace);
        Double newSavingsBalance = currentSavings + depositAmount;
        account.setSavingsBalance(newSavingsBalance);
        
        BankAccountEntity saved = bankAccountRepository.save(account);
        
        // Record transaction
        TransactionEntity transaction = TransactionEntity.builder()
            .accountNumber(accountNumber)
            .transactionDate(LocalDateTime.now())
            .type(TransactionType.DEPOSIT_SAVINGS)
            .amount(depositAmount)
            .balanceAfter(newSavingsBalance)
            .build();
        transactionRepository.save(transaction);
        
        return bankAccountMapper.toDto(saved);
    }

    @Override
    public StatementDto getStatement(String accountNumber) {
        BankAccountEntity account = bankAccountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        
        // Get transactions from last 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<TransactionEntity> transactions = transactionRepository
            .findByAccountNumberAndTransactionDateAfterOrderByTransactionDateDesc(accountNumber, thirtyDaysAgo);
        
        // Convert transactions to DTOs
        List<TransactionDto> transactionDtos = transactions.stream()
            .map(tx -> TransactionDto.builder()
                .date(tx.getTransactionDate())
                .type(tx.getType().getLabel())
                .amount(tx.getAmount())
                .balanceAfter(tx.getBalanceAfter())
                .build())
            .toList();
        
        // Determine account type
        String accountType = determineAccountType(account);
        
        return StatementDto.builder()
            .accountNumber(accountNumber)
            .accountType(accountType)
            .currentBalance(account.getBalance())
            .savingsBalance(account.getSavingsBalance())
            .statementDate(LocalDateTime.now())
            .transactions(transactionDtos)
            .build();
    }

    private String determineAccountType(BankAccountEntity account) {
        boolean hasSavings = account.getSavingsBalance() != null && account.getSavingsBalance() > 0;
        boolean hasCurrentBalance = account.getBalance() != null && account.getBalance() != 0;
        
        if (hasSavings && hasCurrentBalance) {
            return "Compte Courant + Livret d'épargne";
        } else if (hasSavings) {
            return "Livret d'épargne";
        } else {
            return "Compte Courant";
        }
    }

    
}