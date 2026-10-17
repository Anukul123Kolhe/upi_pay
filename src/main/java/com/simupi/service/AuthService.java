package com.simupi.service;

import com.simupi.dto.request.LoginRequest;
import com.simupi.dto.request.RegisterRequest;
import com.simupi.dto.request.SetPinRequest;
import com.simupi.dto.response.UserProfileResponse;
import com.simupi.model.LoginAudit;

import java.util.List;

/**
 * OOP: Interfaces - Service contract for user registration, authentication, and PIN maintenance.
 */
public interface AuthService {

    UserProfileResponse register(RegisterRequest request);

    UserProfileResponse login(LoginRequest request, String ipAddress);

    void setPin(Long userId, SetPinRequest request);

    UserProfileResponse getProfile(Long userId);

    List<LoginAudit> getRecentAudits();
}
