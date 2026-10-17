package com.simupi.dto.response;

import java.time.LocalDateTime;

/**
 * Public profile data of a user safely excluding sensitive hashes and internal counters.
 */
public class UserProfileResponse {

    private Long id;
    private String name;
    private String mobile;
    private String email;
    private String upiId;
    private boolean isPinLocked;
    private LocalDateTime createdAt;

    public UserProfileResponse() {}

    public UserProfileResponse(Long id, String name, String mobile, String email, String upiId, boolean isPinLocked, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.upiId = upiId;
        this.isPinLocked = isPinLocked;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public boolean isPinLocked() {
        return isPinLocked;
    }

    public void setPinLocked(boolean pinLocked) {
        isPinLocked = pinLocked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
