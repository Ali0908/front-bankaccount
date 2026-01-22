package com.bankaccount.back_bankaccount.dto;

import java.io.Serializable;

import com.bankaccount.back_bankaccount.constants.BankAccountConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for deposit operation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositRequestDto implements Serializable {

    @NotBlank(message = BankAccountConstants.ACCOUNT_NUMBER_REQUIRED_MESSAGE)
    private String accountNumber;
    
    @NotNull(message = BankAccountConstants.AMOUNT_REQUIRED_MESSAGE)
    @Positive(message = BankAccountConstants.AMOUNT_POSITIVE_MESSAGE)
    private Double amount;
}
