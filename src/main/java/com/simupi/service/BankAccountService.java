package com.simupi.service;

import com.simupi.dto.request.CheckBalanceRequest;
import com.simupi.dto.response.BankAccountResponse;
import com.simupi.dto.response.BalanceResponse;
import com.simupi.model.BankAccount;
import com.simupi.model.enums.AccountType;

import java.math.BigDecimal;
import java.util.List;

/**
 * OOP: Interfaces - Service contract for managing virtual bank accounts and balance checks.
 */
public interface BankAccountService {

    BankAccountResponse linkAccount(Long userId, String bankName, AccountType type, BigDecimal openingBalance);

    List<BankAccountResponse> getUserAccounts(Long userId);

    BankAccount getPrimaryAccount(Long userId);

    BalanceResponse checkBalance(Long userId, CheckBalanceRequest request);
}
