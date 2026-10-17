package com.simupi.exception;

/**
 * Thrown when a user attempts to transfer money to their own account or UPI ID.
 */
public class SelfTransferException extends SimUpiException {
    public SelfTransferException(String message) {
        super(message);
    }

    public SelfTransferException() {
        super("Self-transfer is not permitted. Sender and receiver cannot be the same user.");
    }
}
