package controller;

import model.Invoice;
import model.Payment;
import model.TimeEntry;
import service.InvoiceService;
import service.PaymentService;
import service.TimeEntryService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * Controller for invoice-related operations using RMI.
 */
public class InvoiceController {
    private InvoiceService invoiceService;
    private PaymentService paymentService;
    private TimeEntryService timeEntryService;
    private Registry registry;
    
    // RMI server configuration
    private static final String RMI_HOST = "127.0.0.1";
    private static final int RMI_PORT = 5555;
    
    /**
     * Constructor - establishes RMI connection
     */
    public InvoiceController() {
        try {
            // Locate RMI registry
            registry = LocateRegistry.getRegistry(RMI_HOST, RMI_PORT);
            
            // Get service stubs
            invoiceService = (InvoiceService) registry.lookup("invoiceService");
            paymentService = (PaymentService) registry.lookup("paymentService");
            timeEntryService = (TimeEntryService) registry.lookup("timeEntryService");
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to connect to RMI server: " + ex.getMessage());
        }
    }
    
    /**
     * Get all invoices
     * 
     * @return List of all invoices
     */
    public List<Invoice> getAllInvoices() {
        try {
            return invoiceService.findAllInvoices();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get an invoice by ID
     * 
     * @param id The invoice ID
     * @return The invoice
     */
    public Invoice getInvoiceById(int id) {
        try {
            Invoice searchInvoice = new Invoice();
            searchInvoice.setId(id);
            return invoiceService.findInvoiceById(searchInvoice);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get an invoice by invoice number
     * 
     * @param invoiceNumber The invoice number
     * @return The invoice
     */
    public Invoice getInvoiceByInvoiceNumber(String invoiceNumber) {
        try {
            return invoiceService.findInvoiceByInvoiceNumber(invoiceNumber);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get an invoice with all details
     * 
     * @param id The invoice ID
     * @return The invoice with all details loaded
     */
    public Invoice getInvoiceWithDetails(int id) {
        try {
            Invoice searchInvoice = new Invoice();
            searchInvoice.setId(id);
            return invoiceService.getInvoiceWithDetails(searchInvoice);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find invoices by client
     * 
     * @param clientId The client ID
     * @return List of invoices for the client
     */
    public List<Invoice> findInvoicesByClient(int clientId) {
        try {
            return invoiceService.findInvoicesByClient(clientId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find invoices by case
     * 
     * @param caseId The case ID
     * @return List of invoices for the case
     */
    public List<Invoice> findInvoicesByCase(int caseId) {
        try {
            return invoiceService.findInvoicesByCase(caseId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find invoices by status
     * 
     * @param status The status
     * @return List of invoices with the status
     */
    public List<Invoice> findInvoicesByStatus(String status) {
        try {
            return invoiceService.findInvoicesByStatus(status);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find invoices by date range
     * 
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @return List of invoices in the date range
     */
    public List<Invoice> findInvoicesByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            return invoiceService.findInvoicesByDateRange(startDate, endDate);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find overdue invoices
     * 
     * @return List of overdue invoices
     */
    public List<Invoice> findOverdueInvoices() {
        try {
            return invoiceService.findOverdueInvoices();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Create a new invoice
     * 
     * @param invoice The invoice to create
     * @return true if successful
     */
    public boolean createInvoice(Invoice invoice) {
        try {
            // Set issue date and due date if not set
            if (invoice.getIssueDate() == null) {
                invoice.setIssueDate(LocalDate.now());
            }
            
            if (invoice.getDueDate() == null) {
                invoice.setDueDate(invoice.getIssueDate().plusDays(30)); // Default due date: 30 days
            }
            
            Invoice result = invoiceService.createInvoice(invoice);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Create a new invoice from unbilled time entries
     * 
     * @param caseId The case ID
     * @param dueDate The due date
     * @return The created invoice, or null if creation failed
     */
    public Invoice createInvoiceFromTimeEntries(int caseId, LocalDate dueDate) {
        try {
            // Generate invoice number
            String invoiceNumber = generateNextInvoiceNumber();
            
            return invoiceService.createInvoiceFromUnbilledTimeEntries(caseId, invoiceNumber, dueDate);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Update an existing invoice
     * 
     * @param invoice The invoice to update
     * @return true if successful
     */
    public boolean updateInvoice(Invoice invoice) {
        try {
            Invoice result = invoiceService.updateInvoice(invoice);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update invoice status
     * 
     * @param invoiceId The invoice ID
     * @param status The new status
     * @return true if successful
     */
    public boolean updateInvoiceStatus(int invoiceId, String status) {
        try {
            Invoice searchInvoice = new Invoice();
            searchInvoice.setId(invoiceId);
            Invoice result = invoiceService.updateInvoiceStatus(searchInvoice, status);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete an invoice
     * 
     * @param invoiceId The invoice ID
     * @return true if successful
     */
    public boolean deleteInvoice(int invoiceId) {
        try {
            Invoice searchInvoice = new Invoice();
            searchInvoice.setId(invoiceId);
            Invoice result = invoiceService.deleteInvoice(searchInvoice);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update the paid amount for an invoice
     * 
     * @param invoiceId The invoice ID
     * @return true if successful
     */
    public boolean updateInvoicePaidAmount(int invoiceId) {
        try {
            Invoice result = invoiceService.updateInvoicePaidAmount(invoiceId);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get time entries for an invoice
     * 
     * @param invoiceId The invoice ID
     * @return List of time entries
     */
    public List<TimeEntry> getInvoiceTimeEntries(int invoiceId) {
        try {
            return timeEntryService.findTimeEntriesByInvoice(invoiceId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get payments for an invoice
     * 
     * @param invoiceId The invoice ID
     * @return List of payments
     */
    public List<Payment> getInvoicePayments(int invoiceId) {
        try {
            return paymentService.findPaymentsByInvoice(invoiceId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Record a payment for an invoice
     * 
     * @param payment The payment to record
     * @return true if successful
     */
    public boolean recordPayment(Payment payment) {
        try {
            // Generate payment ID if not set
            if (payment.getPaymentId() == null || payment.getPaymentId().isEmpty()) {
                payment.setPaymentId(paymentService.generateNextPaymentId());
            }
            
            // Set payment date if not set
            if (payment.getPaymentDate() == null) {
                payment.setPaymentDate(LocalDate.now());
            }
            
            Payment result = paymentService.createPayment(payment);
            
            if (result != null) {
                // Update invoice paid amount
                updateInvoicePaidAmount(payment.getInvoice().getId());
                return true;
            }
            
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete a payment
     * 
     * @param paymentId The payment ID
     * @return true if successful
     */
    public boolean deletePayment(int paymentId) {
        try {
            Payment searchPayment = new Payment();
            searchPayment.setId(paymentId);
            Payment result = paymentService.deletePayment(searchPayment);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get available payment methods
     * 
     * @return Array of payment methods
     */
    public String[] getPaymentMethods() {
        return new String[] {
            "Cash", "Check", "Credit Card", "Bank Transfer", "Wire Transfer", "PayPal", "Other"
        };
    }

    /**
     * Generate next invoice number
     * 
     * @return Next invoice number
     */
    public String generateNextInvoiceNumber() {
        try {
            return invoiceService.generateNextInvoiceNumber();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "INV" + System.currentTimeMillis();
        }
    }
}