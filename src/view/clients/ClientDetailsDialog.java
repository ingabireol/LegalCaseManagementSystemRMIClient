package view.clients;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import model.Client;
import model.Case;
import controller.ClientController;
import view.util.UIConstants;
import view.components.CustomTable;
import view.cases.CaseDetailsDialog;

/**
 * Dialog for viewing client details and related cases.
 */
public class ClientDetailsDialog extends JDialog {
    private Client client;
    private ClientController clientController;
    
    private JPanel clientInfoPanel;
    private JPanel casesPanel;
    private CustomTable casesTable;
    
    private JButton closeButton;
    private JButton editButton;
    
    /**
     * Constructor
     * 
     * @param parent The parent window
     * @param client The client to display
     */
    public ClientDetailsDialog(Window parent, Client client) {
        super(parent, "Client Details", ModalityType.APPLICATION_MODAL);
        
        this.client = client;
        this.clientController = new ClientController();
        
        initializeUI();
        loadClientData();
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setSize(800, 600);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Create title panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Create tabbed pane for client information and cases
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.NORMAL_FONT);
        
        // Create client info panel
        clientInfoPanel = createClientInfoPanel();
        tabbedPane.addTab("Client Information", clientInfoPanel);
        
        // Create cases panel
        casesPanel = createCasesPanel();
        tabbedPane.addTab("Client Cases", casesPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
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
        JLabel titleLabel = new JLabel("Client Details");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        return titlePanel;
    }
    
    /**
     * Create the client information panel
     * 
     * @return The client info panel
     */
    private JPanel createClientInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create grid for client information
        JPanel infoGrid = new JPanel(new GridBagLayout());
        infoGrid.setBackground(Color.WHITE);
        
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = GridBagConstraints.RELATIVE;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(5, 5, 5, 15);
        
        GridBagConstraints valueConstraints = new GridBagConstraints();
        valueConstraints.gridx = 1;
        valueConstraints.gridy = GridBagConstraints.RELATIVE;
        valueConstraints.anchor = GridBagConstraints.WEST;
        valueConstraints.weightx = 1.0;
        valueConstraints.fill = GridBagConstraints.HORIZONTAL;
        valueConstraints.insets = new Insets(5, 5, 5, 5);
        
        // Add client information fields (will be populated in loadClientData)
        
        // Client ID
        infoGrid.add(createFieldLabel("Client ID:"), labelConstraints);
        JLabel clientIdValue = new JLabel();
        clientIdValue.setName("clientId");
        infoGrid.add(clientIdValue, valueConstraints);
        
        // Name
        infoGrid.add(createFieldLabel("Name:"), labelConstraints);
        JLabel nameValue = new JLabel();
        nameValue.setName("name");
        infoGrid.add(nameValue, valueConstraints);
        
        // Client Type
        infoGrid.add(createFieldLabel("Client Type:"), labelConstraints);
        JLabel clientTypeValue = new JLabel();
        clientTypeValue.setName("clientType");
        infoGrid.add(clientTypeValue, valueConstraints);
        
        // Contact Person
        infoGrid.add(createFieldLabel("Contact Person:"), labelConstraints);
        JLabel contactPersonValue = new JLabel();
        contactPersonValue.setName("contactPerson");
        infoGrid.add(contactPersonValue, valueConstraints);
        
        // Email
        infoGrid.add(createFieldLabel("Email:"), labelConstraints);
        JLabel emailValue = new JLabel();
        emailValue.setName("email");
        infoGrid.add(emailValue, valueConstraints);
        
        // Phone
        infoGrid.add(createFieldLabel("Phone:"), labelConstraints);
        JLabel phoneValue = new JLabel();
        phoneValue.setName("phone");
        infoGrid.add(phoneValue, valueConstraints);
        
        // Address
        infoGrid.add(createFieldLabel("Address:"), labelConstraints);
        JTextArea addressValue = new JTextArea(4, 30);
        addressValue.setName("address");
        addressValue.setEditable(false);
        addressValue.setLineWrap(true);
        addressValue.setWrapStyleWord(true);
        addressValue.setBackground(Color.WHITE);
        addressValue.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JScrollPane addressScroll = new JScrollPane(addressValue);
        infoGrid.add(addressScroll, valueConstraints);
        
        // Registration Date
        infoGrid.add(createFieldLabel("Registration Date:"), labelConstraints);
        JLabel registrationDateValue = new JLabel();
        registrationDateValue.setName("registrationDate");
        infoGrid.add(registrationDateValue, valueConstraints);
        
        // Add the info grid to the panel
        panel.add(infoGrid, BorderLayout.NORTH);
        
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
        label.setForeground(UIConstants.PRIMARY_COLOR);
        return label;
    }
    
    /**
     * Create the cases panel with a table of cases
     * 
     * @return The cases panel
     */
    private JPanel createCasesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create table for cases
        String[] columnNames = {
            "Case #", "Title", "Type", "Status", "File Date", "Court"
        };
        
        casesTable = new CustomTable(columnNames);
        
        // Set column widths
        casesTable.setColumnWidth(0, 100);  // Case #
        casesTable.setColumnWidth(1, 200);  // Title
        casesTable.setColumnWidth(2, 100);  // Type
        casesTable.setColumnWidth(3, 100);  // Status
        casesTable.setColumnWidth(4, 100);  // File Date
        casesTable.setColumnWidth(5, 150);  // Court
        
        // Add double-click listener to open case details
        casesTable.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && casesTable.getSelectedRow() != -1) {
                    openCaseDetails();
                }
            }
        });
        
        // Add actions panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JButton viewCaseButton = new JButton("View Case");
        viewCaseButton.setFont(UIConstants.NORMAL_FONT);
        viewCaseButton.addActionListener(e -> openCaseDetails());
        
        JButton newCaseButton = new JButton("New Case");
        newCaseButton.setFont(UIConstants.NORMAL_FONT);
        newCaseButton.setBackground(UIConstants.SECONDARY_COLOR);
        newCaseButton.setForeground(Color.WHITE);
        newCaseButton.addActionListener(e -> createNewCase());
        
        actionsPanel.add(viewCaseButton);
        actionsPanel.add(Box.createHorizontalStrut(10));
        actionsPanel.add(newCaseButton);
        
        // Add components to panel
        panel.add(actionsPanel, BorderLayout.NORTH);
        panel.add(casesTable, BorderLayout.CENTER);
        
        return panel;
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
        
        editButton = new JButton("Edit Client");
        editButton.setFont(UIConstants.NORMAL_FONT);
        editButton.addActionListener(e -> editClient());
        
        closeButton = new JButton("Close");
        closeButton.setFont(UIConstants.NORMAL_FONT);
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(editButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(closeButton);
        
        return buttonPanel;
    }
    
    /**
     * Load client data into the UI
     */
    private void loadClientData() {
        try {
            // Get client with cases
            Client clientWithCases = clientController.getClientWithCases(client.getId());
            this.client = clientWithCases;
            
            // Update client information fields
            JLabel clientIdValue = (JLabel) findComponentByName(clientInfoPanel, "clientId");
            JLabel nameValue = (JLabel) findComponentByName(clientInfoPanel, "name");
            JLabel clientTypeValue = (JLabel) findComponentByName(clientInfoPanel, "clientType");
            JLabel contactPersonValue = (JLabel) findComponentByName(clientInfoPanel, "contactPerson");
            JLabel emailValue = (JLabel) findComponentByName(clientInfoPanel, "email");
            JLabel phoneValue = (JLabel) findComponentByName(clientInfoPanel, "phone");
            JTextArea addressValue = (JTextArea) findComponentByName(clientInfoPanel, "address");
            JLabel registrationDateValue = (JLabel) findComponentByName(clientInfoPanel, "registrationDate");
            
            clientIdValue.setText(client.getClientId());
            nameValue.setText(client.getName());
            clientTypeValue.setText(client.getClientType());
            contactPersonValue.setText(client.getContactPerson() != null ? client.getContactPerson() : "N/A");
            emailValue.setText(client.getEmail());
            phoneValue.setText(client.getPhone() != null ? client.getPhone() : "N/A");
            addressValue.setText(client.getAddress() != null ? client.getAddress() : "N/A");
            registrationDateValue.setText(client.getRegistrationDate() != null ? 
                                          client.getRegistrationDate().toString() : "N/A");
            
            // Load cases
            casesTable.clearTable();
            List<Case> cases = client.getCases();
            if (cases != null) {
                for (Case cse : cases) {
                    Object[] row = {
                        cse.getCaseNumber(),
                        cse.getTitle(),
                        cse.getCaseType(),
                        cse.getStatus(),
                        cse.getFileDate() != null ? cse.getFileDate().toString() : "N/A",
                        cse.getCourt() != null ? cse.getCourt() : "N/A"
                    };
                    casesTable.addRow(row);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Error loading client data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Find a component by name within a parent container
     * 
     * @param container The container to search in
     * @param name The component name to find
     * @return The found component or null
     */
    private Component findComponentByName(Container container, String name) {
        for (Component component : container.getComponents()) {
            if (name.equals(component.getName())) {
                return component;
            }
            
            if (component instanceof Container) {
                Component found = findComponentByName((Container) component, name);
                if (found != null) {
                    return found;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Open the details dialog for the selected case
     */
    private void openCaseDetails() {
        int selectedRow = casesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a case to view.",
                "No Case Selected",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        // Get the case number from the selected row
        String caseNumber = casesTable.getValueAt(selectedRow, 0).toString();
        
        try {
            // Look up the case in the current client's case list
            Case selectedCase = null;
            for (Case cse : client.getCases()) {
                if (caseNumber.equals(cse.getCaseNumber())) {
                    selectedCase = cse;
                    break;
                }
            }
            
            if (selectedCase != null) {
                // Open case details dialog
                CaseDetailsDialog dialog = new CaseDetailsDialog(getOwner(), selectedCase);
                dialog.setVisible(true);
                
                // Refresh client data after returning from case details
                loadClientData();
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Error opening case details: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Create a new case for this client
     */
    private void createNewCase() {
        // This would open the case editor dialog
        JOptionPane.showMessageDialog(
            this,
            "Create new case for client: " + client.getName() + "\n" +
            "This feature will be implemented in the CaseEditorDialog.",
            "Create New Case",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Edit this client
     */
    private void editClient() {
        try {
            // Open client editor dialog
            ClientEditorDialog dialog = new ClientEditorDialog(getOwner(), client);
            dialog.setVisible(true);
            
            // Refresh data if client was saved
            if (dialog.isClientSaved()) {
                // Get updated client
                client = clientController.getClientById(client.getId());
                loadClientData();
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Error editing client: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
}