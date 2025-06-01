package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * Represents a time entry for billing purposes.
 * Records time spent on a case by an attorney.
 */
public class TimeEntry implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    
    private String entryId;
    
    private Case associatedCase;
    
    private Attorney attorney;
    
    private LocalDate entryDate;
    
    private double hours;
    private String description;
    
    private String activityCode;
    
    private BigDecimal hourlyRate;
    
    private boolean billed;
    
    private Invoice invoice; // If billed, reference to invoice
    
    /**
     * Default constructor
     */
    public TimeEntry() {
        this.entryDate = LocalDate.now();
        this.billed = false;
    }
    
    /**
     * Constructor with essential fields
     */
    public TimeEntry(String entryId, Case associatedCase, Attorney attorney, double hours, String description) {
        this();
        this.entryId = entryId;
        this.associatedCase = associatedCase;
        this.attorney = attorney;
        this.hours = hours;
        this.description = description;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getEntryId() { return entryId; }
    public void setEntryId(String entryId) { this.entryId = entryId; }
    
    public Case getCase() { return associatedCase; }
    public void setCase(Case associatedCase) { this.associatedCase = associatedCase; }
    
    public Attorney getAttorney() { return attorney; }
    public void setAttorney(Attorney attorney) { 
        this.attorney = attorney;
        // Update hourly rate if not set
        if (attorney != null && this.hourlyRate == null) {
            this.hourlyRate = new BigDecimal(attorney.getHourlyRate());
        }
    }
    
    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }
    
    public double getHours() { return hours; }
    public void setHours(double hours) { this.hours = hours; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getActivityCode() { return activityCode; }
    public void setActivityCode(String activityCode) { this.activityCode = activityCode; }
    
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }
    
    public boolean isBilled() { return billed; }
    public void setBilled(boolean billed) { this.billed = billed; }
    
    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { 
        this.invoice = invoice;
        if (invoice != null) {
            this.billed = true;
        }
    }
    
    /**
     * Calculate the amount for this time entry
     */
    public BigDecimal getAmount() {
        if (hourlyRate == null) {
            return BigDecimal.ZERO;
        }
        return hourlyRate.multiply(new BigDecimal(hours));
    }
    
    /**
     * Format hours as a string with quarter-hour precision
     */
    public String getFormattedHours() {
        int wholeHours = (int) this.hours;
        int minutes = (int) Math.round((this.hours - wholeHours) * 60);
        return String.format("%d:%02d", wholeHours, minutes);
    }
    
    @Override
    public String toString() {
        return "TimeEntry [id=" + id + ", entryId=" + entryId + ", case=" + 
               (associatedCase != null ? associatedCase.getCaseNumber() : "null") + 
               ", attorney=" + (attorney != null ? attorney.getFullName() : "null") + 
               ", hours=" + hours + ", billed=" + billed + "]";
    }
}