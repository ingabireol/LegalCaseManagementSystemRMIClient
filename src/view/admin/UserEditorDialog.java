package view.admin;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;

import model.User;
import controller.UserController;
import view.util.UIConstants;
import view.util.SwingUtils;

/**
 * Dialog for adding or editing a user.
 */
public class UserEditorDialog extends JDialog {
    private JTextField usernameField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JComboBox<String> roleCombo;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JCheckBox activeCheckbox;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    private User user;
    private UserController userController;
    private boolean userSaved = false;
    private boolean isNewUser;
    
    /**
     * Constructor
     * 
     * @param parent The parent window
     * @param user The user to edit, or null for a new user
     */
    public UserEditorDialog(Window parent, User user) {
        super(parent, user == null ? "Add New User" : "Edit User", ModalityType.APPLICATION_MODAL);
        
        this.user = user;
        this.userController = new UserController();
        this.isNewUser = (user == null);
        
        initializeUI();
        loadUserData();
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setSize(500, 500);
        setMinimumSize(new Dimension(450, 450));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Create title panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Create form panel
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create the title panel
     * 
     * @return The title panel
     */
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UIConstants.PRIMARY_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Title label
        JLabel titleLabel = new JLabel(isNewUser ? "Add New User" : "Edit User");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        return titlePanel;
    }
    
    /**
     * Create the form panel
     * 
     * @return The form panel
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = GridBagConstraints.RELATIVE;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(5, 5, 5, 10);
        
        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = GridBagConstraints.RELATIVE;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1.0;
        fieldConstraints.insets = new Insets(5, 0, 5, 5);
        
        // Username
        JLabel usernameLabel = new JLabel("Username:*");
        usernameLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(usernameLabel, labelConstraints);
        
        usernameField = new JTextField(20);
        usernameField.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(usernameField, fieldConstraints);
        
        // Full Name
        JLabel fullNameLabel = new JLabel("Full Name:*");
        fullNameLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(fullNameLabel, labelConstraints);
        
        fullNameField = new JTextField(20);
        fullNameField.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(fullNameField, fieldConstraints);
        
        // Email
        JLabel emailLabel = new JLabel("Email:*");
        emailLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(emailLabel, labelConstraints);
        
        emailField = new JTextField(20);
        emailField.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(emailField, fieldConstraints);
        
        // Role
        JLabel roleLabel = new JLabel("Role:*");
        roleLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(roleLabel, labelConstraints);
        
        String[] roles = userController.getUserRoles();
        roleCombo = new JComboBox<>(roles);
        roleCombo.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(roleCombo, fieldConstraints);
        
        // Active status
        JLabel activeLabel = new JLabel("Status:");
        activeLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(activeLabel, labelConstraints);
        
        activeCheckbox = new JCheckBox("Active");
        activeCheckbox.setFont(UIConstants.NORMAL_FONT);
        activeCheckbox.setSelected(true);
        activeCheckbox.setBackground(Color.WHITE);
        formPanel.add(activeCheckbox, fieldConstraints);
        
        // Password fields - only shown for new users
        if (isNewUser) {
            // Password
            JLabel passwordLabel = new JLabel("Password:*");
            passwordLabel.setFont(UIConstants.LABEL_FONT);
            formPanel.add(passwordLabel, labelConstraints);
            
            passwordField = new JPasswordField(20);
            passwordField.setFont(UIConstants.NORMAL_FONT);
            formPanel.add(passwordField, fieldConstraints);
            
            // Confirm Password
            JLabel confirmPasswordLabel = new JLabel("Confirm Password:*");
            confirmPasswordLabel.setFont(UIConstants.LABEL_FONT);
            formPanel.add(confirmPasswordLabel, labelConstraints);
            
            confirmPasswordField = new JPasswordField(20);
            confirmPasswordField.setFont(UIConstants.NORMAL_FONT);
            formPanel.add(confirmPasswordField, fieldConstraints);
        }
        
        // Required fields note
        JLabel requiredNote = new JLabel("* Required fields");
        requiredNote.setFont(UIConstants.SMALL_FONT);
        requiredNote.setForeground(UIConstants.ERROR_COLOR);
        
        GridBagConstraints noteConstraints = new GridBagConstraints();
        noteConstraints.gridx = 0;
        noteConstraints.gridy = GridBagConstraints.RELATIVE;
        noteConstraints.gridwidth = 2;
        noteConstraints.anchor = GridBagConstraints.WEST;
        noteConstraints.insets = new Insets(20, 5, 5, 5);
        
        formPanel.add(requiredNote, noteConstraints);
        
        return formPanel;
    }
    
    /**
     * Create the button panel
     * 
     * @return The button panel
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        buttonPanel.setBackground(Color.WHITE);
        
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(UIConstants.NORMAL_FONT);
        cancelButton.addActionListener(e -> dispose());
        
        saveButton = new JButton("Save User");
        saveButton.setFont(UIConstants.NORMAL_FONT);
        saveButton.setBackground(UIConstants.SECONDARY_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveUser());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(saveButton);
        
        return buttonPanel;
    }
    
    /**
     * Load user data into form fields if editing an existing user
     */
    private void loadUserData() {
        if (user != null) {
            // Populate form fields with user data
            usernameField.setText(user.getUsername());
            fullNameField.setText(user.getFullName());
            emailField.setText(user.getEmail());
            roleCombo.setSelectedItem(user.getRole());
            activeCheckbox.setSelected(user.isActive());
            
            // Disable username field when editing
            usernameField.setEditable(false);
        }
    }
    
    /**
     * Validate form data
     * 
     * @return true if form data is valid
     */
    private boolean validateForm() {
        // Check required fields
        if (usernameField.getText().trim().isEmpty()) {
            showError("Username is required.");
            usernameField.requestFocus();
            return false;
        }
        
        if (fullNameField.getText().trim().isEmpty()) {
            showError("Full name is required.");
            fullNameField.requestFocus();
            return false;
        }
        
        if (emailField.getText().trim().isEmpty()) {
            showError("Email address is required.");
            emailField.requestFocus();
            return false;
        }
        
        // Basic email validation
        String email = emailField.getText().trim();
        if (!email.matches("^.+@.+\\..+$")) {
            showError("Please enter a valid email address.");
            emailField.requestFocus();
            return false;
        }
        
        // Check username availability for new users
        if (isNewUser) {
            String username = usernameField.getText().trim();
            if (userController.isUsernameExists(username)) {
                showError("Username already exists. Please choose a different username.");
                usernameField.requestFocus();
                return false;
            }
        }
        
        // Check password fields for new users
        if (isNewUser) {
            if (passwordField.getPassword().length == 0) {
                showError("Password is required.");
                passwordField.requestFocus();
                return false;
            }
            
            if (confirmPasswordField.getPassword().length == 0) {
                showError("Confirm password is required.");
                confirmPasswordField.requestFocus();
                return false;
            }
            
            // Check if passwords match
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            if (!password.equals(confirmPassword)) {
                showError("Passwords do not match.");
                confirmPasswordField.requestFocus();
                return false;
            }
            
            // Check password strength
            if (password.length() < 6) {
                showError("Password must be at least 6 characters long.");
                passwordField.requestFocus();
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Show an error message
     * 
     * @param message The error message
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Validation Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Save the user
     */
    private void saveUser() {
        if (!validateForm()) {
            return;
        }
        
        try {
            // Create or update user object
            if (isNewUser) {
                user = new User();
                user.setRegistrationDate(LocalDate.now());
            }
            
            user.setUsername(usernameField.getText().trim());
            user.setFullName(fullNameField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setRole((String) roleCombo.getSelectedItem());
            user.setActive(activeCheckbox.isSelected());
            
            boolean success;
            if (isNewUser) {
                // Create new user with password
                String password = new String(passwordField.getPassword());
                User createdUser = userController.createUser(user, password);
                success = (createdUser != null);
                if (success) {
                    user = createdUser; // Get the user with ID
                }
            } else {
                // Update existing user
                success = userController.updateUser(user);
            }
            
            if (success) {
                userSaved = true;
                dispose();
            } else {
                showError("Failed to save user. Please try again.");
            }
            
        } catch (Exception e) {
            showError("Error saving user: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Check if user was saved
     * 
     * @return true if user was saved
     */
    public boolean isUserSaved() {
        return userSaved;
    }
    
    /**
     * Get the user
     * 
     * @return The user
     */
    public User getUser() {
        return user;
    }
}