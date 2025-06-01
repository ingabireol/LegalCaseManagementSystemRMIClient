package controller;

import model.User;
import service.UserService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Enhanced controller for login and authentication operations using RMI with OTP support.
 */
public class LoginController {
    private UserService userService;
    private Registry registry;
    
    // RMI server configuration
    private static final String RMI_HOST = "127.0.0.1";
    private static final int RMI_PORT = 5555;
    
    /**
     * Constructor - establishes RMI connection
     */
    public LoginController() {
        try {
            // Locate RMI registry
            registry = LocateRegistry.getRegistry(RMI_HOST, RMI_PORT);
            
            // Get service stub
            userService = (UserService) registry.lookup("userService");
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to connect to RMI server: " + ex.getMessage());
        }
    }
    
    /**
     * Authenticate a user using traditional username/password method
     * 
     * @param username The username
     * @param password The password
     * @return The authenticated user, or null if authentication failed
     */
    public User authenticateUser(String username, String password) {
        try {
            if (username == null || username.trim().isEmpty() || 
                password == null || password.trim().isEmpty()) {
                return null;
            }
            
            return userService.authenticateUser(username.trim(), password);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Initiates OTP-based login by sending OTP to user's email
     * 
     * @param email The user's email address
     * @return LoginResult indicating success/failure and message
     */
    public LoginResult initiateOTPLogin(String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                return new LoginResult(false, "Email address is required");
            }
            
            email = email.trim().toLowerCase();
            
            // Validate email format
            if (!isValidEmail(email)) {
                return new LoginResult(false, "Please enter a valid email address");
            }
            
            // Check rate limiting
            if (!userService.canRequestNewOTP(email)) {
                long remainingSeconds = userService.getRemainingCooldownSeconds(email);
                return new LoginResult(false, "Please wait " + remainingSeconds + " seconds before requesting a new OTP");
            }
            
            // Initiate OTP login
            boolean success = userService.initiateOTPLogin(email);
            if (success) {
                return new LoginResult(true, "OTP has been sent to your email address. Please check your inbox.");
            } else {
                return new LoginResult(false, "Failed to send OTP. Please check your email address and try again.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new LoginResult(false, "Error initiating OTP login: " + ex.getMessage());
        }
    }
    
    /**
     * Verifies OTP and completes authentication
     * 
     * @param email The user's email address
     * @param otpCode The OTP code provided by user
     * @return User object if OTP verification successful, null otherwise
     */
    public User authenticateWithOTP(String email, String otpCode) {
        try {
            if (email == null || email.trim().isEmpty() || 
                otpCode == null || otpCode.trim().isEmpty()) {
                return null;
            }
            
            email = email.trim().toLowerCase();
            otpCode = otpCode.trim();
            
            // Validate OTP format (6 digits)
            if (!isValidOTPFormat(otpCode)) {
                return null;
            }
            
            return userService.authenticateWithOTP(email, otpCode);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Checks if user can request a new OTP
     * 
     * @param email The user's email address
     * @return true if user can request new OTP, false otherwise
     */
    public boolean canRequestNewOTP(String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                return false;
            }
            
            return userService.canRequestNewOTP(email.trim().toLowerCase());
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets remaining cooldown time for OTP requests
     * 
     * @param email The user's email address
     * @return Remaining seconds before new OTP can be requested
     */
    public long getRemainingCooldownSeconds(String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                return 0;
            }
            
            return userService.getRemainingCooldownSeconds(email.trim().toLowerCase());
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Check if username exists
     * 
     * @param username The username to check
     * @return true if username exists
     */
    public boolean isUsernameExists(String username) {
        try {
            return userService.isUsernameExists(username);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Reset password for a user
     * 
     * @param email The user's email
     * @return The new password, or null if reset failed
     */
    public String resetPassword(String email) {
        try {
            return userService.resetPassword(email);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Validates email format
     * 
     * @param email The email to validate
     * @return true if email format is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
    
    /**
     * Validates OTP format (6 digits)
     * 
     * @param otpCode The OTP code to validate
     * @return true if OTP format is valid, false otherwise
     */
    private boolean isValidOTPFormat(String otpCode) {
        return otpCode.matches("^\\d{6}$");
    }
    
    /**
     * Inner class to represent login operation results
     */
    public static class LoginResult {
        private boolean success;
        private String message;
        
        public LoginResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}