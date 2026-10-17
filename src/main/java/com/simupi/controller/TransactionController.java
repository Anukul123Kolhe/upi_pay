package com.simupi.controller;

import com.simupi.config.SessionHelper;
import com.simupi.dto.response.ApiResponse;
import com.simupi.dto.response.TransactionResponse;
import com.simupi.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller exposing transaction history, multi-criteria filtering, and printable receipts.
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final SessionHelper sessionHelper;

    public TransactionController(TransactionService transactionService, SessionHelper sessionHelper) {
        this.transactionService = transactionService;
        this.sessionHelper = sessionHelper;
    }

    /**
     * GET /api/transactions
     * Returns user transaction passbook with dynamic stream filtering (type, status, dates, amounts, search).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactions(
            @RequestParam(value = "type", defaultValue = "ALL") String type,
            @RequestParam(value = "status", defaultValue = "ALL") String status,
            @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(value = "minAmount", required = false) BigDecimal minAmount,
            @RequestParam(value = "maxAmount", required = false) BigDecimal maxAmount,
            @RequestParam(value = "search", required = false) String search,
            HttpSession session) {

        Long userId = sessionHelper.getLoggedInUserId(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized: Please log in first."));
        }

        List<TransactionResponse> history = transactionService.getUserHistory(
                userId, type, status, fromDate, toDate, minAmount, maxAmount, search
        );

        return ResponseEntity.ok(ApiResponse.ok("Transactions retrieved", history));
    }

    /**
     * GET /api/transactions/{utr}
     * Returns full receipt details for an individual transaction by its 12-digit UTR.
     */
    @GetMapping("/{utr}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getReceipt(
            @PathVariable("utr") String utr,
            HttpSession session) {
        Long userId = sessionHelper.getLoggedInUserId(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized: Please log in first."));
        }

        TransactionResponse receipt = transactionService.getTransactionByUtr(utr, userId);
        return ResponseEntity.ok(ApiResponse.ok(receipt));
    }
}
