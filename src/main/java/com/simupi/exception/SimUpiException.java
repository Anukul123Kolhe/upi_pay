package com.simupi.exception;

/**
 * Base unchecked exception for all SimUPI domain errors.
 */
public class SimUpiException extends RuntimeException {
    public SimUpiException(String message) {
        super(message);
    }

    public SimUpiException(String message, Throwable cause) {
        super(message, cause);
    }
}
