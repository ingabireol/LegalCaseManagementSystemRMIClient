package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a payment made by a client for an invoice.
 */
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    
    private String paymentId;
    
    private Invoice invoice;
    
    private Client client;
    
    private LocalDate paymentDate;
    
    private BigDecimal amount;
    
    private String paymentMethod;
    
    private String reference;
    private String notes;
    
    /**
     * Default constructor
     */
    public Payment() {
        this.paymentDate = LocalDate.now();
    }
    
    /**
     * Constructor with essential fields
     */
    public Payment(String paymentId, Invoice invoice, BigDecimal amount, String paymentMethod) {
        this();
        this.paymentId = paymentId;
        this.invoice = invoice;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    
    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { 
        this.invoice = invoice;
        if (invoice != null) {
            this.client = invoice.getClient();
        }
    }
    
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { 
        this.amount = amount;
        // Update invoice payment amount if invoice is set
        if (invoice != null) {
            invoice.recalculateAmountPaid();
        }
    }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    /**
     * Get formatted display text for the payment
     */
    public String getDisplayText() {
        return paymentMethod + " payment of " + amount + " on " + paymentDate + 
               (reference != null && !reference.isEmpty() ? " (Ref: " + reference + ")" : "");
    }
    @Override
    public String toString() {
        return "Payment [id=" + id + ", paymentId=" + paymentId + ", invoiceId=" + 
               (invoice != null ? invoice.getId() : "null") +
               ", amount=" + amount + ", date=" + paymentDate + ", method=" + paymentMethod + "]";
    }
}