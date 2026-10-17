package com.simupi.exception;

/**
 * Thrown when looking up a user by identifier, UPI ID, or mobile number yields no result.
 */
public class UserNotFoundException extends SimUpiException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException byUpiId(String upiId) {
        return new UserNotFoundException("No user found registered with UPI ID: " + upiId);
    }

    public static UserNotFoundException byMobile(String mobile) {
        return new UserNotFoundException("No user found registered with mobile number: " + mobile);
    }

    public static UserNotFoundException byId(Long id) {
        return new UserNotFoundException("User not found with ID: " + id);
    }
}
