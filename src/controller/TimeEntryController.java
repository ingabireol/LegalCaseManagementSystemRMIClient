package controller;

import model.TimeEntry;
import model.Attorney;
import model.Case;
import service.TimeEntryService;
import service.AttorneyService;
import service.CaseService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * Controller for time entry operations using RMI.
 */
public class TimeEntryController {
    private TimeEntryService timeEntryService;
    private AttorneyService attorneyService;
    private CaseService caseService;
    private Registry registry;
    
    // RMI server configuration
    private static final String RMI_HOST = "127.0.0.1";
    private static final int RMI_PORT = 5555;
    
    /**
     * Constructor - establishes RMI connection
     */
    public TimeEntryController() {
        try {
            // Locate RMI registry
            registry = LocateRegistry.getRegistry(RMI_HOST, RMI_PORT);
            
            // Get service stubs
            timeEntryService = (TimeEntryService) registry.lookup("timeEntryService");
            attorneyService = (AttorneyService) registry.lookup("attorneyService");
            caseService = (CaseService) registry.lookup("caseService");
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to connect to RMI server: " + ex.getMessage());
        }
    }
    
    /**
     * Get all time entries
     * 
     * @return List of all time entries
     */
    public List<TimeEntry> getAllTimeEntries() {
        try {
            return timeEntryService.findAllTimeEntries();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get a time entry by ID
     * 
     * @param id The time entry ID
     * @return The time entry
     */
    public TimeEntry getTimeEntryById(int id) {
        try {
            TimeEntry searchEntry = new TimeEntry();
            searchEntry.setId(id);
            return timeEntryService.findTimeEntryById(searchEntry);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get a time entry by entry ID
     * 
     * @param entryId The entry ID string
     * @return The time entry
     */
    public TimeEntry getTimeEntryByEntryId(String entryId) {
        try {
            return timeEntryService.findTimeEntryByEntryId(entryId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get a time entry with details
     * 
     * @param id The time entry ID
     * @return The time entry with details loaded
     */
    public TimeEntry getTimeEntryWithDetails(int id) {
        try {
            TimeEntry searchEntry = new TimeEntry();
            searchEntry.setId(id);
            return timeEntryService.getTimeEntryWithDetails(searchEntry);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find time entries by case
     * 
     * @param caseId The case ID
     * @return List of time entries for the case
     */
    public List<TimeEntry> findTimeEntriesByCase(int caseId) {
        try {
            return timeEntryService.findTimeEntriesByCase(caseId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find time entries by attorney
     * 
     * @param attorneyId The attorney ID
     * @return List of time entries for the attorney
     */
    public List<TimeEntry> findTimeEntriesByAttorney(int attorneyId) {
        try {
            return timeEntryService.findTimeEntriesByAttorney(attorneyId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find time entries by date range
     * 
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @return List of time entries in the date range
     */
    public List<TimeEntry> findTimeEntriesByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            return timeEntryService.findTimeEntriesByDateRange(startDate, endDate);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find unbilled time entries for a case
     * 
     * @param caseId The case ID
     * @return List of unbilled time entries
     */
    public List<TimeEntry> findUnbilledTimeEntries(int caseId) {
        try {
            return timeEntryService.findUnbilledTimeEntriesByCase(caseId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find time entries by invoice
     * 
     * @param invoiceId The invoice ID
     * @return List of time entries for the invoice
     */
    public List<TimeEntry> findTimeEntriesByInvoice(int invoiceId) {
        try {
            return timeEntryService.findTimeEntriesByInvoice(invoiceId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Create a new time entry
     * 
     * @param timeEntry The time entry to create
     * @return true if successful
     */
    public boolean createTimeEntry(TimeEntry timeEntry) {
        try {
            // Set entry date if not set
            if (timeEntry.getEntryDate() == null) {
                timeEntry.setEntryDate(LocalDate.now());
            }
            
            // Set hourly rate based on attorney if not set
            if (timeEntry.getHourlyRate() == null && timeEntry.getAttorney() != null) {
                Attorney attorney = timeEntry.getAttorney();
                if (attorney.getHourlyRate() > 0) {
                    timeEntry.setHourlyRate(new BigDecimal(attorney.getHourlyRate()));
                }
            }
            
            TimeEntry result = timeEntryService.createTimeEntry(timeEntry);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update an existing time entry
     * 
     * @param timeEntry The time entry to update
     * @return true if successful
     */
    public boolean updateTimeEntry(TimeEntry timeEntry) {
        try {
            TimeEntry result = timeEntryService.updateTimeEntry(timeEntry);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mark a time entry as billed
     * 
     * @param timeEntryId The time entry ID
     * @param invoiceId The invoice ID
     * @return true if successful
     */
    public boolean markTimeEntryAsBilled(int timeEntryId, int invoiceId) {
        try {
            TimeEntry searchEntry = new TimeEntry();
            searchEntry.setId(timeEntryId);
            TimeEntry result = timeEntryService.markTimeEntryAsBilled(searchEntry, invoiceId);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete a time entry
     * 
     * @param timeEntryId The time entry ID
     * @return true if successful
     */
    public boolean deleteTimeEntry(int timeEntryId) {
        try {
            TimeEntry searchEntry = new TimeEntry();
            searchEntry.setId(timeEntryId);
            TimeEntry result = timeEntryService.deleteTimeEntry(searchEntry);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get total hours for a case
     * 
     * @param caseId The case ID
     * @return Total hours
     */
    public double getTotalHoursByCase(int caseId) {
        try {
            return timeEntryService.getTotalHoursByCase(caseId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0.0;
        }
    }
    
    /**
     * Get total billable amount for a case
     * 
     * @param caseId The case ID
     * @return Total billable amount
     */
    public BigDecimal getTotalAmountByCase(int caseId) {
        try {
            return timeEntryService.getTotalAmountByCase(caseId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Get available activity codes
     * 
     * @return Array of activity codes
     */
    public String[] getActivityCodes() {
        return new String[] {
            "RES", // Research
            "DRA", // Drafting
            "REV", // Review
            "COM", // Communication
            "MEE", // Meeting
            "HEA", // Hearing
            "TRI", // Trial
            "DEP", // Deposition
            "TRA", // Travel
            "NEG", // Negotiation
            "OTH"  // Other
        };
    }

    /**
     * Get all time entries for a specific case
     * 
     * @param caseId The case ID
     * @return List of time entries for the case
     */
    public List<TimeEntry> getCaseTimeEntries(int caseId) {
        if (caseId <= 0) {
            throw new IllegalArgumentException("Invalid case ID: " + caseId);
        }
        
        try {
            // Validate that the case exists
            Case searchCase = new Case();
            searchCase.setId(caseId);
            Case legalCase = caseService.findCaseById(searchCase);
            if (legalCase == null) {
                throw new IllegalArgumentException("Case not found with ID: " + caseId);
            }
            
            // Use the RMI service to fetch time entries for this case
            return timeEntryService.findTimeEntriesByCase(caseId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Get unbilled time entries for a specific case
     * 
     * @param caseId The case ID
     * @return List of unbilled time entries for the case
     */
    public List<TimeEntry> getUnbilledTimeEntries(int caseId) {
        if (caseId <= 0) {
            throw new IllegalArgumentException("Invalid case ID: " + caseId);
        }
        
        try {
            // Validate that the case exists
            Case searchCase = new Case();
            searchCase.setId(caseId);
            Case legalCase = caseService.findCaseById(searchCase);
            if (legalCase == null) {
                throw new IllegalArgumentException("Case not found with ID: " + caseId);
            }
            
            // Use the RMI service to fetch unbilled time entries for this case
            return timeEntryService.findUnbilledTimeEntriesByCase(caseId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}