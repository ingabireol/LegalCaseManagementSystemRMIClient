package view.calendar;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;

import model.Event;
import model.Case;
import controller.EventController;
import controller.CaseController;
import view.util.UIConstants;
import view.util.SwingUtils;

/**
 * Dialog for viewing event details.
 */
public class EventDetailsDialog extends JDialog {
    private Event event;
    private EventController eventController;
    private CaseController caseController;
    private boolean eventModified = false;
    
    // UI components
    private JPanel infoPanel;
    
    /**
     * Constructor
     * 
     * @param parent The parent window
     * @param event The event to display
     * @param eventController The event controller
     * @param caseController The case controller
     */
    public EventDetailsDialog(Window parent, Event event, 
                             EventController eventController, CaseController caseController) {
        super(parent, "Event Details", ModalityType.APPLICATION_MODAL);
        
        this.event = event;
        this.eventController = eventController;
        this.caseController = caseController;
        
        initializeUI();
        loadEventDetails();
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setSize(600, 500);
        setMinimumSize(new Dimension(500, 400));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Create title panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Create details panel
        JPanel detailsPanel = createDetailsPanel();
        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
        
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
        JLabel titleLabel = new JLabel("Event Details");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        return titlePanel;
    }
    
    /**
     * Create the details panel
     * 
     * @return The details panel
     */
    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Event title
        JLabel eventTitleLabel = new JLabel();
        eventTitleLabel.setFont(UIConstants.SUBTITLE_FONT);
        eventTitleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        eventTitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Status indicator
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel statusLabel = new JLabel("Status: ");
        statusLabel.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        
        JLabel statusValueLabel = new JLabel();
        statusValueLabel.setFont(UIConstants.NORMAL_FONT);
        
        statusPanel.add(statusLabel);
        statusPanel.add(statusValueLabel);
        
        // Information panel with grid layout
        infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        
        // Add components to panel
        panel.add(eventTitleLabel, BorderLayout.NORTH);
        panel.add(statusPanel, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        // Store references to labels
        eventTitleLabel.setName("eventTitleLabel");
        statusValueLabel.setName("statusValueLabel");
        
        return panel;
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
        
        JButton editButton = new JButton("Edit Event");
        editButton.setFont(UIConstants.NORMAL_FONT);
        editButton.addActionListener(e -> editEvent());
        
        JButton closeButton = new JButton("Close");
        closeButton.setFont(UIConstants.NORMAL_FONT);
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(editButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(closeButton);
        
        return buttonPanel;
    }
    
    /**
     * Find a component by name
     * 
     * @param container The container to search
     * @param name The component name
     * @return The found component, or null if not found
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
     * Load event details into the UI
     */
    private void loadEventDetails() {
        if (event == null) {
            return;
        }
        
        try {
            // Load associated case if needed
            Case associatedCase = null;
            if (event.getCase() == null && event.getId()> 0) {
                associatedCase = caseController.getCaseById(event.getId());
                event.setCase(associatedCase);
            } else {
                associatedCase = event.getCase();
            }
            
            // Get the parent container
            Container parent = infoPanel.getParent();
            
            // Set event title
            JLabel eventTitleLabel = (JLabel) findComponentByName(parent, "eventTitleLabel");
            if (eventTitleLabel != null) {
                eventTitleLabel.setText(event.getTitle());
            }
            
            // Set status
            JLabel statusValueLabel = (JLabel) findComponentByName(parent, "statusValueLabel");
            if (statusValueLabel != null) {
                statusValueLabel.setText(event.getStatus());
                
                // Set color based on status
                if ("Completed".equalsIgnoreCase(event.getStatus())) {
                    statusValueLabel.setForeground(UIConstants.SUCCESS_COLOR);
                } else if ("Cancelled".equalsIgnoreCase(event.getStatus())) {
                    statusValueLabel.setForeground(UIConstants.ERROR_COLOR);
                } else if ("Postponed".equalsIgnoreCase(event.getStatus()) || 
                           "Rescheduled".equalsIgnoreCase(event.getStatus())) {
                    statusValueLabel.setForeground(UIConstants.WARNING_COLOR);
                } else {
                    statusValueLabel.setForeground(UIConstants.SECONDARY_COLOR);
                }
            }
            
            // Clear and recreate info panel
            infoPanel.removeAll();
            
            // Set up grid constraints
            GridBagConstraints labelConstraints = new GridBagConstraints();
            labelConstraints.gridx = 0;
            labelConstraints.gridy = GridBagConstraints.RELATIVE;
            labelConstraints.anchor = GridBagConstraints.WEST;
            labelConstraints.insets = new Insets(8, 5, 8, 15);
            
            GridBagConstraints valueConstraints = new GridBagConstraints();
            valueConstraints.gridx = 1;
            valueConstraints.gridy = GridBagConstraints.RELATIVE;
            valueConstraints.fill = GridBagConstraints.HORIZONTAL;
            valueConstraints.weightx = 1.0;
            valueConstraints.insets = new Insets(8, 0, 8, 5);
            
            // Add event type
            addDetailField(infoPanel, "Event Type:", event.getEventType(), 
                           labelConstraints, valueConstraints);
            
            // Add date and time
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
            String dateStr = event.getEventDate() != null ? 
                             event.getEventDate().format(dateFormatter) : "Not specified";
            
            // Check if all-day event or has specific times
            if (event.getStartTime() != null) {
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
                String timeStr = event.getStartTime().format(timeFormatter);
                
                if (event.getEndTime() != null) {
                    timeStr += " - " + event.getEndTime().format(timeFormatter);
                }
                
                dateStr += " at " + timeStr;
            }
            
            addDetailField(infoPanel, "Date & Time:", dateStr, labelConstraints, valueConstraints);
            
            // Add location if specified
            if (event.getLocation() != null && !event.getLocation().isEmpty()) {
                addDetailField(infoPanel, "Location:", event.getLocation(), 
                              labelConstraints, valueConstraints);
            }
            
            // Add case information
            String caseInfo = "Unknown";
            if (associatedCase != null) {
                caseInfo = associatedCase.getCaseNumber() + " - " + associatedCase.getTitle();
            }
            addDetailField(infoPanel, "Related Case:", caseInfo, labelConstraints, valueConstraints);
            
            // Add reminder information
            String reminderInfo = event.isReminderSet() ? 
                                 "Yes, " + event.getReminderDays() + " days before" : "No";
            addDetailField(infoPanel, "Reminder:", reminderInfo, labelConstraints, valueConstraints);
            
            // Add description if available
            if (event.getDescription() != null && !event.getDescription().isEmpty()) {
                // Add a separator
                GridBagConstraints separatorConstraints = new GridBagConstraints();
                separatorConstraints.gridx = 0;
                separatorConstraints.gridy = GridBagConstraints.RELATIVE;
                separatorConstraints.gridwidth = 2;
                separatorConstraints.fill = GridBagConstraints.HORIZONTAL;
                separatorConstraints.insets = new Insets(15, 0, 15, 0);
                
                JSeparator separator = new JSeparator();
                infoPanel.add(separator, separatorConstraints);
                
                // Description label
                JLabel descLabel = new JLabel("Description:");
                descLabel.setFont(UIConstants.LABEL_FONT);
                
                GridBagConstraints descLabelConstraints = new GridBagConstraints();
                descLabelConstraints.gridx = 0;
                descLabelConstraints.gridy = GridBagConstraints.RELATIVE;
                descLabelConstraints.gridwidth = 2;
                descLabelConstraints.anchor = GridBagConstraints.WEST;
                descLabelConstraints.insets = new Insets(0, 5, 5, 5);
                
                infoPanel.add(descLabel, descLabelConstraints);
                
                // Description text
                JTextArea descTextArea = new JTextArea(event.getDescription());
                descTextArea.setFont(UIConstants.NORMAL_FONT);
                descTextArea.setLineWrap(true);
                descTextArea.setWrapStyleWord(true);
                descTextArea.setEditable(false);
                descTextArea.setBackground(Color.WHITE);
                descTextArea.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                
                GridBagConstraints descTextConstraints = new GridBagConstraints();
                descTextConstraints.gridx = 0;
                descTextConstraints.gridy = GridBagConstraints.RELATIVE;
                descTextConstraints.gridwidth = 2;
                descTextConstraints.fill = GridBagConstraints.BOTH;
                descTextConstraints.weightx = 1.0;
                descTextConstraints.weighty = 1.0;
                descTextConstraints.insets = new Insets(0, 5, 5, 5);
                
                infoPanel.add(descTextArea, descTextConstraints);
            }
            
            // Update UI
            infoPanel.revalidate();
            infoPanel.repaint();
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error loading event details: " + e.getMessage(),
                "Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Add a detail field to the info panel
     * 
     * @param panel The panel to add to
     * @param labelText The label text
     * @param valueText The value text
     * @param labelConstraints The label constraints
     * @param valueConstraints The value constraints
     */
    private void addDetailField(JPanel panel, String labelText, String valueText,
                               GridBagConstraints labelConstraints, GridBagConstraints valueConstraints) {
        JLabel label = new JLabel(labelText);
        label.setFont(UIConstants.LABEL_FONT);
        
        JLabel value = new JLabel(valueText != null ? valueText : "");
        value.setFont(UIConstants.NORMAL_FONT);
        
        panel.add(label, labelConstraints);
        panel.add(value, valueConstraints);
    }
    
    /**
     * Edit the event
     */
    private void editEvent() {
        if (event == null) {
            return;
        }
        
        // Open the event editor dialog
        EventEditorDialog dialog = new EventEditorDialog(
            getOwner(),
            event,
            event.getEventDate(),
            eventController,
            caseController
        );
        
        dialog.setVisible(true);
        
        // Check if event was saved
        if (dialog.isEventSaved()) {
            eventModified = true;
            
            // Refresh event details
            event = eventController.getEventById(event.getId());
            loadEventDetails();
        }
    }
    
    /**
     * Check if the event was modified
     * 
     * @return true if the event was modified
     */
    public boolean isEventModified() {
        return eventModified;
    }
}