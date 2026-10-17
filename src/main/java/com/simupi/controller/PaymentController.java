package com.simupi.controller;

import com.simupi.config.SessionHelper;
import com.simupi.dto.request.CollectMoneyRequest;
import com.simupi.dto.request.SendMoneyRequest;
import com.simupi.dto.response.ApiResponse;
import com.simupi.dto.response.TransactionResponse;
import com.simupi.dto.response.UserProfileResponse;
import com.simupi.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller managing P2P transfers, recipient verification, and collect requests.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final SessionHelper sessionHelper;

    public PaymentController(PaymentService paymentService, SessionHelper sessionHelper) {
        this.paymentService = paymentService;
        this.sessionHelper = sessionHelper;
    }

    /**
     * GET /api/payments/search?query=...
     * Looks up and verifies recipient details by UPI ID or Mobile before transferring funds.
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<UserProfileResponse>> searchRecipient(
            @RequestParam("query") String query,
            HttpSession session) {
        Long userId = sessionHelper.getLoggedInUserId(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized: Please log in first."));
        }

        UserProfileResponse recipient = paymentService.searchUser(query);
        return ResponseEntity.ok(ApiResponse.ok("Receiver verified", recipient));
    }

    /**
     * POST /api/payments/send
     * Initiates an atomic P2P transfer with PIN validation, limit checks, and instant settlement.
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<TransactionResponse>> sendMoney(
            @Valid @RequestBody SendMoneyRequest request,
            HttpSession session) {
        Long senderId = sessionHelper.getLoggedInUserId(session);
        if (senderId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized: Please log in first."));
        }

        TransactionResponse tx = paymentService.sendMoney(senderId, request);
        return ResponseEntity.ok(ApiResponse.ok("Payment successful! UTR: " + tx.getUtr(), tx));
    }

    /**
     * POST /api/payments/collect
     * Creates a collect request (pull payment) asking another user to pay.
     */
    @PostMapping("/collect")
    public ResponseEntity<ApiResponse<TransactionResponse>> requestMoney(
            @Valid @RequestBody CollectMoneyRequest request,
            HttpSession session) {
        Long requesterId = sessionHelper.getLoggedInUserId(session);
        if (requesterId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized: Please log in first."));
        }

        TransactionResponse tx = paymentService.collectMoney(requesterId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Collect request created. Pending approval from payer.", tx));
    }

    /**
     * POST /api/payments/collect/approve
     * Approves and pays a pending collect request.
     */
    @PostMapping("/collect/approve")
    public ResponseEntity<ApiResponse<TransactionResponse>> approveCollect(
            @RequestParam("utr") String utr,
            @RequestParam("pin") String pin,
            HttpSession session) {
        Long payerId = sessionHelper.getLoggedInUserId(session);
        if (payerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized: Please log in first."));
        }

        TransactionResponse tx = paymentService.approveCollectRequest(payerId, utr, pin);
        return ResponseEntity.ok(ApiResponse.ok("Collect request approved & paid. UTR: " + tx.getUtr(), tx));
    }
}
