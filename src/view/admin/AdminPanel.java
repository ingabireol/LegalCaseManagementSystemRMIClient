package view.admin;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;

import model.User;
import controller.UserController;
import view.util.UIConstants;
import view.util.SwingUtils;
import view.components.CustomTable;

/**
 * Panel for system administration in the Legal Case Management System.
 * Only accessible to users with administrator privileges.
 */
public class AdminPanel extends JPanel {
    private UserController userController;
    
    // User management components
    private CustomTable usersTable;
    private JButton addUserButton;
    private JButton editUserButton;
    private JButton deleteUserButton;
    private JButton resetPasswordButton;
    private JButton activateButton;
    private JComboBox<String> roleFilterCombo;
    
    /**
     * Constructor
     */
    public AdminPanel() {
        this.userController = new UserController();
        
        initializeUI();
        loadUsers();
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create tabbed pane for admin sections
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.NORMAL_FONT);
        
        // Create user management panel
        JPanel usersPanel = createUsersPanel();
        tabbedPane.addTab("User Management", usersPanel);
        
        // Create system settings panel
        JPanel settingsPanel = createSettingsPanel();
        tabbedPane.addTab("System Settings", settingsPanel);
        
        // Create database management panel
        JPanel databasePanel = createDatabasePanel();
        tabbedPane.addTab("Database Management", databasePanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * Create the header panel with title
     * 
     * @return The header panel
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        
        JLabel titleLabel = new JLabel("System Administration");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        headerPanel.add(titleLabel);
        
        return headerPanel;
    }
    
    /**
     * Create the user management panel
     * 
     * @return The users panel
     */
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel roleFilterLabel = new JLabel("Filter by Role:");
        roleFilterLabel.setFont(UIConstants.NORMAL_FONT);
        
        String[] roles = {"All Roles", "Admin", "Attorney", "Staff", "Finance", "ReadOnly"};
        roleFilterCombo = new JComboBox<>(roles);
        roleFilterCombo.setFont(UIConstants.NORMAL_FONT);
        roleFilterCombo.addActionListener(e -> filterUsersByRole());
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(UIConstants.NORMAL_FONT);
        refreshButton.addActionListener(e -> loadUsers());
        
        filterPanel.add(roleFilterLabel);
        filterPanel.add(roleFilterCombo);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(refreshButton);
        
        // Create table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        
        String[] columnNames = {
            "Username", "Full Name", "Email", "Role", "Status", "Last Login"
        };
        
        usersTable = new CustomTable(columnNames);
        
        // Set column widths
        usersTable.setColumnWidth(0, 120);  // Username
        usersTable.setColumnWidth(1, 180);  // Full Name
        usersTable.setColumnWidth(2, 200);  // Email
        usersTable.setColumnWidth(3, 100);  // Role
        usersTable.setColumnWidth(4, 80);   // Status
        usersTable.setColumnWidth(5, 150);  // Last Login
        
        // Custom renderer for status column
        usersTable.setColumnRenderer(4, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if ("Active".equals(value)) {
                        comp.setForeground(UIConstants.SUCCESS_COLOR);
                    } else {
                        comp.setForeground(UIConstants.ERROR_COLOR);
                    }
                }
                
                return comp;
            }
        });
        
        // Add selection listener
        usersTable.addSelectionListener(e -> updateButtonStates());
        
        tablePanel.add(usersTable, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        addUserButton = new JButton("Add User");
        addUserButton.setFont(UIConstants.NORMAL_FONT);
        addUserButton.setBackground(UIConstants.SECONDARY_COLOR);
        addUserButton.setForeground(Color.WHITE);
        addUserButton.addActionListener(e -> addUser());
        
        editUserButton = new JButton("Edit User");
        editUserButton.setFont(UIConstants.NORMAL_FONT);
        editUserButton.addActionListener(e -> editUser());
        editUserButton.setEnabled(false);
        
        deleteUserButton = new JButton("Delete User");
        deleteUserButton.setFont(UIConstants.NORMAL_FONT);
        deleteUserButton.addActionListener(e -> deleteUser());
        deleteUserButton.setEnabled(false);
        
        resetPasswordButton = new JButton("Reset Password");
        resetPasswordButton.setFont(UIConstants.NORMAL_FONT);
        resetPasswordButton.addActionListener(e -> resetUserPassword());
        resetPasswordButton.setEnabled(false);
        
        activateButton = new JButton("Toggle Active");
        activateButton.setFont(UIConstants.NORMAL_FONT);
        activateButton.addActionListener(e -> toggleUserActive());
        activateButton.setEnabled(false);
        
        buttonPanel.add(addUserButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(editUserButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(deleteUserButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(resetPasswordButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(activateButton);
        
        // Add components to panel
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create the system settings panel
     * 
     * @return The settings panel
     */
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create settings form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = GridBagConstraints.RELATIVE;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(5, 5, 5, 15);
        
        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = GridBagConstraints.RELATIVE;
        fieldConstraints.anchor = GridBagConstraints.WEST;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1.0;
        fieldConstraints.insets = new Insets(5, 5, 5, 5);
        
        // Sample system settings
        JLabel companyNameLabel = SwingUtils.createBoldLabel("Company Name:");
        JTextField companyNameField = new JTextField(30);
        companyNameField.setText("Your Law Firm");
        
        JLabel dbConnectionLabel = SwingUtils.createBoldLabel("Database Connection:");
        JTextField dbConnectionField = new JTextField(30);
        dbConnectionField.setText("jdbc:mysql://localhost:3306/legalcasemgmtdb");
        dbConnectionField.setEditable(false);
        
        JLabel backupDirLabel = SwingUtils.createBoldLabel("Backup Directory:");
        JTextField backupDirField = new JTextField(30);
        backupDirField.setText("backups/");
        
        JLabel invoicePrefixLabel = SwingUtils.createBoldLabel("Invoice Prefix:");
        JTextField invoicePrefixField = new JTextField(10);
        invoicePrefixField.setText("INV");
        
        JLabel autoBackupLabel = SwingUtils.createBoldLabel("Auto Backup:");
        JCheckBox autoBackupCheck = new JCheckBox("Enable automatic daily backup");
        autoBackupCheck.setSelected(true);
        
        // Add components to form
        formPanel.add(companyNameLabel, labelConstraints);
        formPanel.add(companyNameField, fieldConstraints);
        
        formPanel.add(dbConnectionLabel, labelConstraints);
        formPanel.add(dbConnectionField, fieldConstraints);
        
        formPanel.add(backupDirLabel, labelConstraints);
        formPanel.add(backupDirField, fieldConstraints);
        
        formPanel.add(invoicePrefixLabel, labelConstraints);
        formPanel.add(invoicePrefixField, fieldConstraints);
        
        formPanel.add(autoBackupLabel, labelConstraints);
        formPanel.add(autoBackupCheck, fieldConstraints);
        
        // Add save button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JButton saveSettingsButton = new JButton("Save Settings");
        saveSettingsButton.setFont(UIConstants.NORMAL_FONT);
        saveSettingsButton.setBackground(UIConstants.SECONDARY_COLOR);
        saveSettingsButton.setForeground(Color.WHITE);
        saveSettingsButton.addActionListener(e -> saveSystemSettings());
        
        buttonPanel.add(saveSettingsButton);
        
        // Add components to panel
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create the database management panel
     * 
     * @return The database panel
     */
    private JPanel createDatabasePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Database operations panel
        JPanel operationsPanel = new JPanel(new GridLayout(3, 1, 0, 20));
        operationsPanel.setBackground(Color.WHITE);
        operationsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Backup panel
        JPanel backupPanel = new JPanel(new BorderLayout());
        backupPanel.setBackground(Color.WHITE);
        backupPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.SECONDARY_COLOR),
            "Database Backup",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            UIConstants.LABEL_FONT,
            UIConstants.SECONDARY_COLOR
        ));
        
        JPanel backupButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backupButtonsPanel.setBackground(Color.WHITE);
        backupButtonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton createBackupButton = new JButton("Create New Backup");
        createBackupButton.setFont(UIConstants.NORMAL_FONT);
        createBackupButton.addActionListener(e -> createDatabaseBackup());
        
        backupButtonsPanel.add(createBackupButton);
        backupPanel.add(backupButtonsPanel, BorderLayout.CENTER);
        
        // Restore panel
        JPanel restorePanel = new JPanel(new BorderLayout());
        restorePanel.setBackground(Color.WHITE);
        restorePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.SECONDARY_COLOR),
            "Database Restore",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            UIConstants.LABEL_FONT,
            UIConstants.SECONDARY_COLOR
        ));
        
        JPanel restoreButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        restoreButtonsPanel.setBackground(Color.WHITE);
        restoreButtonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton restoreBackupButton = new JButton("Restore from Backup");
        restoreBackupButton.setFont(UIConstants.NORMAL_FONT);
        restoreBackupButton.addActionListener(e -> restoreFromBackup());
        
        restoreButtonsPanel.add(restoreBackupButton);
        restorePanel.add(restoreButtonsPanel, BorderLayout.CENTER);
        
        // Maintenance panel
        JPanel maintenancePanel = new JPanel(new BorderLayout());
        maintenancePanel.setBackground(Color.WHITE);
        maintenancePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.SECONDARY_COLOR),
            "Database Maintenance",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            UIConstants.LABEL_FONT,
            UIConstants.SECONDARY_COLOR
        ));
        
        JPanel maintenanceButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        maintenanceButtonsPanel.setBackground(Color.WHITE);
        maintenanceButtonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton optimizeButton = new JButton("Optimize Database");
        optimizeButton.setFont(UIConstants.NORMAL_FONT);
        optimizeButton.addActionListener(e -> optimizeDatabase());
        
        JButton checkIntegrityButton = new JButton("Check Integrity");
        checkIntegrityButton.setFont(UIConstants.NORMAL_FONT);
        checkIntegrityButton.addActionListener(e -> checkDatabaseIntegrity());
        
        maintenanceButtonsPanel.add(optimizeButton);
        maintenanceButtonsPanel.add(Box.createHorizontalStrut(10));
        maintenanceButtonsPanel.add(checkIntegrityButton);
        maintenancePanel.add(maintenanceButtonsPanel, BorderLayout.CENTER);
        
        // Add panels to operations panel
        operationsPanel.add(backupPanel);
        operationsPanel.add(restorePanel);
        operationsPanel.add(maintenancePanel);
        
        // Database statistics panel
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.SECONDARY_COLOR),
            "Database Statistics",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            UIConstants.LABEL_FONT,
            UIConstants.SECONDARY_COLOR
        ));
        
        // Create a simple statistics table
        String[] statColumnNames = {"Entity", "Count", "Last Updated"};
        DefaultTableModel statsModel = new DefaultTableModel(statColumnNames, 0);
        
        // Sample data - would be populated from database in a real implementation
        statsModel.addRow(new Object[]{"Users", "5", LocalDate.now().toString()});
        statsModel.addRow(new Object[]{"Clients", "32", LocalDate.now().toString()});
        statsModel.addRow(new Object[]{"Cases", "47", LocalDate.now().toString()});
        statsModel.addRow(new Object[]{"Documents", "156", LocalDate.now().toString()});
        statsModel.addRow(new Object[]{"Invoices", "29", LocalDate.now().toString()});
        
        JTable statsTable = new JTable(statsModel);
        statsTable.setFont(UIConstants.NORMAL_FONT);
        statsTable.setRowHeight(25);
        statsTable.getTableHeader().setFont(UIConstants.LABEL_FONT);
        
        JScrollPane statsScrollPane = new JScrollPane(statsTable);
        statsScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        statsPanel.add(statsScrollPane, BorderLayout.CENTER);
        
        // Add components to panel
        panel.add(operationsPanel, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Load users from database and populate table
     */
    private void loadUsers() {
        try {
            // Clear existing data
            usersTable.clearTable();
            
            // Get filter selection
            String selectedRole = (String) roleFilterCombo.getSelectedItem();
            
            // Get users from controller
            List<User> users;
            if ("All Roles".equals(selectedRole)) {
                users = userController.getAllActiveUsers();
            } else {
                users = userController.getUsersByRole(selectedRole);
            }
            
            // Populate table
            for (User user : users) {
                Object[] row = {
                    user.getUsername(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getRole(),
                    user.isActive() ? "Active" : "Inactive",
                    user.getLastLogin() != null ? user.getLastLogin().toString() : "Never"
                };
                usersTable.addRow(row);
            }
            
            // Update button states
            updateButtonStates();
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error loading users: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Filter users by role
     */
    private void filterUsersByRole() {
        loadUsers();
    }
    
    /**
     * Update button states based on selection
     */
    private void updateButtonStates() {
        boolean hasSelection = usersTable.getSelectedRow() != -1;
        editUserButton.setEnabled(hasSelection);
        deleteUserButton.setEnabled(hasSelection);
        resetPasswordButton.setEnabled(hasSelection);
        activateButton.setEnabled(hasSelection);
    }
    
    /**
     * Add a new user
     */
    private void addUser() {
        UserEditorDialog dialog = new UserEditorDialog(SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        
        if (dialog.isUserSaved()) {
            loadUsers();
        }
    }
    
    /**
     * Edit selected user
     */
    private void editUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        String username = usersTable.getValueAt(selectedRow, 0).toString();
        
        try {
            User user = userController.getUserByUsername(username);
            if (user != null) {
                UserEditorDialog dialog = new UserEditorDialog(SwingUtilities.getWindowAncestor(this), user);
                dialog.setVisible(true);
                
                if (dialog.isUserSaved()) {
                    loadUsers();
                }
            }
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error editing user: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Delete selected user
     */
    private void deleteUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        String username = usersTable.getValueAt(selectedRow, 0).toString();
        String fullName = usersTable.getValueAt(selectedRow, 1).toString();
        
        boolean confirmed = SwingUtils.showConfirmDialog(
            this,
            "Are you sure you want to delete user '" + fullName + "' (" + username + ")?\n" +
            "This action cannot be undone.",
            "Confirm Deletion"
        );
        
        if (confirmed) {
            try {
                User user = userController.getUserByUsername(username);
                if (user != null) {
                    boolean success = userController.deactivateUser(user.getId());
                    if (success) {
                        SwingUtils.showInfoMessage(
                            this,
                            "User deactivated successfully.",
                            "Success"
                        );
                        loadUsers();
                    } else {
                        SwingUtils.showErrorMessage(
                            this,
                            "Failed to deactivate user.",
                            "Error"
                        );
                    }
                }
            } catch (Exception e) {
                SwingUtils.showErrorMessage(
                    this,
                    "Error deactivating user: " + e.getMessage(),
                    "Database Error"
                );
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Reset password for selected user
     */
    private void resetUserPassword() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        String username = usersTable.getValueAt(selectedRow, 0).toString();
        String email = usersTable.getValueAt(selectedRow, 2).toString();
        
        boolean confirmed = SwingUtils.showConfirmDialog(
            this,
            "Are you sure you want to reset the password for user '" + username + "'?\n" +
            "A new password will be generated and displayed.",
            "Confirm Password Reset"
        );
        
        if (confirmed) {
            try {
                String newPassword = userController.resetPassword(email);
                if (newPassword != null) {
                    SwingUtils.showInfoMessage(
                        this,
                        "Password reset successfully.\n\n" +
                        "New password: " + newPassword + "\n\n" +
                        "Please inform the user about their new password.",
                        "Password Reset"
                    );
                } else {
                    SwingUtils.showErrorMessage(
                        this,
                        "Failed to reset password.",
                        "Error"
                    );
                }
            } catch (Exception e) {
                SwingUtils.showErrorMessage(
                    this,
                    "Error resetting password: " + e.getMessage(),
                    "Database Error"
                );
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Toggle active status for selected user
     */
    private void toggleUserActive() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        String username = usersTable.getValueAt(selectedRow, 0).toString();
        String status = usersTable.getValueAt(selectedRow, 4).toString();
        boolean isActive = "Active".equals(status);
        
        String action = isActive ? "deactivate" : "activate";
        
        boolean confirmed = SwingUtils.showConfirmDialog(
            this,
            "Are you sure you want to " + action + " user '" + username + "'?",
            "Confirm Status Change"
        );
        
        if (confirmed) {
            try {
                User user = userController.getUserByUsername(username);
                if (user != null) {
                    boolean success;
                    if (isActive) {
                        success = userController.deactivateUser(user.getId());
                    } else {
                        success = userController.reactivateUser(user.getId());
                    }
                    
                    if (success) {
                        SwingUtils.showInfoMessage(
                            this,
                            "User " + action + "d successfully.",
                            "Success"
                        );
                        loadUsers();
                    } else {
                        SwingUtils.showErrorMessage(
                            this,
                            "Failed to " + action + " user.",
                            "Error"
                        );
                    }
                }
            } catch (Exception e) {
                SwingUtils.showErrorMessage(
                    this,
                    "Error changing user status: " + e.getMessage(),
                    "Database Error"
                );
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Save system settings
     */
    private void saveSystemSettings() {
        // This would save system settings to a configuration file or database
        SwingUtils.showInfoMessage(
            this,
            "System settings saved successfully.",
            "Settings Saved"
        );
    }
    
    /**
     * Create database backup
     */
    private void createDatabaseBackup() {
        // This would perform a database backup
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify Backup File Location");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            
            // Show progress dialog
            SwingUtilities.invokeLater(() -> {
                JDialog progressDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Backup in Progress", true);
                progressDialog.setLayout(new BorderLayout());
                progressDialog.setSize(300, 100);
                progressDialog.setLocationRelativeTo(this);
                
                JPanel progressPanel = new JPanel(new BorderLayout(10, 10));
                progressPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                JLabel label = new JLabel("Creating database backup...");
                JProgressBar progressBar = new JProgressBar();
                progressBar.setIndeterminate(true);
                
                progressPanel.add(label, BorderLayout.NORTH);
                progressPanel.add(progressBar, BorderLayout.CENTER);
                
                progressDialog.add(progressPanel, BorderLayout.CENTER);
                
                // Simulate backup process with a worker thread
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        // Simulate backup process
                        Thread.sleep(2000);
                        return null;
                    }
                    
                    @Override
                    protected void done() {
                        progressDialog.dispose();
                        SwingUtils.showInfoMessage(
                            AdminPanel.this,
                            "Database backup completed successfully.\n" +
                            "Backup saved to: " + path,
                            "Backup Complete"
                        );
                    }
                };
                
                worker.execute();
                progressDialog.setVisible(true);
            });
        }
    }
    
    /**
     * Restore from backup
     */
    private void restoreFromBackup() {
        // This would restore from a database backup
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Backup File");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            boolean confirmed = SwingUtils.showConfirmDialog(
                this,
                "WARNING: Restoring from backup will overwrite the current database.\n" +
                "This action cannot be undone.\n\n" +
                "Are you sure you want to continue?",
                "Confirm Restore"
            );
            
            if (confirmed) {
                // Show progress dialog
                SwingUtilities.invokeLater(() -> {
                    JDialog progressDialog = new JDialog((Dialog) SwingUtilities.getWindowAncestor(this), "Restore in Progress", true);
                    progressDialog.setLayout(new BorderLayout());
                    progressDialog.setSize(300, 100);
                    progressDialog.setLocationRelativeTo(this);
                    
                    JPanel progressPanel = new JPanel(new BorderLayout(10, 10));
                    progressPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    
                    JLabel label = new JLabel("Restoring database from backup...");
                    JProgressBar progressBar = new JProgressBar();
                    progressBar.setIndeterminate(true);
                    
                    progressPanel.add(label, BorderLayout.NORTH);
                    progressPanel.add(progressBar, BorderLayout.CENTER);
                    
                    progressDialog.add(progressPanel, BorderLayout.CENTER);
                    
                    // Simulate restore process with a worker thread
                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            // Simulate restore process
                            Thread.sleep(3000);
                            return null;
                        }
                        
                        @Override
                        protected void done() {
                            progressDialog.dispose();
                            SwingUtils.showInfoMessage(
                                AdminPanel.this,
                                "Database restore completed successfully.",
                                "Restore Complete"
                            );
                        }
                    };
                    
                    worker.execute();
                    progressDialog.setVisible(true);
                });
            }
        }
    }
    
    /**
     * Optimize database
     */
    private void optimizeDatabase() {
        // This would optimize the database
        SwingUtils.showInfoMessage(
            this,
            "Database optimization completed successfully.",
            "Optimization Complete"
        );
    }
    
    /**
     * Check database integrity
     */
    private void checkDatabaseIntegrity() {
        // This would check database integrity
        SwingUtils.showInfoMessage(
            this,
            "Database integrity check completed.\n" +
            "No issues found.",
            "Integrity Check"
        );
    }
}