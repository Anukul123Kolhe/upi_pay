package com.simupi.service;

import com.simupi.dto.response.TransactionResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * OOP: Interfaces - Service contract for querying, filtering, and reporting on financial transactions.
 * Demonstrates extensive usage of Java Collections, Maps, and Stream APIs.
 */
public interface TransactionService {

    List<TransactionResponse> getUserHistory(
            Long userId,
            String typeFilter,      // "ALL", "SENT", "RECEIVED"
            String statusFilter,    // "ALL", "SUCCESS", "FAILED", "PENDING"
            LocalDate fromDate,
            LocalDate toDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String searchQuery      // Search by UTR, name, or note
    );

    TransactionResponse getTransactionByUtr(String utr, Long currentUserId);

    // Administrative & analytics aggregation using Java Streams and Maps
    Map<String, Object> getSystemStatistics();

    List<TransactionResponse> getAllSystemTransactions();
}
