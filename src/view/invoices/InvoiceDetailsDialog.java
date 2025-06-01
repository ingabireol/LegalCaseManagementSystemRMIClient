package view.invoices;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import model.Invoice;
import model.Payment;
import model.TimeEntry;
import controller.InvoiceController;
import view.util.UIConstants;
import view.components.CustomTable;
import view.util.SwingUtils;

/**
 * Dialog for viewing invoice details including line items and payments.
 */
public class InvoiceDetailsDialog extends JDialog {
    private Invoice invoice;
    private InvoiceController invoiceController;
    
    private JPanel invoiceInfoPanel;
    private JPanel lineItemsPanel;
    private JPanel paymentsPanel;
    private CustomTable lineItemsTable;
    private CustomTable paymentsTable;
    
    private JButton closeButton;
    private JButton printButton;
    private JButton recordPaymentButton;
    
    /**
     * Constructor
     * 
     * @param parent The parent window
     * @param invoice The invoice to display
     */
    public InvoiceDetailsDialog(Window parent, Invoice invoice) {
        super(parent, "Invoice Details", ModalityType.APPLICATION_MODAL);
        
        this.invoice = invoice;
        this.invoiceController = new InvoiceController();
        
        initializeUI();
        loadInvoiceData();
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setSize(900, 700);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Create title panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Create tabbed pane for different sections
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.NORMAL_FONT);
        
        // Create invoice info panel
        invoiceInfoPanel = createInvoiceInfoPanel();
        tabbedPane.addTab("Invoice Details", invoiceInfoPanel);
        
        // Create line items panel
        lineItemsPanel = createLineItemsPanel();
        tabbedPane.addTab("Line Items", lineItemsPanel);
        
        // Create payments panel
        paymentsPanel = createPaymentsPanel();
        tabbedPane.addTab("Payment History", paymentsPanel);
        
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
        JLabel titleLabel = new JLabel("Invoice Details");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        // Add invoice number to title if available
        if (invoice != null && invoice.getInvoiceNumber() != null) {
            titleLabel.setText("Invoice: " + invoice.getInvoiceNumber());
        }
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Status indicator if available
        if (invoice != null && invoice.getStatus() != null) {
            JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            statusPanel.setOpaque(false);
            
            JLabel statusLabel = new JLabel("Status: ");
            statusLabel.setFont(UIConstants.NORMAL_FONT);
            statusLabel.setForeground(Color.WHITE);
            
            statusPanel.add(statusLabel);
            statusPanel.add(new view.components.StatusIndicator(invoice.getStatus()));
            
            titlePanel.add(statusPanel, BorderLayout.EAST);
        }
        
        return titlePanel;
    }
    
    /**
     * Create the invoice information panel
     * 
     * @return The invoice info panel
     */
    private JPanel createInvoiceInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create grid for invoice information
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
        
        // Add invoice information fields (will be populated in loadInvoiceData)
        
        // Invoice Number
        infoGrid.add(createFieldLabel("Invoice Number:"), labelConstraints);
        JLabel invoiceNumberValue = new JLabel();
        invoiceNumberValue.setName("invoiceNumber");
        infoGrid.add(invoiceNumberValue, valueConstraints);
        
        // Client
        infoGrid.add(createFieldLabel("Client:"), labelConstraints);
        JLabel clientValue = new JLabel();
        clientValue.setName("client");
        infoGrid.add(clientValue, valueConstraints);
        
        // Case
        infoGrid.add(createFieldLabel("Case:"), labelConstraints);
        JLabel caseValue = new JLabel();
        caseValue.setName("case");
        infoGrid.add(caseValue, valueConstraints);
        
        // Issue Date
        infoGrid.add(createFieldLabel("Issue Date:"), labelConstraints);
        JLabel issueDateValue = new JLabel();
        issueDateValue.setName("issueDate");
        infoGrid.add(issueDateValue, valueConstraints);
        
        // Due Date
        infoGrid.add(createFieldLabel("Due Date:"), labelConstraints);
        JLabel dueDateValue = new JLabel();
        dueDateValue.setName("dueDate");
        infoGrid.add(dueDateValue, valueConstraints);
        
        // Amount
        infoGrid.add(createFieldLabel("Amount:"), labelConstraints);
        JLabel amountValue = new JLabel();
        amountValue.setName("amount");
        amountValue.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        infoGrid.add(amountValue, valueConstraints);
        
        // Amount Paid
        infoGrid.add(createFieldLabel("Amount Paid:"), labelConstraints);
        JLabel amountPaidValue = new JLabel();
        amountPaidValue.setName("amountPaid");
        infoGrid.add(amountPaidValue, valueConstraints);
        
        // Balance
        infoGrid.add(createFieldLabel("Balance:"), labelConstraints);
        JLabel balanceValue = new JLabel();
        balanceValue.setName("balance");
        balanceValue.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        infoGrid.add(balanceValue, valueConstraints);
        
        // Status
        infoGrid.add(createFieldLabel("Status:"), labelConstraints);
        JLabel statusValue = new JLabel();
        statusValue.setName("status");
        infoGrid.add(statusValue, valueConstraints);
        
        // Notes
        infoGrid.add(createFieldLabel("Notes:"), labelConstraints);
        JTextArea notesValue = new JTextArea(4, 30);
        notesValue.setName("notes");
        notesValue.setEditable(false);
        notesValue.setLineWrap(true);
        notesValue.setWrapStyleWord(true);
        notesValue.setBackground(Color.WHITE);
        notesValue.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JScrollPane notesScroll = new JScrollPane(notesValue);
        infoGrid.add(notesScroll, valueConstraints);
        
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
     * Create the line items panel with a table of time entries or items
     * 
     * @return The line items panel
     */
    private JPanel createLineItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create table for line items
        String[] columnNames = {
            "Date", "Attorney", "Description", "Hours", "Rate", "Amount"
        };
        
        lineItemsTable = new CustomTable(columnNames);
        
        // Set column widths
        lineItemsTable.setColumnWidth(0, 100);  // Date
        lineItemsTable.setColumnWidth(1, 150);  // Attorney
        lineItemsTable.setColumnWidth(2, 300);  // Description
        lineItemsTable.setColumnWidth(3, 80);   // Hours
        lineItemsTable.setColumnWidth(4, 100);  // Rate
        lineItemsTable.setColumnWidth(5, 100);  // Amount
        
        // Add summary panel at the bottom
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JLabel totalHoursLabel = new JLabel("Total Hours: ");
        totalHoursLabel.setFont(UIConstants.LABEL_FONT);
        
        JLabel totalHoursValue = new JLabel("0.0");
        totalHoursValue.setName("totalHours");
        totalHoursValue.setFont(UIConstants.NORMAL_FONT);
        
        JLabel totalAmountLabel = new JLabel("   Total Amount: ");
        totalAmountLabel.setFont(UIConstants.LABEL_FONT);
        
        JLabel totalAmountValue = new JLabel("$0.00");
        totalAmountValue.setName("totalLineAmount");
        totalAmountValue.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        
        summaryPanel.add(totalHoursLabel);
        summaryPanel.add(totalHoursValue);
        summaryPanel.add(totalAmountLabel);
        summaryPanel.add(totalAmountValue);
        
        // Add components to panel
        panel.add(lineItemsTable, BorderLayout.CENTER);
        panel.add(summaryPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create the payments panel with a table of payments
     * 
     * @return The payments panel
     */
    private JPanel createPaymentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create table for payments
        String[] columnNames = {
            "Payment ID", "Date", "Amount", "Method", "Reference", "Notes"
        };
        
        paymentsTable = new CustomTable(columnNames);
        
        // Set column widths
        paymentsTable.setColumnWidth(0, 120);  // Payment ID
        paymentsTable.setColumnWidth(1, 100);  // Date
        paymentsTable.setColumnWidth(2, 100);  // Amount
        paymentsTable.setColumnWidth(3, 100);  // Method
        paymentsTable.setColumnWidth(4, 120);  // Reference
        paymentsTable.setColumnWidth(5, 200);  // Notes
        
        // Add record payment button at the top
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JButton addPaymentButton = new JButton("Record New Payment");
        addPaymentButton.setFont(UIConstants.NORMAL_FONT);
        addPaymentButton.addActionListener(e -> recordPayment());
        
        actionsPanel.add(addPaymentButton);
        
        // Add summary panel at the bottom
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JLabel totalPaidLabel = new JLabel("Total Paid: ");
        totalPaidLabel.setFont(UIConstants.LABEL_FONT);
        
        JLabel totalPaidValue = new JLabel("$0.00");
        totalPaidValue.setName("totalPaid");
        totalPaidValue.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        
        JLabel balanceLabel = new JLabel("   Balance: ");
        balanceLabel.setFont(UIConstants.LABEL_FONT);
        
        JLabel balanceValue = new JLabel("$0.00");
        balanceValue.setName("paymentBalance");
        balanceValue.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        
        summaryPanel.add(totalPaidLabel);
        summaryPanel.add(totalPaidValue);
        summaryPanel.add(balanceLabel);
        summaryPanel.add(balanceValue);
        
        // Add components to panel
        panel.add(actionsPanel, BorderLayout.NORTH);
        panel.add(paymentsTable, BorderLayout.CENTER);
        panel.add(summaryPanel, BorderLayout.SOUTH);
        
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
        
        printButton = new JButton("Print Invoice");
        printButton.setFont(UIConstants.NORMAL_FONT);
        printButton.addActionListener(e -> printInvoice());
        
        recordPaymentButton = new JButton("Record Payment");
        recordPaymentButton.setFont(UIConstants.NORMAL_FONT);
        recordPaymentButton.addActionListener(e -> recordPayment());
        
        closeButton = new JButton("Close");
        closeButton.setFont(UIConstants.NORMAL_FONT);
        closeButton.addActionListener(e -> dispose());
        
        // Disable print button if invoice is in draft status
        if (invoice != null && Invoice.STATUS_DRAFT.equals(invoice.getStatus())) {
            printButton.setEnabled(false);
        }
        
        buttonPanel.add(printButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(recordPaymentButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(closeButton);
        
        return buttonPanel;
    }
    
    /**
     * Load invoice data into the UI
     */
    private void loadInvoiceData() {
        try {
            // Get invoice with all details
            Invoice invoiceWithDetails = invoiceController.getInvoiceWithDetails(invoice.getId());
            this.invoice = invoiceWithDetails;
            
            // Update invoice information fields
            JLabel invoiceNumberValue = (JLabel) findComponentByName(invoiceInfoPanel, "invoiceNumber");
            JLabel clientValue = (JLabel) findComponentByName(invoiceInfoPanel, "client");
            JLabel caseValue = (JLabel) findComponentByName(invoiceInfoPanel, "case");
            JLabel issueDateValue = (JLabel) findComponentByName(invoiceInfoPanel, "issueDate");
            JLabel dueDateValue = (JLabel) findComponentByName(invoiceInfoPanel, "dueDate");
            JLabel amountValue = (JLabel) findComponentByName(invoiceInfoPanel, "amount");
            JLabel amountPaidValue = (JLabel) findComponentByName(invoiceInfoPanel, "amountPaid");
            JLabel balanceValue = (JLabel) findComponentByName(invoiceInfoPanel, "balance");
            JLabel statusValue = (JLabel) findComponentByName(invoiceInfoPanel, "status");
            JTextArea notesValue = (JTextArea) findComponentByName(invoiceInfoPanel, "notes");
            
            invoiceNumberValue.setText(invoice.getInvoiceNumber());
            
            // Client information
            String clientName = invoice.getClient() != null ? invoice.getClient().getName() : "Client " + invoice.getClient().getClientId();
            clientValue.setText(clientName);
            
            // Case information
            String caseInfo = invoice.getCase() != null ? 
                invoice.getCase().getCaseNumber() + " - " + invoice.getCase().getTitle() : 
                "Case " + invoice.getCase().getId();
            caseValue.setText(caseInfo);
            
            // Format dates
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            String issueDate = invoice.getIssueDate() != null ? invoice.getIssueDate().format(dateFormatter) : "N/A";
            String dueDate = invoice.getDueDate() != null ? invoice.getDueDate().format(dateFormatter) : "N/A";
            
            issueDateValue.setText(issueDate);
            dueDateValue.setText(dueDate);
            
            // Format currency values
            String amount = SwingUtils.formatMoney(invoice.getAmount().doubleValue());
            String amountPaid = SwingUtils.formatMoney(invoice.getAmountPaid().doubleValue());
            String balance = SwingUtils.formatMoney(invoice.getBalance().doubleValue());
            
            amountValue.setText(amount);
            amountPaidValue.setText(amountPaid);
            balanceValue.setText(balance);
            
            // Set color for balance based on if it's paid or not
            if (invoice.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
                balanceValue.setForeground(UIConstants.SUCCESS_COLOR);
            } else {
                balanceValue.setForeground(invoice.isOverdue() ? UIConstants.ERROR_COLOR : UIConstants.TEXT_COLOR);
            }
            
            statusValue.setText(invoice.getStatus());
            notesValue.setText(invoice.getNotes() != null ? invoice.getNotes() : "");
            
            // Load time entries (line items)
            lineItemsTable.clearTable();
            List<TimeEntry> timeEntries = invoice.getTimeEntries();
            double totalHours = 0.0;
            BigDecimal totalLineAmount = BigDecimal.ZERO;
            
            if (timeEntries != null && !timeEntries.isEmpty()) {
                for (TimeEntry entry : timeEntries) {
                    String attorneyName = entry.getAttorney() != null ? 
                        entry.getAttorney().getFullName() : "Attorney " + entry.getAttorney().getAttorneyId();
                    
                    String entryDate = entry.getEntryDate() != null ? 
                        entry.getEntryDate().format(dateFormatter) : "N/A";
                    
                    String hourlyRate = entry.getHourlyRate() != null ? 
                        SwingUtils.formatMoney(entry.getHourlyRate().doubleValue()) : "$0.00";
                    
                    String entryAmount = entry.getAmount() != null ? 
                        SwingUtils.formatMoney(entry.getAmount().doubleValue()) : "$0.00";
                    
                    Object[] row = {
                        entryDate,
                        attorneyName,
                        entry.getDescription(),
                        String.format("%.2f", entry.getHours()),
                        hourlyRate,
                        entryAmount
                    };
                    lineItemsTable.addRow(row);
                    
                    totalHours += entry.getHours();
                    if (entry.getAmount() != null) {
                        totalLineAmount = totalLineAmount.add(entry.getAmount());
                    }
                }
            }
            
            // Update line items summary
            JLabel totalHoursValue = (JLabel) findComponentByName(lineItemsPanel, "totalHours");
            JLabel totalLineAmountValue = (JLabel) findComponentByName(lineItemsPanel, "totalLineAmount");
            
            totalHoursValue.setText(String.format("%.2f", totalHours));
            totalLineAmountValue.setText(SwingUtils.formatMoney(totalLineAmount.doubleValue()));
            
            // Load payments
            paymentsTable.clearTable();
            List<Payment> payments = invoice.getPayments();
            
            if (payments != null && !payments.isEmpty()) {
                for (Payment payment : payments) {
                    String paymentDate = payment.getPaymentDate() != null ? 
                        payment.getPaymentDate().format(dateFormatter) : "N/A";
                    
                    String paymentAmount = payment.getAmount() != null ? 
                        SwingUtils.formatMoney(payment.getAmount().doubleValue()) : "$0.00";
                    
                    Object[] row = {
                        payment.getPaymentId(),
                        paymentDate,
                        paymentAmount,
                        payment.getPaymentMethod(),
                        payment.getReference() != null ? payment.getReference() : "",
                        payment.getNotes() != null ? payment.getNotes() : ""
                    };
                    paymentsTable.addRow(row);
                }
            }
            
            // Update payments summary
            JLabel totalPaidValue = (JLabel) findComponentByName(paymentsPanel, "totalPaid");
            JLabel paymentBalanceValue = (JLabel) findComponentByName(paymentsPanel, "paymentBalance");
            
            totalPaidValue.setText(amountPaid);
            paymentBalanceValue.setText(balance);
            
            // Set color for balance
            if (invoice.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
                paymentBalanceValue.setForeground(UIConstants.SUCCESS_COLOR);
            } else {
                paymentBalanceValue.setForeground(invoice.isOverdue() ? UIConstants.ERROR_COLOR : UIConstants.TEXT_COLOR);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Error loading invoice data: " + e.getMessage(),
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
     * Record a payment for this invoice
     */
    private void recordPayment() {
        try {
            // Open payment dialog
            PaymentDialog dialog = new PaymentDialog(
                getOwner(), 
                invoice
            );
            dialog.setVisible(true);
            
            // Refresh data if payment was recorded
            if (dialog.isPaymentRecorded()) {
                // Reload invoice data
                invoice = invoiceController.getInvoiceWithDetails(invoice.getId());
                loadInvoiceData();
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Error recording payment: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Print the invoice
     */
    private void printInvoice() {
        // This would implement invoice printing functionality
        JOptionPane.showMessageDialog(
            this,
            "Print functionality will be implemented in a future version.",
            "Print Invoice",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}