package com.simupi.exception;

/**
 * Thrown when an operation is requested on an account whose UPI PIN has been locked after 3 consecutive failed attempts.
 */
public class AccountLockedException extends SimUpiException {
    public AccountLockedException(String message) {
        super(message);
    }

    public AccountLockedException() {
        super("Your UPI PIN is locked due to 3 consecutive wrong attempts. Please reset your UPI PIN to continue.");
    }
}
