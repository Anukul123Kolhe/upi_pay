package com.simupi.controller;

import com.simupi.config.SessionHelper;
import com.simupi.dto.request.CheckBalanceRequest;
import com.simupi.dto.response.ApiResponse;
import com.simupi.dto.response.BankAccountResponse;
import com.simupi.dto.response.BalanceResponse;
import com.simupi.service.BankAccountService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller exposing bank account linkage and PIN-protected balance inspection endpoints.
 */
@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final BankAccountService bankAccountService;
    private final SessionHelper sessionHelper;

    public AccountController(BankAccountService bankAccountService, SessionHelper sessionHelper) {
        this.bankAccountService = bankAccountService;
        this.sessionHelper = sessionHelper;
    }

    /**
     * POST /api/account/balance
     * Secure PIN-protected balance check. Returns bank name, masked account number, and current balance.
     */
    @PostMapping("/balance")
    public ResponseEntity<ApiResponse<BalanceResponse>> checkBalance(
            @Valid @RequestBody CheckBalanceRequest request,
            HttpSession session) {
        Long userId = sessionHelper.getLoggedInUserId(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized: Please log in to view balance."));
        }
        BalanceResponse balance = bankAccountService.checkBalance(userId, request);
        return ResponseEntity.ok(ApiResponse.ok("Balance retrieved successfully", balance));
    }

    /**
     * GET /api/account/linked
     * Lists all virtual bank accounts linked to the currently authenticated user.
     */
    @GetMapping("/linked")
    public ResponseEntity<ApiResponse<List<BankAccountResponse>>> getLinkedAccounts(HttpSession session) {
        Long userId = sessionHelper.getLoggedInUserId(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized: Please log in first."));
        }
        List<BankAccountResponse> accounts = bankAccountService.getUserAccounts(userId);
        return ResponseEntity.ok(ApiResponse.ok(accounts));
    }
}
