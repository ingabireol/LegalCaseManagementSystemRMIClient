package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;
import model.Event;

/**
 * Remote service interface for Event operations
 */
public interface EventService extends Remote {
    
    /**
     * Creates a new event
     */
    Event createEvent(Event event) throws RemoteException;
    
    /**
     * Updates an existing event
     */
    Event updateEvent(Event event) throws RemoteException;
    
    /**
     * Deletes an event
     */
    Event deleteEvent(Event event) throws RemoteException;
    
    /**
     * Updates the status of an event
     */
    Event updateEventStatus(Event event, String status) throws RemoteException;
    
    /**
     * Finds an event by ID
     */
    Event findEventById(Event event) throws RemoteException;
    
    /**
     * Finds an event by event ID
     */
    Event findEventByEventId(String eventId) throws RemoteException;
    
    /**
     * Finds events by case ID
     */
    List<Event> findEventsByCase(int caseId) throws RemoteException;
    
    /**
     * Finds events by date
     */
    List<Event> findEventsByDate(LocalDate date) throws RemoteException;
    
    /**
     * Finds events by date range
     */
    List<Event> findEventsByDateRange(LocalDate startDate, LocalDate endDate) throws RemoteException;
    
    /**
     * Finds events by status
     */
    List<Event> findEventsByStatus(String status) throws RemoteException;
    
    /**
     * Finds upcoming events with reminders due
     */
    List<Event> findUpcomingEventsWithReminders() throws RemoteException;
    
    /**
     * Gets all events
     */
    List<Event> findAllEvents() throws RemoteException;
    
    /**
     * Gets an event with its case information
     */
    Event getEventWithCase(Event event) throws RemoteException;
}