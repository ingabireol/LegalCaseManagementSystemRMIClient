package controller;

import model.Document;
import model.Case;
import service.DocumentService;
import service.CaseService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.time.LocalDate;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Controller for document-related operations using RMI.
 */
public class DocumentController {
    private DocumentService documentService;
    private CaseService caseService;
    private Registry registry;
    private final String UPLOAD_DIRECTORY = "uploads/documents/";
    
    // RMI server configuration
    private static final String RMI_HOST = "127.0.0.1";
    private static final int RMI_PORT = 5555;
    
    /**
     * Constructor - establishes RMI connection
     */
    public DocumentController() {
        try {
            // Locate RMI registry
            registry = LocateRegistry.getRegistry(RMI_HOST, RMI_PORT);
            
            // Get service stubs
            documentService = (DocumentService) registry.lookup("documentService");
            caseService = (CaseService) registry.lookup("caseService");
            
            // Ensure upload directory exists
            File directory = new File(UPLOAD_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to connect to RMI server: " + ex.getMessage());
        }
    }
    
    /**
     * Get all documents
     * 
     * @return List of all documents
     */
    public List<Document> getAllDocuments() {
        try {
            return documentService.findAllDocuments();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get a document by ID
     * 
     * @param id The document ID
     * @return The document
     */
    public Document getDocumentById(int id) {
        try {
            Document searchDocument = new Document();
            searchDocument.setId(id);
            return documentService.findDocumentById(searchDocument);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get a document by document ID
     * 
     * @param documentId The document ID string
     * @return The document
     */
    public Document getDocumentByDocumentId(String documentId) {
        try {
            return documentService.findDocumentByDocumentId(documentId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get a document with case information
     * 
     * @param id The document ID
     * @return The document with case loaded
     */
    public Document getDocumentWithCase(int id) {
        try {
            Document searchDocument = new Document();
            searchDocument.setId(id);
            return documentService.getDocumentWithCase(searchDocument);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find documents by text
     * 
     * @param searchText Text to search for
     * @return List of matching documents
     */
    public List<Document> findDocumentsByText(String searchText) {
        try {
            return documentService.findDocumentsByText(searchText);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find documents by type
     * 
     * @param documentType Type to search for
     * @return List of matching documents
     */
    public List<Document> findDocumentsByType(String documentType) {
        try {
            return documentService.findDocumentsByType(documentType);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find documents by case
     * 
     * @param caseId The case ID
     * @return List of documents for the case
     */
    public List<Document> findDocumentsByCase(int caseId) {
        try {
            return documentService.findDocumentsByCase(caseId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find documents by date range
     * 
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @return List of documents in the date range
     */
    public List<Document> findDocumentsByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            return documentService.findDocumentsByDateRange(startDate, endDate);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Create a new document record and save the file
     * 
     * @param document The document to create
     * @param file The uploaded file
     * @return true if successful
     */
    public boolean createDocument(Document document, File file) {
        try {
            // Generate file path
            String fileName = System.currentTimeMillis() + "_" + file.getName();
            String filePath = UPLOAD_DIRECTORY + fileName;
            
            // Copy file to upload directory
            Path targetPath = Paths.get(filePath);
            Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Set file path in document
            document.setFilePath(filePath);
            
            // Set date added if not set
            if (document.getDateAdded() == null) {
                document.setDateAdded(LocalDate.now());
            }
            
            // Save document record via RMI
            Document result = documentService.createDocument(document);
            return result != null;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Create a new document without file (for document references)
     * 
     * @param document The document to create
     * @return true if successful
     */
    public boolean createDocument(Document document) {
        try {
            // Set date added if not set
            if (document.getDateAdded() == null) {
                document.setDateAdded(LocalDate.now());
            }
            
            // Save document record via RMI
            Document result = documentService.createDocument(document);
            return result != null;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update an existing document
     * 
     * @param document The document to update
     * @return true if successful
     */
    public boolean updateDocument(Document document) {
        try {
            Document result = documentService.updateDocument(document);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update document status
     * 
     * @param documentId The document ID
     * @param status The new status
     * @return true if successful
     */
    public boolean updateDocumentStatus(int documentId, String status) {
        try {
            Document searchDocument = new Document();
            searchDocument.setId(documentId);
            Document result = documentService.updateDocumentStatus(searchDocument, status);
            return result != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete a document
     * 
     * @param documentId The document ID
     * @return true if successful
     */
    public boolean deleteDocument(int documentId) {
        try {
            // Get document to get file path
            Document document = getDocumentById(documentId);
            if (document == null) {
                return false;
            }
            
            // Delete file if exists
            String filePath = document.getFilePath();
            if (filePath != null && !filePath.isEmpty()) {
                Path path = Paths.get(filePath);
                Files.deleteIfExists(path);
            }
            
            // Delete database record via RMI
            Document result = documentService.deleteDocument(document);
            return result != null;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get the file content of a document
     * 
     * @param documentId The document ID
     * @return The file content as byte array, or null if error
     */
    public byte[] getDocumentContent(int documentId) {
        try {
            Document document = getDocumentById(documentId);
            if (document == null || document.getFilePath() == null) {
                return null;
            }
            
            Path path = Paths.get(document.getFilePath());
            return Files.readAllBytes(path);
            
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get available document types
     * 
     * @return Array of document types
     */
    public String[] getDocumentTypes() {
        return new String[] {
            "Pleading", "Motion", "Brief", "Contract", "Letter", 
            "Email", "Invoice", "Receipt", "Court Order",
            "Settlement Agreement", "Evidence", "Other"
        };
    }
}