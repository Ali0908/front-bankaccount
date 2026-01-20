-- Create sequence for bank_account id
CREATE SEQUENCE IF NOT EXISTS bank_account_id_seq;

-- Create bank_account table
CREATE TABLE IF NOT EXISTS bank_account (
    id BIGINT DEFAULT NEXTVAL('bank_account_id_seq') PRIMARY KEY,
    account_number VARCHAR(255) NOT NULL UNIQUE,
    balance DOUBLE NOT NULL,
    overdraft_limit DOUBLE NOT NULL DEFAULT 0.0,
    savings_balance DOUBLE NOT NULL DEFAULT 0.0,
    savings_deposit_limit DOUBLE NOT NULL DEFAULT 22950.0
);

-- Create sequence for transaction id
CREATE SEQUENCE IF NOT EXISTS transaction_id_seq;

-- Create transaction table
CREATE TABLE IF NOT EXISTS transaction (
    id BIGINT DEFAULT NEXTVAL('transaction_id_seq') PRIMARY KEY,
    account_number VARCHAR(255) NOT NULL,
    transaction_date TIMESTAMP NOT NULL,
    type VARCHAR(50) NOT NULL,
    amount DOUBLE NOT NULL,
    balance_after DOUBLE NOT NULL
);