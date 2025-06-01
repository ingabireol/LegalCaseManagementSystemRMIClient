package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;
import model.Case;

/**
 * Remote service interface for Case operations
 */
public interface CaseService extends Remote {
    
    /**
     * Creates a new case
     */
    Case createCase(Case legalCase) throws RemoteException;
    
    /**
     * Updates an existing case
     */
    Case updateCase(Case legalCase) throws RemoteException;
    
    /**
     * Deletes a case
     */
    Case deleteCase(Case legalCase) throws RemoteException;
    
    /**
     * Updates the status of a case
     */
    Case updateCaseStatus(Case legalCase, String status) throws RemoteException;
    
    /**
     * Finds a case by ID
     */
    Case findCaseById(Case legalCase) throws RemoteException;
    
    /**
     * Finds a case by case number
     */
    Case findCaseByCaseNumber(String caseNumber) throws RemoteException;
    
    /**
     * Finds cases by title or description
     */
    List<Case> findCasesByText(String searchText) throws RemoteException;
    
    /**
     * Finds cases by client ID
     */
    List<Case> findCasesByClient(int clientId) throws RemoteException;
    
    /**
     * Finds cases by attorney ID
     */
    List<Case> findCasesByAttorney(int attorneyId) throws RemoteException;
    
    /**
     * Finds cases by status
     */
    List<Case> findCasesByStatus(String status) throws RemoteException;
    
    /**
     * Finds cases by case type
     */
    List<Case> findCasesByType(String caseType) throws RemoteException;
    
    /**
     * Finds cases by filing date range
     */
    List<Case> findCasesByDateRange(LocalDate startDate, LocalDate endDate) throws RemoteException;
    
    /**
     * Gets all cases
     */
    List<Case> findAllCases() throws RemoteException;
    
    /**
     * Gets a case with all its details including client, attorneys, documents, events, and time entries
     */
    Case getCaseWithDetails(Case legalCase) throws RemoteException;
    
    /**
     * Assigns an attorney to a case
     */
    boolean assignAttorneyToCase(int caseId, int attorneyId) throws RemoteException;
    
    /**
     * Removes an attorney from a case
     */
    boolean removeAttorneyFromCase(int caseId, int attorneyId) throws RemoteException;
    
    /**
     * Gets cases that are due for review (based on filing date)
     */
    List<Case> getCasesDueForReview(int daysBefore) throws RemoteException;
    
    /**
     * Gets active cases (not closed or cancelled)
     */
    List<Case> getActiveCases() throws RemoteException;
    
    /**
     * Gets cases with upcoming deadlines
     */
    List<Case> getCasesWithUpcomingDeadlines(int daysAhead) throws RemoteException;
    
    /**
     * Generates the next case number
     */
    String generateNextCaseNumber() throws RemoteException;
    
    /**
     * Gets case statistics for dashboard
     */
    java.util.Map<String, Object> getCaseStatistics() throws RemoteException;
}