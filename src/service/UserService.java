package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.User;

/**
 * Remote service interface for User operations and authentication
 */
public interface UserService extends Remote {
    
    /**
     * Authenticates a user by username and password
     */
    User authenticateUser(String username, String password) throws RemoteException;
    
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