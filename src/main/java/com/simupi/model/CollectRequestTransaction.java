package com.simupi.model;

import com.simupi.exception.SelfTransferException;
import com.simupi.model.enums.TransactionType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;

/**
 * OOP: Inheritance - CollectRequestTransaction extends Transaction.
 * Represents a peer-to-peer (P2P) pull payment where a requester asks another user for funds.
 */
@Entity
@DiscriminatorValue("COLLECT_REQUEST")
public class CollectRequestTransaction extends Transaction {

    public static final BigDecimal MAX_COLLECT_LIMIT = new BigDecimal("50000.00");

    public CollectRequestTransaction() {
        super();
    }

    public CollectRequestTransaction(String utr, User requester, User payer, BankAccount requesterAccount,
                                     BankAccount payerAccount, BigDecimal amount, String note) {
        // In collect request: sender is the payer, receiver is the requester
        super(utr, payer, requester, payerAccount, requesterAccount, amount, note);
    }

    // OOP: Polymorphism - Overriding charges calculation for collect requests
    @Override
    public BigDecimal calculateCharges() {
        return BigDecimal.ZERO;
    }

    // OOP: Polymorphism - Overriding validation rules for collect requests
    @Override
    public void validateRules() {
        if (getReceiver() == null) {
            throw new IllegalArgumentException("Requester must be specified for a collect request.");
        }

        if (getSender() != null && getReceiver().getId() != null && getReceiver().getId().equals(getSender().getId())) {
            throw new SelfTransferException("You cannot request money from yourself.");
        }

        if (getAmount() == null || getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Collect request amount must be strictly greater than zero.");
        }

        if (getAmount().compareTo(MAX_COLLECT_LIMIT) > 0) {
            throw new IllegalArgumentException(String.format("Collect request amount ₹%s exceeds the allowed threshold of ₹%s.",
                    getAmount(), MAX_COLLECT_LIMIT));
        }
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.COLLECT_REQUEST;
    }
}
