package view.clients;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import model.Client;
import controller.ClientController;
import view.util.UIConstants;
import view.components.DateChooser;

/**
 * Dialog for adding or editing a client.
 */
public class ClientEditorDialog extends JDialog {
    private JTextField clientIdField;
    private JTextField nameField;
    private JTextField contactPersonField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JComboBox<String> clientTypeCombo;
    private DateChooser registrationDateChooser;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    private Client client;
    private ClientController clientController;
    private boolean clientSaved = false;
    
    /**
     * Constructor for creating a new client
     * 
     * @param parent The parent window
     * @param client The client to edit, or null for a new client
     */
    public ClientEditorDialog(Window parent, Client client) {
        super(parent, client == null ? "Add New Client" : "Edit Client", ModalityType.APPLICATION_MODAL);
        
        this.client = client;
        this.clientController = new ClientController();
        
        initializeUI();
        loadClientData();
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
        
        // Client ID
        JLabel clientIdLabel = new JLabel("Client ID:");
        clientIdLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(clientIdLabel, labelConstraints);
        
        clientIdField = new JTextField(20);
        clientIdField.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(clientIdField, fieldConstraints);
        
        // Name
        JLabel nameLabel = new JLabel("Name:*");
        nameLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(nameLabel, labelConstraints);
        
        nameField = new JTextField(20);
        nameField.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(nameField, fieldConstraints);
        
        // Client Type
        JLabel clientTypeLabel = new JLabel("Client Type:*");
        clientTypeLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(clientTypeLabel, labelConstraints);
        
        clientTypeCombo = new JComboBox<>(new String[]{"Individual", "Organization"});
        clientTypeCombo.setFont(UIConstants.NORMAL_FONT);
        clientTypeCombo.addActionListener(e -> {
            boolean isOrganization = "Organization".equals(clientTypeCombo.getSelectedItem());
            contactPersonField.setEnabled(isOrganization);
            if (!isOrganization) {
                contactPersonField.setText("");
            }
        });
        formPanel.add(clientTypeCombo, fieldConstraints);
        
        // Contact Person
        JLabel contactPersonLabel = new JLabel("Contact Person:");
        contactPersonLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(contactPersonLabel, labelConstraints);
        
        contactPersonField = new JTextField(20);
        contactPersonField.setFont(UIConstants.NORMAL_FONT);
        contactPersonField.setEnabled(false);  // Initially disabled for Individual
        formPanel.add(contactPersonField, fieldConstraints);
        
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
        
        // Address
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(addressLabel, labelConstraints);
        
        addressArea = new JTextArea(4, 20);
        addressArea.setFont(UIConstants.NORMAL_FONT);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        
        JScrollPane addressScrollPane = new JScrollPane(addressArea);
        addressScrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        formPanel.add(addressScrollPane, fieldConstraints);
        
        // Registration Date
        JLabel registrationDateLabel = new JLabel("Registration Date:");
        registrationDateLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(registrationDateLabel, labelConstraints);
        
        registrationDateChooser = new DateChooser();
        registrationDateChooser.setDate(LocalDate.now());
        formPanel.add(registrationDateChooser, fieldConstraints);
        
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
        
        saveButton = new JButton("Save Client");
        saveButton.setFont(UIConstants.NORMAL_FONT);
        saveButton.setBackground(UIConstants.SECONDARY_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveClient());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(saveButton);
        
        return buttonPanel;
    }
    
    /**
     * Load client data into form fields if editing an existing client
     */
    private void loadClientData() {
        if (client != null) {
            // Populate form fields with client data
            clientIdField.setText(client.getClientId());
            nameField.setText(client.getName());
            emailField.setText(client.getEmail());
            phoneField.setText(client.getPhone());
            addressArea.setText(client.getAddress());
            clientTypeCombo.setSelectedItem(client.getClientType());
            
            if (client.getContactPerson() != null) {
                contactPersonField.setText(client.getContactPerson());
            }
            
            if (client.getRegistrationDate() != null) {
                registrationDateChooser.setDate(client.getRegistrationDate());
            }
            
            // Disable client ID field when editing
            clientIdField.setEditable(false);
        } else {
            // Generate a new client ID for new clients
            clientIdField.setText(generateClientId());
        }
    }
    
    /**
     * Generate a new client ID
     * 
     * @return A new client ID
     */
    private String generateClientId() {
        // Format: CLI-YYYY-XXXX where XXXX is a random number
        String year = Integer.toString(LocalDate.now().getYear());
        int randomNum = 1000 + (int)(Math.random() * 9000); // Random 4-digit number
        
        return "CLI-" + year + "-" + randomNum;
    }
    
    /**
     * Validate form data
     * 
     * @return true if form data is valid
     */
    private boolean validateForm() {
        // Check required fields
        if (nameField.getText().trim().isEmpty()) {
            showError("Client name is required.");
            nameField.requestFocus();
            return false;
        }
        
        if (emailField.getText().trim().isEmpty()) {
            showError("Email address is required.");
            emailField.requestFocus();
            return false;
        }
        
        // Check if organization type is selected but no contact person is specified
        if ("Organization".equals(clientTypeCombo.getSelectedItem()) && 
            contactPersonField.getText().trim().isEmpty()) {
            showError("Contact person is required for organization clients.");
            contactPersonField.requestFocus();
            return false;
        }
        
        // Basic email validation
        String email = emailField.getText().trim();
        if (!email.matches("^.+@.+\\..+$")) {
            showError("Please enter a valid email address.");
            emailField.requestFocus();
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
     * Save the client
     */
    private void saveClient() {
        if (!validateForm()) {
            return;
        }
        
        try {
            // Create or update client object
            if (client == null) {
                client = new Client();
            }
            
            client.setClientId(clientIdField.getText().trim());
            client.setName(nameField.getText().trim());
            client.setEmail(emailField.getText().trim());
            client.setPhone(phoneField.getText().trim());
            client.setAddress(addressArea.getText().trim());
            client.setClientType((String) clientTypeCombo.getSelectedItem());
            client.setContactPerson(contactPersonField.getText().trim());
            client.setRegistrationDate(registrationDateChooser.getDate());
            
            boolean success;
            if (client.getId() == 0) {
                // Create new client
                success = clientController.createClient(client);
            } else {
                // Update existing client
                success = clientController.updateClient(client);
            }
            
            if (success) {
                clientSaved = true;
                dispose();
            } else {
                showError("Failed to save client. Please try again.");
            }
            
        } catch (Exception e) {
            showError("Error saving client: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Check if client was saved
     * 
     * @return true if client was saved
     */
    public boolean isClientSaved() {
        return clientSaved;
    }
    
    /**
     * Get the client
     * 
     * @return The client
     */
    public Client getClient() {
        return client;
    }
}