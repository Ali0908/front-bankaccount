package com.bankaccount.back_bankaccount.config;

import com.bankaccount.back_bankaccount.adapters.out.persistence.BankAccountPersistenceAdapter;
import com.bankaccount.back_bankaccount.adapters.out.persistence.TransactionPersistenceAdapter;
import com.bankaccount.back_bankaccount.application.service.BankAccountService;
import com.bankaccount.back_bankaccount.domain.ports.in.*;
import com.bankaccount.back_bankaccount.domain.ports.out.BankAccountRepositoryPort;
import com.bankaccount.back_bankaccount.domain.ports.out.TransactionRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Hexagonal Architecture.
 * Wires together the domain, application, and adapter layers.
 */
@Configuration
public class HexagonalArchitectureConfig {
    
    /**
     * Create a single instance of BankAccountService that implements all use cases.
     * The service is annotated with @Service, so this configuration is optional.
     * However, it can be useful for explicitly wiring dependencies in hexagonal architecture.
     */
    // No additional beans needed - BankAccountService is already annotated with @Service
    // and implements all use case interfaces. Spring will automatically inject it 
    // wherever a use case interface is required.
}
