-- Create sequence for bank_account id
CREATE SEQUENCE IF NOT EXISTS bank_account_id_seq;

-- Create bank_account table
CREATE TABLE IF NOT EXISTS bank_account (
    id BIGINT DEFAULT NEXTVAL('bank_account_id_seq') PRIMARY KEY,
    account_number VARCHAR(255) NOT NULL UNIQUE,
    balance DOUBLE NOT NULL
);
