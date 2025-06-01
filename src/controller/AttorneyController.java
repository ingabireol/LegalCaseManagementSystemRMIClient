package controller;

import model.Attorney;
import model.Case;
import service.AttorneyService;
import service.CaseService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

/**
 * Controller for attorney-related operations using RMI.
 */
public class AttorneyController {
    private AttorneyService attorneyService;
    private CaseService caseService;
    private Registry registry;
    
    // RMI server configuration
    private static final String RMI_HOST = "127.0.0.1";
    private static final int RMI_PORT = 5555;
    
    /**
     * Constructor - establishes RMI connection
     */
    public AttorneyController() {
        try {
            // Locate RMI registry
            registry = LocateRegistry.getRegistry(RMI_HOST, RMI_PORT);
            
            // Get service stubs
            attorneyService = (AttorneyService) registry.lookup("attorneyService");
            caseService = (CaseService) registry.lookup("caseService");
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to connect to RMI server: " + ex.getMessage());
        }
    }
    
    /**
     * Get all attorneys
     * 
     * @return List of all attorneys
     */
    public List<Attorney> getAllAttorneys() {
        try {
            return attorneyService.findAllAttorneys();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get an attorney by ID
     * 
     * @param id The attorney ID
     * @return The attorney
     */
    public Attorney getAttorneyById(int id) {
        try {
            Attorney searchAttorney = new Attorney();
            searchAttorney.setId(id);
            return attorneyService.findAttorneyById(searchAttorney);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get an attorney by attorney ID
     * 
     * @param attorneyId The attorney ID string
     * @return The attorney
     */
    public Attorney getAttorneyByAttorneyId(String attorneyId) {
        try {
            return attorneyService.findAttorneyByAttorneyId(attorneyId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get an attorney with all cases
     * 
     * @param id The attorney ID
     * @return The attorney with all cases loaded
     */
    public Attorney getAttorneyWithCases(int id) {
        try {
            Attorney searchAttorney = new Attorney();
            searchAttorney.setId(id);
            return attorneyService.getAttorneyWithCases(searchAttorney);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find attorneys by name
     * 
     * @param name Name to search for
     * @return List of matching attorneys
     */
    public List<Attorney> findAttorneysByName(String name) {
        try {
            return attorneyService.findAttorneysByName(name);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find attorneys by specialization
     * 
     * @param specialization Specialization to search for
     * @return List of matching attorneys
     */
    public List<Attorney> findAttorneysBySpecialization(String specialization) {
        try {
            return attorneyService.findAttorneysBySpecialization(specialization);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Create a new attorney
     * 
     * @param attorney The attorney to create
     * @return true if successful
     */
    public boolean createAttorney(Attorney attorney) {
        try {
            Attorney result = attorneyService.createAttorney(attorney);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update an existing attorney
     * 
     * @param attorney The attorney to update
     * @return true if successful
     */
    public boolean updateAttorney(Attorney attorney) {
        try {
            Attorney result = attorneyService.updateAttorney(attorney);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete an attorney
     * 
     * @param attorneyId The attorney ID to delete
     * @return true if successful
     */
    public boolean deleteAttorney(String attorneyId) {
        try {
            Attorney attorney = getAttorneyByAttorneyId(attorneyId);
            if (attorney == null) {
                return false;
            }
            
            Attorney result = attorneyService.deleteAttorney(attorney);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get cases for an attorney
     * 
     * @param attorneyId The attorney ID
     * @return List of cases
     */
    public List<Case> getAttorneyCases(int attorneyId) {
        try {
            return caseService.findCasesByAttorney(attorneyId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find attorneys by case
     * 
     * @param caseId The case ID
     * @return List of attorneys assigned to the case
     */
    public List<Attorney> findAttorneysByCase(int caseId) {
        try {
            return attorneyService.findAttorneysByCase(caseId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}