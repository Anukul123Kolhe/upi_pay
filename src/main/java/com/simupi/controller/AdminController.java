package com.simupi.controller;

import com.simupi.dto.response.ApiResponse;
import com.simupi.dto.response.TransactionResponse;
import com.simupi.dto.response.UserProfileResponse;
import com.simupi.model.LoginAudit;
import com.simupi.repository.UserRepository;
import com.simupi.service.AuthService;
import com.simupi.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Administrative controller providing network metrics, system audits, and global ledger views.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final TransactionService transactionService;
    private final AuthService authService;
    private final UserRepository userRepository;

    public AdminController(TransactionService transactionService,
                           AuthService authService,
                           UserRepository userRepository) {
        this.transactionService = transactionService;
        this.authService = authService;
        this.userRepository = userRepository;
    }

    /**
     * GET /api/admin/stats
     * Aggregated network performance KPI metrics.
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemStats() {
        Map<String, Object> stats = transactionService.getSystemStatistics();
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    /**
     * GET /api/admin/transactions
     * Global audit trail of all transactions processed in the simulated UPI network.
     */
    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getAllTransactions() {
        List<TransactionResponse> allTx = transactionService.getAllSystemTransactions();
        return ResponseEntity.ok(ApiResponse.ok(allTx));
    }

    /**
     * GET /api/admin/users
     * Directory of all registered users in the network.
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserProfileResponse>>> getAllUsers() {
        List<UserProfileResponse> users = userRepository.findAll().stream()
                .map(u -> new UserProfileResponse(
                        u.getId(), u.getName(), u.getMobile(), u.getEmail(),
                        u.getUpiId(), u.isPinLocked(), u.getCreatedAt()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    /**
     * GET /api/admin/audits
     * Recent login and authentication audit entries.
     */
    @GetMapping("/audits")
    public ResponseEntity<ApiResponse<List<LoginAudit>>> getLoginAudits() {
        List<LoginAudit> audits = authService.getRecentAudits();
        return ResponseEntity.ok(ApiResponse.ok(audits));
    }
}
