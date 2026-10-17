package com.simupi.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

/**
 * Utility helper to manage user session state safely.
 */
@Component
public class SessionHelper {

    public static final String SESSION_USER_KEY = "LOGGED_IN_USER_ID";

    public void setLoggedInUser(HttpSession session, Long userId) {
        session.setAttribute(SESSION_USER_KEY, userId);
    }

    public Long getLoggedInUserId(HttpSession session) {
        Object val = session.getAttribute(SESSION_USER_KEY);
        if (val instanceof Long) {
            return (Long) val;
        } else if (val instanceof Integer) {
            return ((Integer) val).longValue();
        }
        return null;
    }

    public void clearSession(HttpSession session) {
        session.removeAttribute(SESSION_USER_KEY);
        try {
            session.invalidate();
        } catch (IllegalStateException ignored) {}
    }
}
