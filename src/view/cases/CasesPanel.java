package view.cases;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.List;
import java.time.LocalDate;

import model.Case;
import controller.CaseController;
import view.components.CustomTable;
import view.components.TableFilterPanel;
import view.components.StatusIndicator;
import view.util.UIConstants;
import view.util.SwingUtils;

/**
 * Panel for case management in the Legal Case Management System.
 */
public class CasesPanel extends JPanel {
    private CaseController caseController;
    private CustomTable casesTable;
    private CaseFilterPanel filterPanel;
    
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewDetailsButton;
    
    /**
     * Constructor
     */
    public CasesPanel() {
        this.caseController = new CaseController();
        
        initializeUI();
        loadCases();
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
        
        JLabel titleLabel = new JLabel("Cases Management");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Filter panel
        filterPanel = new CaseFilterPanel();
        headerPanel.add(filterPanel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    /**
     * Create the table panel with cases table
     * 
     * @return The table panel
     */
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Create table
        String[] columnNames = {
            "Case Number", "Title", "Type", "Status", "Client", "Filing Date", "Court"
        };
        casesTable = new CustomTable(columnNames);
        
        // Set column widths
        casesTable.setColumnWidth(0, 120);  // Case Number
        casesTable.setColumnWidth(1, 200);  // Title
        casesTable.setColumnWidth(2, 100);  // Type
        casesTable.setColumnWidth(3, 100);  // Status
        casesTable.setColumnWidth(4, 150);  // Client
        casesTable.setColumnWidth(5, 120);  // Filing Date
        casesTable.setColumnWidth(6, 150);  // Court
        
        // Add custom renderer for Status column
        casesTable.setColumnRenderer(3, (table, value, isSelected, hasFocus, row, column) -> {
            if (value == null) {
                return new JLabel("");
            }
            
            StatusIndicator indicator = new StatusIndicator(value.toString());
            if (isSelected) {
                indicator.setBackground(table.getSelectionBackground());
            } else {
                indicator.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 250));
            }
            return indicator;
        });
        
        // Add double-click listener to open case details
        casesTable.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && casesTable.getSelectedRow() != -1) {
                    viewCaseDetails();
                }
            }
        });
        
        // Enable/disable buttons based on selection
        casesTable.addSelectionListener(e -> updateButtonStates());
        
        tablePanel.add(casesTable, BorderLayout.CENTER);
        
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
        refreshButton.addActionListener(e -> loadCases());
        
        viewDetailsButton = new JButton("View Details");
        viewDetailsButton.setFont(UIConstants.NORMAL_FONT);
        viewDetailsButton.addActionListener(e -> viewCaseDetails());
        
        editButton = new JButton("Edit Case");
        editButton.setFont(UIConstants.NORMAL_FONT);
        editButton.addActionListener(e -> editSelectedCase());
        
        deleteButton = new JButton("Delete Case");
        deleteButton.setFont(UIConstants.NORMAL_FONT);
        deleteButton.addActionListener(e -> deleteSelectedCase());
        
        addButton = new JButton("Add New Case");
        addButton.setFont(UIConstants.NORMAL_FONT);
        addButton.setBackground(UIConstants.SECONDARY_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> addNewCase());
        
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
     * Load cases from the database
     */
    private void loadCases() {
        try {
            // Clear existing data
            casesTable.clearTable();
            casesTable.clearFilters();
            
            // Get cases from controller
            List<Case> cases;
            
            String filterType = filterPanel.getSelectedFilterType();
            String searchText = filterPanel.getSearchText();
            
            if (searchText != null && !searchText.isEmpty()) {
                switch (filterType) {
                    case "Title":
                        cases = caseController.findCasesByText(searchText);
                        break;
                    case "Status":
                        cases = caseController.findCasesByStatus(searchText);
                        break;
                    case "Type":
                        cases = caseController.findCasesByType(searchText);
                        break;
                    case "Client":
                        // This would ideally search by client name, but for now we'll use text search
                        cases = caseController.findCasesByText(searchText);
                        break;
                    default:
                        // Apply filter to the view instead of database for "All"
                        cases = caseController.getAllCases();
                        // Add filters to multiple columns
                        casesTable.addFilter(0, searchText); // Case Number
                        casesTable.addFilter(1, searchText); // Title
                        casesTable.addFilter(2, searchText); // Type
                        casesTable.addFilter(4, searchText); // Client
                        break;
                }
            } else {
                cases = caseController.getAllCases();
            }
            
            // Populate table
            for (Case legalCase : cases) {
                // Get client name (would be populated from client object in a full implementation)
                String clientName = legalCase.getClient() != null ? 
                                   legalCase.getClient().getName() : 
                                   "Client #" + legalCase.getId();
                
                Object[] row = {
                    legalCase.getCaseNumber(),
                    legalCase.getTitle(),
                    legalCase.getCaseType(),
                    legalCase.getStatus(),
                    clientName,
                    legalCase.getFileDate() != null ? legalCase.getFileDate().toString() : "",
                    legalCase.getCourt() != null ? legalCase.getCourt() : ""
                };
                casesTable.addRow(row);
            }
            
            // Display a message if no cases found
            if (cases.isEmpty() && (searchText == null || searchText.isEmpty())) {
                SwingUtils.showInfoMessage(
                    this,
                    "No cases found. Add a new case to get started.",
                    "No Cases"
                );
            }
            
            // Update button states
            updateButtonStates();
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error loading cases: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Update the enabled state of buttons based on table selection
     */
    private void updateButtonStates() {
        boolean hasSelection = casesTable.getSelectedRow() != -1;
        viewDetailsButton.setEnabled(hasSelection);
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
    }
    
    /**
     * View details of the selected case
     */
    private void viewCaseDetails() {
        int selectedRow = casesTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        // Get case number from selected row
        String caseNumber = casesTable.getValueAt(selectedRow, 0).toString();
        
        try {
            // Get the case
            Case legalCase = caseController.getCaseByCaseNumber(caseNumber);
            
            if (legalCase != null) {
                // Load case with all details
                Case caseWithDetails = caseController.getCaseWithDetails(legalCase.getId());
                
                // Open case details dialog
                CaseDetailsDialog dialog = new CaseDetailsDialog(
                    SwingUtilities.getWindowAncestor(this), caseWithDetails);
                dialog.setVisible(true);
                
                // Refresh the cases list after the dialog is closed
                loadCases();
            }
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error viewing case details: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Add a new case
     * This method is public so it can be called from other panels, like MainView
     */
    public void addNewCase() {
        try {
            // Open case editor dialog
            CaseEditorDialog dialog = new CaseEditorDialog(
                SwingUtilities.getWindowAncestor(this), null);
            dialog.setVisible(true);
            
            // Refresh the cases list if a case was added
            if (dialog.isCaseSaved()) {
                loadCases();
            }
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error adding case: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Edit the selected case
     */
    private void editSelectedCase() {
        int selectedRow = casesTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        // Get case number from selected row
        String caseNumber = casesTable.getValueAt(selectedRow, 0).toString();
        
        try {
            // Get the case
            Case legalCase = caseController.getCaseByCaseNumber(caseNumber);
            
            if (legalCase != null) {
                // Load case with all details
                Case caseWithDetails = caseController.getCaseWithDetails(legalCase.getId());
                
                // Open case editor dialog
                CaseEditorDialog dialog = new CaseEditorDialog(
                    SwingUtilities.getWindowAncestor(this), caseWithDetails);
                dialog.setVisible(true);
                
                // Refresh the cases list if the case was updated
                if (dialog.isCaseSaved()) {
                    loadCases();
                }
            }
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error editing case: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Delete the selected case
     */
    private void deleteSelectedCase() {
        int selectedRow = casesTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        // Get case info from selected row
        String caseNumber = casesTable.getValueAt(selectedRow, 0).toString();
        String caseTitle = casesTable.getValueAt(selectedRow, 1).toString();
        
        // Confirm deletion
        boolean confirmed = SwingUtils.showConfirmDialog(
            this,
            "Are you sure you want to delete case '" + caseTitle + "' (" + caseNumber + ")?\n" +
            "This action cannot be undone, and all related data will be lost.",
            "Confirm Deletion"
        );
        
        if (confirmed) {
            try {
                // Get the case
                Case legalCase = caseController.getCaseByCaseNumber(caseNumber);
                
                if (legalCase != null) {
                    // Delete the case
                    boolean success = caseController.deleteCase(legalCase.getId());
                    
                    if (success) {
                        SwingUtils.showInfoMessage(
                            this,
                            "Case deleted successfully.",
                            "Success"
                        );
                        
                        // Refresh the cases list
                        loadCases();
                    } else {
                        SwingUtils.showErrorMessage(
                            this,
                            "Failed to delete case. It may have related records that must be deleted first.",
                            "Deletion Error"
                        );
                    }
                }
                
            } catch (Exception e) {
                SwingUtils.showErrorMessage(
                    this,
                    "Error deleting case: " + e.getMessage(),
                    "Database Error"
                );
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Custom filter panel for cases
     */
    private class CaseFilterPanel extends TableFilterPanel {
        /**
         * Constructor
         */
        public CaseFilterPanel() {
            super(
                new String[]{"All", "Title", "Status", "Type", "Client"},
                searchText -> loadCases(),
                () -> {
                    casesTable.clearFilters();
                    loadCases();
                }
            );
        }
    }
}