package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import model.Payment;

/**
 * Remote service interface for Payment operations
 */
public interface PaymentService extends Remote {
    
    /**
     * Creates a new payment
     */
    Payment createPayment(Payment payment) throws RemoteException;
    
    /**
     * Updates an existing payment
     */
    Payment updatePayment(Payment payment) throws RemoteException;
    
    /**
     * Deletes a payment
     */
    Payment deletePayment(Payment payment) throws RemoteException;
    
    /**
     * Finds a payment by ID
     */
    Payment findPaymentById(Payment payment) throws RemoteException;
    
    /**
     * Finds a payment by payment ID
     */
    Payment findPaymentByPaymentId(String paymentId) throws RemoteException;
    
    /**
     * Finds payments by invoice ID
     */
    List<Payment> findPaymentsByInvoice(int invoiceId) throws RemoteException;
    
    /**
     * Finds payments by client ID
     */
    List<Payment> findPaymentsByClient(int clientId) throws RemoteException;
    
    /**
     * Finds payments by payment method
     */
    List<Payment> findPaymentsByMethod(String paymentMethod) throws RemoteException;
    
    /**
     * Finds payments by date range
     */
    List<Payment> findPaymentsByDateRange(LocalDate startDate, LocalDate endDate) throws RemoteException;
    
    /**
     * Gets all payments
     */
    List<Payment> findAllPayments() throws RemoteException;
    
    /**
     * Gets a payment with invoice and client information
     */
    Payment getPaymentWithDetails(Payment payment) throws RemoteException;
    
    /**
     * Gets the total payments received for a client
     */
    BigDecimal getTotalPaymentsByClient(int clientId) throws RemoteException;
    
    /**
     * Gets the total payments received for a date range
     */
    BigDecimal getTotalPaymentsByDateRange(LocalDate startDate, LocalDate endDate) throws RemoteException;
    
    /**
     * Generates the next payment ID
     */
    String generateNextPaymentId() throws RemoteException;
}