package view.calendar;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import model.Event;
import model.Case;
import controller.EventController;
import controller.CaseController;
import view.util.UIConstants;
import view.components.TableFilterPanel;
import view.util.SwingUtils;

/**
 * Panel for calendar and events management in the Legal Case Management System.
 */
public class CalendarPanel extends JPanel {
    private EventController eventController;
    private CaseController caseController;
    private LocalDate currentDate;
    private LocalDate selectedDate;
    private JPanel calendarGrid;
    private JLabel monthYearLabel;
    private Map<LocalDate, List<Event>> eventsByDate;
    
    // UI components for event display
    private JPanel eventDetailsPanel;
    private JList<Event> dayEventsList;
    private DefaultListModel<Event> eventsListModel;
    private JLabel selectedDateLabel;
    
    /**
     * Constructor
     */
    public CalendarPanel() {
        this.eventController = new EventController();
        this.caseController = new CaseController();
        this.currentDate = LocalDate.now();
        this.selectedDate = currentDate;
        this.eventsByDate = new HashMap<>();
        
        initializeUI();
        loadEvents();
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create split pane for calendar and events
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setDividerLocation(700);
        splitPane.setResizeWeight(0.7);
        
        // Create calendar panel
        JPanel calendarPanel = createCalendarPanel();
        splitPane.setLeftComponent(calendarPanel);
        
        // Create event details panel
        eventDetailsPanel = createEventDetailsPanel();
        splitPane.setRightComponent(eventDetailsPanel);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    /**
     * Create the header panel with title and controls
     * 
     * @return The header panel
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        
        JLabel titleLabel = new JLabel("Calendar & Events");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        // Month navigation panel
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        navigationPanel.setBackground(Color.WHITE);
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        
        JButton prevMonthButton = new JButton("◀");
        prevMonthButton.setFont(UIConstants.NORMAL_FONT);
        prevMonthButton.setFocusPainted(false);
        prevMonthButton.addActionListener(e -> {
            currentDate = currentDate.minusMonths(1);
            updateCalendarView();
        });
        
        monthYearLabel = new JLabel();
        monthYearLabel.setFont(UIConstants.SUBTITLE_FONT);
        monthYearLabel.setHorizontalAlignment(SwingConstants.CENTER);
        monthYearLabel.setPreferredSize(new Dimension(200, 30));
        
        JButton nextMonthButton = new JButton("▶");
        nextMonthButton.setFont(UIConstants.NORMAL_FONT);
        nextMonthButton.setFocusPainted(false);
        nextMonthButton.addActionListener(e -> {
            currentDate = currentDate.plusMonths(1);
            updateCalendarView();
        });
        
        JButton todayButton = new JButton("Today");
        todayButton.setFont(UIConstants.NORMAL_FONT);
        todayButton.setFocusPainted(false);
        todayButton.addActionListener(e -> {
            currentDate = LocalDate.now();
            selectedDate = currentDate;
            updateCalendarView();
            loadEventsForSelectedDate();
        });
        
        // Add "New Event" button
        JButton newEventButton = new JButton("New Event");
        newEventButton.setFont(UIConstants.NORMAL_FONT);
        newEventButton.setBackground(UIConstants.SECONDARY_COLOR);
        newEventButton.setForeground(Color.WHITE);
        newEventButton.setFocusPainted(false);
        newEventButton.addActionListener(e -> createNewEvent());
        
        navigationPanel.add(prevMonthButton);
        navigationPanel.add(Box.createHorizontalStrut(10));
        navigationPanel.add(monthYearLabel);
        navigationPanel.add(Box.createHorizontalStrut(10));
        navigationPanel.add(nextMonthButton);
        navigationPanel.add(Box.createHorizontalStrut(20));
        navigationPanel.add(todayButton);
        navigationPanel.add(Box.createHorizontalStrut(30));
        navigationPanel.add(newEventButton);
        
        headerPanel.add(titlePanel, BorderLayout.NORTH);
        headerPanel.add(navigationPanel, BorderLayout.CENTER);
        
        // Add filter panel
        JPanel filterPanel = createFilterPanel();
        headerPanel.add(filterPanel, BorderLayout.SOUTH);
        
        return headerPanel;
    }
    
    /**
     * Create filter panel for events
     * 
     * @return Filter panel
     */
    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        
        JLabel viewByLabel = new JLabel("View:");
        viewByLabel.setFont(UIConstants.NORMAL_FONT);
        
        JComboBox<String> viewTypeCombo = new JComboBox<>(new String[]{"All Events", "Court Dates", "Meetings", "Deadlines"});
        viewTypeCombo.setFont(UIConstants.NORMAL_FONT);
        viewTypeCombo.addActionListener(e -> {
            // Filter events based on selection
            loadEvents();
        });
        
        JLabel caseLabel = new JLabel("Case:");
        caseLabel.setFont(UIConstants.NORMAL_FONT);
        
        JComboBox<String> caseCombo = new JComboBox<>(new String[]{"All Cases"});
        caseCombo.setFont(UIConstants.NORMAL_FONT);
        caseCombo.addActionListener(e -> {
            // Filter events based on case selection
            loadEvents();
        });
        
        // Add components to filter panel
        filterPanel.add(viewByLabel);
        filterPanel.add(viewTypeCombo);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(caseLabel);
        filterPanel.add(caseCombo);
        
        return filterPanel;
    }
    
    /**
     * Create the calendar panel with month view
     * 
     * @return The calendar panel
     */
    private JPanel createCalendarPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        // Create day of week header
        JPanel weekDaysPanel = new JPanel(new GridLayout(1, 7));
        weekDaysPanel.setBackground(Color.WHITE);
        
        String[] weekDays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        for (String day : weekDays) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
            dayLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            weekDaysPanel.add(dayLabel);
        }
        
        // Create calendar grid
        calendarGrid = new JPanel(new GridLayout(6, 7));
        calendarGrid.setBackground(Color.WHITE);
        
        panel.add(weekDaysPanel, BorderLayout.NORTH);
        panel.add(calendarGrid, BorderLayout.CENTER);
        
        // Initialize calendar view
        updateCalendarView();
        
        return panel;
    }
    
    /**
     * Create the event details panel
     * 
     * @return The event details panel
     */
    private JPanel createEventDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 20, 20));
        
        // Selected date header
        selectedDateLabel = new JLabel("Selected Date: " + selectedDate.toString());
        selectedDateLabel.setFont(UIConstants.SUBTITLE_FONT);
        selectedDateLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Events list
        eventsListModel = new DefaultListModel<>();
        dayEventsList = new JList<>(eventsListModel);
        dayEventsList.setFont(UIConstants.NORMAL_FONT);
        dayEventsList.setCellRenderer(new EventListCellRenderer());
        dayEventsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dayEventsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewEventDetails();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(dayEventsList);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JButton viewButton = new JButton("View");
        viewButton.setFont(UIConstants.NORMAL_FONT);
        viewButton.addActionListener(e -> viewEventDetails());
        
        JButton editButton = new JButton("Edit");
        editButton.setFont(UIConstants.NORMAL_FONT);
        editButton.addActionListener(e -> editSelectedEvent());
        
        JButton deleteButton = new JButton("Delete");
        deleteButton.setFont(UIConstants.NORMAL_FONT);
        deleteButton.addActionListener(e -> deleteSelectedEvent());
        
        buttonPanel.add(viewButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        // Add components to panel
        panel.add(selectedDateLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Update the calendar grid based on the current month
     */
    private void updateCalendarView() {
        // Update month/year label
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        monthYearLabel.setText(currentDate.format(formatter));
        
        // Clear existing calendar cells
        calendarGrid.removeAll();
        
        // Get month information
        YearMonth yearMonth = YearMonth.of(currentDate.getYear(), currentDate.getMonth());
        int daysInMonth = yearMonth.lengthOfMonth();
        int firstDayOfWeek = yearMonth.atDay(1).getDayOfWeek().getValue() % 7; // Adjust for Sunday start
        
        // Add empty cells for days before the first day of the month
        for (int i = 0; i < firstDayOfWeek; i++) {
            JPanel emptyCell = createEmptyCalendarCell();
            calendarGrid.add(emptyCell);
        }
        
        // Add cells for each day of the month
        LocalDate today = LocalDate.now();
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), day);
            boolean isToday = date.equals(today);
            boolean isSelected = date.equals(selectedDate);
            
            // Get events for this day
            List<Event> dayEvents = eventsByDate.getOrDefault(date, new ArrayList<>());
            
            JPanel dayCell = createCalendarDayCell(date, isToday, isSelected, dayEvents);
            calendarGrid.add(dayCell);
        }
        
        // Add empty cells for remaining grid spaces
        int remainingCells = 42 - daysInMonth - firstDayOfWeek; // 6 rows * 7 days = 42
        for (int i = 0; i < remainingCells; i++) {
            JPanel emptyCell = createEmptyCalendarCell();
            calendarGrid.add(emptyCell);
        }
        
        // Refresh the view
        calendarGrid.revalidate();
        calendarGrid.repaint();
    }
    
    /**
     * Create an empty calendar cell
     * 
     * @return Empty cell panel
     */
    private JPanel createEmptyCalendarCell() {
        JPanel cell = new JPanel();
        cell.setBackground(Color.WHITE);
        cell.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return cell;
    }
    
    /**
     * Create a calendar cell for a specific day
     * 
     * @param date The date for the cell
     * @param isToday Whether this date is today
     * @param isSelected Whether this date is selected
     * @param events List of events on this date
     * @return Calendar day cell panel
     */
    private JPanel createCalendarDayCell(LocalDate date, boolean isToday, boolean isSelected, List<Event> events) {
        JPanel cell = new JPanel(new BorderLayout());
        
        // Set background colors
        if (isSelected) {
            cell.setBackground(new Color(230, 240, 255)); // Light blue for selected
        } else {
            cell.setBackground(Color.WHITE);
        }
        
        // Add border, highlight today's date
        if (isToday) {
            cell.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.SECONDARY_COLOR, 2),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)
            ));
        } else {
            cell.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
            ));
        }
        
        // Day number label
        JLabel dayLabel = new JLabel(Integer.toString(date.getDayOfMonth()));
        dayLabel.setFont(UIConstants.NORMAL_FONT);
        if (isToday) {
            dayLabel.setForeground(UIConstants.SECONDARY_COLOR);
            dayLabel.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        }
        
        // Create a panel for the day number with padding
        JPanel dayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dayPanel.setOpaque(false);
        dayPanel.add(dayLabel);
        
        cell.add(dayPanel, BorderLayout.NORTH);
        
        // Add events if there are any
        if (!events.isEmpty()) {
            // Create events panel
            JPanel eventsPanel = new JPanel();
            eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
            eventsPanel.setOpaque(false);
            
            // Show up to 3 events, with a "+X more" if there are more
            int eventsToShow = Math.min(events.size(), 3);
            for (int i = 0; i < eventsToShow; i++) {
                Event event = events.get(i);
                JLabel eventLabel = createEventLabel(event);
                eventsPanel.add(eventLabel);
            }
            
            // Add a "+X more" label if needed
            if (events.size() > 3) {
                JLabel moreLabel = new JLabel("+" + (events.size() - 3) + " more");
                moreLabel.setFont(UIConstants.SMALL_FONT);
                moreLabel.setForeground(UIConstants.PRIMARY_COLOR);
                eventsPanel.add(moreLabel);
            }
            
            cell.add(eventsPanel, BorderLayout.CENTER);
        }
        
        // Make cell interactive
        cell.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedDate = date;
                loadEventsForSelectedDate();
                updateCalendarView(); // Refresh to show selection
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isToday && !isSelected) {
                    cell.setBackground(UIConstants.ACCENT_COLOR);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!isToday && !isSelected) {
                    cell.setBackground(Color.WHITE);
                }
            }
        });
        
        return cell;
    }
    
    /**
     * Create a label for an event in the calendar cell
     * 
     * @param event The event
     * @return Styled event label
     */
    private JLabel createEventLabel(Event event) {
        JLabel label = new JLabel();
        label.setText(truncateText(event.getTitle(), 15));
        label.setFont(UIConstants.SMALL_FONT);
        
        // Set color based on event type
        Color eventColor = getEventTypeColor(event.getEventType());
        label.setForeground(eventColor);
        
        // Add a dot before the text
        label.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));
        
        return label;
    }
    
    /**
     * Get color for event type
     * 
     * @param eventType Type of event
     * @return Color for the event type
     */
    private Color getEventTypeColor(String eventType) {
        if (eventType == null) {
            return UIConstants.PRIMARY_COLOR;
        }
        
        switch (eventType.toLowerCase()) {
            case "court appearance":
            case "hearing":
            case "trial":
                return new Color(176, 42, 55); // Red
                
            case "meeting":
            case "conference call":
                return new Color(46, 204, 113); // Green
                
            case "deadline":
            case "filing":
                return new Color(241, 196, 15); // Yellow/Orange
                
            case "deposition":
                return new Color(155, 89, 182); // Purple
                
            default:
                return UIConstants.PRIMARY_COLOR; // Default blue
        }
    }
    
    /**
     * Truncate text if it's too long
     * 
     * @param text Text to truncate
     * @param maxLength Maximum length
     * @return Truncated text
     */
    private String truncateText(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        
        if (text.length() <= maxLength) {
            return text;
        }
        
        return text.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Load events from the database
     */
    private void loadEvents() {
        try {
            // Clear existing events
            eventsByDate.clear();
            
            // Get start and end date for the current month view
            YearMonth yearMonth = YearMonth.of(currentDate.getYear(), currentDate.getMonth());
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();
            
            // Expand range to include days from previous/next month that appear in the view
            int firstDayOfWeek = startDate.getDayOfWeek().getValue() % 7; // Adjust for Sunday start
            startDate = startDate.minusDays(firstDayOfWeek);
            
            int lastDayOfWeek = endDate.getDayOfWeek().getValue() % 7;
            endDate = endDate.plusDays(6 - lastDayOfWeek);
            
            // Get events for the date range
            List<Event> events = eventController.findEventsByDateRange(startDate, endDate);
            
            // Organize events by date
            for (Event event : events) {
                LocalDate eventDate = event.getEventDate();
                if (eventDate != null) {
                    List<Event> dateEvents = eventsByDate.computeIfAbsent(eventDate, k -> new ArrayList<>());
                    dateEvents.add(event);
                }
            }
            
            // Update calendar and event list
            updateCalendarView();
            loadEventsForSelectedDate();
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error loading events: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Load events for the selected date
     */
    private void loadEventsForSelectedDate() {
        // Update selected date label
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        selectedDateLabel.setText("Events for " + selectedDate.format(formatter));
        
        // Clear existing events
        eventsListModel.clear();
        
        // Get events for selected date
        List<Event> events = eventsByDate.getOrDefault(selectedDate, new ArrayList<>());
        
        // Add events to the list model
        for (Event event : events) {
            eventsListModel.addElement(event);
        }
    }
    
    /**
     * Create a new event
     */
    private void createNewEvent() {
        // Show the event editor dialog
        EventEditorDialog dialog = new EventEditorDialog(
            SwingUtilities.getWindowAncestor(this), 
            null,
            selectedDate,
            eventController,
            caseController
        );
        
        dialog.setVisible(true);
        
        // Refresh the calendar if an event was created
        if (dialog.isEventSaved()) {
            loadEvents();
        }
    }
    
    /**
     * View details of the selected event
     */
    private void viewEventDetails() {
        Event selectedEvent = dayEventsList.getSelectedValue();
        if (selectedEvent == null) {
            SwingUtils.showInfoMessage(
                this,
                "Please select an event to view.",
                "No Event Selected"
            );
            return;
        }
        
        // Show event details in a dialog
        EventDetailsDialog dialog = new EventDetailsDialog(
            SwingUtilities.getWindowAncestor(this),
            selectedEvent,
            eventController,
            caseController
        );
        
        dialog.setVisible(true);
        
        // Refresh the calendar if the event was modified
        if (dialog.isEventModified()) {
            loadEvents();
        }
    }
    
    /**
     * Edit the selected event
     */
    private void editSelectedEvent() {
        Event selectedEvent = dayEventsList.getSelectedValue();
        if (selectedEvent == null) {
            SwingUtils.showInfoMessage(
                this,
                "Please select an event to edit.",
                "No Event Selected"
            );
            return;
        }
        
        // Show the event editor dialog
        EventEditorDialog dialog = new EventEditorDialog(
            SwingUtilities.getWindowAncestor(this), 
            selectedEvent,
            selectedDate,
            eventController,
            caseController
        );
        
        dialog.setVisible(true);
        
        // Refresh the calendar if the event was edited
        if (dialog.isEventSaved()) {
            loadEvents();
        }
    }
    
    /**
     * Delete the selected event
     */
    private void deleteSelectedEvent() {
        Event selectedEvent = dayEventsList.getSelectedValue();
        if (selectedEvent == null) {
            SwingUtils.showInfoMessage(
                this,
                "Please select an event to delete.",
                "No Event Selected"
            );
            return;
        }
        
        // Confirm deletion
        boolean confirmed = SwingUtils.showConfirmDialog(
            this,
            "Are you sure you want to delete the event '" + selectedEvent.getTitle() + "'?",
            "Confirm Deletion"
        );
        
        if (confirmed) {
            try {
                // Delete the event
                boolean success = eventController.deleteEvent(selectedEvent.getId());
                
                if (success) {
                    SwingUtils.showInfoMessage(
                        this,
                        "Event deleted successfully.",
                        "Success"
                    );
                    
                    // Refresh events
                    loadEvents();
                } else {
                    SwingUtils.showErrorMessage(
                        this,
                        "Failed to delete event.",
                        "Deletion Error"
                    );
                }
                
            } catch (Exception e) {
                SwingUtils.showErrorMessage(
                    this,
                    "Error deleting event: " + e.getMessage(),
                    "Database Error"
                );
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Custom cell renderer for events in the list
     */
    private class EventListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Event) {
                Event event = (Event) value;
                
                // Format the time if available
                String timeText = "";
                if (event.getStartTime() != null) {
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
                    timeText = event.getStartTime().format(timeFormatter);
                    
                    if (event.getEndTime() != null) {
                        timeText += " - " + event.getEndTime().format(timeFormatter);
                    }
                    
                    timeText += " • ";
                }
                
                // Set text with time and title
                setText(timeText + event.getTitle());
                
                // Set icon based on event type
                // (You can add icons later)
                
                // Set foreground color based on event type if not selected
                if (!isSelected) {
                    setForeground(getEventTypeColor(event.getEventType()));
                }
            }
            
            return this;
        }
    }
}