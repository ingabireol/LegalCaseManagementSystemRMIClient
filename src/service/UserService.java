package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.User;

/**
 * Enhanced Remote service interface for User operations and authentication with OTP support
 */
public interface UserService extends Remote {
    
    /**
     * Authenticates a user by username and password (traditional method)
     */
    User authenticateUser(String username, String password) throws RemoteException;
    
    /**
     * Initiates OTP-based authentication by sending OTP to user's email
     * 
     * @param email The user's email address
     * @return true if OTP sent successfully, false otherwise
     */
    boolean initiateOTPLogin(String email) throws RemoteException;
    
    /**
     * Verifies OTP and completes authentication
     * 
     * @param email The user's email address
     * @param otpCode The OTP code provided by user
     * @return User object if OTP verification successful, null otherwise
     */
    User authenticateWithOTP(String email, String otpCode) throws RemoteException;
    
    /**
     * Checks if user can request a new OTP (rate limiting)
     * 
     * @param email The user's email address
     * @return true if user can request new OTP, false otherwise
     */
    boolean canRequestNewOTP(String email) throws RemoteException;
    
    /**
     * Gets remaining time before user can request new OTP
     * 
     * @param email The user's email address
     * @return Remaining seconds before new OTP can be requested
     */
    long getRemainingCooldownSeconds(String email) throws RemoteException;
    
    /**
     * Creates a new user
     */
    User createUser(User user, String password) throws RemoteException;
    
    /**
     * Updates an existing user
     */
    User updateUser(User user) throws RemoteException;
    
    /**
     * Checks if a username already exists
     */
    boolean isUsernameExists(String username) throws RemoteException;
    
    /**
     * Changes a user's password
     */
    boolean changePassword(int userId, String currentPassword, String newPassword) throws RemoteException;
    
    /**
     * Resets a user's password to a new random password
     */
    String resetPassword(String email) throws RemoteException;
    
    /**
     * Deactivates a user account
     */
    boolean deactivateUser(User user) throws RemoteException;
    
    /**
     * Reactivates a user account
     */
    boolean reactivateUser(User user) throws RemoteException;
    
    /**
     * Finds a user by ID
     */
    User findUserById(User user) throws RemoteException;
    
    /**
     * Finds a user by username
     */
    User findUserByUsername(String username) throws RemoteException;
    
    /**
     * Finds a user by email
     */
    User findUserByEmail(String email) throws RemoteException;
    
    /**
     * Gets a list of all active users
     */
    List<User> findAllActiveUsers() throws RemoteException;
    
    /**
     * Gets a list of users by role
     */
    List<User> findUsersByRole(String role) throws RemoteException;
}