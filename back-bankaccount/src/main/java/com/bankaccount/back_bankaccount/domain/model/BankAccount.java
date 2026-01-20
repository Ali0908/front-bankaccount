package com.bankaccount.back_bankaccount.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pure domain model for BankAccount.
 * No infrastructure dependencies (JPA, Spring, etc.)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    
    private Long id;
    private String accountNumber;
    private Double balance;
    private Double overdraftLimit;
    private Double savingsBalance;
    private Double savingsDepositLimit;

    /**
     * Business rule: Check if account can withdraw amount
     */
    public boolean canWithdraw(Double amount) {
        Double finalBalance = this.balance - amount;
        Double minAllowedBalance = -this.overdraftLimit;
        return finalBalance >= minAllowedBalance;
    }

    /**
     * Business rule: Perform withdrawal
     */
    public void withdraw(Double amount) {
        if (!canWithdraw(amount)) {
            throw new IllegalStateException("Insufficient balance for withdrawal");
        }
        this.balance -= amount;
    }

    /**
     * Business rule: Perform deposit
     */
    public void deposit(Double amount) {
        this.balance += amount;
    }

    /**
     * Business rule: Check if account is a savings account
     */
    public boolean isSavingsAccount() {
        return this.accountNumber != null && 
               this.accountNumber.toUpperCase().startsWith("SAV-");
    }

    /**
     * Business rule: Set overdraft limit (max 300€, not allowed for savings)
     */
    public void setOverdraft(Double limit) {
        if (limit < 0 || limit > 300) {
            throw new IllegalArgumentException("Overdraft limit must be between 0 and 300");
        }
        if (isSavingsAccount()) {
            throw new IllegalStateException("Savings accounts cannot have overdraft");
        }
        this.overdraftLimit = limit;
    }

    /**
     * Business rule: Get available space in savings account
     */
    public Double getSavingsAvailableSpace() {
        Double currentSavings = this.savingsBalance != null ? this.savingsBalance : 0.0;
        return this.savingsDepositLimit - currentSavings;
    }

    /**
     * Business rule: Deposit to savings (partial if exceeds limit)
     */
    public Double depositToSavings(Double amount) {
        Double availableSpace = getSavingsAvailableSpace();
        
        if (availableSpace <= 0) {
            throw new IllegalStateException("Savings account is at maximum capacity");
        }
        
        Double depositAmount = Math.min(amount, availableSpace);
        Double currentSavings = this.savingsBalance != null ? this.savingsBalance : 0.0;
        this.savingsBalance = currentSavings + depositAmount;
        
        return depositAmount;
    }

    /**
     * Business rule: Determine account type
     */
    public String getAccountType() {
        boolean hasSavings = this.savingsBalance != null && this.savingsBalance > 0;
        boolean hasCurrentBalance = this.balance != null && this.balance != 0;
        
        if (hasSavings && hasCurrentBalance) {
            return "Compte Courant + Livret d'épargne";
        } else if (hasSavings) {
            return "Livret d'épargne";
        } else {
            return "Compte Courant";
        }
    }
}
