package view.invoices;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import model.Invoice;
import model.Client;
import model.Case;
import model.TimeEntry;
import controller.InvoiceController;
import controller.ClientController;
import controller.CaseController;
import controller.TimeEntryController;
import javax.swing.table.TableCellRenderer;
import view.util.UIConstants;
import view.components.CustomTable;
import view.components.DateChooser;
import view.util.SwingUtils;

/**
 * Dialog for creating a new invoice or editing an existing one.
 */
public class InvoiceEditorDialog extends JDialog {
    private Invoice invoice;
    private InvoiceController invoiceController;
    private ClientController clientController;
    private CaseController caseController;
    private TimeEntryController timeEntryController;
    
    private JTextField invoiceNumberField;
    private JComboBox<ComboItem> clientCombo;
    private JComboBox<ComboItem> caseCombo;
    private DateChooser issueDateChooser;
    private DateChooser dueDateChooser;
    private JTextField amountField;
    private JComboBox<String> statusCombo;
    private JTextArea notesArea;
    
    private CustomTable timeEntriesTable;
    private List<TimeEntry> selectedTimeEntries;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    private boolean invoiceSaved = false;
    
    /**
     * Constructor for creating a new invoice
     * 
     * @param parent The parent window
     */
    public InvoiceEditorDialog(Window parent) {
        this(parent, null);
    }
    
    /**
     * Constructor for editing an existing invoice
     * 
     * @param parent The parent window
     * @param invoice The invoice to edit
     */
    public InvoiceEditorDialog(Window parent, Invoice invoice) {
        super(parent, invoice == null ? "Create New Invoice" : "Edit Invoice", ModalityType.APPLICATION_MODAL);
        
        this.invoice = invoice;
        this.invoiceController = new InvoiceController();
        this.clientController = new ClientController();
        this.caseController = new CaseController();
        this.timeEntryController = new TimeEntryController();
        
        this.selectedTimeEntries = new ArrayList<>();
        
        initializeUI();
        loadData();
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
        
        // Create content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        
        // Create form panel for invoice details
        JPanel formPanel = createFormPanel();
        contentPanel.add(formPanel, BorderLayout.NORTH);
        
        // Create time entries panel
        JPanel timeEntriesPanel = createTimeEntriesPanel();
        contentPanel.add(timeEntriesPanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        
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
        JLabel titleLabel = new JLabel(invoice == null ? "Create New Invoice" : "Edit Invoice");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        return titlePanel;
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
        
        GridBagConstraints fullWidthConstraints = new GridBagConstraints();
        fullWidthConstraints.gridx = 0;
        fullWidthConstraints.gridy = GridBagConstraints.RELATIVE;
        fullWidthConstraints.gridwidth = 2;
        fullWidthConstraints.fill = GridBagConstraints.HORIZONTAL;
        fullWidthConstraints.weightx = 1.0;
        fullWidthConstraints.insets = new Insets(5, 5, 5, 5);
        
        // Add invoice information fields
        
        // Invoice Number
        formPanel.add(createFieldLabel("Invoice Number:*"), labelConstraints);
        
        invoiceNumberField = new JTextField(20);
        invoiceNumberField.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(invoiceNumberField, fieldConstraints);
        
        // Client
        formPanel.add(createFieldLabel("Client:*"), labelConstraints);
        
        clientCombo = new JComboBox<>();
        clientCombo.setFont(UIConstants.NORMAL_FONT);
        clientCombo.addActionListener(e -> clientSelected());
        formPanel.add(clientCombo, fieldConstraints);
        
        // Case
        formPanel.add(createFieldLabel("Case:*"), labelConstraints);
        
        caseCombo = new JComboBox<>();
        caseCombo.setFont(UIConstants.NORMAL_FONT);
        caseCombo.addActionListener(e -> caseSelected());
        formPanel.add(caseCombo, fieldConstraints);
        
        // Create a panel for dates with two columns
        JPanel datesPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        datesPanel.setBackground(Color.WHITE);
        
        // Issue Date
        JPanel issueDatePanel = new JPanel(new BorderLayout());
        issueDatePanel.setBackground(Color.WHITE);
        issueDatePanel.add(createFieldLabel("Issue Date:*"), BorderLayout.NORTH);
        
        issueDateChooser = new DateChooser();
        issueDateChooser.setDate(LocalDate.now());
        issueDatePanel.add(issueDateChooser, BorderLayout.CENTER);
        
        // Due Date
        JPanel dueDatePanel = new JPanel(new BorderLayout());
        dueDatePanel.setBackground(Color.WHITE);
        dueDatePanel.add(createFieldLabel("Due Date:*"), BorderLayout.NORTH);
        
        dueDateChooser = new DateChooser();
        // Default due date is 30 days from now
        dueDateChooser.setDate(LocalDate.now().plusDays(30));
        dueDatePanel.add(dueDateChooser, BorderLayout.CENTER);
        
        datesPanel.add(issueDatePanel);
        datesPanel.add(dueDatePanel);
        
        formPanel.add(datesPanel, fullWidthConstraints);
        
        // Amount
        formPanel.add(createFieldLabel("Amount:*"), labelConstraints);
        
        amountField = new JTextField(20);
        amountField.setFont(UIConstants.NORMAL_FONT);
        amountField.setEditable(false); // Will be calculated from time entries
        formPanel.add(amountField, fieldConstraints);
        
        // Status
        formPanel.add(createFieldLabel("Status:"), labelConstraints);
        
        String[] statuses = {
            Invoice.STATUS_DRAFT, 
            Invoice.STATUS_ISSUED, 
            Invoice.STATUS_PAID,
            Invoice.STATUS_PARTIALLY_PAID,
            Invoice.STATUS_OVERDUE,
            Invoice.STATUS_CANCELLED
        };
        statusCombo = new JComboBox<>(statuses);
        statusCombo.setFont(UIConstants.NORMAL_FONT);
        // Default status is Draft for new invoices
        statusCombo.setSelectedItem(Invoice.STATUS_DRAFT);
        formPanel.add(statusCombo, fieldConstraints);
        
        // Notes
        formPanel.add(createFieldLabel("Notes:"), labelConstraints);
        
        notesArea = new JTextArea(3, 20);
        notesArea.setFont(UIConstants.NORMAL_FONT);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        notesScrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        formPanel.add(notesScrollPane, fieldConstraints);
        
        // Required fields note
        JLabel requiredNote = new JLabel("* Required fields");
        requiredNote.setFont(UIConstants.SMALL_FONT);
        requiredNote.setForeground(UIConstants.ERROR_COLOR);
        
        GridBagConstraints noteConstraints = new GridBagConstraints();
        noteConstraints.gridx = 0;
        noteConstraints.gridy = GridBagConstraints.RELATIVE;
        noteConstraints.gridwidth = 2;
        noteConstraints.anchor = GridBagConstraints.WEST;
        noteConstraints.insets = new Insets(10, 5, 5, 5);
        
        formPanel.add(requiredNote, noteConstraints);
        
        return formPanel;
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
     * Create the time entries panel
     * 
     * @return The time entries panel
     */
    private JPanel createTimeEntriesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Time entries header
        JLabel timeEntriesLabel = new JLabel("Billable Time Entries");
        timeEntriesLabel.setFont(UIConstants.SUBTITLE_FONT);
        timeEntriesLabel.setForeground(UIConstants.PRIMARY_COLOR);
        
        panel.add(timeEntriesLabel, BorderLayout.NORTH);
        
        // Create table for time entries
        String[] columnNames = {
            "Select", "Date", "Attorney", "Description", "Hours", "Rate", "Amount"
        };
        
        timeEntriesTable = new CustomTable(columnNames);
        
        // Set column widths
        timeEntriesTable.setColumnWidth(0, 50);   // Select checkbox
        timeEntriesTable.setColumnWidth(1, 100);  // Date
        timeEntriesTable.setColumnWidth(2, 150);  // Attorney
        timeEntriesTable.setColumnWidth(3, 300);  // Description
        timeEntriesTable.setColumnWidth(4, 80);   // Hours
        timeEntriesTable.setColumnWidth(5, 100);  // Rate
        timeEntriesTable.setColumnWidth(6, 100);  // Amount
        
        // Add checkbox renderer and editor for first column
        timeEntriesTable.getTable().getColumnModel().getColumn(0).setCellRenderer((TableCellRenderer) new CheckBoxRenderer());
        timeEntriesTable.getTable().getColumnModel().getColumn(0).setCellEditor(new CheckBoxEditor(new JCheckBox()));
        
        // Add summary panel at the bottom
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JLabel totalSelectedLabel = new JLabel("Total Selected: ");
        totalSelectedLabel.setFont(UIConstants.LABEL_FONT);
        
        JLabel totalSelectedValue = new JLabel("0");
        totalSelectedValue.setName("totalSelected");
        totalSelectedValue.setFont(UIConstants.NORMAL_FONT);
        
        JLabel totalHoursLabel = new JLabel("   Total Hours: ");
        totalHoursLabel.setFont(UIConstants.LABEL_FONT);
        
        JLabel totalHoursValue = new JLabel("0.0");
        totalHoursValue.setName("totalHours");
        totalHoursValue.setFont(UIConstants.NORMAL_FONT);
        
        JLabel totalAmountLabel = new JLabel("   Total Amount: ");
        totalAmountLabel.setFont(UIConstants.LABEL_FONT);
        
        JLabel totalAmountValue = new JLabel("$0.00");
        totalAmountValue.setName("totalAmount");
        totalAmountValue.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        
        summaryPanel.add(totalSelectedLabel);
        summaryPanel.add(totalSelectedValue);
        summaryPanel.add(totalHoursLabel);
        summaryPanel.add(totalHoursValue);
        summaryPanel.add(totalAmountLabel);
        summaryPanel.add(totalAmountValue);
        
        // Add listeners to update totals when checkboxes are clicked
        timeEntriesTable.getTable().getModel().addTableModelListener(e -> {
            updateTotals();
        });
        
        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(timeEntriesTable.getTable());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(summaryPanel, BorderLayout.SOUTH);
        
        return panel;
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
        
        saveButton = new JButton("Save Invoice");
        saveButton.setFont(UIConstants.NORMAL_FONT);
        saveButton.setBackground(UIConstants.SECONDARY_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveInvoice());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(saveButton);
        
        return buttonPanel;
    }
    
    /**
     * Load data for the form
     */
    private void loadData() {
        try {
            // Load client list
            loadClientCombo();
            
            // Generate invoice number for new invoices
            if (invoice == null) {
                generateInvoiceNumber();
            } else {
                // Load existing invoice data
                loadInvoiceData();
            }
        } catch (Exception e) {
            showError("Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load the client dropdown
     */
    private void loadClientCombo() {
        try {
            // Clear existing items
            clientCombo.removeAllItems();
            
            // Get all clients
            List<Client> clients = clientController.getAllClients();
            
            // Add clients to combo box
            for (Client client : clients) {
                clientCombo.addItem(new ComboItem(client.getId(), client.getName()));
            }
            
        } catch (Exception e) {
            showError("Error loading clients: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Generate a new invoice number
     */
    private void generateInvoiceNumber() {
        try {
            String invoiceNumber = invoiceController.generateNextInvoiceNumber();
            invoiceNumberField.setText(invoiceNumber);
        } catch (Exception e) {
            e.printStackTrace();
            invoiceNumberField.setText("INV" + System.currentTimeMillis());
        }
    }
    
    /**
     * Load existing invoice data when editing
     */
    private void loadInvoiceData() {
        try {
            // Get invoice with full details
            Invoice fullInvoice = invoiceController.getInvoiceWithDetails(invoice.getId());
            this.invoice = fullInvoice;
            
            // Populate form fields
            invoiceNumberField.setText(invoice.getInvoiceNumber());
            
            // Select client
            for (int i = 0; i < clientCombo.getItemCount(); i++) {
                ComboItem item = clientCombo.getItemAt(i);
                if (item.getId() == invoice.getClient().getId()) {
                    clientCombo.setSelectedIndex(i);
                    break;
                }
            }
            
            // Load and select case
            loadCaseCombo(invoice.getClient().getId());
            for (int i = 0; i < caseCombo.getItemCount(); i++) {
                ComboItem item = caseCombo.getItemAt(i);
                if (item.getId() == invoice.getCase().getId()) {
                    caseCombo.setSelectedIndex(i);
                    break;
                }
            }
            
            // Dates
            if (invoice.getIssueDate() != null) {
                issueDateChooser.setDate(invoice.getIssueDate());
            }
            
            if (invoice.getDueDate() != null) {
                dueDateChooser.setDate(invoice.getDueDate());
            }
            
            // Amount
            if (invoice.getAmount() != null) {
                amountField.setText(invoice.getAmount().toString());
            }
            
            // Status
            statusCombo.setSelectedItem(invoice.getStatus());
            
            // Notes
            if (invoice.getNotes() != null) {
                notesArea.setText(invoice.getNotes());
            }
            
            // Load time entries
            if (invoice.getTimeEntries() != null) {
                this.selectedTimeEntries = new ArrayList<>(invoice.getTimeEntries());
            }
            
            // Load time entries for case
            loadTimeEntries(invoice.getCase().getId());
            
        } catch (Exception e) {
            showError("Error loading invoice data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handler for client selection
     */
    private void clientSelected() {
        ComboItem selectedClient = (ComboItem) clientCombo.getSelectedItem();
        if (selectedClient != null) {
            // Load cases for the selected client
            loadCaseCombo(selectedClient.getId());
        } else {
            // Clear case combo
            caseCombo.removeAllItems();
        }
    }
    
    /**
     * Load the case dropdown for a client
     * 
     * @param clientId The client ID
     */
    private void loadCaseCombo(int clientId) {
        try {
            // Clear existing items
            caseCombo.removeAllItems();
            
            // Get cases for the client
            List<Case> cases = caseController.findCasesByClient(clientId);
            
            // Add cases to combo box
            for (Case legalCase : cases) {
                caseCombo.addItem(new ComboItem(legalCase.getId(), 
                    legalCase.getCaseNumber() + " - " + legalCase.getTitle()));
            }
            
        } catch (Exception e) {
            showError("Error loading cases: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handler for case selection
     */
    private void caseSelected() {
        ComboItem selectedCase = (ComboItem) caseCombo.getSelectedItem();
        if (selectedCase != null) {
            // Load time entries for the selected case
            loadTimeEntries(selectedCase.getId());
        } else {
            // Clear time entries table
            timeEntriesTable.clearTable();
        }
    }
    
    /**
     * Load time entries for a case
     * 
     * @param caseId The case ID
     */
    private void loadTimeEntries(int caseId) {
        try {
            // Clear existing items
            timeEntriesTable.clearTable();
            
            // Get unbilled time entries for the case
            List<TimeEntry> timeEntries;
            
            // If editing an existing invoice, get all time entries for the case
            if (invoice != null && invoice.getId() > 0) {
                timeEntries = timeEntryController.getCaseTimeEntries(caseId);
            } else {
                // Otherwise, only get unbilled entries
                timeEntries = timeEntryController.getUnbilledTimeEntries(caseId);
            }
            
            // Add time entries to table
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            
            for (TimeEntry entry : timeEntries) {
                String attorneyName = entry.getAttorney() != null ? 
                    entry.getAttorney().getFullName() : "Attorney " + entry.getAttorney().getAttorneyId();
                
                String entryDate = entry.getEntryDate() != null ? 
                    entry.getEntryDate().format(dateFormatter) : "N/A";
                
                String hourlyRate = entry.getHourlyRate() != null ? 
                    SwingUtils.formatMoney(entry.getHourlyRate().doubleValue()) : "$0.00";
                
                String entryAmount = entry.getAmount() != null ? 
                    SwingUtils.formatMoney(entry.getAmount().doubleValue()) : "$0.00";
                
                // Check if this entry should be selected (if it's in the invoice)
                boolean isSelected = isTimeEntrySelected(entry);
                
                Object[] row = {
                    isSelected,
                    entryDate,
                    attorneyName,
                    entry.getDescription(),
                    String.format("%.2f", entry.getHours()),
                    hourlyRate,
                    entryAmount
                };
                
                // Store the time entry ID in the row's tag for reference
                timeEntriesTable.addRow(row);
                
                // Store time entry in table model for lookup
                int rowIndex = timeEntriesTable.getRowCount() - 1;
                timeEntriesTable.getTable().getModel().setValueAt(entry, rowIndex, -1);
            }
            
            // Update totals
            updateTotals();
            
        } catch (Exception e) {
            showError("Error loading time entries: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Check if a time entry is already selected
     * 
     * @param entry The time entry to check
     * @return true if the entry is selected
     */
    private boolean isTimeEntrySelected(TimeEntry entry) {
        if (selectedTimeEntries == null || selectedTimeEntries.isEmpty()) {
            return false;
        }
        
        for (TimeEntry selectedEntry : selectedTimeEntries) {
            if (selectedEntry.getId() == entry.getId()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Update the totals based on selected time entries
     */
    private void updateTotals() {
        try {
            // Get totals components
            JLabel totalSelectedLabel = (JLabel) findComponentByName(timeEntriesTable.getParent(), "totalSelected");
            JLabel totalHoursLabel = (JLabel) findComponentByName(timeEntriesTable.getParent(), "totalHours");
            JLabel totalAmountLabel = (JLabel) findComponentByName(timeEntriesTable.getParent(), "totalAmount");
            
            // Calculate totals
            int totalSelected = 0;
            double totalHours = 0.0;
            BigDecimal totalAmount = BigDecimal.ZERO;
            
            selectedTimeEntries.clear();
            
            // Process each row
            for (int i = 0; i < timeEntriesTable.getRowCount(); i++) {
                Boolean selected = (Boolean) timeEntriesTable.getTable().getValueAt(i, 0);
                
                if (selected != null && selected) {
                    totalSelected++;
                    
                    // Get the time entry from the table model
                    TimeEntry entry = (TimeEntry) timeEntriesTable.getTable().getModel().getValueAt(i, -1);
                    
                    if (entry != null) {
                        selectedTimeEntries.add(entry);
                        totalHours += entry.getHours();
                        
                        if (entry.getAmount() != null) {
                            totalAmount = totalAmount.add(entry.getAmount());
                        }
                    }
                }
            }
            
            // Update labels
            if (totalSelectedLabel != null) {
                totalSelectedLabel.setText(Integer.toString(totalSelected));
            }
            
            if (totalHoursLabel != null) {
                totalHoursLabel.setText(String.format("%.2f", totalHours));
            }
            
            if (totalAmountLabel != null) {
                totalAmountLabel.setText(SwingUtils.formatMoney(totalAmount.doubleValue()));
            }
            
            // Update amount field
            amountField.setText(totalAmount.toString());
            
        } catch (Exception e) {
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
     * Validate form data
     * 
     * @return true if form data is valid
     */
    private boolean validateForm() {
        // Check invoice number
        if (invoiceNumberField.getText().trim().isEmpty()) {
            showError("Invoice number is required.");
            invoiceNumberField.requestFocus();
            return false;
        }
        
        // Check client selection
        if (clientCombo.getSelectedItem() == null) {
            showError("Please select a client.");
            clientCombo.requestFocus();
            return false;
        }
        
        // Check case selection
        if (caseCombo.getSelectedItem() == null) {
            showError("Please select a case.");
            caseCombo.requestFocus();
            return false;
        }
        
        // Check issue date
        if (issueDateChooser.getDate() == null) {
            showError("Issue date is required.");
            issueDateChooser.requestFocus();
            return false;
        }
        
        // Check due date
        if (dueDateChooser.getDate() == null) {
            showError("Due date is required.");
            dueDateChooser.requestFocus();
            return false;
        }
        
        // Check if due date is before issue date
        if (dueDateChooser.getDate().isBefore(issueDateChooser.getDate())) {
            showError("Due date cannot be before issue date.");
            dueDateChooser.requestFocus();
            return false;
        }
        
        // Check if at least one time entry is selected
        if (selectedTimeEntries.isEmpty()) {
            boolean proceed = SwingUtils.showConfirmDialog(
                this,
                "No time entries have been selected. Do you want to create an empty invoice?",
                "No Time Entries"
            );
            
            if (!proceed) {
                return false;
            }
        }
        
        // Check amount
        try {
            BigDecimal amount = new BigDecimal(amountField.getText().trim());
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                showError("Invoice amount cannot be negative.");
                amountField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid invoice amount.");
            amountField.requestFocus();
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
     * Save the invoice
     */
    private void saveInvoice() {
        if (!validateForm()) {
            return;
        }
        
        try {
            // Create or update invoice object
            Invoice newInvoice = (invoice == null) ? new Invoice() : invoice;
            
            newInvoice.setInvoiceNumber(invoiceNumberField.getText().trim());
            
            // Get client ID
            ComboItem selectedClient = (ComboItem) clientCombo.getSelectedItem();
            Client client = new Client();
            client.setId(selectedClient.getId());
            newInvoice.setClient(client);
            
            // Get case ID
            ComboItem selectedCase = (ComboItem) caseCombo.getSelectedItem();
            Case case1 = new Case();
            case1.setId(selectedCase.getId());
            newInvoice.setCase(case1);
            
            // Set dates
            newInvoice.setIssueDate(issueDateChooser.getDate());
            newInvoice.setDueDate(dueDateChooser.getDate());
            
            // Set amount
            newInvoice.setAmount(new BigDecimal(amountField.getText().trim()));
            
            // Set status
            newInvoice.setStatus((String) statusCombo.getSelectedItem());
            
            // Set notes
            newInvoice.setNotes(notesArea.getText().trim());
            
            // Set time entries
            newInvoice.setTimeEntries(selectedTimeEntries);
            
            // Save invoice
            boolean success;
            if (invoice == null) {
                // Create new invoice
                success = invoiceController.createInvoice(newInvoice);
            } else {
                // Update existing invoice
                success = invoiceController.updateInvoice(newInvoice);
            }
            
            if (success) {
                invoiceSaved = true;
                SwingUtils.showInfoMessage(
                    this,
                    "Invoice has been saved successfully.",
                    "Invoice Saved"
                );
                dispose();
            } else {
                showError("Failed to save invoice. Please try again.");
            }
            
        } catch (Exception e) {
            showError("Error saving invoice: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Check if an invoice was saved
     * 
     * @return true if invoice was saved
     */
    public boolean isInvoiceSaved() {
        return invoiceSaved;
    }
    
    /**
     * Combo item class for dropdowns
     */
    private class ComboItem {
        private int id;
        private String text;
        
        public ComboItem(int id, String text) {
            this.id = id;
            this.text = text;
        }
        
        public int getId() {
            return id;
        }
        
        public String getText() {
            return text;
        }
        
        @Override
        public String toString() {
            return text;
        }
    }
    
    /**
     * Checkbox renderer for time entry selection
     */
    private class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {
        public CheckBoxRenderer() {
            setHorizontalAlignment(JCheckBox.CENTER);
            setBackground(Color.WHITE);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            if (value instanceof Boolean) {
                setSelected((Boolean) value);
            } else {
                setSelected(false);
            }
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 250));
            }
            
            return this;
        }
    }
    
    /**
     * Checkbox editor for time entry selection
     */
    private class CheckBoxEditor extends DefaultCellEditor {
        public CheckBoxEditor(JCheckBox checkBox) {
            super(checkBox);
            checkBox.setHorizontalAlignment(JCheckBox.CENTER);
        }
    }
}