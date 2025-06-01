package view.calendar;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import model.Event;
import model.Case;
import controller.EventController;
import controller.CaseController;
import view.util.UIConstants;
import view.components.DateChooser;
import view.util.SwingUtils;

/**
 * Dialog for creating or editing an event.
 */
public class EventEditorDialog extends JDialog {
    private Event event;
    private LocalDate initialDate;
    private EventController eventController;
    private CaseController caseController;
    private boolean eventSaved = false;
    
    // Form fields
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> eventTypeCombo;
    private DateChooser dateChooser;
    private JCheckBox allDayCheckBox;
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JTextField locationField;
    private JComboBox<String> statusCombo;
    private JComboBox<String> caseCombo;
    private JCheckBox reminderCheckBox;
    private JSpinner reminderDaysSpinner;
    
    // Case ID mapping for the combo box
    private int[] caseIds;
    
    /**
     * Constructor for creating a new event
     * 
     * @param parent The parent window
     * @param event The event to edit, or null for a new event
     * @param initialDate The initial date for a new event
     * @param eventController The event controller
     * @param caseController The case controller
     */
    public EventEditorDialog(Window parent, Event event, LocalDate initialDate, 
                            EventController eventController, CaseController caseController) {
        super(parent, event == null ? "Create New Event" : "Edit Event", ModalityType.APPLICATION_MODAL);
        
        this.event = event;
        this.initialDate = initialDate;
        this.eventController = eventController;
        this.caseController = caseController;
        
        initializeUI();
        loadData();
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setSize(600, 650);
        setMinimumSize(new Dimension(550, 600));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Create title panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Create form panel
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
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
        JLabel titleLabel = new JLabel(event == null ? "Create New Event" : "Edit Event");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        return titlePanel;
    }
    
    /**
     * Create the form panel with input fields
     * 
     * @return The form panel
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = GridBagConstraints.RELATIVE;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(10, 5, 5, 15);
        
        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = GridBagConstraints.RELATIVE;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1.0;
        fieldConstraints.insets = new Insets(10, 0, 5, 5);
        
        // Event title
        formPanel.add(createFieldLabel("Title:*"), labelConstraints);
        titleField = new JTextField(30);
        titleField.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(titleField, fieldConstraints);
        
        // Event description
        formPanel.add(createFieldLabel("Description:"), labelConstraints);
        descriptionArea = new JTextArea(4, 30);
        descriptionArea.setFont(UIConstants.NORMAL_FONT);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        formPanel.add(descriptionScroll, fieldConstraints);
        
        // Event type
        formPanel.add(createFieldLabel("Event Type:*"), labelConstraints);
        eventTypeCombo = new JComboBox<>(eventController.getEventTypes());
        eventTypeCombo.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(eventTypeCombo, fieldConstraints);
        
        // Event date
        formPanel.add(createFieldLabel("Date:*"), labelConstraints);
        dateChooser = new DateChooser(initialDate);
        formPanel.add(dateChooser, fieldConstraints);
        
        // All day event checkbox
        formPanel.add(createFieldLabel("All Day Event:"), labelConstraints);
        allDayCheckBox = new JCheckBox();
        allDayCheckBox.setBackground(Color.WHITE);
        allDayCheckBox.addActionListener(e -> toggleTimeFields());
        formPanel.add(allDayCheckBox, fieldConstraints);
        
        // Start time
        formPanel.add(createFieldLabel("Start Time:"), labelConstraints);
        startTimeField = new JTextField(10);
        startTimeField.setFont(UIConstants.NORMAL_FONT);
        startTimeField.setToolTipText("Format: HH:MM (24-hour) or HH:MM AM/PM");
        formPanel.add(startTimeField, fieldConstraints);
        
        // End time
        formPanel.add(createFieldLabel("End Time:"), labelConstraints);
        endTimeField = new JTextField(10);
        endTimeField.setFont(UIConstants.NORMAL_FONT);
        endTimeField.setToolTipText("Format: HH:MM (24-hour) or HH:MM AM/PM");
        formPanel.add(endTimeField, fieldConstraints);
        
        // Location
        formPanel.add(createFieldLabel("Location:"), labelConstraints);
        locationField = new JTextField(30);
        locationField.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(locationField, fieldConstraints);
        
        // Status
        formPanel.add(createFieldLabel("Status:"), labelConstraints);
        statusCombo = new JComboBox<>(eventController.getEventStatuses());
        statusCombo.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(statusCombo, fieldConstraints);
        
        // Case
        formPanel.add(createFieldLabel("Related Case:*"), labelConstraints);
        caseCombo = new JComboBox<>();
        caseCombo.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(caseCombo, fieldConstraints);
        
        // Reminder checkbox
        formPanel.add(createFieldLabel("Set Reminder:"), labelConstraints);
        JPanel reminderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        reminderPanel.setBackground(Color.WHITE);
        
        reminderCheckBox = new JCheckBox();
        reminderCheckBox.setBackground(Color.WHITE);
        reminderCheckBox.setSelected(true);
        reminderCheckBox.addActionListener(e -> reminderDaysSpinner.setEnabled(reminderCheckBox.isSelected()));
        
        reminderDaysSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
        reminderDaysSpinner.setFont(UIConstants.NORMAL_FONT);
        reminderDaysSpinner.setPreferredSize(new Dimension(60, 25));
        
        JLabel daysLabel = new JLabel(" days before");
        daysLabel.setFont(UIConstants.NORMAL_FONT);
        
        reminderPanel.add(reminderCheckBox);
        reminderPanel.add(Box.createHorizontalStrut(10));
        reminderPanel.add(reminderDaysSpinner);
        reminderPanel.add(daysLabel);
        
        formPanel.add(reminderPanel, fieldConstraints);
        
        // Required fields note
        JLabel requiredNote = new JLabel("* Required fields");
        requiredNote.setFont(UIConstants.SMALL_FONT);
        requiredNote.setForeground(UIConstants.ERROR_COLOR);
        
        GridBagConstraints noteConstraints = new GridBagConstraints();
        noteConstraints.gridx = 0;
        noteConstraints.gridy = GridBagConstraints.RELATIVE;
        noteConstraints.gridwidth = 2;
        noteConstraints.anchor = GridBagConstraints.WEST;
        noteConstraints.insets = new Insets(20, 5, 5, 5);
        
        formPanel.add(requiredNote, noteConstraints);
        
        return formPanel;
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
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(UIConstants.NORMAL_FONT);
        cancelButton.addActionListener(e -> dispose());
        
        JButton saveButton = new JButton(event == null ? "Create Event" : "Save Changes");
        saveButton.setFont(UIConstants.NORMAL_FONT);
        saveButton.setBackground(UIConstants.SECONDARY_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveEvent());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(saveButton);
        
        return buttonPanel;
    }
    
    /**
     * Load data into form fields
     */
    private void loadData() {
        // Load cases for the combo box
        loadCases();
        
        // If editing an existing event, populate fields
        if (event != null) {
            titleField.setText(event.getTitle());
            descriptionArea.setText(event.getDescription());
            
            if (event.getEventType() != null) {
                eventTypeCombo.setSelectedItem(event.getEventType());
            }
            
            if (event.getEventDate() != null) {
                dateChooser.setDate(event.getEventDate());
            }
            
            // Check if it's an all-day event
            boolean isAllDay = (event.getStartTime() == null && event.getEndTime() == null);
            allDayCheckBox.setSelected(isAllDay);
            
            // Set times if not an all-day event
            if (!isAllDay) {
                if (event.getStartTime() != null) {
                    startTimeField.setText(formatTime(event.getStartTime()));
                }
                
                if (event.getEndTime() != null) {
                    endTimeField.setText(formatTime(event.getEndTime()));
                }
            }
            
            locationField.setText(event.getLocation());
            
            if (event.getStatus() != null) {
                statusCombo.setSelectedItem(event.getStatus());
            }
            
            // Set case
            selectCase(event.getId());
            
            // Set reminder options
            reminderCheckBox.setSelected(event.isReminderSet());
            reminderDaysSpinner.setValue(event.getReminderDays());
            reminderDaysSpinner.setEnabled(event.isReminderSet());
        } else {
            // Default values for a new event
            statusCombo.setSelectedItem("Scheduled");
            allDayCheckBox.setSelected(false);
            
            // Default start/end times
            startTimeField.setText("9:00 AM");
            endTimeField.setText("10:00 AM");
        }
        
        // Enable/disable time fields based on all-day checkbox
        toggleTimeFields();
    }
    
    /**
     * Load cases for the combo box
     */
    private void loadCases() {
        try {
            // Get all cases
            List<Case> cases = caseController.getAllCases();
            
            // Create arrays for the combo box
            String[] caseNames = new String[cases.size()];
            caseIds = new int[cases.size()];
            
            // Fill arrays
            for (int i = 0; i < cases.size(); i++) {
                Case c = cases.get(i);
                caseNames[i] = c.getCaseNumber() + " - " + c.getTitle();
                caseIds[i] = c.getId();
            }
            
            // Set model for the combo box
            caseCombo.setModel(new DefaultComboBoxModel<>(caseNames));
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error loading cases: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Select a case in the combo box
     * 
     * @param caseId The case ID to select
     */
    private void selectCase(int caseId) {
        for (int i = 0; i < caseIds.length; i++) {
            if (caseIds[i] == caseId) {
                caseCombo.setSelectedIndex(i);
                return;
            }
        }
    }
    
    /**
     * Format time for display
     * 
     * @param time The time to format
     * @return Formatted time string
     */
    private String formatTime(LocalTime time) {
        if (time == null) {
            return "";
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        return time.format(formatter);
    }
    
    /**
     * Parse time from a string
     * 
     * @param timeStr The time string
     * @return Parsed LocalTime or null if invalid
     */
    private LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Try parsing with various formats
            String[] formats = {
                "HH:mm", "H:mm",           // 24-hour
                "h:mm a", "hh:mm a",       // 12-hour with AM/PM
                "h:mma", "hh:mma",         // 12-hour with AM/PM (no space)
                "h:mm A", "hh:mm A",       // 12-hour with AM/PM (uppercase)
                "h:mmA", "hh:mmA"          // 12-hour with AM/PM (uppercase, no space)
            };
            
            for (String format : formats) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                    return LocalTime.parse(timeStr.trim(), formatter);
                } catch (DateTimeParseException e) {
                    // Try next format
                }
            }
            
            // If all formats fail, throw exception
            throw new DateTimeParseException("Invalid time format", timeStr, 0);
            
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Toggle the enabled state of time fields based on all-day checkbox
     */
    private void toggleTimeFields() {
        boolean enabled = !allDayCheckBox.isSelected();
        startTimeField.setEnabled(enabled);
        endTimeField.setEnabled(enabled);
    }
    
    /**
     * Validate form data
     * 
     * @return true if form data is valid
     */
    private boolean validateForm() {
        // Check required fields
        if (titleField.getText().trim().isEmpty()) {
            SwingUtils.showErrorMessage(this, "Title is required.", "Validation Error");
            titleField.requestFocus();
            return false;
        }
        
        // Check that a case is selected
        if (caseCombo.getSelectedIndex() == -1) {
            SwingUtils.showErrorMessage(this, "A case must be selected.", "Validation Error");
            caseCombo.requestFocus();
            return false;
        }
        
        // If not all-day event, validate times
        if (!allDayCheckBox.isSelected()) {
            // Parse start time
            LocalTime startTime = parseTime(startTimeField.getText());
            if (startTime == null) {
                SwingUtils.showErrorMessage(this, 
                    "Invalid start time. Please use format HH:MM or HH:MM AM/PM.",
                    "Validation Error");
                startTimeField.requestFocus();
                return false;
            }
            
            // Parse end time
            LocalTime endTime = parseTime(endTimeField.getText());
            if (endTime == null) {
                SwingUtils.showErrorMessage(this,
                    "Invalid end time. Please use format HH:MM or HH:MM AM/PM.",
                    "Validation Error");
                endTimeField.requestFocus();
                return false;
            }
            
            // Check that end time is after start time
            if (endTime.isBefore(startTime)) {
                SwingUtils.showErrorMessage(this,
                    "End time must be after start time.",
                    "Validation Error");
                endTimeField.requestFocus();
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Save the event
     */
    private void saveEvent() {
        if (!validateForm()) {
            return;
        }
        
        try {
            // Create or update event
            if (event == null) {
                event = new Event();
                
                // Generate a unique event ID
                event.setEventId("EVT" + System.currentTimeMillis());
            }
            
            // Set event data from form
            event.setTitle(titleField.getText().trim());
            event.setDescription(descriptionArea.getText().trim());
            event.setEventType((String) eventTypeCombo.getSelectedItem());
            event.setEventDate(dateChooser.getDate());
            
            // Set times based on all-day checkbox
            if (allDayCheckBox.isSelected()) {
                event.setStartTime(null);
                event.setEndTime(null);
            } else {
                event.setStartTime(parseTime(startTimeField.getText()));
                event.setEndTime(parseTime(endTimeField.getText()));
            }
            
            event.setLocation(locationField.getText().trim());
            event.setStatus((String) statusCombo.getSelectedItem());
            
            // Get the selected case ID
            int selectedIndex = caseCombo.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < caseIds.length) {
                event.setId(caseIds[selectedIndex]);
            }
            
            // Set reminder settings
            event.setReminderSet(reminderCheckBox.isSelected());
            event.setReminderDays((Integer) reminderDaysSpinner.getValue());
            
            // Save to database
            boolean success;
            if (event.getId() == 0) {
                success = eventController.createEvent(event);
            } else {
                success = eventController.updateEvent(event);
            }
            
            if (success) {
                eventSaved = true;
                dispose();
            } else {
                SwingUtils.showErrorMessage(
                    this,
                    "Failed to save event. Please try again.",
                    "Database Error"
                );
            }
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error saving event: " + e.getMessage(),
                "Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Check if the event was saved
     * 
     * @return true if event was saved
     */
    public boolean isEventSaved() {
        return eventSaved;
    }
}