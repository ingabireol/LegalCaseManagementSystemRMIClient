package controller;

import model.Event;
import model.Case;
import service.EventService;
import service.CaseService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.time.LocalDate;

/**
 * Controller for event-related operations using RMI.
 */
public class EventController {
    private EventService eventService;
    private CaseService caseService;
    private Registry registry;
    
    // RMI server configuration
    private static final String RMI_HOST = "127.0.0.1";
    private static final int RMI_PORT = 5555;
    
    /**
     * Constructor - establishes RMI connection
     */
    public EventController() {
        try {
            // Locate RMI registry
            registry = LocateRegistry.getRegistry(RMI_HOST, RMI_PORT);
            
            // Get service stubs
            eventService = (EventService) registry.lookup("eventService");
            caseService = (CaseService) registry.lookup("caseService");
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to connect to RMI server: " + ex.getMessage());
        }
    }
    
    /**
     * Get all events
     * 
     * @return List of all events
     */
    public List<Event> getAllEvents() {
        try {
            return eventService.findAllEvents();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get an event by ID
     * 
     * @param id The event ID
     * @return The event
     */
    public Event getEventById(int id) {
        try {
            Event searchEvent = new Event();
            searchEvent.setId(id);
            return eventService.findEventById(searchEvent);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get an event by event ID
     * 
     * @param eventId The event ID string
     * @return The event
     */
    public Event getEventByEventId(String eventId) {
        try {
            return eventService.findEventByEventId(eventId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get an event with case information
     * 
     * @param id The event ID
     * @return The event with case loaded
     */
    public Event getEventWithCase(int id) {
        try {
            Event searchEvent = new Event();
            searchEvent.setId(id);
            return eventService.getEventWithCase(searchEvent);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find events by case
     * 
     * @param caseId The case ID
     * @return List of events for the case
     */
    public List<Event> findEventsByCase(int caseId) {
        try {
            return eventService.findEventsByCase(caseId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find events by date
     * 
     * @param date The date to search for
     * @return List of events on the date
     */
    public List<Event> findEventsByDate(LocalDate date) {
        try {
            return eventService.findEventsByDate(date);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find events by date range
     * 
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @return List of events in the date range
     */
    public List<Event> findEventsByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            return eventService.findEventsByDateRange(startDate, endDate);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find events by status
     * 
     * @param status The status
     * @return List of events with the status
     */
    public List<Event> findEventsByStatus(String status) {
        try {
            return eventService.findEventsByStatus(status);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find upcoming events with reminders due
     * 
     * @return List of events with due reminders
     */
    public List<Event> findUpcomingEventsWithReminders() {
        try {
            return eventService.findUpcomingEventsWithReminders();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Create a new event
     * 
     * @param event The event to create
     * @return true if successful
     */
    public boolean createEvent(Event event) {
        try {
            Event result = eventService.createEvent(event);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update an existing event
     * 
     * @param event The event to update
     * @return true if successful
     */
    public boolean updateEvent(Event event) {
        try {
            Event result = eventService.updateEvent(event);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update event status
     * 
     * @param eventId The event ID
     * @param status The new status
     * @return true if successful
     */
    public boolean updateEventStatus(int eventId, String status) {
        try {
            Event searchEvent = new Event();
            searchEvent.setId(eventId);
            Event result = eventService.updateEventStatus(searchEvent, status);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete an event
     * 
     * @param eventId The event ID
     * @return true if successful
     */
    public boolean deleteEvent(int eventId) {
        try {
            Event searchEvent = new Event();
            searchEvent.setId(eventId);
            Event result = eventService.deleteEvent(searchEvent);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get available event types
     * 
     * @return Array of event types
     */
    public String[] getEventTypes() {
        return new String[] {
            "Court Appearance", "Hearing", "Meeting", "Deposition", 
            "Trial", "Deadline", "Filing", "Conference Call", "Other"
        };
    }
    
    /**
     * Get available event statuses
     * 
     * @return Array of event statuses
     */
    public String[] getEventStatuses() {
        return new String[] {
            "Scheduled", "Completed", "Cancelled", "Postponed", "Rescheduled"
        };
    }
}