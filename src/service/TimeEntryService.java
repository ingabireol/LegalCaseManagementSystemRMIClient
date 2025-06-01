package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import model.TimeEntry;

/**
 * Remote service interface for TimeEntry operations
 */
public interface TimeEntryService extends Remote {
    
    /**
     * Creates a new time entry
     */
    TimeEntry createTimeEntry(TimeEntry timeEntry) throws RemoteException;
    
    /**
     * Updates an existing time entry
     */
    TimeEntry updateTimeEntry(TimeEntry timeEntry) throws RemoteException;
    
    /**
     * Deletes a time entry
     */
    TimeEntry deleteTimeEntry(TimeEntry timeEntry) throws RemoteException;
    
    /**
     * Marks a time entry as billed
     */
    TimeEntry markTimeEntryAsBilled(TimeEntry timeEntry, int invoiceId) throws RemoteException;
    
    /**
     * Finds a time entry by ID
     */
    TimeEntry findTimeEntryById(TimeEntry timeEntry) throws RemoteException;
    
    /**
     * Finds a time entry by entry ID
     */
    TimeEntry findTimeEntryByEntryId(String entryId) throws RemoteException;
    
    /**
     * Finds time entries by case ID
     */
    List<TimeEntry> findTimeEntriesByCase(int caseId) throws RemoteException;
    
    /**
     * Finds time entries by attorney ID
     */
    List<TimeEntry> findTimeEntriesByAttorney(int attorneyId) throws RemoteException;
    
    /**
     * Finds time entries by date range
     */
    List<TimeEntry> findTimeEntriesByDateRange(LocalDate startDate, LocalDate endDate) throws RemoteException;
    
    /**
     * Finds unbilled time entries for a case
     */
    List<TimeEntry> findUnbilledTimeEntriesByCase(int caseId) throws RemoteException;
    
    /**
     * Finds time entries by invoice ID
     */
    List<TimeEntry> findTimeEntriesByInvoice(int invoiceId) throws RemoteException;
    
    /**
     * Gets all time entries
     */
    List<TimeEntry> findAllTimeEntries() throws RemoteException;
    
    /**
     * Gets a time entry with case and attorney information
     */
    TimeEntry getTimeEntryWithDetails(TimeEntry timeEntry) throws RemoteException;
    
    /**
     * Gets the total hours worked on a case
     */
    double getTotalHoursByCase(int caseId) throws RemoteException;
    
    /**
     * Gets the total billable amount for a case
     */
    BigDecimal getTotalAmountByCase(int caseId) throws RemoteException;
}