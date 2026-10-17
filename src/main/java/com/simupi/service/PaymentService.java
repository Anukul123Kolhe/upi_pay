package com.simupi.service;

import com.simupi.dto.request.CollectMoneyRequest;
import com.simupi.dto.request.SendMoneyRequest;
import com.simupi.dto.response.TransactionResponse;
import com.simupi.dto.response.UserProfileResponse;

/**
 * OOP: Interfaces - Core financial payment service contract.
 * Demonstrates compile-time polymorphism through method overloading for user search.
 */
public interface PaymentService {

    // OOP: Polymorphism (Compile-time / Method Overloading) - Search recipient by UPI ID String
    UserProfileResponse searchUser(String upiId);

    // OOP: Polymorphism (Compile-time / Method Overloading) - Search recipient by 10-digit Mobile Long
    UserProfileResponse searchUser(Long mobile);

    // Atomic Send Money (push payment)
    TransactionResponse sendMoney(Long senderUserId, SendMoneyRequest request);

    // Create Collect Money request (pull payment)
    TransactionResponse collectMoney(Long requesterUserId, CollectMoneyRequest request);

    // Approve and pay an incoming Collect Request
    TransactionResponse approveCollectRequest(Long payerUserId, String utr, String upiPin);
}
