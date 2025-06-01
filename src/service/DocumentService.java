package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;
import model.Document;

/**
 * Remote service interface for Document operations
 */
public interface DocumentService extends Remote {
    
    /**
     * Creates a new document
     */
    Document createDocument(Document document) throws RemoteException;
    
    /**
     * Updates an existing document
     */
    Document updateDocument(Document document) throws RemoteException;
    
    /**
     * Deletes a document
     */
    Document deleteDocument(Document document) throws RemoteException;
    
    /**
     * Updates the status of a document
     */
    Document updateDocumentStatus(Document document, String status) throws RemoteException;
    
    /**
     * Finds a document by ID
     */
    Document findDocumentById(Document document) throws RemoteException;
    
    /**
     * Finds a document by document ID
     */
    Document findDocumentByDocumentId(String documentId) throws RemoteException;
    
    /**
     * Finds documents by title or description
     */
    List<Document> findDocumentsByText(String searchText) throws RemoteException;
    
    /**
     * Finds documents by case ID
     */
    List<Document> findDocumentsByCase(int caseId) throws RemoteException;
    
    /**
     * Finds documents by document type
     */
    List<Document> findDocumentsByType(String documentType) throws RemoteException;
    
    /**
     * Finds documents by date range
     */
    List<Document> findDocumentsByDateRange(LocalDate startDate, LocalDate endDate) throws RemoteException;
    
    /**
     * Gets all documents
     */
    List<Document> findAllDocuments() throws RemoteException;
    
    /**
     * Gets a document with its case information
     */
    Document getDocumentWithCase(Document document) throws RemoteException;
}