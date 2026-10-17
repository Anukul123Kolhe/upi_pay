package com.simupi.dto.response;

import com.simupi.model.enums.TransactionStatus;
import com.simupi.model.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representation of a transaction suitable for UI display and digital receipts.
 */
public class TransactionResponse {

    private Long id;
    private String utr;
    private Long senderId;
    private String senderName;
    private String senderUpiId;
    private String senderMaskedAccount;
    private Long receiverId;
    private String receiverName;
    private String receiverUpiId;
    private String receiverMaskedAccount;
    private BigDecimal amount;
    private BigDecimal charges;
    private TransactionStatus status;
    private TransactionType transactionType;
    private String failureReason;
    private String note;
    private LocalDateTime createdAt;
    private boolean isDebit;

    public TransactionResponse() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUtr() {
        return utr;
    }

    public void setUtr(String utr) {
        this.utr = utr;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderUpiId() {
        return senderUpiId;
    }

    public void setSenderUpiId(String senderUpiId) {
        this.senderUpiId = senderUpiId;
    }

    public String getSenderMaskedAccount() {
        return senderMaskedAccount;
    }

    public void setSenderMaskedAccount(String senderMaskedAccount) {
        this.senderMaskedAccount = senderMaskedAccount;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverUpiId() {
        return receiverUpiId;
    }

    public void setReceiverUpiId(String receiverUpiId) {
        this.receiverUpiId = receiverUpiId;
    }

    public String getReceiverMaskedAccount() {
        return receiverMaskedAccount;
    }

    public void setReceiverMaskedAccount(String receiverMaskedAccount) {
        this.receiverMaskedAccount = receiverMaskedAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCharges() {
        return charges;
    }

    public void setCharges(BigDecimal charges) {
        this.charges = charges;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDebit() {
        return isDebit;
    }

    public void setDebit(boolean debit) {
        isDebit = debit;
    }
}
