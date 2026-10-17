-- ====================================================================
-- SIMULATED UPI PAYMENT SYSTEM - DATABASE SCHEMA (schema.sql)
-- Compatible with H2 (File/Memory) and MySQL 8.x
-- ====================================================================

-- 1. Table: users
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    mobile VARCHAR(10) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    upi_id VARCHAR(50) NOT NULL UNIQUE,
    upi_pin_hash VARCHAR(255) NOT NULL,
    failed_pin_attempts INT NOT NULL DEFAULT 0,
    is_pin_locked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Table: bank_accounts
-- OOP: Abstraction & Inheritance (SavingsAccount & CurrentAccount stored via Single Table strategy with account_type discriminator)
CREATE TABLE IF NOT EXISTS bank_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    account_number VARCHAR(16) NOT NULL UNIQUE,
    bank_name VARCHAR(100) NOT NULL,
    ifsc_code VARCHAR(11) NOT NULL,
    account_type VARCHAR(20) NOT NULL, -- 'SAVINGS' or 'CURRENT'
    balance DECIMAL(15, 2) NOT NULL,
    minimum_balance DECIMAL(15, 2) NOT NULL,
    daily_transfer_limit DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bank_accounts_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Table: transactions
-- OOP: Abstraction & Inheritance (SendMoneyTransaction & CollectRequestTransaction stored via discriminator transaction_type)
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    utr VARCHAR(12) NOT NULL UNIQUE,
    sender_user_id BIGINT,
    receiver_user_id BIGINT NOT NULL,
    sender_account_id BIGINT,
    receiver_account_id BIGINT NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    charges DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    transaction_type VARCHAR(30) NOT NULL, -- 'SEND_MONEY' or 'COLLECT_REQUEST'
    status VARCHAR(20) NOT NULL,          -- 'SUCCESS', 'FAILED', 'PENDING'
    failure_reason VARCHAR(255),
    note VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tx_sender_user FOREIGN KEY (sender_user_id) REFERENCES users(id),
    CONSTRAINT fk_tx_receiver_user FOREIGN KEY (receiver_user_id) REFERENCES users(id),
    CONSTRAINT fk_tx_sender_account FOREIGN KEY (sender_account_id) REFERENCES bank_accounts(id),
    CONSTRAINT fk_tx_receiver_account FOREIGN KEY (receiver_account_id) REFERENCES bank_accounts(id)
);

-- 4. Table: login_audit
CREATE TABLE IF NOT EXISTS login_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    mobile VARCHAR(10) NOT NULL,
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    status VARCHAR(20) NOT NULL, -- 'SUCCESS' or 'FAILURE'
    failure_reason VARCHAR(255),
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_users_mobile ON users(mobile);
CREATE INDEX IF NOT EXISTS idx_users_upi_id ON users(upi_id);
CREATE INDEX IF NOT EXISTS idx_tx_utr ON transactions(utr);
CREATE INDEX IF NOT EXISTS idx_tx_created_at ON transactions(created_at);
