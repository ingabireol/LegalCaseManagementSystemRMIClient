package view.attorneys;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.List;

import model.Attorney;
import controller.AttorneyController;
import view.components.CustomTable;
import view.components.TableFilterPanel;
import view.components.StatusIndicator;
import view.util.UIConstants;
import view.util.SwingUtils;

/**
 * Panel for attorney management in the Legal Case Management System.
 */
public class AttorneysPanel extends JPanel {
    private AttorneyController attorneyController;
    private CustomTable attorneysTable;
    private AttorneyFilterPanel filterPanel;
    
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewDetailsButton;
    
    /**
     * Constructor
     */
    public AttorneysPanel() {
        this.attorneyController = new AttorneyController();
        
        initializeUI();
        loadAttorneys();
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
        
        JLabel titleLabel = new JLabel("Attorneys Management");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Filter panel
        filterPanel = new AttorneyFilterPanel();
        headerPanel.add(filterPanel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    /**
     * Create the table panel with attorneys table
     * 
     * @return The table panel
     */
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Create table
        String[] columnNames = {
            "Attorney ID", "Name", "Specialization", "Bar Number", "Email", "Phone", "Hourly Rate"
        };
        attorneysTable = new CustomTable(columnNames);
        
        // Set column widths
        attorneysTable.setColumnWidth(0, 100);  // Attorney ID
        attorneysTable.setColumnWidth(1, 200);  // Name
        attorneysTable.setColumnWidth(2, 150);  // Specialization
        attorneysTable.setColumnWidth(3, 120);  // Bar Number
        attorneysTable.setColumnWidth(4, 180);  // Email
        attorneysTable.setColumnWidth(5, 120);  // Phone
        attorneysTable.setColumnWidth(6, 100);  // Hourly Rate
        
        // Add double-click listener to open attorney details
        attorneysTable.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && attorneysTable.getSelectedRow() != -1) {
                    viewAttorneyDetails();
                }
            }
        });
        
        // Enable/disable buttons based on selection
        attorneysTable.addSelectionListener(e -> updateButtonStates());
        
        tablePanel.add(attorneysTable, BorderLayout.CENTER);
        
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
        refreshButton.addActionListener(e -> loadAttorneys());
        
        viewDetailsButton = new JButton("View Details");
        viewDetailsButton.setFont(UIConstants.NORMAL_FONT);
        viewDetailsButton.addActionListener(e -> viewAttorneyDetails());
        
        editButton = new JButton("Edit Attorney");
        editButton.setFont(UIConstants.NORMAL_FONT);
        editButton.addActionListener(e -> editSelectedAttorney());
        
        deleteButton = new JButton("Delete Attorney");
        deleteButton.setFont(UIConstants.NORMAL_FONT);
        deleteButton.addActionListener(e -> deleteSelectedAttorney());
        
        addButton = new JButton("Add New Attorney");
        addButton.setFont(UIConstants.NORMAL_FONT);
        addButton.setBackground(UIConstants.SECONDARY_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> addNewAttorney());
        
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
     * Load attorneys from the database
     */
    private void loadAttorneys() {
        try {
            // Clear existing data
            attorneysTable.clearTable();
            attorneysTable.clearFilters();
            
            // Get attorneys from controller
            List<Attorney> attorneys;
            
            String filterType = filterPanel.getSelectedFilterType();
            String searchText = filterPanel.getSearchText();
            
            if (searchText != null && !searchText.isEmpty()) {
                if ("Name".equals(filterType)) {
                    attorneys = attorneyController.findAttorneysByName(searchText);
                } else if ("Specialization".equals(filterType)) {
                    attorneys = attorneyController.findAttorneysBySpecialization(searchText);
                } else {
                    // Apply filter to the view instead of database for "All"
                    attorneys = attorneyController.getAllAttorneys();
                    attorneysTable.addFilter(1, searchText); // Name column
                    attorneysTable.addFilter(2, searchText); // Specialization column
                }
            } else {
                attorneys = attorneyController.getAllAttorneys();
            }
            
            // Populate table
            for (Attorney attorney : attorneys) {
                Object[] row = {
                    attorney.getAttorneyId(),
                    attorney.getFullName(),
                    attorney.getSpecialization() != null ? attorney.getSpecialization() : "",
                    attorney.getBarNumber() != null ? attorney.getBarNumber() : "",
                    attorney.getEmail(),
                    attorney.getPhone() != null ? attorney.getPhone() : "",
                    String.format("$%.2f", attorney.getHourlyRate())
                };
                attorneysTable.addRow(row);
            }
            
            // Display a message if no attorneys found
            if (attorneys.isEmpty() && (searchText == null || searchText.isEmpty())) {
                SwingUtils.showInfoMessage(
                    this,
                    "No attorneys found. Add a new attorney to get started.",
                    "No Attorneys"
                );
            }
            
            // Update button states
            updateButtonStates();
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error loading attorneys: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Update the enabled state of buttons based on table selection
     */
    private void updateButtonStates() {
        boolean hasSelection = attorneysTable.getSelectedRow() != -1;
        viewDetailsButton.setEnabled(hasSelection);
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
    }
    
    /**
     * View details of the selected attorney
     */
    private void viewAttorneyDetails() {
        int selectedRow = attorneysTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        // Get attorney ID from selected row
        String attorneyId = attorneysTable.getValueAt(selectedRow, 0).toString();
        
        try {
            // Get the attorney
            Attorney attorney = attorneyController.getAttorneyByAttorneyId(attorneyId);
            
            if (attorney != null) {
                // Open attorney details dialog
                AttorneyDetailsDialog dialog = new AttorneyDetailsDialog(
                    SwingUtilities.getWindowAncestor(this), attorney);
                dialog.setVisible(true);
                
                // Refresh the attorneys list after the dialog is closed
                loadAttorneys();
            }
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error viewing attorney details: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Add a new attorney
     * This method is public so it can be called from other panels, like MainView
     */
    public void addNewAttorney() {
        try {
            // Open attorney editor dialog
            AttorneyEditorDialog dialog = new AttorneyEditorDialog(
                SwingUtilities.getWindowAncestor(this), null);
            dialog.setVisible(true);
            
            // Refresh the attorneys list if an attorney was added
            if (dialog.isAttorneySaved()) {
                loadAttorneys();
            }
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error adding attorney: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Edit the selected attorney
     */
    private void editSelectedAttorney() {
        int selectedRow = attorneysTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        // Get attorney ID from selected row
        String attorneyId = attorneysTable.getValueAt(selectedRow, 0).toString();
        
        try {
            // Get the attorney
            Attorney attorney = attorneyController.getAttorneyByAttorneyId(attorneyId);
            
            if (attorney != null) {
                // Open attorney editor dialog
                AttorneyEditorDialog dialog = new AttorneyEditorDialog(
                    SwingUtilities.getWindowAncestor(this), attorney);
                dialog.setVisible(true);
                
                // Refresh the attorneys list if the attorney was updated
                if (dialog.isAttorneySaved()) {
                    loadAttorneys();
                }
            }
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error editing attorney: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Delete the selected attorney
     */
    private void deleteSelectedAttorney() {
        int selectedRow = attorneysTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        // Get attorney info from selected row
        String attorneyId = attorneysTable.getValueAt(selectedRow, 0).toString();
        String attorneyName = attorneysTable.getValueAt(selectedRow, 1).toString();
        
        // Confirm deletion
        boolean confirmed = SwingUtils.showConfirmDialog(
            this,
            "Are you sure you want to delete attorney '" + attorneyName + "' (" + attorneyId + ")?\n" +
            "This action cannot be undone, and all related data will be lost.",
            "Confirm Deletion"
        );
        
        if (confirmed) {
            try {
                // Delete the attorney
                boolean success = attorneyController.deleteAttorney(attorneyId);
                
                if (success) {
                    SwingUtils.showInfoMessage(
                        this,
                        "Attorney deleted successfully.",
                        "Success"
                    );
                    
                    // Refresh the attorneys list
                    loadAttorneys();
                } else {
                    SwingUtils.showErrorMessage(
                        this,
                        "Failed to delete attorney. They may have cases or other related records.",
                        "Deletion Error"
                    );
                }
                
            } catch (Exception e) {
                SwingUtils.showErrorMessage(
                    this,
                    "Error deleting attorney: " + e.getMessage(),
                    "Database Error"
                );
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Custom filter panel for attorneys
     */
    private class AttorneyFilterPanel extends TableFilterPanel {
        /**
         * Constructor
         */
        public AttorneyFilterPanel() {
            super(
                new String[]{"All", "Name", "Specialization"},
                    
                searchText -> loadAttorneys(),
                () -> {
                    attorneysTable.clearFilters();
                    loadAttorneys();
                }
            );
        }
        
        /**
         * Apply filters based on filter type
         */
        private void applyFilters() {
            loadAttorneys();
        }
    }
}