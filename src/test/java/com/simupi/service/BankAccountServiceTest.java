package com.simupi.service;

import com.simupi.dto.request.CheckBalanceRequest;
import com.simupi.dto.response.BalanceResponse;
import com.simupi.exception.AccountLockedException;
import com.simupi.exception.InvalidPinException;
import com.simupi.model.BankAccount;
import com.simupi.model.CurrentAccount;
import com.simupi.model.SavingsAccount;
import com.simupi.model.User;
import com.simupi.model.enums.AccountType;
import com.simupi.pattern.factory.BankAccountFactory;
import com.simupi.pattern.strategy.CurrentFeeStrategy;
import com.simupi.pattern.strategy.SavingsFeeStrategy;
import com.simupi.repository.BankAccountRepository;
import com.simupi.repository.UserRepository;
import com.simupi.service.impl.BankAccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit test suite verifying BankAccountFactory, Fee Strategy calculations,
 * and secure PIN-protected balance inquiries.
 */
@ExtendWith(MockitoExtension.class)
public class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private UserRepository userRepository;

    private BankAccountFactory bankAccountFactory = new BankAccountFactory();

    @Mock
    private PasswordEncoder passwordEncoder;

    private BankAccountServiceImpl bankAccountService;

    private User testUser;
    private SavingsAccount testAccount;

    @BeforeEach
    void setUp() {
        bankAccountService = new BankAccountServiceImpl(
                bankAccountRepository,
                userRepository,
                bankAccountFactory,
                passwordEncoder
        );

        testUser = new User("Anukul Sharma", "9876543210", "anukul@simupi.com", "passHash", "anukul@simupi", "pinHash");
        testUser.setId(1L);

        testAccount = new SavingsAccount(testUser, "5010023412340001", "HDFC Bank", "HDFC0001234", new BigDecimal("45000.00"));
        testAccount.setId(10L);
    }

    // -------------------------------------------------------------------------
    // TEST 9: Factory Pattern & Polymorphic Account Subclasses
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Test 9: BankAccountFactory instantiates correct subtype with specific minimum balances")
    void testBankAccountFactory_Polymorphism() {
        BankAccountFactory factory = new BankAccountFactory();

        // 1. Create Savings Account
        BankAccount savings = factory.createAccount(AccountType.SAVINGS, testUser, "HDFC Bank", new BigDecimal("10000.00"));
        assertTrue(savings instanceof SavingsAccount);
        assertEquals(AccountType.SAVINGS, savings.getAccountType());
        assertEquals(new BigDecimal("1000.00"), savings.getMinimumBalance());
        assertEquals(new BigDecimal("100000.00"), savings.getDailyTransferLimit());

        // 2. Create Current Account
        BankAccount current = factory.createAccount(AccountType.CURRENT, testUser, "ICICI Bank", new BigDecimal("50000.00"));
        assertTrue(current instanceof CurrentAccount);
        assertEquals(AccountType.CURRENT, current.getAccountType());
        assertEquals(new BigDecimal("5000.00"), current.getMinimumBalance());
        assertEquals(new BigDecimal("200000.00"), current.getDailyTransferLimit());
    }

    // -------------------------------------------------------------------------
    // TEST 10: Strategy Pattern for Dynamic Fee Calculation
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Test 10: Strategy pattern calculates 0% for Savings vs 0.1% for high-value Current accounts")
    void testFeeCalculationStrategies() {
        SavingsFeeStrategy savingsStrategy = new SavingsFeeStrategy();
        CurrentFeeStrategy currentStrategy = new CurrentFeeStrategy();

        BigDecimal smallAmount = new BigDecimal("5000.00");
        BigDecimal largeAmount = new BigDecimal("80000.00");

        // Retail Savings transfer is strictly zero-fee
        assertEquals(BigDecimal.ZERO, savingsStrategy.calculateFee(smallAmount));
        assertEquals(BigDecimal.ZERO, savingsStrategy.calculateFee(largeAmount));

        // Commercial Current account: no fee below threshold (₹50,000), 0.1% surcharge above threshold
        assertEquals(BigDecimal.ZERO, currentStrategy.calculateFee(smallAmount));
        // ₹80,000 * 0.1% = ₹80.00, capped at max fee of ₹25.00
        assertEquals(new BigDecimal("25.00"), currentStrategy.calculateFee(largeAmount));
    }

    // -------------------------------------------------------------------------
    // Additional Test: Secure PIN-Protected Balance Check
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Test 11: checkBalance returns masked account number and live balance on correct PIN")
    void testCheckBalance_Success() {
        CheckBalanceRequest request = new CheckBalanceRequest("1234");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("1234", "pinHash")).thenReturn(true);
        when(bankAccountRepository.findFirstByUserId(1L)).thenReturn(Optional.of(testAccount));

        BalanceResponse response = bankAccountService.checkBalance(1L, request);

        assertNotNull(response);
        assertEquals("HDFC Bank", response.getBankName());
        assertEquals("XXXX0001", response.getMaskedAccountNumber()); // Masks 16-digit account to XXXX + last 4
        assertEquals(new BigDecimal("45000.00"), response.getBalance());
        assertEquals(AccountType.SAVINGS, response.getAccountType());
    }
}
