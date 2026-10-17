package com.simupi.controller;

import com.simupi.config.SessionHelper;
import com.simupi.dto.request.LoginRequest;
import com.simupi.dto.request.RegisterRequest;
import com.simupi.dto.request.SetPinRequest;
import com.simupi.dto.response.ApiResponse;
import com.simupi.dto.response.UserProfileResponse;
import com.simupi.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller exposing authentication, registration, session management, and PIN change endpoints.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final SessionHelper sessionHelper;

    public AuthController(AuthService authService, SessionHelper sessionHelper) {
        this.authService = authService;
        this.sessionHelper = sessionHelper;
    }

    /**
     * POST /api/auth/register
     * Registers a new user, auto-generates a UPI ID, and links initial virtual bank account.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserProfileResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpSession session) {
        UserProfileResponse profile = authService.register(request);
        sessionHelper.setLoggedInUser(session, profile.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registration successful. Virtual account linked.", profile));
    }

    /**
     * POST /api/auth/login
     * Authenticates user using mobile and password, establishing an active session.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserProfileResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpSession session) {
        String clientIp = httpRequest.getRemoteAddr();
        UserProfileResponse profile = authService.login(request, clientIp);
        sessionHelper.setLoggedInUser(session, profile.getId());
        return ResponseEntity.ok(ApiResponse.ok("Login successful. Welcome back, " + profile.getName() + "!", profile));
    }

    /**
     * POST /api/auth/logout
     * Terminates the current user session.
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        sessionHelper.clearSession(session);
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully.", null));
    }

    /**
     * GET /api/auth/me
     * Returns the profile of the currently logged-in user.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(HttpSession session) {
        Long userId = sessionHelper.getLoggedInUserId(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Session expired or user not logged in."));
        }
        UserProfileResponse profile = authService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    /**
     * POST /api/auth/pin
     * Allows setting or changing 4/6-digit UPI PIN using password verification.
     */
    @PostMapping("/pin")
    public ResponseEntity<ApiResponse<Void>> setPin(
            @Valid @RequestBody SetPinRequest request,
            HttpSession session) {
        Long userId = sessionHelper.getLoggedInUserId(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized: Please log in first."));
        }
        authService.setPin(userId, request);
        return ResponseEntity.ok(ApiResponse.ok("UPI PIN updated successfully.", null));
    }
}
