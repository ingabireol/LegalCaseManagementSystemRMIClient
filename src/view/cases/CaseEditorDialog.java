package view.cases;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.Case;
import model.Client;
import model.Attorney;
import controller.CaseController;
import controller.ClientController;
import controller.AttorneyController;
import view.util.UIConstants;
import view.components.DateChooser;
import view.util.SwingUtils;

/**
 * Dialog for adding or editing a case.
 */
public class CaseEditorDialog extends JDialog {
    private JTextField caseNumberField;
    private JTextField titleField;
    private JComboBox<String> caseTypeCombo;
    private JComboBox<String> statusCombo;
    private JTextArea descriptionArea;
    private DateChooser fileDateChooser;
    private DateChooser closingDateChooser;
    private JTextField courtField;
    private JTextField judgeField;
    private JTextField opposingPartyField;
    private JTextField opposingCounselField;
    private JComboBox<Client> clientCombo;
    private JList<Attorney> attorneyList;
    private DefaultListModel<Attorney> attorneyListModel;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    private Case legalCase;
    private CaseController caseController;
    private ClientController clientController;
    private AttorneyController attorneyController;
    private boolean caseSaved = false;
    
    /**
     * Constructor for creating a new case or editing an existing one
     * 
     * @param parent The parent window
     * @param legalCase The case to edit, or null for a new case
     */
    public CaseEditorDialog(Window parent, Case legalCase) {
        super(parent, legalCase == null ? "Add New Case" : "Edit Case", ModalityType.APPLICATION_MODAL);
        
        this.legalCase = legalCase;
        this.caseController = new CaseController();
        this.clientController = new ClientController();
        this.attorneyController = new AttorneyController();
        
        initializeUI();
        loadCaseData();
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setSize(700, 700);
        setMinimumSize(new Dimension(600, 600));
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
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        formPanel.setBackground(Color.WHITE);
        
        // Create tabbed pane for organizing form sections
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.NORMAL_FONT);
        
        // Case Details panel
        JPanel detailsPanel = createCaseDetailsPanel();
        tabbedPane.addTab("Case Details", detailsPanel);
        
        // Parties panel
        JPanel partiesPanel = createPartiesPanel();
        tabbedPane.addTab("Parties", partiesPanel);
        
        formPanel.add(tabbedPane);
        
        return formPanel;
    }
    
    /**
     * Create the panel for case details
     * 
     * @return The case details panel
     */
    private JPanel createCaseDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = GridBagConstraints.RELATIVE;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(5, 5, 5, 15);
        
        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = GridBagConstraints.RELATIVE;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1.0;
        fieldConstraints.insets = new Insets(5, 0, 5, 5);
        
        // Case Number
        panel.add(createFieldLabel("Case Number:*"), labelConstraints);
        
        caseNumberField = new JTextField(20);
        caseNumberField.setFont(UIConstants.NORMAL_FONT);
        panel.add(caseNumberField, fieldConstraints);
        
        // Title
        panel.add(createFieldLabel("Title:*"), labelConstraints);
        
        titleField = new JTextField(20);
        titleField.setFont(UIConstants.NORMAL_FONT);
        panel.add(titleField, fieldConstraints);
        
        // Case Type
        panel.add(createFieldLabel("Case Type:*"), labelConstraints);
        
        caseTypeCombo = new JComboBox<>(new String[]{
            "Civil", "Criminal", "Family", "Estate", "Corporate", "Intellectual Property", 
            "Real Estate", "Labor", "Administrative", "Other"
        });
        caseTypeCombo.setFont(UIConstants.NORMAL_FONT);
        panel.add(caseTypeCombo, fieldConstraints);
        
        // Status
        panel.add(createFieldLabel("Status:*"), labelConstraints);
        
        statusCombo = new JComboBox<>(new String[]{
            "Open", "Pending", "In Progress", "On Hold", "Closed", "Archived"
        });
        statusCombo.setFont(UIConstants.NORMAL_FONT);
        panel.add(statusCombo, fieldConstraints);
        
        // File Date
        panel.add(createFieldLabel("File Date:"), labelConstraints);
        
        fileDateChooser = new DateChooser();
        panel.add(fileDateChooser, fieldConstraints);
        
        // Closing Date
        panel.add(createFieldLabel("Closing Date:"), labelConstraints);
        
        closingDateChooser = new DateChooser();
        panel.add(closingDateChooser, fieldConstraints);
        
        // Court
        panel.add(createFieldLabel("Court:"), labelConstraints);
        
        courtField = new JTextField(20);
        courtField.setFont(UIConstants.NORMAL_FONT);
        panel.add(courtField, fieldConstraints);
        
        // Judge
        panel.add(createFieldLabel("Judge:"), labelConstraints);
        
        judgeField = new JTextField(20);
        judgeField.setFont(UIConstants.NORMAL_FONT);
        panel.add(judgeField, fieldConstraints);
        
        // Description
        panel.add(createFieldLabel("Description:"), labelConstraints);
        
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setFont(UIConstants.NORMAL_FONT);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionScrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.add(descriptionScrollPane, fieldConstraints);
        
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
        
        panel.add(requiredNote, noteConstraints);
        
        return panel;
    }
    
    /**
     * Create the panel for parties information
     * 
     * @return The parties panel
     */
    private JPanel createPartiesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = GridBagConstraints.RELATIVE;
        labelConstraints.anchor = GridBagConstraints.NORTHWEST;
        labelConstraints.insets = new Insets(5, 5, 5, 15);
        
        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = GridBagConstraints.RELATIVE;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1.0;
        fieldConstraints.insets = new Insets(5, 0, 5, 5);
        
        // Client
        panel.add(createFieldLabel("Client:*"), labelConstraints);
        
        clientCombo = new JComboBox<>();
        clientCombo.setFont(UIConstants.NORMAL_FONT);
        
        // Load clients from database
        loadClients();
        
        panel.add(clientCombo, fieldConstraints);
        
        // Attorneys
        panel.add(createFieldLabel("Attorneys:"), labelConstraints);
        
        attorneyListModel = new DefaultListModel<>();
        attorneyList = new JList<>(attorneyListModel);
        attorneyList.setFont(UIConstants.NORMAL_FONT);
        attorneyList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        // Load attorneys from database
        loadAttorneys();
        
        JScrollPane attorneyScrollPane = new JScrollPane(attorneyList);
        attorneyScrollPane.setPreferredSize(new Dimension(300, 150));
        
        fieldConstraints.fill = GridBagConstraints.BOTH;
        fieldConstraints.weighty = 1.0;
        panel.add(attorneyScrollPane, fieldConstraints);
        
        // Opposing Party
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weighty = 0.0;
        panel.add(createFieldLabel("Opposing Party:"), labelConstraints);
        
        opposingPartyField = new JTextField(20);
        opposingPartyField.setFont(UIConstants.NORMAL_FONT);
        panel.add(opposingPartyField, fieldConstraints);
        
        // Opposing Counsel
        panel.add(createFieldLabel("Opposing Counsel:"), labelConstraints);
        
        opposingCounselField = new JTextField(20);
        opposingCounselField.setFont(UIConstants.NORMAL_FONT);
        panel.add(opposingCounselField, fieldConstraints);
        
        return panel;
    }
    
    /**
     * Create a field label with consistent styling
     * 
     * @param text The label text
     * @return The styled label
     */
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.LABEL_FONT);
        return label;
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
        
        saveButton = new JButton("Save Case");
        saveButton.setFont(UIConstants.NORMAL_FONT);
        saveButton.setBackground(UIConstants.SECONDARY_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveCase());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(saveButton);
        
        return buttonPanel;
    }
    
    /**
     * Load clients from the database into the client combo box
     */
    private void loadClients() {
        try {
            // Get all clients
            List<Client> clients = clientController.getAllClients();
            
            // Add to combo box
            for (Client client : clients) {
                clientCombo.addItem(client);
            }
            
            // Set renderer to display client names
            clientCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, 
                        int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    
                    if (value instanceof Client) {
                        Client client = (Client) value;
                        setText(client.getDisplayName());
                    }
                    
                    return this;
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            SwingUtils.showErrorMessage(
                this,
                "Error loading clients: " + e.getMessage(),
                "Database Error"
            );
        }
    }
    
    /**
     * Load attorneys from the database into the attorney list
     */
    private void loadAttorneys() {
        try {
            // Get all attorneys
            List<Attorney> attorneys = attorneyController.getAllAttorneys();
            
            // Add to list model
            for (Attorney attorney : attorneys) {
                attorneyListModel.addElement(attorney);
            }
            
            // Set renderer to display attorney names
            attorneyList.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, 
                        int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    
                    if (value instanceof Attorney) {
                        Attorney attorney = (Attorney) value;
                        setText(attorney.getDisplayName());
                    }
                    
                    return this;
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            SwingUtils.showErrorMessage(
                this,
                "Error loading attorneys: " + e.getMessage(),
                "Database Error"
            );
        }
    }
    
    /**
     * Load case data into form fields if editing an existing case
     */
    private void loadCaseData() {
        if (legalCase != null) {
            // Populate form fields with case data
            caseNumberField.setText(legalCase.getCaseNumber());
            titleField.setText(legalCase.getTitle());
            
            if (legalCase.getCaseType() != null) {
                caseTypeCombo.setSelectedItem(legalCase.getCaseType());
            }
            
            if (legalCase.getStatus() != null) {
                statusCombo.setSelectedItem(legalCase.getStatus());
            }
            
            if (legalCase.getFileDate() != null) {
                fileDateChooser.setDate(legalCase.getFileDate());
            }
            
            if (legalCase.getClosingDate() != null) {
                closingDateChooser.setDate(legalCase.getClosingDate());
            }
            
            if (legalCase.getCourt() != null) {
                courtField.setText(legalCase.getCourt());
            }
            
            if (legalCase.getJudge() != null) {
                judgeField.setText(legalCase.getJudge());
            }
            
            if (legalCase.getDescription() != null) {
                descriptionArea.setText(legalCase.getDescription());
            }
            
            if (legalCase.getOpposingParty() != null) {
                opposingPartyField.setText(legalCase.getOpposingParty());
            }
            
            if (legalCase.getOpposingCounsel() != null) {
                opposingCounselField.setText(legalCase.getOpposingCounsel());
            }
            
            // Select client
            if (legalCase.getClient() != null) {
                selectClientInComboBox(legalCase.getClient());
            } else if (legalCase.getId()> 0) {
                // Try to load client by ID
                try {
                    Client client = clientController.getClientById(legalCase.getId());
                    if (client != null) {
                        selectClientInComboBox(client);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            // Select attorneys
            if (legalCase.getAttorneys() != null && !legalCase.getAttorneys().isEmpty()) {
                selectAttorneysInList(legalCase.getAttorneys());
            }
            
            // Disable case number field when editing
            caseNumberField.setEditable(false);
        } else {
            // Generate a new case number for new cases
            caseNumberField.setText(generateCaseNumber());
        }
    }
    
    /**
     * Select a client in the combo box
     * 
     * @param client The client to select
     */
    private void selectClientInComboBox(Client client) {
        for (int i = 0; i < clientCombo.getItemCount(); i++) {
            Client item = clientCombo.getItemAt(i);
            if (item.getId() == client.getId()) {
                clientCombo.setSelectedIndex(i);
                break;
            }
        }
    }
    
    /**
     * Select attorneys in the attorney list
     * 
     * @param attorneys The attorneys to select
     */
    private void selectAttorneysInList(List<Attorney> attorneys) {
        List<Integer> selectedIndices = new ArrayList<>();
        
        for (int i = 0; i < attorneyListModel.getSize(); i++) {
            Attorney listAttorney = attorneyListModel.getElementAt(i);
            
            for (Attorney caseAttorney : attorneys) {
                if (listAttorney.getId() == caseAttorney.getId()) {
                    selectedIndices.add(i);
                    break;
                }
            }
        }
        
        // Convert to int array for selection
        int[] indices = new int[selectedIndices.size()];
        for (int i = 0; i < selectedIndices.size(); i++) {
            indices[i] = selectedIndices.get(i);
        }
        
        attorneyList.setSelectedIndices(indices);
    }
    
    /**
     * Generate a new case number
     * 
     * @return A new case number
     */
    private String generateCaseNumber() {
        // Format: CASE-YYYY-XXXX where XXXX is a random number
        String year = Integer.toString(LocalDate.now().getYear());
        int randomNum = 1000 + (int)(Math.random() * 9000); // Random 4-digit number
        
        return "CASE-" + year + "-" + randomNum;
    }
    
    /**
     * Validate form data
     * 
     * @return true if form data is valid
     */
    private boolean validateForm() {
        // Check required fields
        if (caseNumberField.getText().trim().isEmpty()) {
            showError("Case number is required.");
            caseNumberField.requestFocus();
            return false;
        }
        
        if (titleField.getText().trim().isEmpty()) {
            showError("Case title is required.");
            titleField.requestFocus();
            return false;
        }
        
        if (clientCombo.getSelectedItem() == null) {
            showError("Client selection is required.");
            clientCombo.requestFocus();
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
     * Save the case
     */
    private void saveCase() {
        if (!validateForm()) {
            return;
        }
        
        try {
            // Create or update case object
            if (legalCase == null) {
                legalCase = new Case();
            }
            
            legalCase.setCaseNumber(caseNumberField.getText().trim());
            legalCase.setTitle(titleField.getText().trim());
            legalCase.setCaseType((String) caseTypeCombo.getSelectedItem());
            legalCase.setStatus((String) statusCombo.getSelectedItem());
            legalCase.setDescription(descriptionArea.getText().trim());
            legalCase.setFileDate(fileDateChooser.getDate());
            legalCase.setClosingDate(closingDateChooser.getDate());
            legalCase.setCourt(courtField.getText().trim());
            legalCase.setJudge(judgeField.getText().trim());
            legalCase.setOpposingParty(opposingPartyField.getText().trim());
            legalCase.setOpposingCounsel(opposingCounselField.getText().trim());
            
            // Set client
            Client selectedClient = (Client) clientCombo.getSelectedItem();
            legalCase.setId(selectedClient.getId());
            legalCase.setClient(selectedClient);
            
            // Set attorneys
            List<Attorney> selectedAttorneys = attorneyList.getSelectedValuesList();
            legalCase.setAttorneys(selectedAttorneys);
            
            boolean success;
            if (legalCase.getId() == 0) {
                // Create new case
                success = caseController.createCase(legalCase);
            } else {
                // Update existing case
                success = caseController.updateCase(legalCase);
            }
            
            if (success) {
                caseSaved = true;
                dispose();
            } else {
                showError("Failed to save case. Please try again.");
            }
            
        } catch (Exception e) {
            showError("Error saving case: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Check if case was saved
     * 
     * @return true if case was saved
     */
    public boolean isCaseSaved() {
        return caseSaved;
    }
    
    /**
     * Get the case
     * 
     * @return The case
     */
    public Case getCase() {
        return legalCase;
    }
}