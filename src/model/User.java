package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a user in the legal case management system.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    
    private String username;
    
    private String passwordHash;
    
    private String passwordSalt;
    
    private String email;
    
    private String fullName;
    
    private String role;
    
    private LocalDate registrationDate;
    
    private LocalDateTime lastLogin;
    
    private boolean active;
    
    // Role constants
    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_ATTORNEY = "Attorney";
    public static final String ROLE_STAFF = "Staff";
    public static final String ROLE_FINANCE = "Finance";
    public static final String ROLE_READONLY = "ReadOnly";
    
    /**
     * Default constructor
     */
    public User() {
        this.registrationDate = LocalDate.now();
        this.active = true;
    }
    
    /**
     * Constructor with essential fields
     */
    public User(String username, String email, String fullName, String role) {
        this();
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getPasswordSalt() { return passwordSalt; }
    public void setPasswordSalt(String passwordSalt) { this.passwordSalt = passwordSalt; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
    
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    /**
     * Check if this user is an administrator
     */
    public boolean isAdmin() {
        return ROLE_ADMIN.equals(role);
    }
    
    /**
     * Check if this user is an attorney
     */
    public boolean isAttorney() {
        return ROLE_ATTORNEY.equals(role);
    }
    
    /**
     * Check if this user has finance access
     */
    public boolean isFinance() {
        return ROLE_FINANCE.equals(role);
    }
    
    /**
     * Check if this user has staff-level access
     */
    public boolean isStaff() {
        return ROLE_STAFF.equals(role);
    }
    
    /**
     * Check if this user has read-only access
     */
    public boolean isReadOnly() {
        return ROLE_READONLY.equals(role);
    }
    
    /**
     * Determines if the user has permission to modify case details
     */
    public boolean canModifyCases() {
        return isAdmin() || isAttorney() || isStaff();
    }
    
    /**
     * Determines if the user has permission to view financial information
     */
    public boolean canViewFinancials() {
        return isAdmin() || isFinance() || isAttorney();
    }
    
    /**
     * Determines if the user has permission to modify financial information
     */
    public boolean canModifyFinancials() {
        return isAdmin() || isFinance();
    }
    
    /**
     * Determines if the user has permission to create or modify users
     */
    public boolean canManageUsers() {
        return isAdmin();
    }
    
    /**
     * Returns a formatted display name for the user
     */
    public String getDisplayName() {
        return fullName + " (" + role + ")";
    }
    
    /**
     * Checks if the account is newly created (no login yet)
     */
    public boolean isNewAccount() {
        return lastLogin == null;
    }
    
    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", fullName=" + fullName + 
               ", role=" + role + ", active=" + active + "]";
    }
}