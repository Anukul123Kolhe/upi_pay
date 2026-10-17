package com.simupi.service.impl;

import com.simupi.dto.response.TransactionResponse;
import com.simupi.model.Transaction;
import com.simupi.model.enums.TransactionStatus;
import com.simupi.repository.TransactionRepository;
import com.simupi.repository.UserRepository;
import com.simupi.service.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation demonstrating OOP: Collections & Streams
 * for multi-criteria filtering, in-memory sorting, and statistical grouping.
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves user transactions sorted newest first, with dynamic stream filtering.
     * Demonstrates OOP: Collections (List, Map) and Stream API pipelines.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getUserHistory(
            Long userId,
            String typeFilter,
            String statusFilter,
            LocalDate fromDate,
            LocalDate toDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String searchQuery) {

        // 1. Fetch raw transaction records from repository
        List<Transaction> allUserTx = transactionRepository.findAllUserTransactions(userId);

        // 2. Stream pipeline for dynamic multi-criteria filtering and sorting
        return allUserTx.stream()
                // Filter by Type: SENT (where user is sender) vs RECEIVED (where user is receiver)
                .filter(tx -> {
                    if (typeFilter == null || typeFilter.equalsIgnoreCase("ALL")) return true;
                    boolean isSender = tx.getSender() != null && tx.getSender().getId().equals(userId);
                    if (typeFilter.equalsIgnoreCase("SENT")) return isSender;
                    if (typeFilter.equalsIgnoreCase("RECEIVED")) return !isSender;
                    return true;
                })
                // Filter by Status: SUCCESS, FAILED, PENDING
                .filter(tx -> {
                    if (statusFilter == null || statusFilter.equalsIgnoreCase("ALL")) return true;
                    return tx.getStatus().name().equalsIgnoreCase(statusFilter);
                })
                // Filter by Date Range (From Date)
                .filter(tx -> {
                    if (fromDate == null) return true;
                    return !tx.getCreatedAt().toLocalDate().isBefore(fromDate);
                })
                // Filter by Date Range (To Date)
                .filter(tx -> {
                    if (toDate == null) return true;
                    return !tx.getCreatedAt().toLocalDate().isAfter(toDate);
                })
                // Filter by Minimum Amount
                .filter(tx -> {
                    if (minAmount == null) return true;
                    return tx.getAmount().compareTo(minAmount) >= 0;
                })
                // Filter by Maximum Amount
                .filter(tx -> {
                    if (maxAmount == null) return true;
                    return tx.getAmount().compareTo(maxAmount) <= 0;
                })
                // Full-text Search across UTR, Counterparty Name, Counterparty UPI ID, and Remarks
                .filter(tx -> {
                    if (searchQuery == null || searchQuery.trim().isEmpty()) return true;
                    String q = searchQuery.toLowerCase().trim();
                    boolean matchUtr = tx.getUtr().toLowerCase().contains(q);
                    boolean matchNote = tx.getNote() != null && tx.getNote().toLowerCase().contains(q);
                    boolean matchSender = tx.getSender() != null &&
                            (tx.getSender().getName().toLowerCase().contains(q) || tx.getSender().getUpiId().toLowerCase().contains(q));
                    boolean matchReceiver = tx.getReceiver() != null &&
                            (tx.getReceiver().getName().toLowerCase().contains(q) || tx.getReceiver().getUpiId().toLowerCase().contains(q));
                    return matchUtr || matchNote || matchSender || matchReceiver;
                })
                // Sort Newest First (Descending by createdAt timestamp)
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                // Map to client-safe DTO
                .map(tx -> toResponse(tx, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionByUtr(String utr, Long currentUserId) {
        Transaction tx = transactionRepository.findByUtr(utr)
                .orElseThrow(() -> new IllegalArgumentException("Transaction receipt not found for UTR: " + utr));
        return toResponse(tx, currentUserId);
    }

    /**
     * Aggregates platform-wide KPI metrics using Java Collections and Stream reducers.
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSystemStatistics() {
        List<Transaction> allTransactions = transactionRepository.findAll();

        long totalCount = allTransactions.size();
        long successCount = allTransactions.stream()
                .filter(tx -> tx.getStatus() == TransactionStatus.SUCCESS)
                .count();
        long failedCount = allTransactions.stream()
                .filter(tx -> tx.getStatus() == TransactionStatus.FAILED)
                .count();

        BigDecimal totalVolume = allTransactions.stream()
                .filter(tx -> tx.getStatus() == TransactionStatus.SUCCESS)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalTransactions", totalCount);
        stats.put("successfulTransactions", successCount);
        stats.put("failedTransactions", failedCount);
        stats.put("totalTurnoverVolume", totalVolume);
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllSystemTransactions() {
        return transactionRepository.findAll().stream()
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .map(tx -> toResponse(tx, null))
                .collect(Collectors.toList());
    }

    private TransactionResponse toResponse(Transaction tx, Long currentUserId) {
        TransactionResponse resp = new TransactionResponse();
        resp.setId(tx.getId());
        resp.setUtr(tx.getUtr());
        resp.setAmount(tx.getAmount());
        resp.setCharges(tx.getCharges());
        resp.setStatus(tx.getStatus());
        resp.setTransactionType(tx.getTransactionType());
        resp.setFailureReason(tx.getFailureReason());
        resp.setNote(tx.getNote());
        resp.setCreatedAt(tx.getCreatedAt());

        if (tx.getSender() != null) {
            resp.setSenderId(tx.getSender().getId());
            resp.setSenderName(tx.getSender().getName());
            resp.setSenderUpiId(tx.getSender().getUpiId());
        }
        if (tx.getSenderAccount() != null) {
            resp.setSenderMaskedAccount(tx.getSenderAccount().getMaskedAccountNumber());
        }

        if (tx.getReceiver() != null) {
            resp.setReceiverId(tx.getReceiver().getId());
            resp.setReceiverName(tx.getReceiver().getName());
            resp.setReceiverUpiId(tx.getReceiver().getUpiId());
        }
        if (tx.getReceiverAccount() != null) {
            resp.setReceiverMaskedAccount(tx.getReceiverAccount().getMaskedAccountNumber());
        }

        boolean isDebit = currentUserId != null && tx.getSender() != null && tx.getSender().getId().equals(currentUserId);
        resp.setDebit(isDebit);

        return resp;
    }
}
