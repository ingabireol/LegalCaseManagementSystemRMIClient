package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a legal case in the system.
 * Contains case details and relationships to clients, attorneys, documents, etc.
 */
public class Case implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    
    private String caseNumber;
    
    private String title;
    
    private String caseType;
    
    private String status;
    private String description;
    
    private LocalDate fileDate;
    
    private LocalDate closingDate;
    
    private String court;
    private String judge;
    
    private String opposingParty;
    
    private String opposingCounsel;
    
    private Client client;
    
    private List<Attorney> attorneys = new ArrayList<>();
    
    private List<Document> documents = new ArrayList<>();
    
    private List<Event> events = new ArrayList<>();
    
    private List<TimeEntry> timeEntries = new ArrayList<>();
    
    /**
     * Default constructor
     */
    public Case() {
        this.fileDate = LocalDate.now();
        this.status = "Open";
    }
    
    /**
     * Constructor with essential fields
     */
    public Case(String caseNumber, String title, String caseType, Client client) {
        this();
        this.caseNumber = caseNumber;
        this.title = title;
        this.caseType = caseType;
        this.client = client;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getCaseType() { return caseType; }
    public void setCaseType(String caseType) { this.caseType = caseType; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDate getFileDate() { return fileDate; }
    public void setFileDate(LocalDate fileDate) { this.fileDate = fileDate; }
    
    public LocalDate getClosingDate() { return closingDate; }
    public void setClosingDate(LocalDate closingDate) { this.closingDate = closingDate; }
    
    public String getCourt() { return court; }
    public void setCourt(String court) { this.court = court; }
    
    public String getJudge() { return judge; }
    public void setJudge(String judge) { this.judge = judge; }
    
    public String getOpposingParty() { return opposingParty; }
    public void setOpposingParty(String opposingParty) { this.opposingParty = opposingParty; }
    
    public String getOpposingCounsel() { return opposingCounsel; }
    public void setOpposingCounsel(String opposingCounsel) { this.opposingCounsel = opposingCounsel; }
    
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    
    public List<Attorney> getAttorneys() { return attorneys; }
    public void setAttorneys(List<Attorney> attorneys) { this.attorneys = attorneys; }
    
    public List<Document> getDocuments() { return documents; }
    public void setDocuments(List<Document> documents) { this.documents = documents; }
    
    public List<Event> getEvents() { return events; }
    public void setEvents(List<Event> events) { this.events = events; }
    
    public List<TimeEntry> getTimeEntries() { return timeEntries; }
    public void setTimeEntries(List<TimeEntry> timeEntries) { this.timeEntries = timeEntries; }
    
    /**
     * Add an attorney to this case
     */
    public void addAttorney(Attorney attorney) {
        this.attorneys.add(attorney);
        attorney.getCases().add(this);
    }
    
    /**
     * Add a document to this case
     */
    public void addDocument(Document document) {
        this.documents.add(document);
        document.setCase(this);
    }
    
    /**
     * Add an event to this case
     */
    public void addEvent(Event event) {
        this.events.add(event);
        event.setCase(this);
    }
    
    /**
     * Add a time entry to this case
     */
    public void addTimeEntry(TimeEntry timeEntry) {
        this.timeEntries.add(timeEntry);
        timeEntry.setCase(this);
    }
    
    /**
     * Check if the case is closed
     */
    public boolean isClosed() {
        return "Closed".equalsIgnoreCase(status);
    }
    
    /**
     * Calculate the total billable hours for this case
     */
    public double getTotalHours() {
        double totalHours = 0.0;
        for (TimeEntry entry : timeEntries) {
            totalHours += entry.getHours();
        }
        return totalHours;
    }
    
    @Override
    public String toString() {
        return "Case [id=" + id + ", caseNumber=" + caseNumber + ", title=" + title + 
               ", status=" + status + ", client=" + (client != null ? client.getName() : "Unknown") + "]";
    }
}