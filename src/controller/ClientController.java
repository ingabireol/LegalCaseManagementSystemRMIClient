package controller;

import model.Client;
import model.Case;
import service.ClientService;
import service.CaseService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.time.LocalDate;

/**
 * Controller for client-related operations using RMI.
 */
public class ClientController {
    private ClientService clientService;
    private CaseService caseService;
    private Registry registry;
    
    // RMI server configuration
    private static final String RMI_HOST = "127.0.0.1";
    private static final int RMI_PORT = 5555;
    
    /**
     * Constructor - establishes RMI connection
     */
    public ClientController() {
        try {
            // Locate RMI registry
            registry = LocateRegistry.getRegistry(RMI_HOST, RMI_PORT);
            
            // Get service stubs
            clientService = (ClientService) registry.lookup("clientService");
            caseService = (CaseService) registry.lookup("caseService");
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to connect to RMI server: " + ex.getMessage());
        }
    }
    
    /**
     * Get all clients
     * 
     * @return List of all clients
     */
    public List<Client> getAllClients() {
        try {
            return clientService.findAllClients();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get a client by ID
     * 
     * @param id The client ID
     * @return The client
     */
    public Client getClientById(int id) {
        try {
            Client searchClient = new Client();
            searchClient.setId(id);
            return clientService.findClientById(searchClient);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get a client by client ID
     * 
     * @param clientId The client ID string
     * @return The client
     */
    public Client getClientByClientId(String clientId) {
        try {
            return clientService.findClientByClientId(clientId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get a client with all cases
     * 
     * @param id The client ID
     * @return The client with all cases loaded
     */
    public Client getClientWithCases(int id) {
        try {
            Client searchClient = new Client();
            searchClient.setId(id);
            return clientService.getClientWithCases(searchClient);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find clients by name
     * 
     * @param name Name to search for
     * @return List of matching clients
     */
    public List<Client> findClientsByName(String name) {
        try {
            return clientService.findClientsByName(name);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find clients by type
     * 
     * @param clientType Type to search for
     * @return List of matching clients
     */
    public List<Client> findClientsByType(String clientType) {
        try {
            return clientService.findClientsByType(clientType);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Create a new client
     * 
     * @param client The client to create
     * @return true if successful
     */
    public boolean createClient(Client client) {
        try {
            // Set registration date if not set
            if (client.getRegistrationDate() == null) {
                client.setRegistrationDate(LocalDate.now());
            }
            
            Client result = clientService.createClient(client);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update an existing client
     * 
     * @param client The client to update
     * @return true if successful
     */
    public boolean updateClient(Client client) {
        try {
            Client result = clientService.updateClient(client);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete a client
     * 
     * @param clientId The client ID to delete
     * @return true if successful
     */
    public boolean deleteClient(String clientId) {
        try {
            // First check if client has cases
            Client client = getClientByClientId(clientId);
            if (client == null) {
                return false;
            }
            
            List<Case> cases = getClientCases(client.getId());
            if (cases != null && !cases.isEmpty()) {
                return false; // Cannot delete client with cases
            }
            
            Client result = clientService.deleteClient(client);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get cases for a client
     * 
     * @param clientId The client ID
     * @return List of cases
     */
    public List<Case> getClientCases(int clientId) {
        try {
            return caseService.findCasesByClient(clientId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find client by email
     * 
     * @param email The email to search for
     * @return The client if found, null otherwise
     */
    public Client findClientByEmail(String email) {
        try {
            return clientService.findClientByEmail(email);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}