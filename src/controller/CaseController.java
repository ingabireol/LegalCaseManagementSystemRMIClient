package controller;

import model.Case;
import model.Client;
import model.Attorney;
import model.Document;
import model.Event;
import model.TimeEntry;
import service.CaseService;
import service.ClientService;
import service.AttorneyService;
import service.DocumentService;
import service.EventService;
import service.TimeEntryService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.time.LocalDate;

/**
 * Controller for case-related operations using RMI.
 */
public class CaseController {
    private CaseService caseService;
    private ClientService clientService;
    private AttorneyService attorneyService;
    private DocumentService documentService;
    private EventService eventService;
    private TimeEntryService timeEntryService;
    private Registry registry;
    
    // RMI server configuration
    private static final String RMI_HOST = "127.0.0.1";
    private static final int RMI_PORT = 5555;
    
    /**
     * Constructor - establishes RMI connection
     */
    public CaseController() {
        try {
            // Locate RMI registry
            registry = LocateRegistry.getRegistry(RMI_HOST, RMI_PORT);
            
            // Get service stubs
            caseService = (CaseService) registry.lookup("caseService");
            clientService = (ClientService) registry.lookup("clientService");
            attorneyService = (AttorneyService) registry.lookup("attorneyService");
            documentService = (DocumentService) registry.lookup("documentService");
            eventService = (EventService) registry.lookup("eventService");
            timeEntryService = (TimeEntryService) registry.lookup("timeEntryService");
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to connect to RMI server: " + ex.getMessage());
        }
    }
    
    /**
     * Get all cases
     * 
     * @return List of all cases
     */
    public List<Case> getAllCases() {
        try {
            return caseService.findAllCases();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get a case by ID
     * 
     * @param id The case ID
     * @return The case
     */
    public Case getCaseById(int id) {
        try {
            Case searchCase = new Case();
            searchCase.setId(id);
            return caseService.findCaseById(searchCase);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get a case by case number
     * 
     * @param caseNumber The case number
     * @return The case
     */
    public Case getCaseByCaseNumber(String caseNumber) {
        try {
            return caseService.findCaseByCaseNumber(caseNumber);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get a case with all details
     * 
     * @param id The case ID
     * @return The case with all details loaded
     */
    public Case getCaseWithDetails(int id) {
        try {
            Case searchCase = new Case();
            searchCase.setId(id);
            return caseService.getCaseWithDetails(searchCase);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find cases by client
     * 
     * @param clientId The client ID
     * @return List of cases
     */
    public List<Case> findCasesByClient(int clientId) {
        try {
            return caseService.findCasesByClient(clientId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find cases by attorney
     * 
     * @param attorneyId The attorney ID
     * @return List of cases
     */
    public List<Case> findCasesByAttorney(int attorneyId) {
        try {
            return caseService.findCasesByAttorney(attorneyId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find cases by status
     * 
     * @param status The status
     * @return List of cases
     */
    public List<Case> findCasesByStatus(String status) {
        try {
            return caseService.findCasesByStatus(status);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find cases by type
     * 
     * @param caseType The case type
     * @return List of cases
     */
    public List<Case> findCasesByType(String caseType) {
        try {
            return caseService.findCasesByType(caseType);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find cases by text search
     * 
     * @param searchText The text to search for
     * @return List of cases
     */
    public List<Case> findCasesByText(String searchText) {
        try {
            return caseService.findCasesByText(searchText);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find cases by date range
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return List of cases
     */
    public List<Case> findCasesByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            return caseService.findCasesByDateRange(startDate, endDate);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Create a new case
     * 
     * @param legalCase The case to create
     * @return true if successful
     */
    public boolean createCase(Case legalCase) {
        try {
            // Set file date if not set
            if (legalCase.getFileDate() == null) {
                legalCase.setFileDate(LocalDate.now());
            }
            
            Case result = caseService.createCase(legalCase);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update an existing case
     * 
     * @param legalCase The case to update
     * @return true if successful
     */
    public boolean updateCase(Case legalCase) {
        try {
            Case result = caseService.updateCase(legalCase);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update case status
     * 
     * @param caseId The case ID
     * @param status The new status
     * @return true if successful
     */
    public boolean updateCaseStatus(int caseId, String status) {
        try {
            Case searchCase = new Case();
            searchCase.setId(caseId);
            Case result = caseService.updateCaseStatus(searchCase, status);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete a case
     * 
     * @param caseId The case ID
     * @return true if successful
     */
    public boolean deleteCase(int caseId) {
        try {
            Case searchCase = new Case();
            searchCase.setId(caseId);
            Case result = caseService.deleteCase(searchCase);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get all documents for a case
     * 
     * @param caseId The case ID
     * @return List of documents
     */
    public List<Document> getCaseDocuments(int caseId) {
        try {
            return documentService.findDocumentsByCase(caseId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get all events for a case
     * 
     * @param caseId The case ID
     * @return List of events
     */
    public List<Event> getCaseEvents(int caseId) {
        try {
            return eventService.findEventsByCase(caseId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get all time entries for a case
     * 
     * @param caseId The case ID
     * @return List of time entries
     */
    public List<TimeEntry> getCaseTimeEntries(int caseId) {
        try {
            return timeEntryService.findTimeEntriesByCase(caseId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get all unbilled time entries for a case
     * 
     * @param caseId The case ID
     * @return List of unbilled time entries
     */
    public List<TimeEntry> getUnbilledTimeEntries(int caseId) {
        try {
            return timeEntryService.findUnbilledTimeEntriesByCase(caseId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get total hours for a case
     * 
     * @param caseId The case ID
     * @return Total hours
     */
    public double getTotalHours(int caseId) {
        try {
            return timeEntryService.getTotalHoursByCase(caseId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0.0;
        }
    }
    
    /**
     * Get attorneys assigned to a case
     * 
     * @param caseId The case ID
     * @return List of attorneys
     */
    public List<Attorney> getCaseAttorneys(int caseId) {
        try {
            return attorneyService.findAttorneysByCase(caseId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Assign an attorney to a case
     * 
     * @param caseId The case ID
     * @param attorneyId The attorney ID
     * @return true if successful
     */
    public boolean assignAttorneyToCase(int caseId, int attorneyId) {
        try {
            return caseService.assignAttorneyToCase(caseId, attorneyId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get active cases
     * 
     * @return List of active cases
     */
    public List<Case> getActiveCases() {
        try {
            return caseService.getActiveCases();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Generate next case number
     * 
     * @return Next case number
     */
    public String generateNextCaseNumber() {
        try {
            return caseService.generateNextCaseNumber();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "CASE" + System.currentTimeMillis();
        }
    }
}