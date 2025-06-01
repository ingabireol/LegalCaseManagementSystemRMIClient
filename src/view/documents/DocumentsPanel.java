package view.documents;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.File;

import model.Document;
import model.Case;
import controller.DocumentController;
import controller.CaseController;
import view.components.CustomTable;
import view.components.TableFilterPanel;
import view.components.StatusIndicator;
import view.util.UIConstants;
import view.util.SwingUtils;

/**
 * Panel for document management in the Legal Case Management System.
 */
public class DocumentsPanel extends JPanel {
    private DocumentController documentController;
    private CaseController caseController;
    private CustomTable documentsTable;
    private DocumentFilterPanel filterPanel;
    
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewDetailsButton;
    private JButton downloadButton;
    
    /**
     * Constructor
     */
    public DocumentsPanel() {
        this.documentController = new DocumentController();
        this.caseController = new CaseController();
        
        initializeUI();
        loadDocuments();
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create table panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create the header panel with title and filters
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
        
        JLabel titleLabel = new JLabel("Documents Management");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Filter panel
        filterPanel = new DocumentFilterPanel();
        headerPanel.add(filterPanel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    /**
     * Create the table panel with documents table
     * 
     * @return The table panel
     */
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Create table
        String[] columnNames = {
            "Document ID", "Title", "Type", "Case", "Date Added", "Document Date", "Status"
        };
        documentsTable = new CustomTable(columnNames);
        
        // Set column widths
        documentsTable.setColumnWidth(0, 100);  // Document ID
        documentsTable.setColumnWidth(1, 250);  // Title
        documentsTable.setColumnWidth(2, 120);  // Type
        documentsTable.setColumnWidth(3, 150);  // Case
        documentsTable.setColumnWidth(4, 100);  // Date Added
        documentsTable.setColumnWidth(5, 100);  // Document Date
        documentsTable.setColumnWidth(6, 80);   // Status
        
        // Add double-click listener to open document details
        documentsTable.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && documentsTable.getSelectedRow() != -1) {
                    viewDocumentDetails();
                }
            }
        });
        
        // Enable/disable buttons based on selection
        documentsTable.addSelectionListener(e -> updateButtonStates());
        
        // Add a custom renderer for the Status column
        documentsTable.setColumnRenderer(6, (table, value, isSelected, hasFocus, row, column) -> {
            if (value == null) {
                return new JLabel();
            }
            
            StatusIndicator indicator = new StatusIndicator(value.toString());
            if (isSelected) {
                indicator.setBackground(table.getSelectionBackground());
            }
            return indicator;
        });
        
        tablePanel.add(documentsTable, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    /**
     * Create the button panel with action buttons
     * 
     * @return The button panel
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Create buttons
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(UIConstants.NORMAL_FONT);
        refreshButton.addActionListener(e -> loadDocuments());
        
        downloadButton = new JButton("Download");
        downloadButton.setFont(UIConstants.NORMAL_FONT);
        downloadButton.addActionListener(e -> downloadSelectedDocument());
        
        viewDetailsButton = new JButton("View Details");
        viewDetailsButton.setFont(UIConstants.NORMAL_FONT);
        viewDetailsButton.addActionListener(e -> viewDocumentDetails());
        
        editButton = new JButton("Edit Document");
        editButton.setFont(UIConstants.NORMAL_FONT);
        editButton.addActionListener(e -> editSelectedDocument());
        
        deleteButton = new JButton("Delete Document");
        deleteButton.setFont(UIConstants.NORMAL_FONT);
        deleteButton.addActionListener(e -> deleteSelectedDocument());
        
        addButton = new JButton("Upload Document");
        addButton.setFont(UIConstants.NORMAL_FONT);
        addButton.setBackground(UIConstants.SECONDARY_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> uploadNewDocument());
        
        // Add buttons to panel
        buttonPanel.add(refreshButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(downloadButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(editButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createHorizontalStrut(30));
        buttonPanel.add(addButton);
        
        // Initialize button states
        downloadButton.setEnabled(false);
        viewDetailsButton.setEnabled(false);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        return buttonPanel;
    }
    
    /**
     * Load documents from the database
     */
    private void loadDocuments() {
        try {
            // Clear existing data
            documentsTable.clearTable();
            documentsTable.clearFilters();
            
            // Get documents from controller
            List<Document> documents;
            
            String filterType = filterPanel.getSelectedFilterType();
            String searchText = filterPanel.getSearchText();
            
            if (searchText != null && !searchText.isEmpty()) {
                if ("Title".equals(filterType)) {
                    documents = documentController.findDocumentsByText(searchText);
                } else if ("Type".equals(filterType)) {
                    documents = documentController.findDocumentsByType(searchText);
                } else if ("Case".equals(filterType)) {
                    // Get case by title or number
                    List<Case> cases = caseController.findCasesByText(searchText);
                    documents = new java.util.ArrayList<>();
                    for (Case legalCase : cases) {
                        documents.addAll(documentController.findDocumentsByCase(legalCase.getId()));
                    }
                } else {
                    // Apply filter to the view instead of database for "All"
                    documents = documentController.getAllDocuments();
                    documentsTable.addFilter(1, searchText); // Title column
                    documentsTable.addFilter(2, searchText); // Type column
                }
            } else {
                documents = documentController.getAllDocuments();
            }
            
            // Load case information for documents
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            for (Document document : documents) {
                // Get case information if needed
                if (document.getCase() == null && document.getCase().getId()> 0) {
                    Case legalCase = caseController.getCaseById(document.getCase().getId());
                    document.setCase(legalCase);
                }
                
                String caseInfo = document.getCase() != null ? 
                        document.getCase().getCaseNumber() + " - " + document.getCase().getTitle() : "N/A";
                
                Object[] row = {
                    document.getDocumentId(),
                    document.getTitle(),
                    document.getDocumentType(),
                    caseInfo,
                    document.getDateAdded().format(dateFormatter),
                    document.getDocumentDate() != null ? document.getDocumentDate().format(dateFormatter) : "N/A",
                    document.getStatus()
                };
                documentsTable.addRow(row);
            }
            
            // Display a message if no documents found
            if (documents.isEmpty() && (searchText == null || searchText.isEmpty())) {
                SwingUtils.showInfoMessage(
                    this,
                    "No documents found. Upload a new document to get started.",
                    "No Documents"
                );
            }
            
            // Update button states
            updateButtonStates();
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error loading documents: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Update the enabled state of buttons based on table selection
     */
    private void updateButtonStates() {
        boolean hasSelection = documentsTable.getSelectedRow() != -1;
        downloadButton.setEnabled(hasSelection);
        viewDetailsButton.setEnabled(hasSelection);
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
    }
    
    /**
     * View details of the selected document
     */
    private void viewDocumentDetails() {
        int selectedRow = documentsTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        // Get document ID from selected row
        String documentId = documentsTable.getValueAt(selectedRow, 0).toString();
        
        try {
            // Get the document
            Document document = documentController.getDocumentByDocumentId(documentId);
            
            if (document != null) {
                // Open document details dialog
                DocumentDetailsDialog dialog = new DocumentDetailsDialog(
                    SwingUtilities.getWindowAncestor(this), document);
                dialog.setVisible(true);
                
                // Refresh the documents list after the dialog is closed
                loadDocuments();
            }
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error viewing document details: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Upload a new document
     * This method is public so it can be called from other panels, like MainView
     */
    public void uploadNewDocument() {
        try {
            // Show file chooser dialog
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                
                // Open document editor dialog
                DocumentEditorDialog dialog = new DocumentEditorDialog(
                    SwingUtilities.getWindowAncestor(this), null, selectedFile);
                dialog.setVisible(true);
                
                // Refresh the documents list if a document was added
                if (dialog.isDocumentSaved()) {
                    loadDocuments();
                }
            }
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error uploading document: " + e.getMessage(),
                "Upload Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Edit the selected document
     */
    private void editSelectedDocument() {
        int selectedRow = documentsTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        // Get document ID from selected row
        String documentId = documentsTable.getValueAt(selectedRow, 0).toString();
        
        try {
            // Get the document
            Document document = documentController.getDocumentByDocumentId(documentId);
            
            if (document != null) {
                // Open document editor dialog
                DocumentEditorDialog dialog = new DocumentEditorDialog(
                    SwingUtilities.getWindowAncestor(this), document, null);
                dialog.setVisible(true);
                
                // Refresh the documents list if the document was updated
                if (dialog.isDocumentSaved()) {
                    loadDocuments();
                }
            }
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error editing document: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Delete the selected document
     */
    private void deleteSelectedDocument() {
        int selectedRow = documentsTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        // Get document info from selected row
        String documentId = documentsTable.getValueAt(selectedRow, 0).toString();
        String documentTitle = documentsTable.getValueAt(selectedRow, 1).toString();
        
        // Confirm deletion
        boolean confirmed = SwingUtils.showConfirmDialog(
            this,
            "Are you sure you want to delete document '" + documentTitle + "' (" + documentId + ")?\n" +
            "This action cannot be undone, and the file will be permanently deleted.",
            "Confirm Deletion"
        );
        
        if (confirmed) {
            try {
                // Get the document
                Document document = documentController.getDocumentByDocumentId(documentId);
                
                if (document != null) {
                    // Delete the document
                    boolean success = documentController.deleteDocument(document.getId());
                    
                    if (success) {
                        SwingUtils.showInfoMessage(
                            this,
                            "Document deleted successfully.",
                            "Success"
                        );
                        
                        // Refresh the documents list
                        loadDocuments();
                    } else {
                        SwingUtils.showErrorMessage(
                            this,
                            "Failed to delete document. It may be in use by the system.",
                            "Deletion Error"
                        );
                    }
                }
                
            } catch (Exception e) {
                SwingUtils.showErrorMessage(
                    this,
                    "Error deleting document: " + e.getMessage(),
                    "Database Error"
                );
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Download the selected document
     */
    private void downloadSelectedDocument() {
        int selectedRow = documentsTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        // Get document ID from selected row
        String documentId = documentsTable.getValueAt(selectedRow, 0).toString();
        
        try {
            // Get the document
            Document document = documentController.getDocumentByDocumentId(documentId);
            
            if (document != null) {
                // Show save file dialog
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File(document.getFilePath()));
                int result = fileChooser.showSaveDialog(this);
                
                if (result == JFileChooser.APPROVE_OPTION) {
                    File outputFile = fileChooser.getSelectedFile();
                    
                    // Get document content and save to file
                    byte[] content = documentController.getDocumentContent(document.getId());
                    if (content != null) {
                        try {
                            java.nio.file.Files.write(outputFile.toPath(), content);
                            SwingUtils.showInfoMessage(
                                this,
                                "Document downloaded successfully.",
                                "Success"
                            );
                        } catch (Exception e) {
                            SwingUtils.showErrorMessage(
                                this,
                                "Error saving document: " + e.getMessage(),
                                "Download Error"
                            );
                            e.printStackTrace();
                        }
                    } else {
                        SwingUtils.showErrorMessage(
                            this,
                            "Could not read document content.",
                            "Download Error"
                        );
                    }
                }
            }
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error downloading document: " + e.getMessage(),
                "Database Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Custom filter panel for documents
     */
    private class DocumentFilterPanel extends TableFilterPanel {
        /**
         * Constructor
         */
        public DocumentFilterPanel() {
            super(
                new String[]{"All", "Title", "Type", "Case"},
                    
                searchText -> loadDocuments(),
                () -> {
                    documentsTable.clearFilters();
                    loadDocuments();
                }
            );
        }
        
        /**
         * Apply filters based on filter type
         */
        private void applyFilters() {
            loadDocuments();
        }
    }
}