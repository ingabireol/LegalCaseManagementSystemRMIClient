package view.invoices;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import model.Invoice;
import controller.InvoiceController;
import java.util.Arrays;
import view.util.UIConstants;
import view.components.CustomTable;
import view.components.TableFilterPanel;
import view.components.StatusIndicator;
import view.util.SwingUtils;

/**
 * Panel for invoice and payment management in the Legal Case Management System.
 */
public class InvoicesPanel extends JPanel {
    private InvoiceController invoiceController;
    private CustomTable invoicesTable;
    private InvoiceFilterPanel filterPanel;
    
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewDetailsButton;
    private JButton recordPaymentButton;
    
    /**
     * Constructor
     */
    public InvoicesPanel() {
        this.invoiceController = new InvoiceController();
        
        initializeUI();
        loadInvoices();
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
        
        JLabel titleLabel = new JLabel("Invoices & Payments Management");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Filter panel
        filterPanel = new InvoiceFilterPanel();
        headerPanel.add(filterPanel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    /**
     * Create the table panel with invoices table
     * 
     * @return The table panel
     */
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Create table
        String[] columnNames = {
            "Invoice #", "Client", "Case #", "Issue Date", "Due Date", "Amount", "Paid", "Balance", "Status"
        };
        invoicesTable = new CustomTable(columnNames);
        
        // Set column widths
        invoicesTable.setColumnWidth(0, 120);  // Invoice #
        invoicesTable.setColumnWidth(1, 180);  // Client
        invoicesTable.setColumnWidth(2, 120);  // Case #
        invoicesTable.setColumnWidth(3, 100);  // Issue Date
        invoicesTable.setColumnWidth(4, 100);  // Due Date
        invoicesTable.setColumnWidth(5, 100);  // Amount
        invoicesTable.setColumnWidth(6, 100);  // Paid
        invoicesTable.setColumnWidth(7, 100);  // Balance
        invoicesTable.setColumnWidth(8, 100);  // Status
        
        // Set custom renderer for Status column
        invoicesTable.setColumnRenderer(8, (table, value, isSelected, hasFocus, row, column) -> {
            if (value == null) return new JLabel();
            
            StatusIndicator statusIndicator = new StatusIndicator(value.toString());
            if (isSelected) {
                statusIndicator.setForeground(Color.WHITE);
            }
            return statusIndicator;
        });
        
        // Add double-click listener to open invoice details
        invoicesTable.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && invoicesTable.getSelectedRow() != -1) {
                    viewInvoiceDetails();
                }
            }
        });
        
        // Enable/disable buttons based on selection
        invoicesTable.addSelectionListener(e -> updateButtonStates());
        
        tablePanel.add(invoicesTable, BorderLayout.CENTER);
        
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
        refreshButton.addActionListener(e -> loadInvoices());
        
        viewDetailsButton = new JButton("View Details");
        viewDetailsButton.setFont(UIConstants.NORMAL_FONT);
        viewDetailsButton.addActionListener(e -> viewInvoiceDetails());
        
        recordPaymentButton = new JButton("Record Payment");
        recordPaymentButton.setFont(UIConstants.NORMAL_FONT);
        recordPaymentButton.addActionListener(e -> recordPayment());
        
        editButton = new JButton("Edit Invoice");
        editButton.setFont(UIConstants.NORMAL_FONT);
        editButton.addActionListener(e -> editSelectedInvoice());
        
        deleteButton = new JButton("Delete Invoice");
        deleteButton.setFont(UIConstants.NORMAL_FONT);
        deleteButton.addActionListener(e -> deleteSelectedInvoice());
        
        addButton = new JButton("Create Invoice");
        addButton.setFont(UIConstants.NORMAL_FONT);
        addButton.setBackground(UIConstants.SECONDARY_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> createNewInvoice());
        
        // Add buttons to panel
        buttonPanel.add(refreshButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(recordPaymentButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(editButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createHorizontalStrut(30));
        buttonPanel.add(addButton);
        
        // Initialize button states
        viewDetailsButton.setEnabled(false);
        recordPaymentButton.setEnabled(false);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        return buttonPanel;
    }
    
    /**
     * Load invoices from the database
     */
    private void loadInvoices() {
        try {
            // Clear existing data
            invoicesTable.clearTable();
            invoicesTable.clearFilters();
            
            // Get invoices from controller
            List<Invoice> invoices;
            
            String filterType = filterPanel.getSelectedFilterType();
            String searchText = filterPanel.getSearchText();
            String statusFilter = filterPanel.getSelectedStatus();
            
            if (statusFilter != null && !statusFilter.equals("All")) {
                // Filter by status
                invoices = invoiceController.findInvoicesByStatus(statusFilter);
            } else if (searchText != null && !searchText.isEmpty()) {
                // Apply specific text filter based on filter type
                if ("Invoice #".equals(filterType)) {
                    Invoice invoice = invoiceController.getInvoiceByInvoiceNumber(searchText);
                    invoices = invoice != null ? Arrays.asList(invoice) : Arrays.asList();
                } else if ("Client".equals(filterType)) {
                    // This is simplified - in a real implementation, you'd lookup the client ID
                    // and then find invoices by client
                    invoices = invoiceController.getAllInvoices();
                    invoicesTable.addFilter(1, searchText); // Filter client column
                } else if ("Case #".equals(filterType)) {
                    // This is simplified - in a real implementation, you'd lookup the case ID
                    // and then find invoices by case
                    invoices = invoiceController.getAllInvoices();
                    invoicesTable.addFilter(2, searchText); // Filter case column
                } else {
                    // For "All" filter type, get all invoices and filter in the view
                    invoices = invoiceController.getAllInvoices();
                    // Add filters for multiple columns
                    invoicesTable.addFilter(0, searchText); // Invoice number
                    invoicesTable.addFilter(1, searchText); // Client
                    invoicesTable.addFilter(2, searchText); // Case
                }
            } else {
                // No specific filters, get all invoices
                invoices = invoiceController.getAllInvoices();
            }
            
            // Process date range filter if set
            LocalDate startDate = filterPanel.getStartDate();
            LocalDate endDate = filterPanel.getEndDate();
            if (startDate != null && endDate != null) {
                invoices = invoiceController.findInvoicesByDateRange(startDate, endDate);
            }
            
            // Populate table
            for (Invoice invoice : invoices) {
                // Get the client and case information
                String clientName = invoice.getClient() != null ? 
                    invoice.getClient().getName() : "Client " + invoice.getId();
                    
                String caseNumber = invoice.getCase() != null ? 
                    invoice.getCase().getCaseNumber() : "Case " + invoice.getCase().getId();
                
                // Calculate balance
                BigDecimal balance = invoice.getAmount().subtract(invoice.getAmountPaid());
                
                Object[] row = {
                    invoice.getInvoiceNumber(),
                    clientName,
                    caseNumber,
                    invoice.getIssueDate().toString(),
                    invoice.getDueDate().toString(),
                    SwingUtils.formatMoney(invoice.getAmount().doubleValue()),
                    SwingUtils.formatMoney(invoice.getAmountPaid().doubleValue()),
                    SwingUtils.formatMoney(balance.doubleValue()),
                    invoice.getStatus()
                };
                invoicesTable.addRow(row);
            }
            
            // Display a message if no invoices found
            if (invoices.isEmpty() && (searchText == null || searchText.isEmpty()) && 
                (statusFilter == null || statusFilter.equals("All"))) {
                SwingUtils.showInfoMessage(
                    this,
                    "No invoices found. Create a new invoice to get started.",
                    "No Invoices"
                );
            }
            
            // Update button states
            updateButtonStates();
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error loading invoices: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Update the enabled state of buttons based on table selection
     */
    private void updateButtonStates() {
        boolean hasSelection = invoicesTable.getSelectedRow() != -1;
        viewDetailsButton.setEnabled(hasSelection);
        recordPaymentButton.setEnabled(hasSelection);
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
    }
    
    /**
     * View details of the selected invoice
     */
    private void viewInvoiceDetails() {
        int selectedRow = invoicesTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        // Get invoice number from selected row
        String invoiceNumber = invoicesTable.getValueAt(selectedRow, 0).toString();
        
        try {
            // Get the invoice
            Invoice invoice = invoiceController.getInvoiceByInvoiceNumber(invoiceNumber);
            
            if (invoice != null) {
                // Open invoice details dialog
                InvoiceDetailsDialog dialog = new InvoiceDetailsDialog(
                    SwingUtilities.getWindowAncestor(this), 
                    invoice
                );
                dialog.setVisible(true);
                
                // Refresh the invoices list after the dialog is closed
                loadInvoices();
            }
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error viewing invoice details: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Record a payment for the selected invoice
     */
    private void recordPayment() {
        int selectedRow = invoicesTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        // Get invoice number from selected row
        String invoiceNumber = invoicesTable.getValueAt(selectedRow, 0).toString();
        
        try {
            // Get the invoice
            Invoice invoice = invoiceController.getInvoiceByInvoiceNumber(invoiceNumber);
            
            if (invoice != null) {
                // Open payment dialog
                PaymentDialog dialog = new PaymentDialog(
                    SwingUtilities.getWindowAncestor(this), 
                    invoice
                );
                dialog.setVisible(true);
                
                // Refresh the invoices list after the dialog is closed
                if (dialog.isPaymentRecorded()) {
                    loadInvoices();
                }
            }
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error recording payment: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Create a new invoice
     */
    public void createNewInvoice() {
        try {
            // Open invoice editor dialog
            InvoiceEditorDialog dialog = new InvoiceEditorDialog(
                SwingUtilities.getWindowAncestor(this)
            );
            dialog.setVisible(true);
            
            // Refresh the invoices list if an invoice was created
            if (dialog.isInvoiceSaved()) {
                loadInvoices();
            }
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error creating invoice: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Edit the selected invoice
     */
    private void editSelectedInvoice() {
        int selectedRow = invoicesTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        // Get invoice number from selected row
        String invoiceNumber = invoicesTable.getValueAt(selectedRow, 0).toString();
        
        try {
            // Get the invoice
            Invoice invoice = invoiceController.getInvoiceByInvoiceNumber(invoiceNumber);
            
            if (invoice != null) {
                // Open invoice editor dialog
                InvoiceEditorDialog dialog = new InvoiceEditorDialog(
                    SwingUtilities.getWindowAncestor(this), 
                    invoice
                );
                dialog.setVisible(true);
                
                // Refresh the invoices list if the invoice was updated
                if (dialog.isInvoiceSaved()) {
                    loadInvoices();
                }
            }
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error editing invoice: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Delete the selected invoice
     */
    private void deleteSelectedInvoice() {
        int selectedRow = invoicesTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        // Get invoice info from selected row
        String invoiceNumber = invoicesTable.getValueAt(selectedRow, 0).toString();
        
        // Confirm deletion
        boolean confirmed = SwingUtils.showConfirmDialog(
            this,
            "Are you sure you want to delete invoice '" + invoiceNumber + "'?\n" +
            "This action cannot be undone, and all related data will be lost.",
            "Confirm Deletion"
        );
        
        if (confirmed) {
            try {
                // Get the invoice ID
                Invoice invoice = invoiceController.getInvoiceByInvoiceNumber(invoiceNumber);
                
                if (invoice != null) {
                    // Delete the invoice
                    boolean success = invoiceController.deleteInvoice(invoice.getId());
                    
                    if (success) {
                        SwingUtils.showInfoMessage(
                            this,
                            "Invoice deleted successfully.",
                            "Success"
                        );
                        
                        // Refresh the invoices list
                        loadInvoices();
                    } else {
                        SwingUtils.showErrorMessage(
                            this,
                            "Failed to delete invoice. It may have payments or other related records.",
                            "Deletion Error"
                        );
                    }
                }
                
            } catch (Exception e) {
                SwingUtils.showErrorMessage(
                    this,
                    "Error deleting invoice: " + e.getMessage(),
                    "Database Error"
                );
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Custom filter panel for invoices
     */
    private class InvoiceFilterPanel extends TableFilterPanel {
        private JComboBox<String> statusCombo;
        private JTextField startDateField;
        private JTextField endDateField;
        
        /**
         * Constructor
         */
        public InvoiceFilterPanel() {
            super(
                new String[]{"All", "Invoice #", "Client", "Case #"},
                searchText -> loadInvoices(),
                () -> loadInvoices() // Call our custom clear method
            );
            
            // Add additional filters
            JPanel additionalFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            additionalFilters.setBackground(Color.WHITE);
            
            // Status filter
            JLabel statusLabel = new JLabel("Status:");
            statusLabel.setFont(UIConstants.NORMAL_FONT);
            additionalFilters.add(statusLabel);
            
            String[] statuses = {"All", "Issued", "Paid", "Partially Paid", "Overdue", "Draft", "Cancelled"};
            statusCombo = new JComboBox<>(statuses);
            statusCombo.setFont(UIConstants.NORMAL_FONT);
            statusCombo.addActionListener(e -> loadInvoices());
            additionalFilters.add(statusCombo);
            
            // Date range filter
            JLabel dateRangeLabel = new JLabel("Date Range:");
            dateRangeLabel.setFont(UIConstants.NORMAL_FONT);
            additionalFilters.add(dateRangeLabel);
            
            startDateField = new JTextField(10);
            startDateField.setFont(UIConstants.NORMAL_FONT);
            startDateField.setToolTipText("Start date (YYYY-MM-DD)");
            additionalFilters.add(startDateField);
            
            JLabel toLabel = new JLabel("to");
            toLabel.setFont(UIConstants.NORMAL_FONT);
            additionalFilters.add(toLabel);
            
            endDateField = new JTextField(10);
            endDateField.setFont(UIConstants.NORMAL_FONT);
            endDateField.setToolTipText("End date (YYYY-MM-DD)");
            additionalFilters.add(endDateField);
            
            JButton applyDateButton = new JButton("Apply");
            applyDateButton.setFont(UIConstants.NORMAL_FONT);
            applyDateButton.addActionListener(e -> loadInvoices());
            additionalFilters.add(applyDateButton);
            
            addFilter(additionalFilters);
        }
        
        /**
         * Clear custom filter fields and reload invoices
         */
        private void clearCustomFilters() {
            statusCombo.setSelectedIndex(0);
            startDateField.setText("");
            endDateField.setText("");
            loadInvoices();
        }
        
        /**
         * Get the selected status filter
         * 
         * @return The selected status
         */
        public String getSelectedStatus() {
            String status = (String) statusCombo.getSelectedItem();
            return "All".equals(status) ? null : status;
        }
        
        /**
         * Get the start date for date range filter
         * 
         * @return The start date or null if not set
         */
        public LocalDate getStartDate() {
            try {
                String dateStr = startDateField.getText().trim();
                return dateStr.isEmpty() ? null : LocalDate.parse(dateStr);
            } catch (Exception e) {
                return null;
            }
        }
        
        /**
         * Get the end date for date range filter
         * 
         * @return The end date or null if not set
         */
        public LocalDate getEndDate() {
            try {
                String dateStr = endDateField.getText().trim();
                return dateStr.isEmpty() ? null : LocalDate.parse(dateStr);
            } catch (Exception e) {
                return null;
            }
        }
    }
}