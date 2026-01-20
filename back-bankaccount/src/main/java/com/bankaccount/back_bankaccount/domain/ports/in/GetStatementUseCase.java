package com.bankaccount.back_bankaccount.domain.ports.in;

import com.bankaccount.back_bankaccount.domain.model.Statement;

/**
 * Input port for getting account statement.
 * This is a use case interface (primary port).
 */
public interface GetStatementUseCase {
    Statement getStatement(String accountNumber);
}
