package com.bankaccount.back_bankaccount.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankAccountDto implements Serializable {

    private Long id;
    private String accountNumber;
    private Double balance;
    private Double overdraftLimit;
    private Double savingsBalance;
    private Double savingsDepositLimit;
}
