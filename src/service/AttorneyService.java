package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Attorney;

/**
 * Remote service interface for Attorney operations
 */
public interface AttorneyService extends Remote {
    
    /**
     * Creates a new attorney
     */
    Attorney createAttorney(Attorney attorney) throws RemoteException;
    
    /**
     * Updates an existing attorney
     */
    Attorney updateAttorney(Attorney attorney) throws RemoteException;
    
    /**
     * Deletes an attorney
     */
    Attorney deleteAttorney(Attorney attorney) throws RemoteException;
    
    /**
     * Finds an attorney by ID
     */
    Attorney findAttorneyById(Attorney attorney) throws RemoteException;
    
    /**
     * Finds an attorney by attorney ID
     */
    Attorney findAttorneyByAttorneyId(String attorneyId) throws RemoteException;
    
    /**
     * Finds attorneys by name
     */
    List<Attorney> findAttorneysByName(String name) throws RemoteException;
    
    /**
     * Finds attorneys by specialization
     */
    List<Attorney> findAttorneysBySpecialization(String specialization) throws RemoteException;
    
    /**
     * Finds attorneys by case ID
     */
    List<Attorney> findAttorneysByCase(int caseId) throws RemoteException;
    
    /**
     * Gets all attorneys
     */
    List<Attorney> findAllAttorneys() throws RemoteException;
    
    /**
     * Gets an attorney with all their cases loaded
     */
    Attorney getAttorneyWithCases(Attorney attorney) throws RemoteException;
}