package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Client;

/**
 * Remote service interface for Client operations
 */
public interface ClientService extends Remote {
    
    /**
     * Creates a new client
     */
    Client createClient(Client client) throws RemoteException;
    
    /**
     * Updates an existing client
     */
    Client updateClient(Client client) throws RemoteException;
    
    /**
     * Deletes a client
     */
    Client deleteClient(Client client) throws RemoteException;
    
    /**
     * Finds a client by ID
     */
    Client findClientById(Client client) throws RemoteException;
    
    /**
     * Finds a client by client ID
     */
    Client findClientByClientId(String clientId) throws RemoteException;
    
    /**
     * Finds clients by name
     */
    List<Client> findClientsByName(String name) throws RemoteException;
    
    /**
     * Finds a client by email
     */
    Client findClientByEmail(String email) throws RemoteException;
    
    /**
     * Finds clients by type
     */
    List<Client> findClientsByType(String clientType) throws RemoteException;
    
    /**
     * Gets all clients
     */
    List<Client> findAllClients() throws RemoteException;
    
    /**
     * Gets a client with all their cases loaded
     */
    Client getClientWithCases(Client client) throws RemoteException;
}