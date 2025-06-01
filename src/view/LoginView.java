package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import controller.LoginController;
import model.User;

/**
 * Enhanced Login screen with OTP support for the Legal Case Management System.
 */
public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField otpField;
    private JButton loginButton;
    private JButton otpLoginButton;
    private JButton sendOTPButton;
    private JButton verifyOTPButton;
    private JButton backButton;
    private JButton exitButton;
    private JLabel statusLabel;
    private JLabel countdownLabel;
    private LoginController loginController;
    
    // Panel references for switching between views
    private JPanel traditionalLoginPanel;
    private JPanel otpLoginPanel;
    private JPanel otpVerificationPanel;
    private JPanel currentPanel;
    
    // OTP countdown timer
    private Timer countdownTimer;
    private long remainingSeconds;
    
    // Custom colors for the application
    private static final Color PRIMARY_COLOR = new Color(42, 58, 86);     // Dark blue
    private static final Color SECONDARY_COLOR = new Color(2, 119, 189);  // Bright blue
    private static final Color ACCENT_COLOR = new Color(245, 245, 245);   // Light gray
    private static final Color TEXT_COLOR = new Color(51, 51, 51);        // Dark gray
    private static final Color ERROR_COLOR = new Color(176, 42, 55);      // Red
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);    // Green
    
    /**
     * Constructor
     */
    public LoginView() {
        loginController = new LoginController();
        initializeUI();
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setTitle("Legal Case Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 700);
        setLocationRelativeTo(null); // Center on screen
        setResizable(true);
        
        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY_COLOR,
                    0, getHeight(), SECONDARY_COLOR);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        // Logo/title panel
        JPanel logoPanel = createLogoPanel();
        
        // Create different login panels
        traditionalLoginPanel = createTraditionalLoginPanel();
        otpLoginPanel = createOTPLoginPanel();
        otpVerificationPanel = createOTPVerificationPanel();
        
        // Form container panel
        JPanel formContainer = new JPanel();
        formContainer.setOpaque(false);
        formContainer.setLayout(new CardLayout());
        formContainer.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));
        
        // Add panels to card layout
        formContainer.add(traditionalLoginPanel, "traditional");
        formContainer.add(otpLoginPanel, "otp");
        formContainer.add(otpVerificationPanel, "verify");
        
        currentPanel = traditionalLoginPanel;
        
        // Add components to main panel
        mainPanel.add(logoPanel);
        mainPanel.add(formContainer);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Initialize focus
        SwingUtilities.invokeLater(() -> usernameField.requestFocus());
    }
    
    /**
     * Creates the logo panel
     */
    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 30, 0));
        
        // System logo image
        ImageIcon logoIcon = createLogo(120, 120);
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // System name
        JLabel titleLabel = new JLabel("Legal Case Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Professional Legal Practice Solution");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        logoPanel.add(logoLabel);
        logoPanel.add(titleLabel);
        logoPanel.add(subtitleLabel);
        
        return logoPanel;
    }
    
    /**
     * Creates the traditional login panel
     */
    private JPanel createTraditionalLoginPanel() {
        JPanel panel = createStyledPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Header
        JLabel headerLabel = new JLabel("User Login");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(ERROR_COLOR);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Username field
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameField = createStyledTextField();
        
        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordField = createStyledPasswordField();
        
        // Buttons
        loginButton = createStyledButton("Login", SECONDARY_COLOR);
        otpLoginButton = createStyledButton("Login with OTP", new Color(76, 175, 80));
        exitButton = createStyledButton("Exit", PRIMARY_COLOR);
        
        // Layout components
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(headerLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(statusLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(usernameLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(passwordLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(passwordField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(exitButton);
        buttonPanel.add(loginButton);
        buttonPanel.add(new JLabel()); // Empty space
        buttonPanel.add(otpLoginButton);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        panel.add(buttonPanel, gbc);
        
        // Add action listeners
        loginButton.addActionListener(e -> performTraditionalLogin());
        otpLoginButton.addActionListener(e -> switchToOTPLogin());
        exitButton.addActionListener(e -> System.exit(0));
        
        // Add key listeners
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performTraditionalLogin();
                }
            }
        });
        
        return panel;
    }
    
    /**
     * Creates the OTP login panel
     */
    private JPanel createOTPLoginPanel() {
        JPanel panel = createStyledPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Header
        JLabel headerLabel = new JLabel("Login with OTP");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Info label
        JLabel infoLabel = new JLabel("<html><center>Enter your email address to receive<br>a One-Time Password (OTP)</center></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setForeground(TEXT_COLOR);
        
        // Email field
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailField = createStyledTextField();
        
        // Buttons
        sendOTPButton = createStyledButton("Send OTP", SECONDARY_COLOR);
        backButton = createStyledButton("Back to Login", PRIMARY_COLOR);
        
        // Countdown label
        countdownLabel = new JLabel(" ");
        countdownLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        countdownLabel.setHorizontalAlignment(SwingConstants.CENTER);
        countdownLabel.setForeground(new Color(255, 152, 0));
        
        // Layout components
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(headerLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(infoLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(emailLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(countdownLabel, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);
        buttonPanel.add(sendOTPButton);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        panel.add(buttonPanel, gbc);
        
        // Add action listeners
        sendOTPButton.addActionListener(e -> sendOTP());
        backButton.addActionListener(e -> switchToTraditionalLogin());
        
        emailField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendOTP();
                }
            }
        });
        
        return panel;
    }
    
    /**
     * Creates the OTP verification panel
     */
    private JPanel createOTPVerificationPanel() {
        JPanel panel = createStyledPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Header
        JLabel headerLabel = new JLabel("Enter OTP");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Info label
        JLabel infoLabel = new JLabel("<html><center>Enter the 6-digit code sent to your email<br>The OTP is valid for 10 minutes</center></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setForeground(TEXT_COLOR);
        
        // OTP field
        JLabel otpLabel = new JLabel("OTP Code");
        otpLabel.setFont(new Font("Arial", Font.BOLD, 14));
        otpField = createStyledTextField();
        otpField.setFont(new Font("Arial", Font.BOLD, 18));
        otpField.setHorizontalAlignment(JTextField.CENTER);
        
        // Buttons
        verifyOTPButton = createStyledButton("Verify & Login", SUCCESS_COLOR);
        JButton resendOTPButton = createStyledButton("Resend OTP", new Color(255, 152, 0));
        JButton cancelButton = createStyledButton("Cancel", PRIMARY_COLOR);
        
        // Layout components
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(headerLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(infoLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(otpLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(otpField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(cancelButton);
        buttonPanel.add(verifyOTPButton);
        buttonPanel.add(new JLabel()); // Empty space
        buttonPanel.add(resendOTPButton);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        panel.add(buttonPanel, gbc);
        
        // Add action listeners
        verifyOTPButton.addActionListener(e -> verifyOTP());
        resendOTPButton.addActionListener(e -> resendOTP());
        cancelButton.addActionListener(e -> switchToOTPLogin());
        
        otpField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    verifyOTP();
                }
            }
        });
        
        return panel;
    }
    
    /**
     * Creates a styled panel with rounded corners
     */
    private JPanel createStyledPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        return panel;
    }
    
    /**
     * Creates a styled text field
     */
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, SECONDARY_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }
    
    /**
     * Creates a styled password field
     */
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, SECONDARY_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }
    
    /**
     * Creates a styled button
     */
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        return button;
    }
    
    /**
     * Switches to traditional login panel
     */
    private void switchToTraditionalLogin() {
        CardLayout cl = (CardLayout) currentPanel.getParent().getLayout();
        cl.show(currentPanel.getParent(), "traditional");
        currentPanel = traditionalLoginPanel;
        clearStatusMessage();
        SwingUtilities.invokeLater(() -> usernameField.requestFocus());
    }
    
    /**
     * Switches to OTP login panel
     */
    private void switchToOTPLogin() {
        CardLayout cl = (CardLayout) currentPanel.getParent().getLayout();
        cl.show(currentPanel.getParent(), "otp");
        currentPanel = otpLoginPanel;
        clearStatusMessage();
        updateOTPButtonState();
        SwingUtilities.invokeLater(() -> emailField.requestFocus());
    }
    
    /**
     * Switches to OTP verification panel
     */
    private void switchToOTPVerification() {
        CardLayout cl = (CardLayout) currentPanel.getParent().getLayout();
        cl.show(currentPanel.getParent(), "verify");
        currentPanel = otpVerificationPanel;
        clearStatusMessage();
        SwingUtilities.invokeLater(() -> otpField.requestFocus());
    }
    
    /**
     * Performs traditional username/password login
     */
    private void performTraditionalLogin() {
        String username = usernameField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);
        
        if (username.isEmpty() || password.isEmpty()) {
            showErrorMessage("Please enter both username and password");
            return;
        }
        
        try {
            User user = loginController.authenticateUser(username, password);
            
            if (user != null) {
                // Clear the password array for security
                Arrays.fill(passwordChars, '0');
                
                // Open main application
                openMainApplication(user);
                
                // Close login window
                dispose();
            } else {
                showErrorMessage("Invalid username or password");
                passwordField.setText("");
            }
        } catch (Exception ex) {
            showErrorMessage("Login error: " + ex.getMessage());
        }
    }
    
    /**
     * Sends OTP to the specified email
     */
    private void sendOTP() {
        String email = emailField.getText();
        
        if (email.isEmpty()) {
            showErrorMessage("Please enter your email address");
            return;
        }
        
        try {
            LoginController.LoginResult result = loginController.initiateOTPLogin(email);
            
            if (result.isSuccess()) {
                showSuccessMessage(result.getMessage());
                switchToOTPVerification();
            } else {
                showErrorMessage(result.getMessage());
                if (result.getMessage().contains("wait")) {
                    startCountdownTimer(email);
                }
            }
        } catch (Exception ex) {
            showErrorMessage("Error sending OTP: " + ex.getMessage());
        }
    }
    
    /**
     * Verifies the entered OTP
     */
    private void verifyOTP() {
        String email = emailField.getText();
        String otpCode = otpField.getText();
        
        if (otpCode.isEmpty()) {
            showErrorMessage("Please enter the OTP code");
            return;
        }
        
        if (otpCode.length() != 6) {
            showErrorMessage("OTP must be 6 digits");
            return;
        }
        
        try {
            User user = loginController.authenticateWithOTP(email, otpCode);
            
            if (user != null) {
                showSuccessMessage("OTP verified successfully! Logging in...");
                
                // Small delay to show success message
                Timer timer = new Timer(1000, e -> {
                    openMainApplication(user);
                    dispose();
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                showErrorMessage("Invalid or expired OTP. Please try again.");
                otpField.setText("");
            }
        } catch (Exception ex) {
            showErrorMessage("Error verifying OTP: " + ex.getMessage());
        }
    }
    
    /**
     * Resends OTP to the email
     */
    private void resendOTP() {
        String email = emailField.getText();
        if (!email.isEmpty()) {
            sendOTP();
        } else {
            switchToOTPLogin();
        }
    }
    
    /**
     * Updates the state of OTP-related buttons based on cooldown
     */
    private void updateOTPButtonState() {
        String email = emailField.getText();
        if (email.isEmpty()) {
            sendOTPButton.setEnabled(true);
            countdownLabel.setText(" ");
            return;
        }
        
        if (loginController.canRequestNewOTP(email)) {
            sendOTPButton.setEnabled(true);
            countdownLabel.setText(" ");
        } else {
            startCountdownTimer(email);
        }
    }
    
    /**
     * Starts countdown timer for OTP cooldown
     */
    private void startCountdownTimer(String email) {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        
        remainingSeconds = loginController.getRemainingCooldownSeconds(email);
        sendOTPButton.setEnabled(false);
        
        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (remainingSeconds <= 0) {
                    countdownTimer.stop();
                    sendOTPButton.setEnabled(true);
                    countdownLabel.setText(" ");
                } else {
                    countdownLabel.setText("Wait " + remainingSeconds + "s before requesting new OTP");
                    remainingSeconds--;
                }
            }
        });
        countdownTimer.start();
    }
    
    /**
     * Shows error message
     */
    private void showErrorMessage(String message) {
        if (currentPanel == traditionalLoginPanel) {
            statusLabel.setForeground(ERROR_COLOR);
            statusLabel.setText(message);
        } else {
            JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Shows success message
     */
    private void showSuccessMessage(String message) {
        if (currentPanel == traditionalLoginPanel) {
            statusLabel.setForeground(SUCCESS_COLOR);
            statusLabel.setText(message);
        } else {
            JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Clears status message
     */
    private void clearStatusMessage() {
        if (statusLabel != null) {
            statusLabel.setText(" ");
        }
    }
    
    /**
     * Open the main application window after successful login
     * 
     * @param user The authenticated user
     */
    private void openMainApplication(User user) {
        SwingUtilities.invokeLater(() -> {
            MainView mainView = new MainView(user);
            mainView.setVisible(true);
        });
    }
    
    /**
     * Create a simple law scales logo image
     * 
     * @param width Width of the logo
     * @param height Height of the logo
     * @return An ImageIcon with the logo
     */
    private ImageIcon createLogo(int width, int height) {
        // Create a buffered image for the logo
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
            width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw a scales of justice symbol
        g2d.setColor(Color.WHITE);
        
        // Base of the scales
        g2d.fillRect(width/2 - 2, height/3, 4, 2*height/3 - 10);
        
        // Top horizontal line
        g2d.fillRect(width/4, height/3, width/2, 4);
        
        // Left scale
        g2d.drawOval(width/4 - 20, height/3 + 10, 40, 40);
        
        // Right scale
        g2d.drawOval(3*width/4 - 20, height/3 + 10, 40, 40);
        
        // Gavel
        g2d.fillRoundRect(width/2 - 25, height/6, 50, 20, 5, 5);
        g2d.fillRect(width/2 + 15, height/6 - 10, 5, 40);
        
        g2d.dispose();
        
        return new ImageIcon(image);
    }
    
    /**
     * Main method to start the application
     */
    public static void main(String[] args) {
        try {
            // Set look and feel to system default
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }
}