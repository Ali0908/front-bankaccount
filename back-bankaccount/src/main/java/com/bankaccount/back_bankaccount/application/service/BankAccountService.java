package com.bankaccount.back_bankaccount.application.service;

import com.bankaccount.back_bankaccount.domain.model.BankAccount;
import com.bankaccount.back_bankaccount.domain.model.Statement;
import com.bankaccount.back_bankaccount.domain.model.Transaction;
import com.bankaccount.back_bankaccount.domain.ports.in.*;
import com.bankaccount.back_bankaccount.domain.ports.out.BankAccountRepositoryPort;
import com.bankaccount.back_bankaccount.domain.ports.out.TransactionRepositoryPort;
import com.bankaccount.back_bankaccount.exception.AccountNotFoundException;
import com.bankaccount.back_bankaccount.exception.InsufficientBalanceException;
import com.bankaccount.back_bankaccount.exception.SavingsAccountOverdraftException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Application service implementing all use cases.
 * This is the core business logic layer (application layer).
 * It orchestrates domain objects and uses ports for external dependencies.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class BankAccountService implements 
        GetAllAccountsUseCase,
        DepositMoneyUseCase,
        WithdrawMoneyUseCase,
        SetOverdraftLimitUseCase,
        DepositToSavingsUseCase,
        GetStatementUseCase {

    private final BankAccountRepositoryPort accountRepository;
    private final TransactionRepositoryPort transactionRepository;

    @Override
    public List<BankAccount> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public BankAccount deposit(String accountNumber, Double amount) {
        BankAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        
        // Use domain logic
        account.deposit(amount);
        
        // Save account
        BankAccount savedAccount = accountRepository.save(account);
        
        // Record transaction using domain factory method
        Transaction transaction = Transaction.createDeposit(
                accountNumber, 
                amount, 
                savedAccount.getBalance()
        );
        transactionRepository.save(transaction);
        
        return savedAccount;
    }

    @Override
    public BankAccount withdraw(String accountNumber, Double amount) {
        BankAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        
        // Check business rule
        if (!account.canWithdraw(amount)) {
            throw new InsufficientBalanceException(account.getBalance(), amount);
        }
        
        // Use domain logic
        account.withdraw(amount);
        
        // Save account
        BankAccount savedAccount = accountRepository.save(account);
        
        // Record transaction using domain factory method
        Transaction transaction = Transaction.createWithdrawal(
                accountNumber, 
                amount, 
                savedAccount.getBalance()
        );
        transactionRepository.save(transaction);
        
        return savedAccount;
    }

    @Override
    public BankAccount setOverdraftLimit(String accountNumber, Double overdraftLimit) {
        BankAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        
        // Use domain logic (includes validation)
        try {
            account.setOverdraft(overdraftLimit);
        } catch (IllegalStateException e) {
            throw new SavingsAccountOverdraftException();
        }
        
        return accountRepository.save(account);
    }

    @Override
    public BankAccount depositToSavings(String accountNumber, Double amount) {
        BankAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        
        // Use domain logic (handles partial deposit)
        Double depositedAmount = account.depositToSavings(amount);
        
        // Save account
        BankAccount savedAccount = accountRepository.save(account);
        
        // Record transaction using domain factory method
        Transaction transaction = Transaction.createSavingsDeposit(
                accountNumber, 
                depositedAmount, 
                savedAccount.getSavingsBalance()
        );
        transactionRepository.save(transaction);
        
        return savedAccount;
    }

    @Override
    public Statement getStatement(String accountNumber) {
        BankAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        
        // Get transactions from last 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Transaction> transactions = transactionRepository
                .findByAccountNumberAndDateAfter(accountNumber, thirtyDaysAgo);
        
        // Build statement using domain logic
        return Statement.builder()
                .accountNumber(accountNumber)
                .accountType(account.getAccountType())
                .currentBalance(account.getBalance())
                .savingsBalance(account.getSavingsBalance())
                .statementDate(LocalDateTime.now())
                .transactions(transactions)
                .build();
    }
}
