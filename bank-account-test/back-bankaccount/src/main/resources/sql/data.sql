-- Sample bank accounts for testing
-- This file is automatically executed by Spring Boot on application startup

MERGE INTO bank_account (id, account_number, balance) 
KEY (account_number) 
VALUES (NEXTVAL('bank_account_id_seq'), 'ACC001', 2500.50);
