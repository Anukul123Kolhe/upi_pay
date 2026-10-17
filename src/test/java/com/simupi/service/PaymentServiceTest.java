package com.simupi.service;

import com.simupi.dto.request.SendMoneyRequest;
import com.simupi.dto.response.TransactionResponse;
import com.simupi.dto.response.UserProfileResponse;
import com.simupi.exception.*;
import com.simupi.model.*;
import com.simupi.model.enums.AccountType;
import com.simupi.model.enums.TransactionStatus;
import com.simupi.pattern.strategy.CurrentFeeStrategy;
import com.simupi.pattern.strategy.SavingsFeeStrategy;
import com.simupi.repository.BankAccountRepository;
import com.simupi.repository.TransactionRepository;
import com.simupi.repository.UserRepository;
import com.simupi.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit test suite verifying core payment processing business rules, PIN security,
 * and method overloading polymorphism using JUnit 5 and Mockito.
 */
@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BankAccountService bankAccountService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private SavingsFeeStrategy savingsFeeStrategy = new SavingsFeeStrategy();
    private CurrentFeeStrategy currentFeeStrategy = new CurrentFeeStrategy();

    private PaymentServiceImpl paymentService;

    private User senderUser;
    private User receiverUser;
    private SavingsAccount senderAccount;
    private SavingsAccount receiverAccount;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(
                userRepository,
                bankAccountRepository,
                transactionRepository,
                bankAccountService,
                notificationService,
                passwordEncoder,
                savingsFeeStrategy,
                currentFeeStrategy
        );

        senderUser = new User("Anukul Sharma", "9876543210", "anukul@simupi.com", "passHash", "anukul@simupi", "pinHash");
        senderUser.setId(1L);

        receiverUser = new User("Priya Patel", "9876543211", "priya@simupi.com", "passHash", "priya@simupi", "pinHash");
        receiverUser.setId(2L);

        senderAccount = new SavingsAccount(senderUser, "5010023412340001", "HDFC Bank", "HDFC0001234", new BigDecimal("45000.00"));
        senderAccount.setId(101L);

        receiverAccount = new SavingsAccount(receiverUser, "3010045612340002", "State Bank of India", "SBIN0005678", new BigDecimal("28500.00"));
        receiverAccount.setId(102L);
    }

    // -------------------------------------------------------------------------
    // TEST 1: Successful Transfer (Atomic Debit & Credit)
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Test 1: Successful P2P transfer atomically debits sender and credits receiver")
    void testSendMoney_Success() {
        SendMoneyRequest request = new SendMoneyRequest("priya@simupi", new BigDecimal("5000.00"), "Project share", "1234");

        when(userRepository.findById(1L)).thenReturn(Optional.of(senderUser));
        when(passwordEncoder.matches("1234", "pinHash")).thenReturn(true);
        when(userRepository.findByUpiId("priya@simupi")).thenReturn(Optional.of(receiverUser));
        when(bankAccountService.getPrimaryAccount(1L)).thenReturn(senderAccount);
        when(bankAccountService.getPrimaryAccount(2L)).thenReturn(receiverAccount);
        when(transactionRepository.calculateTotalDebitedToday(eq(101L), any(LocalDateTime.class))).thenReturn(BigDecimal.ZERO);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction tx = invocation.getArgument(0);
            tx.setId(999L);
            return tx;
        });

        TransactionResponse response = paymentService.sendMoney(1L, request);

        assertNotNull(response);
        assertEquals(TransactionStatus.SUCCESS, response.getStatus());
        assertEquals(new BigDecimal("5000.00"), response.getAmount());
        assertEquals(new BigDecimal("40000.00"), senderAccount.getBalance()); // 45,000 - 5,000
        assertEquals(new BigDecimal("33500.00"), receiverAccount.getBalance()); // 28,500 + 5,000
        verify(bankAccountRepository, times(1)).save(senderAccount);
        verify(bankAccountRepository, times(1)).save(receiverAccount);
        verify(notificationService, times(1)).notifyTransferSuccess(any(), any(), any(), any());
    }

    // -------------------------------------------------------------------------
    // TEST 2: Insufficient Balance Violation (Minimum Balance Threshold)
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Test 2: Throws InsufficientBalanceException when debit breaches minimum balance of ₹1,000")
    void testSendMoney_InsufficientBalance() {
        // Attempting to send ₹44,500 would leave ₹500, violating ₹1,000 minimum balance requirement
        SendMoneyRequest request = new SendMoneyRequest("priya@simupi", new BigDecimal("44500.00"), "College fee", "1234");

        when(userRepository.findById(1L)).thenReturn(Optional.of(senderUser));
        when(passwordEncoder.matches("1234", "pinHash")).thenReturn(true);
        when(userRepository.findByUpiId("priya@simupi")).thenReturn(Optional.of(receiverUser));
        when(bankAccountService.getPrimaryAccount(1L)).thenReturn(senderAccount);
        when(bankAccountService.getPrimaryAccount(2L)).thenReturn(receiverAccount);
        when(transactionRepository.calculateTotalDebitedToday(eq(101L), any(LocalDateTime.class))).thenReturn(BigDecimal.ZERO);

        assertThrows(InsufficientBalanceException.class, () -> paymentService.sendMoney(1L, request));
        assertEquals(new BigDecimal("45000.00"), senderAccount.getBalance()); // Balance must remain untouched
    }

    // -------------------------------------------------------------------------
    // TEST 3: Self-Transfer Prevention
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Test 3: Throws SelfTransferException when sender attempts transfer to own account")
    void testSendMoney_SelfTransfer() {
        SendMoneyRequest request = new SendMoneyRequest("anukul@simupi", new BigDecimal("1000.00"), "Self transfer", "1234");

        when(userRepository.findById(1L)).thenReturn(Optional.of(senderUser));
        when(passwordEncoder.matches("1234", "pinHash")).thenReturn(true);
        when(userRepository.findByUpiId("anukul@simupi")).thenReturn(Optional.of(senderUser));

        assertThrows(SelfTransferException.class, () -> paymentService.sendMoney(1L, request));
    }

    // -------------------------------------------------------------------------
    // TEST 4: Daily Limit Exceeded
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Test 4: Throws DailyLimitExceededException when cumulative daily transfers exceed limit of ₹1,00,000")
    void testSendMoney_DailyLimitExceeded() {
        SendMoneyRequest request = new SendMoneyRequest("priya@simupi", new BigDecimal("20000.00"), "Shopping", "1234");

        when(userRepository.findById(1L)).thenReturn(Optional.of(senderUser));
        when(passwordEncoder.matches("1234", "pinHash")).thenReturn(true);
        when(userRepository.findByUpiId("priya@simupi")).thenReturn(Optional.of(receiverUser));
        when(bankAccountService.getPrimaryAccount(1L)).thenReturn(senderAccount);
        when(bankAccountService.getPrimaryAccount(2L)).thenReturn(receiverAccount);
        // Already transferred ₹90,000 today; adding ₹20,000 would reach ₹1,10,000 > ₹1,00,000
        when(transactionRepository.calculateTotalDebitedToday(eq(101L), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("90000.00"));

        assertThrows(DailyLimitExceededException.class, () -> paymentService.sendMoney(1L, request));
    }

    // -------------------------------------------------------------------------
    // TEST 5: Wrong UPI PIN Attempt (Tracks Remaining Attempts)
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Test 5: Throws InvalidPinException on wrong PIN and records failed attempt counter")
    void testSendMoney_WrongPinIncrementsFailureCount() {
        SendMoneyRequest request = new SendMoneyRequest("priya@simupi", new BigDecimal("1000.00"), "Rent", "9999");

        when(userRepository.findById(1L)).thenReturn(Optional.of(senderUser));
        when(passwordEncoder.matches("9999", "pinHash")).thenReturn(false);

        InvalidPinException ex = assertThrows(InvalidPinException.class, () -> paymentService.sendMoney(1L, request));
        assertEquals(2, ex.getAttemptsRemaining());
        assertEquals(1, senderUser.getFailedPinAttempts());
        assertFalse(senderUser.isPinLocked());
        verify(userRepository, times(1)).save(senderUser);
    }

    // -------------------------------------------------------------------------
    // TEST 6: Account Locked After 3 Failed PIN Attempts
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Test 6: Locks UPI PIN permanently after 3 consecutive wrong attempts")
    void testSendMoney_LocksAfterThreeWrongAttempts() {
        senderUser.setFailedPinAttempts(2); // Already failed twice
        SendMoneyRequest request = new SendMoneyRequest("priya@simupi", new BigDecimal("1000.00"), "Rent", "0000");

        when(userRepository.findById(1L)).thenReturn(Optional.of(senderUser));
        when(passwordEncoder.matches("0000", "pinHash")).thenReturn(false);

        assertThrows(AccountLockedException.class, () -> paymentService.sendMoney(1L, request));
        assertEquals(3, senderUser.getFailedPinAttempts());
        assertTrue(senderUser.isPinLocked());
        verify(userRepository, times(1)).save(senderUser);
    }

    // -------------------------------------------------------------------------
    // TEST 7: Correct PIN Resets Failure Counter
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Test 7: Successful PIN entry clears previous failed attempt count back to 0")
    void testSendMoney_CorrectPinResetsCounter() {
        senderUser.setFailedPinAttempts(2); // Had 2 previous failures
        SendMoneyRequest request = new SendMoneyRequest("priya@simupi", new BigDecimal("500.00"), "Snacks", "1234");

        when(userRepository.findById(1L)).thenReturn(Optional.of(senderUser));
        when(passwordEncoder.matches("1234", "pinHash")).thenReturn(true);
        when(userRepository.findByUpiId("priya@simupi")).thenReturn(Optional.of(receiverUser));
        when(bankAccountService.getPrimaryAccount(1L)).thenReturn(senderAccount);
        when(bankAccountService.getPrimaryAccount(2L)).thenReturn(receiverAccount);
        when(transactionRepository.calculateTotalDebitedToday(eq(101L), any(LocalDateTime.class))).thenReturn(BigDecimal.ZERO);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        paymentService.sendMoney(1L, request);

        assertEquals(0, senderUser.getFailedPinAttempts());
        assertFalse(senderUser.isPinLocked());
    }

    // -------------------------------------------------------------------------
    // TEST 8: Method Overloading (Compile-Time Polymorphism)
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Test 8: Polymorphic searchUser handles both UPI ID String and Mobile Long overloads")
    void testSearchUser_MethodOverloading() {
        when(userRepository.findByUpiId("priya@simupi")).thenReturn(Optional.of(receiverUser));
        when(userRepository.findByMobile("9876543211")).thenReturn(Optional.of(receiverUser));

        // Call Overload 1: searchUser(String upiId)
        UserProfileResponse resByUpi = paymentService.searchUser("priya@simupi");
        assertNotNull(resByUpi);
        assertEquals("Priya Patel", resByUpi.getName());

        // Call Overload 2: searchUser(Long mobile)
        UserProfileResponse resByMobile = paymentService.searchUser(9876543211L);
        assertNotNull(resByMobile);
        assertEquals("9876543211", resByMobile.getMobile());
    }
}
