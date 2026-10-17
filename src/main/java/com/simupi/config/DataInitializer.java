package com.simupi.config;

import com.simupi.model.*;
import com.simupi.model.enums.AccountType;
import com.simupi.model.enums.TransactionStatus;
import com.simupi.pattern.factory.BankAccountFactory;
import com.simupi.repository.BankAccountRepository;
import com.simupi.repository.LoginAuditRepository;
import com.simupi.repository.TransactionRepository;
import com.simupi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Initializes seed data if the database is fresh.
 * Guarantees that test accounts (with BCrypt hashes for 'password123' and PIN '1234')
 * are ready immediately out-of-the-box.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final LoginAuditRepository loginAuditRepository;
    private final BankAccountFactory bankAccountFactory;
    private final PasswordEncoder passwordEncoder;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    public DataInitializer(UserRepository userRepository,
                           BankAccountRepository bankAccountRepository,
                           TransactionRepository transactionRepository,
                           LoginAuditRepository loginAuditRepository,
                           BankAccountFactory bankAccountFactory,
                           PasswordEncoder passwordEncoder,
                           org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
        this.loginAuditRepository = loginAuditRepository;
        this.bankAccountFactory = bankAccountFactory;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    private void resetSequences() {
        try {
            Long maxUser = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(id), 0) FROM users", Long.class);
            jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH " + ((maxUser != null ? maxUser : 0) + 1));

            Long maxAcc = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(id), 0) FROM bank_accounts", Long.class);
            jdbcTemplate.execute("ALTER TABLE bank_accounts ALTER COLUMN id RESTART WITH " + ((maxAcc != null ? maxAcc : 0) + 1));

            Long maxTx = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(id), 0) FROM transactions", Long.class);
            jdbcTemplate.execute("ALTER TABLE transactions ALTER COLUMN id RESTART WITH " + ((maxTx != null ? maxTx : 0) + 1));

            Long maxAudit = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(id), 0) FROM login_audit", Long.class);
            jdbcTemplate.execute("ALTER TABLE login_audit ALTER COLUMN id RESTART WITH " + ((maxAudit != null ? maxAudit : 0) + 1));
        } catch (Exception ex) {
            log.warn("Auto-increment sequence advance notice: {}", ex.getMessage());
        }
    }

    @Override
    public void run(String... args) {
        String defaultPasswordHash = passwordEncoder.encode("password123");
        String defaultPinHash = passwordEncoder.encode("1234");

        if (userRepository.count() > 0) {
            log.info("Synchronizing password and PIN credentials for all existing users...");
            for (User u : userRepository.findAll()) {
                u.setPasswordHash(defaultPasswordHash);
                u.setUpiPinHash(defaultPinHash);
                u.setPinLocked(false);
                u.setFailedPinAttempts(0);
                userRepository.save(u);
            }
            resetSequences();
            log.info("Credentials successfully synchronized for {} users.", userRepository.count());
            return;
        }

        log.info("Fresh database detected. Initializing test users and transactions...");

        // 1. Create 5 Test Users
        User u1 = new User("Anukul Sharma", "9876543210", "anukul@simupi.com", defaultPasswordHash, "anukul@simupi", defaultPinHash);
        User u2 = new User("Priya Patel", "9876543211", "priya@simupi.com", defaultPasswordHash, "priya@simupi", defaultPinHash);
        User u3 = new User("Rahul Verma", "9876543212", "rahul@simupi.com", defaultPasswordHash, "rahul@simupi", defaultPinHash);
        User u4 = new User("Sneha Reddy", "9876543213", "sneha@simupi.com", defaultPasswordHash, "sneha@simupi", defaultPinHash);
        User u5 = new User("Vikram Singh", "9876543214", "vikram@simupi.com", defaultPasswordHash, "vikram@simupi", defaultPinHash);

        u1 = userRepository.save(u1);
        u2 = userRepository.save(u2);
        u3 = userRepository.save(u3);
        u4 = userRepository.save(u4);
        u5 = userRepository.save(u5);

        // 2. Create Linked Bank Accounts via Factory
        BankAccount b1 = bankAccountFactory.createAccount(AccountType.SAVINGS, u1, "HDFC Bank", new BigDecimal("45000.00"));
        BankAccount b2 = bankAccountFactory.createAccount(AccountType.SAVINGS, u2, "State Bank of India", new BigDecimal("28500.00"));
        BankAccount b3 = bankAccountFactory.createAccount(AccountType.CURRENT, u3, "ICICI Bank", new BigDecimal("112000.00"));
        BankAccount b4 = bankAccountFactory.createAccount(AccountType.SAVINGS, u4, "Axis Bank", new BigDecimal("15400.00"));
        BankAccount b5 = bankAccountFactory.createAccount(AccountType.CURRENT, u5, "Punjab National Bank", new BigDecimal("73500.00"));

        b1 = bankAccountRepository.save(b1);
        b2 = bankAccountRepository.save(b2);
        b3 = bankAccountRepository.save(b3);
        b4 = bankAccountRepository.save(b4);
        b5 = bankAccountRepository.save(b5);

        // 3. Transactions start completely clean - users will create their own live payments
        
        resetSequences();
        log.info("Seed data successfully loaded: 5 Users, 5 Accounts, 0 Transactions (Fresh Passbook).");
    }

    private void createTx(String utr, User sender, User receiver, BankAccount senderAcc, BankAccount receiverAcc,
                          BigDecimal amount, String note, TransactionStatus status, String failureReason, int daysAgo) {
        SendMoneyTransaction tx = new SendMoneyTransaction(utr, sender, receiver, senderAcc, receiverAcc, amount, note);
        tx.setStatus(status);
        tx.setFailureReason(failureReason);
        tx.setCreatedAt(LocalDateTime.now().minusDays(daysAgo).minusHours(2));
        transactionRepository.save(tx);
    }
}
