package controller;

import model.User;
import service.UserService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

/**
 * Enhanced controller for user-related operations using RMI with OTP support.
 */
public class UserController {
    private UserService userService;
    private Registry registry;
    
    // RMI server configuration
    private static final String RMI_HOST = "127.0.0.1";
    private static final int RMI_PORT = 5555;
    
    /**
     * Constructor - establishes RMI connection
     */
    public UserController() {
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
            return userService.authenticateUser(username, password);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Initiates OTP-based authentication by sending OTP to user's email
     * 
     * @param email The user's email address
     * @return true if OTP sent successfully, false otherwise
     */
    public boolean initiateOTPLogin(String email) {
        try {
            return userService.initiateOTPLogin(email);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
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
            return userService.authenticateWithOTP(email, otpCode);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Checks if user can request a new OTP (rate limiting)
     * 
     * @param email The user's email address
     * @return true if user can request new OTP, false otherwise
     */
    public boolean canRequestNewOTP(String email) {
        try {
            return userService.canRequestNewOTP(email);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets remaining time before user can request new OTP
     * 
     * @param email The user's email address
     * @return Remaining seconds before new OTP can be requested
     */
    public long getRemainingCooldownSeconds(String email) {
        try {
            return userService.getRemainingCooldownSeconds(email);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Get a user by ID
     * 
     * @param userId The user ID
     * @return The user
     */
    public User getUserById(int userId) {
        try {
            User searchUser = new User();
            searchUser.setId(userId);
            return userService.findUserById(searchUser);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get a user by username
     * 
     * @param username The username
     * @return The user
     */
    public User getUserByUsername(String username) {
        try {
            return userService.findUserByUsername(username);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get a user by email
     * 
     * @param email The email
     * @return The user
     */
    public User getUserByEmail(String email) {
        try {
            return userService.findUserByEmail(email);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get all active users
     * 
     * @return List of active users
     */
    public List<User> getAllActiveUsers() {
        try {
            return userService.findAllActiveUsers();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get users by role
     * 
     * @param role The role
     * @return List of users with the role
     */
    public List<User> getUsersByRole(String role) {
        try {
            return userService.findUsersByRole(role);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Create a new user
     * 
     * @param user The user to create
     * @param password The password
     * @return The created user, or null if creation failed
     */
    public User createUser(User user, String password) {
        try {
            return userService.createUser(user, password);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Update a user
     * 
     * @param user The user to update
     * @return true if successful
     */
    public boolean updateUser(User user) {
        try {
            User result = userService.updateUser(user);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Change a user's password
     * 
     * @param userId The user ID
     * @param currentPassword The current password
     * @param newPassword The new password
     * @return true if successful
     */
    public boolean changePassword(int userId, String currentPassword, String newPassword) {
        try {
            return userService.changePassword(userId, currentPassword, newPassword);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Reset a user's password
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
     * Deactivate a user
     * 
     * @param userId The user ID
     * @return true if successful
     */
    public boolean deactivateUser(int userId) {
        try {
            User searchUser = new User();
            searchUser.setId(userId);
            return userService.deactivateUser(searchUser);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Reactivate a user
     * 
     * @param userId The user ID
     * @return true if successful
     */
    public boolean reactivateUser(int userId) {
        try {
            User searchUser = new User();
            searchUser.setId(userId);
            return userService.reactivateUser(searchUser);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Check if a username exists
     * 
     * @param username The username to check
     * @return true if the username exists
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
     * Get available user roles
     * 
     * @return Array of user roles
     */
    public String[] getUserRoles() {
        return new String[] {
            User.ROLE_ADMIN,
            User.ROLE_ATTORNEY,
            User.ROLE_STAFF,
            User.ROLE_FINANCE,
            User.ROLE_READONLY
        };
    }
}