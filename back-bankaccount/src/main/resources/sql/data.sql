-- Sample bank accounts for testing
-- This file is automatically executed by Spring Boot on application startup

INSERT INTO bank_account (account_number, balance, overdraft_limit, savings_balance, savings_deposit_limit) 
VALUES ('ACC001-001', 2500.50, 300.0, 500.00, 22950.0);

-- Sample transactions for testing
INSERT INTO transaction (account_number, transaction_date, type, amount, balance_after)
VALUES 
    ('ACC001', DATEADD('DAY', -5, CURRENT_TIMESTAMP), 'DEPOSIT_CURRENT', 500.0, 2500.50),
    ('ACC001', DATEADD('DAY', -10, CURRENT_TIMESTAMP), 'WITHDRAWAL', -200.0, 2000.50),
    ('ACC001', DATEADD('DAY', -15, CURRENT_TIMESTAMP), 'DEPOSIT_CURRENT', 1000.0, 2200.50),
    ('ACC001', DATEADD('DAY', -20, CURRENT_TIMESTAMP), 'DEPOSIT_SAVINGS', 500.0, 500.0),
    ('ACC001', DATEADD('DAY', -25, CURRENT_TIMESTAMP), 'WITHDRAWAL', -300.0, 1200.50);