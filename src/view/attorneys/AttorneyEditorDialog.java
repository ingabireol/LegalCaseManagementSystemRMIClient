package view.attorneys;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;

import model.Attorney;
import controller.AttorneyController;
import view.util.UIConstants;
import view.util.SwingUtils;

/**
 * Dialog for adding or editing an attorney.
 */
public class AttorneyEditorDialog extends JDialog {
    private JTextField attorneyIdField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField barNumberField;
    private JTextField specializationField;
    private JFormattedTextField hourlyRateField;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    private Attorney attorney;
    private AttorneyController attorneyController;
    private boolean attorneySaved = false;
    
    /**
     * Constructor for creating a new attorney or editing an existing one
     * 
     * @param parent The parent window
     * @param attorney The attorney to edit, or null for a new attorney
     */
    public AttorneyEditorDialog(Window parent, Attorney attorney) {
        super(parent, attorney == null ? "Add New Attorney" : "Edit Attorney", ModalityType.APPLICATION_MODAL);
        
        this.attorney = attorney;
        this.attorneyController = new AttorneyController();
        
        initializeUI();
        loadAttorneyData();
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setSize(600, 500);
        setMinimumSize(new Dimension(500, 400));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Create form panel
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create the form panel with input fields
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
        
        // Attorney ID
        JLabel attorneyIdLabel = new JLabel("Attorney ID:");
        attorneyIdLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(attorneyIdLabel, labelConstraints);
        
        attorneyIdField = new JTextField(20);
        attorneyIdField.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(attorneyIdField, fieldConstraints);
        
        // First Name
        JLabel firstNameLabel = new JLabel("First Name:*");
        firstNameLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(firstNameLabel, labelConstraints);
        
        firstNameField = new JTextField(20);
        firstNameField.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(firstNameField, fieldConstraints);
        
        // Last Name
        JLabel lastNameLabel = new JLabel("Last Name:*");
        lastNameLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(lastNameLabel, labelConstraints);
        
        lastNameField = new JTextField(20);
        lastNameField.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(lastNameField, fieldConstraints);
        
        // Email
        JLabel emailLabel = new JLabel("Email:*");
        emailLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(emailLabel, labelConstraints);
        
        emailField = new JTextField(20);
        emailField.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(emailField, fieldConstraints);
        
        // Phone
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(phoneLabel, labelConstraints);
        
        phoneField = new JTextField(20);
        phoneField.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(phoneField, fieldConstraints);
        
        // Specialization
        JLabel specializationLabel = new JLabel("Specialization:");
        specializationLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(specializationLabel, labelConstraints);
        
        specializationField = new JTextField(20);
        specializationField.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(specializationField, fieldConstraints);
        
        // Bar Number
        JLabel barNumberLabel = new JLabel("Bar Number:*");
        barNumberLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(barNumberLabel, labelConstraints);
        
        barNumberField = new JTextField(20);
        barNumberField.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(barNumberField, fieldConstraints);
        
        // Hourly Rate
        JLabel hourlyRateLabel = new JLabel("Hourly Rate:*");
        hourlyRateLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(hourlyRateLabel, labelConstraints);
        
        // Create a formatted text field for currency
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        currencyFormat.setMinimumFractionDigits(2);
        hourlyRateField = new JFormattedTextField(currencyFormat);
        hourlyRateField.setFont(UIConstants.NORMAL_FONT);
        hourlyRateField.setValue(0.0);
        formPanel.add(hourlyRateField, fieldConstraints);
        
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
     * Create the button panel with action buttons
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
        
        saveButton = new JButton("Save Attorney");
        saveButton.setFont(UIConstants.NORMAL_FONT);
        saveButton.setBackground(UIConstants.SECONDARY_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveAttorney());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(saveButton);
        
        return buttonPanel;
    }
    
    /**
     * Load attorney data into form fields if editing an existing attorney
     */
    private void loadAttorneyData() {
        if (attorney != null) {
            // Populate form fields with attorney data
            attorneyIdField.setText(attorney.getAttorneyId());
            firstNameField.setText(attorney.getFirstName());
            lastNameField.setText(attorney.getLastName());
            emailField.setText(attorney.getEmail());
            phoneField.setText(attorney.getPhone());
            specializationField.setText(attorney.getSpecialization());
            barNumberField.setText(attorney.getBarNumber());
            hourlyRateField.setValue(attorney.getHourlyRate());
            
            // Disable attorney ID field when editing
            attorneyIdField.setEditable(false);
        } else {
            // Generate a new attorney ID for new attorneys
            attorneyIdField.setText(generateAttorneyId());
        }
    }
    
    /**
     * Generate a new attorney ID
     * 
     * @return A new attorney ID
     */
    private String generateAttorneyId() {
        // Format: ATT-YYYY-XXXX where XXXX is a random number
        String year = Integer.toString(java.time.LocalDate.now().getYear());
        int randomNum = 1000 + (int)(Math.random() * 9000); // Random 4-digit number
        
        return "ATT-" + year + "-" + randomNum;
    }
    
    /**
     * Validate form data
     * 
     * @return true if form data is valid
     */
    private boolean validateForm() {
        // Check required fields
        if (firstNameField.getText().trim().isEmpty()) {
            showError("First name is required.");
            firstNameField.requestFocus();
            return false;
        }
        
        if (lastNameField.getText().trim().isEmpty()) {
            showError("Last name is required.");
            lastNameField.requestFocus();
            return false;
        }
        
        if (emailField.getText().trim().isEmpty()) {
            showError("Email address is required.");
            emailField.requestFocus();
            return false;
        }
        
        if (barNumberField.getText().trim().isEmpty()) {
            showError("Bar number is required.");
            barNumberField.requestFocus();
            return false;
        }
        
        // Basic email validation
        String email = emailField.getText().trim();
        if (!email.matches("^.+@.+\\..+$")) {
            showError("Please enter a valid email address.");
            emailField.requestFocus();
            return false;
        }
        
        // Validate hourly rate
        try {
            Number rate = (Number) hourlyRateField.getValue();
            if (rate == null || rate.doubleValue() <= 0) {
                showError("Please enter a valid hourly rate greater than zero.");
                hourlyRateField.requestFocus();
                return false;
            }
        } catch (Exception e) {
            showError("Please enter a valid hourly rate.");
            hourlyRateField.requestFocus();
            return false;
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
     * Save the attorney
     */
    private void saveAttorney() {
        if (!validateForm()) {
            return;
        }
        
        try {
            // Create or update attorney object
            if (attorney == null) {
                attorney = new Attorney();
            }
            
            attorney.setAttorneyId(attorneyIdField.getText().trim());
            attorney.setFirstName(firstNameField.getText().trim());
            attorney.setLastName(lastNameField.getText().trim());
            attorney.setEmail(emailField.getText().trim());
            attorney.setPhone(phoneField.getText().trim());
            attorney.setSpecialization(specializationField.getText().trim());
            attorney.setBarNumber(barNumberField.getText().trim());
            
            // Get hourly rate from formatted field
            Number rateValue = (Number) hourlyRateField.getValue();
            double hourlyRate = rateValue != null ? rateValue.doubleValue() : 0.0;
            attorney.setHourlyRate(hourlyRate);
            
            boolean success;
            if (attorney.getId() == 0) {
                // Create new attorney
                success = attorneyController.createAttorney(attorney);
            } else {
                // Update existing attorney
                success = attorneyController.updateAttorney(attorney);
            }
            
            if (success) {
                attorneySaved = true;
                dispose();
            } else {
                showError("Failed to save attorney. Please try again.");
            }
            
        } catch (Exception e) {
            showError("Error saving attorney: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Check if attorney was saved
     * 
     * @return true if attorney was saved
     */
    public boolean isAttorneySaved() {
        return attorneySaved;
    }
    
    /**
     * Get the attorney
     * 
     * @return The attorney
     */
    public Attorney getAttorney() {
        return attorney;
    }
}