-- ====================================================================
-- SIMULATED UPI PAYMENT SYSTEM - SEED DATA (data.sql)
-- 5 Users, 5 Bank Accounts, 15 Transactions, and Login Audits
-- Default Password for all users: 'password123'
-- Default UPI PIN for all users: '1234'
-- ====================================================================

-- 1. Insert 5 Users
-- BCrypt hash for 'password123' -> $2a$10$7EqJtq98hPqEX7fNZaFWoOhi5F788gcWCuqcWB35LF6L45pq4Cd/2
-- BCrypt hash for '1234'        -> $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
MERGE INTO users (id, name, mobile, email, password_hash, upi_id, upi_pin_hash, failed_pin_attempts, is_pin_locked, created_at)
KEY(id)
VALUES
(1, 'Anukul Sharma', '9876543210', 'anukul@simupi.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5F788gcWCuqcWB35LF6L45pq4Cd/2', 'anukul@simupi', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 0, FALSE, '2026-09-01 10:00:00'),
(2, 'Priya Patel', '9876543211', 'priya@simupi.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5F788gcWCuqcWB35LF6L45pq4Cd/2', 'priya@simupi', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 0, FALSE, '2026-09-02 11:30:00'),
(3, 'Rahul Verma', '9876543212', 'rahul@simupi.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5F788gcWCuqcWB35LF6L45pq4Cd/2', 'rahul@simupi', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 0, FALSE, '2026-09-05 14:15:00'),
(4, 'Sneha Reddy', '9876543213', 'sneha@simupi.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5F788gcWCuqcWB35LF6L45pq4Cd/2', 'sneha@simupi', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 0, FALSE, '2026-09-10 09:45:00'),
(5, 'Vikram Singh', '9876543214', 'vikram@simupi.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5F788gcWCuqcWB35LF6L45pq4Cd/2', 'vikram@simupi', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 0, FALSE, '2026-09-12 16:20:00');

-- 2. Insert 5 Linked Bank Accounts
-- Savings: Min Balance = Rs. 1,000, Daily Limit = Rs. 1,00,000
-- Current: Min Balance = Rs. 5,000, Daily Limit = Rs. 2,00,000
MERGE INTO bank_accounts (id, user_id, account_number, bank_name, ifsc_code, account_type, balance, minimum_balance, daily_transfer_limit, created_at)
KEY(id)
VALUES
(1, 1, '5010023412340001', 'HDFC Bank', 'HDFC0001234', 'SAVINGS', 45000.00, 1000.00, 100000.00, '2026-09-01 10:05:00'),
(2, 2, '3010045612340002', 'State Bank of India', 'SBIN0005678', 'SAVINGS', 28500.00, 1000.00, 100000.00, '2026-09-02 11:35:00'),
(3, 3, '4010078912340003', 'ICICI Bank', 'ICIC0009012', 'CURRENT', 112000.00, 5000.00, 200000.00, '2026-09-05 14:20:00'),
(4, 4, '6010012312340004', 'Axis Bank', 'UTIB0003456', 'SAVINGS', 15400.00, 1000.00, 100000.00, '2026-09-10 09:50:00'),
(5, 5, '7010098712340005', 'Punjab National Bank', 'PUNB0007890', 'CURRENT', 73500.00, 5000.00, 200000.00, '2026-09-12 16:25:00');

