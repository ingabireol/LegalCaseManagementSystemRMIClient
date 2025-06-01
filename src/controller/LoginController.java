package controller;

import model.User;
import service.UserService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Controller for login and authentication operations using RMI.
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
     * Authenticate a user
     * 
     * @param username The username
     * @param password The password
     * @return The authenticated user, or null if authentication failed
     */
    public User authenticateUser(String username, String password) {
        try {
            return userService.authenticateUser(username, password);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
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
}