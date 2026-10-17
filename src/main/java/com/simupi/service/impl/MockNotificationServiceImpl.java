package com.simupi.service.impl;

import com.simupi.model.User;
import com.simupi.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Concrete implementation of NotificationService simulating SMS/In-App push notifications.
 */
@Service
public class MockNotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(MockNotificationServiceImpl.class);

    @Override
    public void notifyTransferSuccess(User sender, User receiver, BigDecimal amount, String utr) {
        log.info("[SMS-ALERT] Sent to {}: ₹{} successfully debited for payment to {}. UTR: {}",
                sender.getMobile(), amount, receiver.getUpiId(), utr);
    }

    @Override
    public void notifyPaymentReceived(User receiver, User sender, BigDecimal amount, String utr) {
        log.info("[SMS-ALERT] Sent to {}: ₹{} credited to your account from {}. UTR: {}",
                receiver.getMobile(), amount, sender.getUpiId(), utr);
    }

    @Override
    public void notifyPaymentFailure(User sender, String reason, BigDecimal amount) {
        log.warn("[SMS-ALERT] Sent to {}: Payment of ₹{} failed. Reason: {}",
                sender.getMobile(), amount, reason);
    }
}
