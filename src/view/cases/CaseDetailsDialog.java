package view.cases;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;

import model.Case;
import model.Attorney;
import model.Document;
import model.Event;
import model.TimeEntry;
import controller.CaseController;
import view.util.UIConstants;
import view.util.SwingUtils;
import view.components.CustomTable;
import view.components.StatusIndicator;

/**
 * Dialog for viewing case details.
 */
public class CaseDetailsDialog extends JDialog {
    private Case legalCase;
    private CaseController caseController;
    
    private JPanel caseInfoPanel;
    private JPanel documentsPanel;
    private JPanel eventsPanel;
    private JPanel timeEntriesPanel;
    
    private CustomTable documentsTable;
    private CustomTable eventsTable;
    private CustomTable timeEntriesTable;
    
    private JButton closeButton;
    private JButton editButton;
    
    /**
     * Constructor
     * 
     * @param parent The parent window
     * @param legalCase The case to display
     */
    public CaseDetailsDialog(Window parent, Case legalCase) {
        super(parent, "Case Details", ModalityType.APPLICATION_MODAL);
        
        this.legalCase = legalCase;
        this.caseController = new CaseController();
        
        initializeUI();
        loadCaseData();
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setSize(900, 700);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Create title panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Create tabbed pane for content
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.NORMAL_FONT);
        
        // Create info panel
        caseInfoPanel = createCaseInfoPanel();
        tabbedPane.addTab("Case Information", caseInfoPanel);
        
        // Create documents panel
        documentsPanel = createDocumentsPanel();
        tabbedPane.addTab("Documents", documentsPanel);
        
        // Create events panel
        eventsPanel = createEventsPanel();
        tabbedPane.addTab("Events", eventsPanel);
        
        // Create time entries panel
        timeEntriesPanel = createTimeEntriesPanel();
        tabbedPane.addTab("Time Entries", timeEntriesPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create the title panel
     * 
     * @return The title panel
     */
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UIConstants.PRIMARY_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Title label
        JLabel titleLabel = new JLabel("Case Details");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        // Create a status indicator for case status
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setOpaque(false);
        
        JLabel statusLabel = new JLabel("Status: ");
        statusLabel.setFont(UIConstants.NORMAL_FONT);
        statusLabel.setForeground(Color.WHITE);
        
        StatusIndicator statusIndicator = new StatusIndicator(
            legalCase != null ? legalCase.getStatus() : "Unknown");
        
        statusPanel.add(statusLabel);
        statusPanel.add(statusIndicator);
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(statusPanel, BorderLayout.EAST);
        
        return titlePanel;
    }
    
    /**
     * Create the case information panel
     * 
     * @return The case info panel
     */
    private JPanel createCaseInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create multi-column layout for case details
        JPanel detailsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        detailsPanel.setBackground(Color.WHITE);
        
        // Left column - basic details
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);
        
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = GridBagConstraints.RELATIVE;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(5, 5, 5, 15);
        
        GridBagConstraints valueConstraints = new GridBagConstraints();
        valueConstraints.gridx = 1;
        valueConstraints.gridy = GridBagConstraints.RELATIVE;
        valueConstraints.anchor = GridBagConstraints.WEST;
        valueConstraints.weightx = 1.0;
        valueConstraints.fill = GridBagConstraints.HORIZONTAL;
        valueConstraints.insets = new Insets(5, 5, 5, 5);
        
        // Add basic case information fields (will be populated in loadCaseData)
        
        // Case Number
        leftPanel.add(createFieldLabel("Case Number:"), labelConstraints);
        JLabel caseNumberValue = new JLabel();
        caseNumberValue.setName("caseNumber");
        leftPanel.add(caseNumberValue, valueConstraints);
        
        // Title
        leftPanel.add(createFieldLabel("Title:"), labelConstraints);
        JLabel titleValue = new JLabel();
        titleValue.setName("title");
        leftPanel.add(titleValue, valueConstraints);
        
        // Case Type
        leftPanel.add(createFieldLabel("Case Type:"), labelConstraints);
        JLabel caseTypeValue = new JLabel();
        caseTypeValue.setName("caseType");
        leftPanel.add(caseTypeValue, valueConstraints);
        
        // Status
        leftPanel.add(createFieldLabel("Status:"), labelConstraints);
        JLabel statusValue = new JLabel();
        statusValue.setName("status");
        leftPanel.add(statusValue, valueConstraints);
        
        // Client
        leftPanel.add(createFieldLabel("Client:"), labelConstraints);
        JLabel clientValue = new JLabel();
        clientValue.setName("client");
        leftPanel.add(clientValue, valueConstraints);
        
        // File Date
        leftPanel.add(createFieldLabel("File Date:"), labelConstraints);
        JLabel fileDateValue = new JLabel();
        fileDateValue.setName("fileDate");
        leftPanel.add(fileDateValue, valueConstraints);
        
        // Closing Date
        leftPanel.add(createFieldLabel("Closing Date:"), labelConstraints);
        JLabel closingDateValue = new JLabel();
        closingDateValue.setName("closingDate");
        leftPanel.add(closingDateValue, valueConstraints);
        
        // Right column - additional details
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        
        // Court
        rightPanel.add(createFieldLabel("Court:"), labelConstraints);
        JLabel courtValue = new JLabel();
        courtValue.setName("court");
        rightPanel.add(courtValue, valueConstraints);
        
        // Judge
        rightPanel.add(createFieldLabel("Judge:"), labelConstraints);
        JLabel judgeValue = new JLabel();
        judgeValue.setName("judge");
        rightPanel.add(judgeValue, valueConstraints);
        
        // Opposing Party
        rightPanel.add(createFieldLabel("Opposing Party:"), labelConstraints);
        JLabel opposingPartyValue = new JLabel();
        opposingPartyValue.setName("opposingParty");
        rightPanel.add(opposingPartyValue, valueConstraints);
        
        // Opposing Counsel
        rightPanel.add(createFieldLabel("Opposing Counsel:"), labelConstraints);
        JLabel opposingCounselValue = new JLabel();
        opposingCounselValue.setName("opposingCounsel");
        rightPanel.add(opposingCounselValue, valueConstraints);
        
        // Attorneys
        rightPanel.add(createFieldLabel("Attorneys:"), labelConstraints);
        JPanel attorneysPanel = new JPanel();
        attorneysPanel.setName("attorneys");
        attorneysPanel.setLayout(new BoxLayout(attorneysPanel, BoxLayout.Y_AXIS));
        attorneysPanel.setBackground(Color.WHITE);
        attorneysPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        rightPanel.add(attorneysPanel, valueConstraints);
        
        // Add columns to details panel
        detailsPanel.add(leftPanel);
        detailsPanel.add(rightPanel);
        
        // Description
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.setBackground(Color.WHITE);
        descriptionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.SECONDARY_COLOR),
            "Description",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            UIConstants.LABEL_FONT,
            UIConstants.SECONDARY_COLOR
        ));
        
        JTextArea descriptionArea = new JTextArea(5, 30);
        descriptionArea.setName("description");
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(Color.WHITE);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        descriptionPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
        
        // Add components to main panel
        panel.add(detailsPanel, BorderLayout.NORTH);
        panel.add(descriptionPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create the documents panel
     * 
     * @return The documents panel
     */
    private JPanel createDocumentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create table for documents
        String[] columnNames = {
            "Document ID", "Title", "Type", "Date Added", "Status"
        };
        
        documentsTable = new CustomTable(columnNames);
        
        // Set column widths
        documentsTable.setColumnWidth(0, 100);  // Document ID
        documentsTable.setColumnWidth(1, 200);  // Title
        documentsTable.setColumnWidth(2, 100);  // Type
        documentsTable.setColumnWidth(3, 100);  // Date Added
        documentsTable.setColumnWidth(4, 100);  // Status
        
        // Add custom renderer for Status column
        documentsTable.setColumnRenderer(4, (table, value, isSelected, hasFocus, row, column) -> {
            if (value == null) {
                return new JLabel("");
            }
            
            StatusIndicator indicator = new StatusIndicator(value.toString());
            if (isSelected) {
                indicator.setBackground(table.getSelectionBackground());
            } else {
                indicator.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 250));
            }
            return indicator;
        });
        
        // Add actions panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JButton viewDocumentButton = new JButton("View Document");
        viewDocumentButton.setFont(UIConstants.NORMAL_FONT);
        viewDocumentButton.addActionListener(e -> viewSelectedDocument());
        
        JButton addDocumentButton = new JButton("Add Document");
        addDocumentButton.setFont(UIConstants.NORMAL_FONT);
        addDocumentButton.setBackground(UIConstants.SECONDARY_COLOR);
        addDocumentButton.setForeground(Color.WHITE);
        addDocumentButton.addActionListener(e -> addNewDocument());
        
        actionsPanel.add(viewDocumentButton);
        actionsPanel.add(Box.createHorizontalStrut(10));
        actionsPanel.add(addDocumentButton);
        
        // Add components to panel
        panel.add(actionsPanel, BorderLayout.NORTH);
        panel.add(documentsTable, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create the events panel
     * 
     * @return The events panel
     */
    private JPanel createEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create table for events
        String[] columnNames = {
            "Event ID", "Title", "Type", "Date", "Status", "Location"
        };
        
        eventsTable = new CustomTable(columnNames);
        
        // Set column widths
        eventsTable.setColumnWidth(0, 100);  // Event ID
        eventsTable.setColumnWidth(1, 200);  // Title
        eventsTable.setColumnWidth(2, 100);  // Type
        eventsTable.setColumnWidth(3, 100);  // Date
        eventsTable.setColumnWidth(4, 100);  // Status
        eventsTable.setColumnWidth(5, 150);  // Location
        
        // Add custom renderer for Status column
        eventsTable.setColumnRenderer(4, (table, value, isSelected, hasFocus, row, column) -> {
            if (value == null) {
                return new JLabel("");
            }
            
            StatusIndicator indicator = new StatusIndicator(value.toString());
            if (isSelected) {
                indicator.setBackground(table.getSelectionBackground());
            } else {
                indicator.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 250));
            }
            return indicator;
        });
        
        // Add actions panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JButton viewEventButton = new JButton("View Event");
        viewEventButton.setFont(UIConstants.NORMAL_FONT);
        viewEventButton.addActionListener(e -> viewSelectedEvent());
        
        JButton addEventButton = new JButton("Add Event");
        addEventButton.setFont(UIConstants.NORMAL_FONT);
        addEventButton.setBackground(UIConstants.SECONDARY_COLOR);
        addEventButton.setForeground(Color.WHITE);
        addEventButton.addActionListener(e -> addNewEvent());
        
        actionsPanel.add(viewEventButton);
        actionsPanel.add(Box.createHorizontalStrut(10));
        actionsPanel.add(addEventButton);
        
        // Add components to panel
        panel.add(actionsPanel, BorderLayout.NORTH);
        panel.add(eventsTable, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create the time entries panel
     * 
     * @return The time entries panel
     */
    private JPanel createTimeEntriesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create table for time entries
        String[] columnNames = {
            "Entry ID", "Attorney", "Date", "Hours", "Activity", "Description", "Billed"
        };
        
        timeEntriesTable = new CustomTable(columnNames);
        
        // Set column widths
        timeEntriesTable.setColumnWidth(0, 100);  // Entry ID
        timeEntriesTable.setColumnWidth(1, 150);  // Attorney
        timeEntriesTable.setColumnWidth(2, 100);  // Date
        timeEntriesTable.setColumnWidth(3, 80);   // Hours
        timeEntriesTable.setColumnWidth(4, 100);  // Activity
        timeEntriesTable.setColumnWidth(5, 200);  // Description
        timeEntriesTable.setColumnWidth(6, 80);   // Billed
        
        // Add actions panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JButton viewTimeEntryButton = new JButton("View Time Entry");
        viewTimeEntryButton.setFont(UIConstants.NORMAL_FONT);
        viewTimeEntryButton.addActionListener(e -> viewSelectedTimeEntry());
        
        JButton addTimeEntryButton = new JButton("Add Time Entry");
        addTimeEntryButton.setFont(UIConstants.NORMAL_FONT);
        addTimeEntryButton.setBackground(UIConstants.SECONDARY_COLOR);
        addTimeEntryButton.setForeground(Color.WHITE);
        addTimeEntryButton.addActionListener(e -> addNewTimeEntry());
        
        actionsPanel.add(viewTimeEntryButton);
        actionsPanel.add(Box.createHorizontalStrut(10));
        actionsPanel.add(addTimeEntryButton);
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JLabel totalHoursLabel = new JLabel("Total Hours: ");
        totalHoursLabel.setFont(UIConstants.LABEL_FONT);
        
        JLabel totalHoursValue = new JLabel("0.0");
        totalHoursValue.setName("totalHours");
        totalHoursValue.setFont(UIConstants.LABEL_FONT);
        
        summaryPanel.add(totalHoursLabel);
        summaryPanel.add(totalHoursValue);
        
        // Add components to panel
        panel.add(actionsPanel, BorderLayout.NORTH);
        panel.add(timeEntriesTable, BorderLayout.CENTER);
        panel.add(summaryPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create a field label with consistent styling
     * 
     * @param text The label text
     * @return The styled label
     */
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.LABEL_FONT);
        label.setForeground(UIConstants.PRIMARY_COLOR);
        return label;
    }
    
    /**
     * Create the button panel
     * 
     * @return The button panel
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        buttonPanel.setBackground(Color.WHITE);
        
        editButton = new JButton("Edit Case");
        editButton.setFont(UIConstants.NORMAL_FONT);
        editButton.addActionListener(e -> editCase());
        
        closeButton = new JButton("Close");
        closeButton.setFont(UIConstants.NORMAL_FONT);
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(editButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(closeButton);
        
        return buttonPanel;
    }
    
    /**
     * Load case data into the UI
     */
    private void loadCaseData() {
        if (legalCase == null) {
            return;
        }

        try {
            // Update case information fields
            updateCaseInfoFields();
            
            // Update documents table
            loadDocumentsTable();
            
            // Update events table
            loadEventsTable();
            
            // Update time entries table
            loadTimeEntriesTable();
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error loading case data: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Update case information fields
     */
    private void updateCaseInfoFields() {
        // Basic info
        setLabelText("caseNumber", legalCase.getCaseNumber());
        setLabelText("title", legalCase.getTitle());
        setLabelText("caseType", legalCase.getCaseType());
        setLabelText("status", legalCase.getStatus());
        
        // Client info
        String clientName = legalCase.getClient() != null ? 
            legalCase.getClient().getName() : 
            "Client #" + legalCase.getId();
        setLabelText("client", clientName);
        
        // Dates
        setLabelText("fileDate", formatDate(legalCase.getFileDate()));
        setLabelText("closingDate", formatDate(legalCase.getClosingDate()));
        
        // Court info
        setLabelText("court", legalCase.getCourt());
        setLabelText("judge", legalCase.getJudge());
        setLabelText("opposingParty", legalCase.getOpposingParty());
        setLabelText("opposingCounsel", legalCase.getOpposingCounsel());
        
        // Attorneys
        if (legalCase.getAttorneys() != null && !legalCase.getAttorneys().isEmpty()) {
            JPanel attorneysPanel = (JPanel) findComponentByName(caseInfoPanel, "attorneys");
            attorneysPanel.removeAll();
            
            for (Attorney attorney : legalCase.getAttorneys()) {
                JLabel attorneyLabel = new JLabel(attorney.getDisplayName());
                attorneyLabel.setFont(UIConstants.NORMAL_FONT);
                attorneyLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                
                attorneysPanel.add(attorneyLabel);
                attorneysPanel.add(Box.createVerticalStrut(5));
            }
            
            attorneysPanel.revalidate();
            attorneysPanel.repaint();
        } else {
            setLabelText("attorneys", "No attorneys assigned");
        }
        
        // Description
        JTextArea descriptionArea = (JTextArea) findComponentByName(caseInfoPanel, "description");
        if (descriptionArea != null && legalCase.getDescription() != null) {
            descriptionArea.setText(legalCase.getDescription());
        }
    }
    
    /**
     * Load documents into the documents table
     */
    private void loadDocumentsTable() {
        documentsTable.clearTable();
        
        if (legalCase.getDocuments() != null && !legalCase.getDocuments().isEmpty()) {
            for (Document document : legalCase.getDocuments()) {
                Object[] row = {
                    document.getDocumentId(),
                    document.getTitle(),
                    document.getDocumentType(),
                    formatDate(document.getDateAdded()),
                    document.getStatus()
                };
                documentsTable.addRow(row);
            }
        }
    }
    
    /**
     * Load events into the events table
     */
    private void loadEventsTable() {
        eventsTable.clearTable();
        
        if (legalCase.getEvents() != null && !legalCase.getEvents().isEmpty()) {
            for (Event event : legalCase.getEvents()) {
                Object[] row = {
                    event.getEventId(),
                    event.getTitle(),
                    event.getEventType(),
                    formatDate(event.getEventDate()),
                    event.getStatus(),
                    event.getLocation() != null ? event.getLocation() : ""
                };
                eventsTable.addRow(row);
            }
        }
    }
    
    /**
     * Load time entries into the time entries table
     */
    private void loadTimeEntriesTable() {
        timeEntriesTable.clearTable();
        
        if (legalCase.getTimeEntries() != null && !legalCase.getTimeEntries().isEmpty()) {
            double totalHours = 0.0;
            
            for (TimeEntry entry : legalCase.getTimeEntries()) {
                String attorneyName = entry.getAttorney() != null ? 
                    entry.getAttorney().getFullName() : 
                    "Attorney #" + entry.getAttorney().getAttorneyId();
                
                Object[] row = {
                    entry.getEntryId(),
                    attorneyName,
                    formatDate(entry.getEntryDate()),
                    entry.getFormattedHours(),
                    entry.getActivityCode(),
                    entry.getDescription(),
                    entry.isBilled() ? "Yes" : "No"
                };
                timeEntriesTable.addRow(row);
                
                totalHours += entry.getHours();
            }
            
            // Update total hours
            JLabel totalHoursLabel = (JLabel) findComponentByName(timeEntriesPanel, "totalHours");
            if (totalHoursLabel != null) {
                totalHoursLabel.setText(String.format("%.2f", totalHours));
            }
        }
    }
    
    /**
     * Set the text of a label by name
     * 
     * @param name The component name
     * @param text The text to set
     */
    private void setLabelText(String name, String text) {
        JLabel label = (JLabel) findComponentByName(caseInfoPanel, name);
        if (label != null) {
            label.setText(text != null && !text.isEmpty() ? text : "N/A");
        }
    }
    
    /**
     * Format a date for display
     * 
     * @param date The date to format
     * @return Formatted date string
     */
    private String formatDate(LocalDate date) {
        return date != null ? date.toString() : "N/A";
    }
    
    /**
     * Find a component by name within a parent container
     * 
     * @param container The container to search in
     * @param name The component name to find
     * @return The found component or null
     */
    private Component findComponentByName(Container container, String name) {
        for (Component component : container.getComponents()) {
            if (name.equals(component.getName())) {
                return component;
            }
            
            if (component instanceof Container) {
                Component found = findComponentByName((Container) component, name);
                if (found != null) {
                    return found;
                }
            }
        }
        
        return null;
    }
    
    /**
     * View the selected document
     */
    private void viewSelectedDocument() {
        int selectedRow = documentsTable.getSelectedRow();
        if (selectedRow == -1) {
            SwingUtils.showInfoMessage(
                this,
                "Please select a document to view.",
                "No Document Selected"
            );
            return;
        }
        
        // Get document ID from selected row
        String documentId = documentsTable.getValueAt(selectedRow, 0).toString();
        
        // This would open a document viewer in a full implementation
        SwingUtils.showInfoMessage(
            this,
            "View document: " + documentId + "\n" +
            "This feature will be implemented in a DocumentViewerDialog.",
            "View Document"
        );
    }
    
    /**
     * Add a new document to this case
     */
    private void addNewDocument() {
        // This would open a document upload dialog in a full implementation
        SwingUtils.showInfoMessage(
            this,
            "Add document to case: " + legalCase.getCaseNumber() + "\n" +
            "This feature will be implemented in a DocumentUploadDialog.",
            "Add Document"
        );
    }
    
    /**
     * View the selected event
     */
    private void viewSelectedEvent() {
        int selectedRow = eventsTable.getSelectedRow();
        if (selectedRow == -1) {
            SwingUtils.showInfoMessage(
                this,
                "Please select an event to view.",
                "No Event Selected"
            );
            return;
        }
        
        // Get event ID from selected row
        String eventId = eventsTable.getValueAt(selectedRow, 0).toString();
        
        // This would open an event viewer in a full implementation
        SwingUtils.showInfoMessage(
            this,
            "View event: " + eventId + "\n" +
            "This feature will be implemented in an EventDetailsDialog.",
            "View Event"
        );
    }
    
    /**
     * Add a new event to this case
     */
    private void addNewEvent() {
        // This would open an event editor dialog in a full implementation
        SwingUtils.showInfoMessage(
            this,
            "Add event to case: " + legalCase.getCaseNumber() + "\n" +
            "This feature will be implemented in an EventEditorDialog.",
            "Add Event"
        );
    }
    
    /**
     * View the selected time entry
     */
    private void viewSelectedTimeEntry() {
        int selectedRow = timeEntriesTable.getSelectedRow();
        if (selectedRow == -1) {
            SwingUtils.showInfoMessage(
                this,
                "Please select a time entry to view.",
                "No Time Entry Selected"
            );
            return;
        }
        
        // Get time entry ID from selected row
        String timeEntryId = timeEntriesTable.getValueAt(selectedRow, 0).toString();
        
        // This would open a time entry viewer in a full implementation
        SwingUtils.showInfoMessage(
            this,
            "View time entry: " + timeEntryId + "\n" +
            "This feature will be implemented in a TimeEntryDetailsDialog.",
            "View Time Entry"
        );
    }
    
    /**
     * Add a new time entry to this case
     */
    private void addNewTimeEntry() {
        // This would open a time entry editor dialog in a full implementation
        SwingUtils.showInfoMessage(
            this,
            "Add time entry to case: " + legalCase.getCaseNumber() + "\n" +
            "This feature will be implemented in a TimeEntryEditorDialog.",
            "Add Time Entry"
        );
    }
    
    /**
     * Edit this case
     */
    private void editCase() {
        try {
            // Open case editor dialog
            CaseEditorDialog dialog = new CaseEditorDialog(
                SwingUtilities.getWindowAncestor(this), legalCase);
            dialog.setVisible(true);
            
            // Refresh data if case was saved
            if (dialog.isCaseSaved()) {
                // Get updated case
                legalCase = caseController.getCaseWithDetails(legalCase.getId());
                loadCaseData();
            }
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error editing case: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
}