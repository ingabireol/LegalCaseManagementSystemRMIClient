package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a client in the legal system.
 * Can be either an individual or an organization.
 */
public class Client implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    
    private String clientId;
    
    private String name;
    
    private String contactPerson;
    
    private String email;
    private String phone;
    private String address;
    
    private String clientType; // Individual or Organization
    
    private LocalDate registrationDate;
    
    private List<Case> cases = new ArrayList<>();
    
    /**
     * Default constructor
     */
    public Client() {
        this.registrationDate = LocalDate.now();
    }
    
    /**
     * Constructor with essential fields
     */
    public Client(String clientId, String name, String email, String clientType) {
        this();
        this.clientId = clientId;
        this.name = name;
        this.email = email;
        this.clientType = clientType;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getClientType() { return clientType; }
    public void setClientType(String clientType) { this.clientType = clientType; }
    
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
    
    public List<Case> getCases() { return cases; }
    public void setCases(List<Case> cases) { this.cases = cases; }
    
    /**
     * Add a case to this client
     */
    public void addCase(Case legalCase) {
        this.cases.add(legalCase);
        legalCase.setClient(this);
    }
    
    /**
     * Check if this client is an individual
     */
    public boolean isIndividual() {
        return "Individual".equalsIgnoreCase(clientType);
    }
    
    /**
     * Check if this client is an organization
     */
    public boolean isOrganization() {
        return "Organization".equalsIgnoreCase(clientType);
    }
    
    /**
     * Gets the display name for the client based on type
     */
    public String getDisplayName() {
        if (isOrganization() && contactPerson != null && !contactPerson.isEmpty()) {
            return name + " (Org, Contact: " + contactPerson + ")";
        } else if (isOrganization()) {
            return name + " (Organization)";
        } else {
            return name + " (Individual)";
        }
    }
    
    @Override
    public String toString() {
        return "Client [id=" + id + ", clientId=" + clientId + ", name=" + name + 
               ", type=" + clientType + ", email=" + email + "]";
    }
}