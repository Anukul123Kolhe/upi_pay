package com.simupi.service;

import com.simupi.model.User;
import java.math.BigDecimal;

/**
 * OOP: Interfaces - Decouples real-time payment alerts from transaction processing logic.
 */
public interface NotificationService {

    void notifyTransferSuccess(User sender, User receiver, BigDecimal amount, String utr);

    void notifyPaymentReceived(User receiver, User sender, BigDecimal amount, String utr);

    void notifyPaymentFailure(User sender, String reason, BigDecimal amount);
}
