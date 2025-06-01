package view.documents;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import model.Document;
import model.Case;
import controller.DocumentController;
import controller.CaseController;
import view.util.UIConstants;
import view.components.DateChooser;

/**
 * Dialog for adding or editing a document.
 */
public class DocumentEditorDialog extends JDialog {
    private JTextField documentIdField;
    private JTextField titleField;
    private JComboBox<String> documentTypeCombo;
    private JComboBox<CaseItem> caseCombo;
    private JTextArea descriptionArea;
    private DateChooser dateAddedChooser;
    private DateChooser documentDateChooser;
    private JLabel filePathLabel;
    private JComboBox<String> statusCombo;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    private Document document;
    private File uploadFile;
    private DocumentController documentController;
    private CaseController caseController;
    private boolean documentSaved = false;
    
    /**
     * Constructor for creating/editing a document
     * 
     * @param parent The parent window
     * @param document The document to edit, or null for a new document
     * @param uploadFile The file to upload, or null if editing
     */
    public DocumentEditorDialog(Window parent, Document document, File uploadFile) {
        super(parent, document == null ? "Upload New Document" : "Edit Document", ModalityType.APPLICATION_MODAL);
        
        this.document = document;
        this.uploadFile = uploadFile;
        this.documentController = new DocumentController();
        this.caseController = new CaseController();
        
        initializeUI();
        loadDocumentData();
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setSize(600, 600);
        setMinimumSize(new Dimension(500, 500));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Create form panel
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create the form panel with input fields
     * 
     * @return The form panel
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = GridBagConstraints.RELATIVE;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(5, 5, 5, 10);
        
        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = GridBagConstraints.RELATIVE;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1.0;
        fieldConstraints.insets = new Insets(5, 0, 5, 5);
        
        // Document ID
        JLabel documentIdLabel = new JLabel("Document ID:");
        documentIdLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(documentIdLabel, labelConstraints);
        
        documentIdField = new JTextField(20);
        documentIdField.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(documentIdField, fieldConstraints);
        
        // Title
        JLabel titleLabel = new JLabel("Title:*");
        titleLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(titleLabel, labelConstraints);
        
        titleField = new JTextField(20);
        titleField.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(titleField, fieldConstraints);
        
        // Document Type
        JLabel documentTypeLabel = new JLabel("Document Type:*");
        documentTypeLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(documentTypeLabel, labelConstraints);
        
        documentTypeCombo = new JComboBox<>(documentController.getDocumentTypes());
        documentTypeCombo.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(documentTypeCombo, fieldConstraints);
        
        // Associated Case
        JLabel caseLabel = new JLabel("Associated Case:*");
        caseLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(caseLabel, labelConstraints);
        
        caseCombo = new JComboBox<>();
        caseCombo.setFont(UIConstants.NORMAL_FONT);
        loadCases();
        formPanel.add(caseCombo, fieldConstraints);
        
        // File Path (for display only)
        JLabel filePathTitleLabel = new JLabel("File:");
        filePathTitleLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(filePathTitleLabel, labelConstraints);
        
        filePathLabel = new JLabel();
        filePathLabel.setFont(UIConstants.NORMAL_FONT);
        if (uploadFile != null) {
            filePathLabel.setText(uploadFile.getName());
        }
        formPanel.add(filePathLabel, fieldConstraints);
        
        // Document Date
        JLabel documentDateLabel = new JLabel("Document Date:");
        documentDateLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(documentDateLabel, labelConstraints);
        
        documentDateChooser = new DateChooser();
        formPanel.add(documentDateChooser, fieldConstraints);
        
        // Date Added
        JLabel dateAddedLabel = new JLabel("Date Added:");
        dateAddedLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(dateAddedLabel, labelConstraints);
        
        dateAddedChooser = new DateChooser(LocalDate.now());
        formPanel.add(dateAddedChooser, fieldConstraints);
        
        // Status
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(statusLabel, labelConstraints);
        
        statusCombo = new JComboBox<>(new String[]{"Active", "Archived", "Draft", "Pending"});
        statusCombo.setFont(UIConstants.NORMAL_FONT);
        formPanel.add(statusCombo, fieldConstraints);
        
        // Description
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(UIConstants.LABEL_FONT);
        formPanel.add(descriptionLabel, labelConstraints);
        
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setFont(UIConstants.NORMAL_FONT);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionScrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        GridBagConstraints textAreaConstraints = new GridBagConstraints();
        textAreaConstraints.gridx = 1;
        textAreaConstraints.gridy = GridBagConstraints.RELATIVE;
        textAreaConstraints.fill = GridBagConstraints.BOTH;
        textAreaConstraints.weightx = 1.0;
        textAreaConstraints.weighty = 1.0;
        textAreaConstraints.insets = new Insets(5, 0, 5, 5);
        
        formPanel.add(descriptionScrollPane, textAreaConstraints);
        
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
     * Create the button panel with action buttons
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
        
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(UIConstants.NORMAL_FONT);
        cancelButton.addActionListener(e -> dispose());
        
        saveButton = new JButton(document == null ? "Upload Document" : "Save Document");
        saveButton.setFont(UIConstants.NORMAL_FONT);
        saveButton.setBackground(UIConstants.SECONDARY_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveDocument());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(saveButton);
        
        return buttonPanel;
    }
    
    /**
     * Load cases for the combo box
     */
    private void loadCases() {
        try {
            List<Case> cases = caseController.getAllCases();
            caseCombo.removeAllItems();
            
            for (Case legalCase : cases) {
                caseCombo.addItem(new CaseItem(legalCase));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Error loading cases: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Load document data into form fields if editing an existing document
     */
    private void loadDocumentData() {
        if (document != null) {
            // Populate form fields with document data
            documentIdField.setText(document.getDocumentId());
            titleField.setText(document.getTitle());
            
            // Set document type
            for (int i = 0; i < documentTypeCombo.getItemCount(); i++) {
                if (documentTypeCombo.getItemAt(i).equals(document.getDocumentType())) {
                    documentTypeCombo.setSelectedIndex(i);
                    break;
                }
            }
            
            // Set case
            if (document.getCase() == null && document.getCase().getId()> 0) {
                try {
                    Case legalCase = caseController.getCaseById(document.getCase().getId());
                    document.setCase(legalCase);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            if (document.getCase() != null) {
                for (int i = 0; i < caseCombo.getItemCount(); i++) {
                    CaseItem item = caseCombo.getItemAt(i);
                    if (item.getCase().getId() == document.getCase().getId()) {
                        caseCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            descriptionArea.setText(document.getDescription());
            
            if (document.getDocumentDate() != null) {
                documentDateChooser.setDate(document.getDocumentDate());
            }
            
            dateAddedChooser.setDate(document.getDateAdded());
            filePathLabel.setText(document.getFilePath());
            
            // Set status
            for (int i = 0; i < statusCombo.getItemCount(); i++) {
                if (statusCombo.getItemAt(i).equals(document.getStatus())) {
                    statusCombo.setSelectedIndex(i);
                    break;
                }
            }
            
            // Disable document ID field when editing
            documentIdField.setEditable(false);
        } else {
            // Generate a new document ID for new documents
            documentIdField.setText(generateDocumentId());
            
            // Set default status to Active
            statusCombo.setSelectedItem("Active");
        }
    }
    
    /**
     * Generate a new document ID
     * 
     * @return A new document ID
     */
    private String generateDocumentId() {
        // Format: DOC-YYYY-XXXX where XXXX is a random number
        String year = Integer.toString(LocalDate.now().getYear());
        int randomNum = 1000 + (int)(Math.random() * 9000); // Random 4-digit number
        
        return "DOC-" + year + "-" + randomNum;
    }
    
    /**
     * Validate form data
     * 
     * @return true if form data is valid
     */
    private boolean validateForm() {
        // Check required fields
        if (titleField.getText().trim().isEmpty()) {
            showError("Document title is required.");
            titleField.requestFocus();
            return false;
        }
        
        if (caseCombo.getSelectedItem() == null) {
            showError("Please select an associated case.");
            caseCombo.requestFocus();
            return false;
        }
        
        // Check if a file is provided for new documents
        if (document == null && uploadFile == null) {
            showError("No file selected for upload.");
            return false;
        }
        
        return true;
    }
    
    /**
     * Show an error message
     * 
     * @param message The error message
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Validation Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Save the document
     */
    private void saveDocument() {
        if (!validateForm()) {
            return;
        }
        
        try {
            // Create or update document object
            if (document == null) {
                document = new Document();
            }
            
            document.setDocumentId(documentIdField.getText().trim());
            document.setTitle(titleField.getText().trim());
            document.setDocumentType((String) documentTypeCombo.getSelectedItem());
            document.setDescription(descriptionArea.getText().trim());
            document.setDocumentDate(documentDateChooser.getDate());
            document.setDateAdded(dateAddedChooser.getDate());
            document.setStatus((String) statusCombo.getSelectedItem());
            
            // Set case ID
            CaseItem selectedCase = (CaseItem) caseCombo.getSelectedItem();
            document.setCase(selectedCase.getCase());
            
            // Set created by (would typically be the current user's ID)
            if (document.getCreatedBy() == 0) {
                document.setCreatedBy(1); // Default to user ID 1 for now
            }
            
            boolean success;
            if (document.getId() == 0) {
                // Create new document and upload file
                success = documentController.createDocument(document, uploadFile);
            } else {
                // Update existing document
                success = documentController.updateDocument(document);
            }
            
            if (success) {
                documentSaved = true;
                dispose();
            } else {
                showError("Failed to save document. Please try again.");
            }
            
        } catch (Exception e) {
            showError("Error saving document: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Check if document was saved
     * 
     * @return true if document was saved
     */
    public boolean isDocumentSaved() {
        return documentSaved;
    }
    
    /**
     * Get the document
     * 
     * @return The document
     */
    public Document getDocument() {
        return document;
    }
    
    /**
     * Helper class to represent cases in the combo box
     */
    private class CaseItem {
        private Case legalCase;
        
        public CaseItem(Case legalCase) {
            this.legalCase = legalCase;
        }
        
        public Case getCase() {
            return legalCase;
        }
        
        @Override
        public String toString() {
            return legalCase.getCaseNumber() + " - " + legalCase.getTitle();
        }
    }
}