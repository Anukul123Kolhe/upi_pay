package com.simupi.exception;

/**
 * Thrown when an incorrect UPI PIN is supplied. Tracks attempts remaining before lockout.
 */
public class InvalidPinException extends SimUpiException {
    private final int attemptsRemaining;

    public InvalidPinException(String message) {
        super(message);
        this.attemptsRemaining = 0;
    }

    public InvalidPinException(int attemptsRemaining) {
        super(attemptsRemaining > 0 
            ? String.format("Incorrect UPI PIN. You have %d attempt(s) remaining before your PIN is locked.", attemptsRemaining)
            : "Incorrect UPI PIN. Maximum attempts exceeded. Your UPI PIN is now locked.");
        this.attemptsRemaining = attemptsRemaining;
    }

    public int getAttemptsRemaining() {
        return attemptsRemaining;
    }
}
