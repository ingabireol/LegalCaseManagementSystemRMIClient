package view.clients;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.List;

import model.Client;
import controller.ClientController;
import view.components.CustomTable;
import view.components.TableFilterPanel;
import view.components.StatusIndicator;
import view.util.UIConstants;
import view.util.SwingUtils;

/**
 * Panel for client management in the Legal Case Management System.
 */
public class ClientsPanel extends JPanel {
    private ClientController clientController;
    private CustomTable clientsTable;
    private ClientFilterPanel filterPanel;
    
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewDetailsButton;
    
    /**
     * Constructor
     */
    public ClientsPanel() {
        this.clientController = new ClientController();
        
        initializeUI();
        loadClients();
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create table panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create the header panel with title and filters
     * 
     * @return The header panel
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        
        JLabel titleLabel = new JLabel("Clients Management");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Filter panel
        filterPanel = new ClientFilterPanel();
        headerPanel.add(filterPanel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    /**
     * Create the table panel with clients table
     * 
     * @return The table panel
     */
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Create table
        String[] columnNames = {
            "Client ID", "Name", "Type", "Contact Person", "Email", "Phone", "Registration Date"
        };
        clientsTable = new CustomTable(columnNames);
        
        // Set column widths
        clientsTable.setColumnWidth(0, 100);  // Client ID
        clientsTable.setColumnWidth(1, 200);  // Name
        clientsTable.setColumnWidth(2, 100);  // Type
        clientsTable.setColumnWidth(3, 150);  // Contact Person
        clientsTable.setColumnWidth(4, 180);  // Email
        clientsTable.setColumnWidth(5, 120);  // Phone
        clientsTable.setColumnWidth(6, 120);  // Registration Date
        
        // Add double-click listener to open client details
        clientsTable.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && clientsTable.getSelectedRow() != -1) {
                    viewClientDetails();
                }
            }
        });
        
        // Enable/disable buttons based on selection
        clientsTable.addSelectionListener(e -> updateButtonStates());
        
        tablePanel.add(clientsTable, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    /**
     * Create the button panel with action buttons
     * 
     * @return The button panel
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Create buttons
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(UIConstants.NORMAL_FONT);
        refreshButton.addActionListener(e -> loadClients());
        
        viewDetailsButton = new JButton("View Details");
        viewDetailsButton.setFont(UIConstants.NORMAL_FONT);
        viewDetailsButton.addActionListener(e -> viewClientDetails());
        
        editButton = new JButton("Edit Client");
        editButton.setFont(UIConstants.NORMAL_FONT);
        editButton.addActionListener(e -> editSelectedClient());
        
        deleteButton = new JButton("Delete Client");
        deleteButton.setFont(UIConstants.NORMAL_FONT);
        deleteButton.addActionListener(e -> deleteSelectedClient());
        
        addButton = new JButton("Add New Client");
        addButton.setFont(UIConstants.NORMAL_FONT);
        addButton.setBackground(UIConstants.SECONDARY_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> addNewClient());
        
        // Add buttons to panel
        buttonPanel.add(refreshButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(editButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createHorizontalStrut(30));
        buttonPanel.add(addButton);
        
        // Initialize button states
        viewDetailsButton.setEnabled(false);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        return buttonPanel;
    }
    
    /**
     * Load clients from the database
     */
    private void loadClients() {
        try {
            // Clear existing data
            clientsTable.clearTable();
            clientsTable.clearFilters();
            
            // Get clients from controller
            List<Client> clients;
            
            String filterType = filterPanel.getSelectedFilterType();
            String searchText = filterPanel.getSearchText();
            
            if (searchText != null && !searchText.isEmpty()) {
                if ("Name".equals(filterType)) {
                    clients = clientController.findClientsByName(searchText);
                } else if ("Type".equals(filterType)) {
                    clients = clientController.findClientsByType(searchText);
                } else {
                    // Apply filter to the view instead of database for "All"
                    clients = clientController.getAllClients();
                    clientsTable.addFilter(2, searchText); // Client Type column
                    clientsTable.addFilter(1, searchText); // Name column
                }
            } else {
                clients = clientController.getAllClients();
            }
            
            // Populate table
            for (Client client : clients) {
                Object[] row = {
                    client.getClientId(),
                    client.getName(),
                    client.getClientType(),
                    client.getContactPerson() != null ? client.getContactPerson() : "",
                    client.getEmail(),
                    client.getPhone() != null ? client.getPhone() : "",
                    client.getRegistrationDate() != null ? client.getRegistrationDate().toString() : ""
                };
                clientsTable.addRow(row);
            }
            
            // Display a message if no clients found
            if (clients.isEmpty() && (searchText == null || searchText.isEmpty())) {
                SwingUtils.showInfoMessage(
                    this,
                    "No clients found. Add a new client to get started.",
                    "No Clients"
                );
            }
            
            // Update button states
            updateButtonStates();
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error loading clients: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Update the enabled state of buttons based on table selection
     */
    private void updateButtonStates() {
        boolean hasSelection = clientsTable.getSelectedRow() != -1;
        viewDetailsButton.setEnabled(hasSelection);
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
    }
    
    /**
     * View details of the selected client
     */
    private void viewClientDetails() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        // Get client ID from selected row
        String clientId = clientsTable.getValueAt(selectedRow, 0).toString();
        
        try {
            // Get the client
            Client client = clientController.getClientByClientId(clientId);
            
            if (client != null) {
                // Open client details dialog
                ClientDetailsDialog dialog = new ClientDetailsDialog(
                    SwingUtilities.getWindowAncestor(this), client);
                dialog.setVisible(true);
                
                // Refresh the clients list after the dialog is closed
                loadClients();
            }
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error viewing client details: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Add a new client
     * This method is public so it can be called from other panels, like MainView
     */
    public void addNewClient() {
        try {
            // Open client editor dialog
            ClientEditorDialog dialog = new ClientEditorDialog(
                SwingUtilities.getWindowAncestor(this), null);
            dialog.setVisible(true);
            
            // Refresh the clients list if a client was added
            if (dialog.isClientSaved()) {
                loadClients();
            }
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error adding client: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Edit the selected client
     */
    private void editSelectedClient() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        // Get client ID from selected row
        String clientId = clientsTable.getValueAt(selectedRow, 0).toString();
        
        try {
            // Get the client
            Client client = clientController.getClientByClientId(clientId);
            
            if (client != null) {
                // Open client editor dialog
                ClientEditorDialog dialog = new ClientEditorDialog(
                    SwingUtilities.getWindowAncestor(this), client);
                dialog.setVisible(true);
                
                // Refresh the clients list if the client was updated
                if (dialog.isClientSaved()) {
                    loadClients();
                }
            }
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error editing client: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Delete the selected client
     */
    private void deleteSelectedClient() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        // Get client info from selected row
        String clientId = clientsTable.getValueAt(selectedRow, 0).toString();
        String clientName = clientsTable.getValueAt(selectedRow, 1).toString();
        
        // Confirm deletion
        boolean confirmed = SwingUtils.showConfirmDialog(
            this,
            "Are you sure you want to delete client '" + clientName + "' (" + clientId + ")?\n" +
            "This action cannot be undone, and all related data will be lost.",
            "Confirm Deletion"
        );
        
        if (confirmed) {
            try {
                // Delete the client
                boolean success = clientController.deleteClient(clientId);
                
                if (success) {
                    SwingUtils.showInfoMessage(
                        this,
                        "Client deleted successfully.",
                        "Success"
                    );
                    
                    // Refresh the clients list
                    loadClients();
                } else {
                    SwingUtils.showErrorMessage(
                        this,
                        "Failed to delete client. It may have cases or other related records.",
                        "Deletion Error"
                    );
                }
                
            } catch (Exception e) {
                SwingUtils.showErrorMessage(
                    this,
                    "Error deleting client: " + e.getMessage(),
                    "Database Error"
                );
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Custom filter panel for clients
     */
    private class ClientFilterPanel extends TableFilterPanel {
        /**
         * Constructor
         */
        public ClientFilterPanel() {
            super(
                new String[]{"All", "Name", "Type"},
                    
                searchText -> loadClients(),
                () -> {
                    clientsTable.clearFilters();
                    loadClients();
                }
            );
        }
        
        /**
         * Apply filters based on filter type
         */
        private void applyFilters() {
            loadClients();
        }
    }
}