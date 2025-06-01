package view.documents;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.format.DateTimeFormatter;

import model.Document;
import model.Case;
import controller.DocumentController;
import controller.CaseController;
import view.util.UIConstants;
import view.cases.CaseDetailsDialog;

/**
 * Dialog for viewing document details.
 */
public class DocumentDetailsDialog extends JDialog {
    private Document document;
    private DocumentController documentController;
    private CaseController caseController;
    
    private JPanel documentInfoPanel;
    private JButton closeButton;
    private JButton editButton;
    private JButton downloadButton;
    
    /**
     * Constructor
     * 
     * @param parent The parent window
     * @param document The document to display
     */
    public DocumentDetailsDialog(Window parent, Document document) {
        super(parent, "Document Details", ModalityType.APPLICATION_MODAL);
        
        this.document = document;
        this.documentController = new DocumentController();
        this.caseController = new CaseController();
        
        initializeUI();
        loadDocumentData();
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setSize(700, 500);
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Create title panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Create content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        
        // Create document info panel
        documentInfoPanel = createDocumentInfoPanel();
        contentPanel.add(documentInfoPanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        
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
        JLabel titleLabel = new JLabel("Document Details");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        return titlePanel;
    }
    
    /**
     * Create the document information panel
     * 
     * @return The document info panel
     */
    private JPanel createDocumentInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create grid for document information
        JPanel infoGrid = new JPanel(new GridBagLayout());
        infoGrid.setBackground(Color.WHITE);
        
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
        
        // Add document information fields (will be populated in loadDocumentData)
        
        // Document ID
        infoGrid.add(createFieldLabel("Document ID:"), labelConstraints);
        JLabel documentIdValue = new JLabel();
        documentIdValue.setName("documentId");
        infoGrid.add(documentIdValue, valueConstraints);
        
        // Title
        infoGrid.add(createFieldLabel("Title:"), labelConstraints);
        JLabel titleValue = new JLabel();
        titleValue.setName("title");
        infoGrid.add(titleValue, valueConstraints);
        
        // Document Type
        infoGrid.add(createFieldLabel("Document Type:"), labelConstraints);
        JLabel documentTypeValue = new JLabel();
        documentTypeValue.setName("documentType");
        infoGrid.add(documentTypeValue, valueConstraints);
        
        // Associated Case
        infoGrid.add(createFieldLabel("Associated Case:"), labelConstraints);
        JPanel casePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        casePanel.setBackground(Color.WHITE);
        
        JLabel caseValue = new JLabel();
        caseValue.setName("case");
        casePanel.add(caseValue);
        
        JButton viewCaseButton = new JButton("View Case");
        viewCaseButton.setFont(UIConstants.SMALL_FONT);
        viewCaseButton.addActionListener(e -> viewCase());
        casePanel.add(viewCaseButton);
        
        infoGrid.add(casePanel, valueConstraints);
        
        // File
        infoGrid.add(createFieldLabel("File:"), labelConstraints);
        JLabel filePathValue = new JLabel();
        filePathValue.setName("filePath");
        infoGrid.add(filePathValue, valueConstraints);
        
        // Document Date
        infoGrid.add(createFieldLabel("Document Date:"), labelConstraints);
        JLabel documentDateValue = new JLabel();
        documentDateValue.setName("documentDate");
        infoGrid.add(documentDateValue, valueConstraints);
        
        // Date Added
        infoGrid.add(createFieldLabel("Date Added:"), labelConstraints);
        JLabel dateAddedValue = new JLabel();
        dateAddedValue.setName("dateAdded");
        infoGrid.add(dateAddedValue, valueConstraints);
        
        // Status
        infoGrid.add(createFieldLabel("Status:"), labelConstraints);
        JLabel statusValue = new JLabel();
        statusValue.setName("status");
        infoGrid.add(statusValue, valueConstraints);
        
        // Description
        infoGrid.add(createFieldLabel("Description:"), labelConstraints);
        JTextArea descriptionValue = new JTextArea(6, 30);
        descriptionValue.setName("description");
        descriptionValue.setEditable(false);
        descriptionValue.setLineWrap(true);
        descriptionValue.setWrapStyleWord(true);
        descriptionValue.setBackground(Color.WHITE);
        descriptionValue.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JScrollPane descriptionScroll = new JScrollPane(descriptionValue);
        
        GridBagConstraints descConstraints = new GridBagConstraints();
        descConstraints.gridx = 1;
        descConstraints.gridy = GridBagConstraints.RELATIVE;
        descConstraints.anchor = GridBagConstraints.WEST;
        descConstraints.weightx = 1.0;
        descConstraints.weighty = 1.0;
        descConstraints.fill = GridBagConstraints.BOTH;
        descConstraints.insets = new Insets(5, 5, 5, 5);
        
        infoGrid.add(descriptionScroll, descConstraints);
        
        // Add the info grid to the panel
        panel.add(infoGrid, BorderLayout.CENTER);
        
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
        
        downloadButton = new JButton("Download Document");
        downloadButton.setFont(UIConstants.NORMAL_FONT);
        downloadButton.addActionListener(e -> downloadDocument());
        
        editButton = new JButton("Edit Document");
        editButton.setFont(UIConstants.NORMAL_FONT);
        editButton.addActionListener(e -> editDocument());
        
        closeButton = new JButton("Close");
        closeButton.setFont(UIConstants.NORMAL_FONT);
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(downloadButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(editButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(closeButton);
        
        return buttonPanel;
    }
    
    /**
     * Load document data into the UI
     */
    private void loadDocumentData() {
        try {
            // Make sure we have the full document with case info
            if (document.getCase() == null && document.getCase().getId()> 0) {
                Document fullDocument = documentController.getDocumentWithCase(document.getId());
                if (fullDocument != null) {
                    document = fullDocument;
                }
            }
            
            // Update document information fields
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            
            JLabel documentIdValue = (JLabel) findComponentByName(documentInfoPanel, "documentId");
            JLabel titleValue = (JLabel) findComponentByName(documentInfoPanel, "title");
            JLabel documentTypeValue = (JLabel) findComponentByName(documentInfoPanel, "documentType");
            JLabel caseValue = (JLabel) findComponentByName(documentInfoPanel, "case");
            JLabel filePathValue = (JLabel) findComponentByName(documentInfoPanel, "filePath");
            JLabel documentDateValue = (JLabel) findComponentByName(documentInfoPanel, "documentDate");
            JLabel dateAddedValue = (JLabel) findComponentByName(documentInfoPanel, "dateAdded");
            JLabel statusValue = (JLabel) findComponentByName(documentInfoPanel, "status");
            JTextArea descriptionValue = (JTextArea) findComponentByName(documentInfoPanel, "description");
            
            documentIdValue.setText(document.getDocumentId());
            titleValue.setText(document.getTitle());
            documentTypeValue.setText(document.getDocumentType());
            
            // Case info
            if (document.getCase() != null) {
                caseValue.setText(document.getCase().getCaseNumber() + " - " + document.getCase().getTitle());
            } else {
                caseValue.setText("N/A");
            }
            
            // File path - just show filename to user
            String fileName = new File(document.getFilePath()).getName();
            filePathValue.setText(fileName);
            
            // Dates
            documentDateValue.setText(document.getDocumentDate() != null ? 
                                      document.getDocumentDate().format(dateFormatter) : "N/A");
            dateAddedValue.setText(document.getDateAdded().format(dateFormatter));
            
            // Status
            statusValue.setText(document.getStatus());
            
            // Description
            descriptionValue.setText(document.getDescription() != null ? document.getDescription() : "");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Error loading document data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
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
     * View the case associated with this document
     */
    private void viewCase() {
        try {
            if (document.getCase() != null) {
                CaseDetailsDialog dialog = new CaseDetailsDialog(getOwner(), document.getCase());
                dialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "No case associated with this document.",
                    "No Case",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Error viewing case: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Edit this document
     */
    private void editDocument() {
        try {
            // Open document editor dialog
            DocumentEditorDialog dialog = new DocumentEditorDialog(getOwner(), document, null);
            dialog.setVisible(true);
            
            // Refresh data if document was saved
            if (dialog.isDocumentSaved()) {
                // Get updated document
                document = documentController.getDocumentById(document.getId());
                loadDocumentData();
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Error editing document: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Download this document
     */
    private void downloadDocument() {
        try {
            // Show save file dialog
            JFileChooser fileChooser = new JFileChooser();
            String suggestedName = new File(document.getFilePath()).getName();
            fileChooser.setSelectedFile(new File(suggestedName));
            int result = fileChooser.showSaveDialog(this);
            
            if (result == JFileChooser.APPROVE_OPTION) {
                File outputFile = fileChooser.getSelectedFile();
                
                // Get document content and save to file
                byte[] content = documentController.getDocumentContent(document.getId());
                if (content != null) {
                    try {
                        java.nio.file.Files.write(outputFile.toPath(), content);
                        JOptionPane.showMessageDialog(
                            this,
                            "Document downloaded successfully.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(
                            this,
                            "Error saving document: " + e.getMessage(),
                            "Download Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                        e.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Could not read document content.",
                        "Download Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Error downloading document: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
}