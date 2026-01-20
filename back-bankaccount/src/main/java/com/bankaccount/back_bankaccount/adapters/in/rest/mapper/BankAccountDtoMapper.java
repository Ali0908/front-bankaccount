package com.bankaccount.back_bankaccount.adapters.in.rest.mapper;

import com.bankaccount.back_bankaccount.domain.model.BankAccount;
import com.bankaccount.back_bankaccount.dto.BankAccountDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper between domain BankAccount and REST DTO.
 * Part of the primary adapter (REST API).
 */
@Component
public class BankAccountDtoMapper {
    
    /**
     * Convert domain model to DTO
     */
    public BankAccountDto toDto(BankAccount domain) {
        if (domain == null) {
            return null;
        }
        
        BankAccountDto dto = new BankAccountDto();
        dto.setId(domain.getId());
        dto.setAccountNumber(domain.getAccountNumber());
        dto.setBalance(domain.getBalance());
        dto.setOverdraftLimit(domain.getOverdraftLimit());
        dto.setSavingsBalance(domain.getSavingsBalance());
        dto.setSavingsDepositLimit(domain.getSavingsDepositLimit());
        
        return dto;
    }
    
    /**
     * Convert list of domain models to DTOs
     */
    public List<BankAccountDto> toDtoList(List<BankAccount> domains) {
        if (domains == null) {
            return null;
        }
        
        return domains.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
