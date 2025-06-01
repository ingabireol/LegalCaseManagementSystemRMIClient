package view.invoices;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import model.Invoice;
import model.Payment;
import controller.InvoiceController;
import view.util.UIConstants;
import view.components.DateChooser;
import view.util.SwingUtils;

/**
 * Dialog for recording a payment against an invoice.
 */
public class PaymentDialog extends JDialog {
    private Invoice invoice;
    private InvoiceController invoiceController;
    
    private JTextField paymentIdField;
    private JComboBox<String> paymentMethodCombo;
    private DateChooser paymentDateChooser;
    private JTextField amountField;
    private JTextField referenceField;
    private JTextArea notesArea;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    private boolean paymentRecorded = false;
    
    /**
     * Constructor
     * 
     * @param parent The parent window
     * @param invoice The invoice to record payment for
     */
    public PaymentDialog(Window parent, Invoice invoice) {
        super(parent, "Record Payment", ModalityType.APPLICATION_MODAL);
        
        this.invoice = invoice;
        this.invoiceController = new InvoiceController();
        
        initializeUI();
        populateInitialValues();
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setSize(500, 500);
        setMinimumSize(new Dimension(450, 450));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Create title panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Create form panel
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        
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
        JLabel titleLabel = new JLabel("Record Payment");
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
        
        // Add invoice information (non-editable)
        JLabel invoiceInfoLabel = new JLabel("Invoice Information");
        invoiceInfoLabel.setFont(UIConstants.HEADER_FONT);
        invoiceInfoLabel.setForeground(UIConstants.PRIMARY_COLOR);
        
        GridBagConstraints headerConstraints = new GridBagConstraints();
        headerConstraints.gridx = 0;
        headerConstraints.gridy = 0;
        headerConstraints.gridwidth = 2;
        headerConstraints.fill = GridBagConstraints.HORIZONTAL;
        headerConstraints.insets = new Insets(0, 5, 10, 5);
        
        formPanel.add(invoiceInfoLabel, headerConstraints);
        
        // Invoice Number
        formPanel.add(createFieldLabel("Invoice Number:"), labelConstraints);
        
        JLabel invoiceNumberValue = new JLabel(invoice.getInvoiceNumber());
        invoiceNumberValue.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(invoiceNumberValue, fieldConstraints);
        
        // Client
        formPanel.add(createFieldLabel("Client:"), labelConstraints);
        
        String clientName = invoice.getClient() != null ? 
            invoice.getClient().getName() : "Client " + invoice.getClient().getId();
        JLabel clientValue = new JLabel(clientName);
        clientValue.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(clientValue, fieldConstraints);
        
        // Invoice Amount
        formPanel.add(createFieldLabel("Invoice Amount:"), labelConstraints);
        
        JLabel amountValue = new JLabel(SwingUtils.formatMoney(invoice.getAmount().doubleValue()));
        amountValue.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(amountValue, fieldConstraints);
        
        // Balance Due
        formPanel.add(createFieldLabel("Balance Due:"), labelConstraints);
        
        JLabel balanceValue = new JLabel(SwingUtils.formatMoney(invoice.getBalance().doubleValue()));
        balanceValue.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        if (invoice.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            balanceValue.setForeground(invoice.isOverdue() ? UIConstants.ERROR_COLOR : UIConstants.TEXT_COLOR);
        } else {
            balanceValue.setForeground(UIConstants.SUCCESS_COLOR);
        }
        formPanel.add(balanceValue, fieldConstraints);
        
        // Payment section header
        JLabel paymentInfoLabel = new JLabel("Payment Details");
        paymentInfoLabel.setFont(UIConstants.HEADER_FONT);
        paymentInfoLabel.setForeground(UIConstants.PRIMARY_COLOR);
        
        GridBagConstraints paymentHeaderConstraints = new GridBagConstraints();
        paymentHeaderConstraints.gridx = 0;
        paymentHeaderConstraints.gridy = GridBagConstraints.RELATIVE;
        paymentHeaderConstraints.gridwidth = 2;
        paymentHeaderConstraints.fill = GridBagConstraints.HORIZONTAL;
        paymentHeaderConstraints.insets = new Insets(15, 5, 10, 5);
        
        formPanel.add(paymentInfoLabel, paymentHeaderConstraints);
        
        // Payment ID
        formPanel.add(createFieldLabel("Payment ID:"), labelConstraints);
        
        paymentIdField = new JTextField(20);
        paymentIdField.setEditable(false); // Generated automatically
        paymentIdField.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(paymentIdField, fieldConstraints);
        
        // Payment Method
        formPanel.add(createFieldLabel("Payment Method:*"), labelConstraints);
        
        String[] paymentMethods = invoiceController.getPaymentMethods();
        paymentMethodCombo = new JComboBox<>(paymentMethods);
        paymentMethodCombo.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(paymentMethodCombo, fieldConstraints);
        
        // Payment Date
        formPanel.add(createFieldLabel("Payment Date:*"), labelConstraints);
        
        paymentDateChooser = new DateChooser();
        paymentDateChooser.setDate(LocalDate.now());
        formPanel.add(paymentDateChooser, fieldConstraints);
        
        // Amount
        formPanel.add(createFieldLabel("Payment Amount:*"), labelConstraints);
        
        amountField = new JTextField(20);
        amountField.setFont(UIConstants.NORMAL_FONT);
        // Set amount to remaining balance by default
        amountField.setText(invoice.getBalance().toString());
        formPanel.add(amountField, fieldConstraints);
        
        // Reference
        formPanel.add(createFieldLabel("Reference:"), labelConstraints);
        
        referenceField = new JTextField(20);
        referenceField.setFont(UIConstants.NORMAL_FONT);
        referenceField.setToolTipText("Check number, transaction ID, etc.");
        formPanel.add(referenceField, fieldConstraints);
        
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
        noteConstraints.insets = new Insets(20, 5, 5, 5);
        
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
        
        saveButton = new JButton("Save Payment");
        saveButton.setFont(UIConstants.NORMAL_FONT);
        saveButton.setBackground(UIConstants.SECONDARY_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> savePayment());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(saveButton);
        
        return buttonPanel;
    }
    
    /**
     * Populate initial values in the form
     */
    private void populateInitialValues() {
        // Generate payment ID
        try {
            String paymentId = "PMT" + System.currentTimeMillis();
            paymentIdField.setText(paymentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Validate form data
     * 
     * @return true if form data is valid
     */
    private boolean validateForm() {
        // Check payment method
        if (paymentMethodCombo.getSelectedItem() == null) {
            showError("Please select a payment method.");
            paymentMethodCombo.requestFocus();
            return false;
        }
        
        // Check payment date
        if (paymentDateChooser.getDate() == null) {
            showError("Please enter a valid payment date.");
            paymentDateChooser.requestFocus();
            return false;
        }
        
        // Check amount
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountField.getText().trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Payment amount must be greater than zero.");
                amountField.requestFocus();
                return false;
            }
            
            // Check if amount exceeds remaining balance
            if (amount.compareTo(invoice.getBalance()) > 0) {
                boolean proceed = SwingUtils.showConfirmDialog(
                    this,
                    "The payment amount exceeds the remaining balance of " + 
                    SwingUtils.formatMoney(invoice.getBalance().doubleValue()) + 
                    ". Do you want to proceed with overpayment?",
                    "Amount Exceeds Balance"
                );
                
                if (!proceed) {
                    amountField.requestFocus();
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid payment amount.");
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
     * Save the payment
     */
    private void savePayment() {
        if (!validateForm()) {
            return;
        }
        
        try {
            // Create payment object
            Payment payment = new Payment();
            payment.setPaymentId(paymentIdField.getText().trim());
            
            payment.setInvoice(invoice);
            payment.setClient(invoice.getClient());
            payment.setPaymentDate(paymentDateChooser.getDate());
            payment.setAmount(new BigDecimal(amountField.getText().trim()));
            payment.setPaymentMethod((String) paymentMethodCombo.getSelectedItem());
            payment.setReference(referenceField.getText().trim());
            payment.setNotes(notesArea.getText().trim());
            
            // Record payment
            boolean success = invoiceController.recordPayment(payment);
            
            if (success) {
                paymentRecorded = true;
                SwingUtils.showInfoMessage(
                    this,
                    "Payment has been recorded successfully.",
                    "Payment Recorded"
                );
                dispose();
            } else {
                showError("Failed to record payment. Please try again.");
            }
            
        } catch (Exception e) {
            showError("Error recording payment: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Check if a payment was recorded
     * 
     * @return true if payment was recorded
     */
    public boolean isPaymentRecorded() {
        return paymentRecorded;
    }
}