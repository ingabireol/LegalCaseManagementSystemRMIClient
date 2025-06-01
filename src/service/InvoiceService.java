package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;
import model.Invoice;

/**
 * Remote service interface for Invoice operations
 */
public interface InvoiceService extends Remote {
    
    /**
     * Creates a new invoice
     */
    Invoice createInvoice(Invoice invoice) throws RemoteException;
    
    /**
     * Updates an existing invoice
     */
    Invoice updateInvoice(Invoice invoice) throws RemoteException;
    
    /**
     * Deletes an invoice
     */
    Invoice deleteInvoice(Invoice invoice) throws RemoteException;
    
    /**
     * Updates the status of an invoice
     */
    Invoice updateInvoiceStatus(Invoice invoice, String status) throws RemoteException;
    
    /**
     * Finds an invoice by ID
     */
    Invoice findInvoiceById(Invoice invoice) throws RemoteException;
    
    /**
     * Finds an invoice by invoice number
     */
    Invoice findInvoiceByInvoiceNumber(String invoiceNumber) throws RemoteException;
    
    /**
     * Finds invoices by client ID
     */
    List<Invoice> findInvoicesByClient(int clientId) throws RemoteException;
    
    /**
     * Finds invoices by case ID
     */
    List<Invoice> findInvoicesByCase(int caseId) throws RemoteException;
    
    /**
     * Finds invoices by status
     */
    List<Invoice> findInvoicesByStatus(String status) throws RemoteException;
    
    /**
     * Finds invoices by date range
     */
    List<Invoice> findInvoicesByDateRange(LocalDate startDate, LocalDate endDate) throws RemoteException;
    
    /**
     * Finds overdue invoices
     */
    List<Invoice> findOverdueInvoices() throws RemoteException;
    
    /**
     * Gets all invoices
     */
    List<Invoice> findAllInvoices() throws RemoteException;
    
    /**
     * Gets an invoice with all related details (client, case, time entries, payments)
     */
    Invoice getInvoiceWithDetails(Invoice invoice) throws RemoteException;
    
    /**
     * Updates the paid amount for an invoice based on payments
     */
    Invoice updateInvoicePaidAmount(int invoiceId) throws RemoteException;
    
    /**
     * Creates a new invoice from unbilled time entries for a case
     */
    Invoice createInvoiceFromUnbilledTimeEntries(int caseId, String invoiceNumber, LocalDate dueDate) throws RemoteException;
    
    /**
     * Generates the next invoice number
     */
    String generateNextInvoiceNumber() throws RemoteException;
}