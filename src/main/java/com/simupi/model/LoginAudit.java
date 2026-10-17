package com.simupi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Audit entity tracking authentication attempts for security verification and auditing.
 * Demonstrates OOP: Encapsulation.
 */
@Entity
@Table(name = "login_audit")
public class LoginAudit {

    // OOP: Encapsulation - Private fields with managed access
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(nullable = false, length = 10)
    private String mobile;

    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(nullable = false, length = 20)
    private String status; // "SUCCESS" or "FAILURE"

    @Column(name = "failure_reason")
    private String failureReason;

    public LoginAudit() {
        this.loginTime = LocalDateTime.now();
    }

    public LoginAudit(User user, String mobile, String ipAddress, String status, String failureReason) {
        this();
        this.user = user;
        this.mobile = mobile;
        this.ipAddress = ipAddress;
        this.status = status;
        this.failureReason = failureReason;
    }

    // Getters and Setters (OOP: Encapsulation)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
