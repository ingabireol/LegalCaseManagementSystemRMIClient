package view.attorneys;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import model.Attorney;
import model.Case;
import controller.AttorneyController;
import view.util.UIConstants;
import view.components.CustomTable;
import view.cases.CaseDetailsDialog;
import view.util.SwingUtils;

/**
 * Dialog for viewing attorney details and related cases.
 */
public class AttorneyDetailsDialog extends JDialog {
    private Attorney attorney;
    private AttorneyController attorneyController;
    
    private JPanel attorneyInfoPanel;
    private JPanel casesPanel;
    private CustomTable casesTable;
    
    private JButton closeButton;
    private JButton editButton;
    
    /**
     * Constructor
     * 
     * @param parent The parent window
     * @param attorney The attorney to display
     */
    public AttorneyDetailsDialog(Window parent, Attorney attorney) {
        super(parent, "Attorney Details", Dialog.ModalityType.APPLICATION_MODAL);
        
        this.attorney = attorney;
        this.attorneyController = new AttorneyController();
        
        initializeUI();
        loadAttorneyData();
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setSize(800, 600);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Create title panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Create tabbed pane for attorney information and cases
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.NORMAL_FONT);
        
        // Create attorney info panel
        attorneyInfoPanel = createAttorneyInfoPanel();
        tabbedPane.addTab("Attorney Information", attorneyInfoPanel);
        
        // Create cases panel
        casesPanel = createCasesPanel();
        tabbedPane.addTab("Assigned Cases", casesPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
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
        JLabel titleLabel = new JLabel("Attorney Details");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        return titlePanel;
    }
    
    /**
     * Create the attorney information panel
     * 
     * @return The attorney info panel
     */
    private JPanel createAttorneyInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create grid for attorney information
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
        
        // Add attorney information fields (will be populated in loadAttorneyData)
        
        // Attorney ID
        infoGrid.add(createFieldLabel("Attorney ID:"), labelConstraints);
        JLabel attorneyIdValue = new JLabel();
        attorneyIdValue.setName("attorneyId");
        infoGrid.add(attorneyIdValue, valueConstraints);
        
        // Full Name
        infoGrid.add(createFieldLabel("Full Name:"), labelConstraints);
        JLabel nameValue = new JLabel();
        nameValue.setName("name");
        infoGrid.add(nameValue, valueConstraints);
        
        // Specialization
        infoGrid.add(createFieldLabel("Specialization:"), labelConstraints);
        JLabel specializationValue = new JLabel();
        specializationValue.setName("specialization");
        infoGrid.add(specializationValue, valueConstraints);
        
        // Bar Number
        infoGrid.add(createFieldLabel("Bar Number:"), labelConstraints);
        JLabel barNumberValue = new JLabel();
        barNumberValue.setName("barNumber");
        infoGrid.add(barNumberValue, valueConstraints);
        
        // Email
        infoGrid.add(createFieldLabel("Email:"), labelConstraints);
        JLabel emailValue = new JLabel();
        emailValue.setName("email");
        infoGrid.add(emailValue, valueConstraints);
        
        // Phone
        infoGrid.add(createFieldLabel("Phone:"), labelConstraints);
        JLabel phoneValue = new JLabel();
        phoneValue.setName("phone");
        infoGrid.add(phoneValue, valueConstraints);
        
        // Hourly Rate
        infoGrid.add(createFieldLabel("Hourly Rate:"), labelConstraints);
        JLabel hourlyRateValue = new JLabel();
        hourlyRateValue.setName("hourlyRate");
        infoGrid.add(hourlyRateValue, valueConstraints);
        
        // Add the info grid to the panel
        panel.add(infoGrid, BorderLayout.NORTH);
        
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
     * Create the cases panel with a table of cases
     * 
     * @return The cases panel
     */
    private JPanel createCasesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create table for cases
        String[] columnNames = {
            "Case #", "Title", "Type", "Status", "File Date", "Client"
        };
        
        casesTable = new CustomTable(columnNames);
        
        // Set column widths
        casesTable.setColumnWidth(0, 100);  // Case #
        casesTable.setColumnWidth(1, 200);  // Title
        casesTable.setColumnWidth(2, 100);  // Type
        casesTable.setColumnWidth(3, 100);  // Status
        casesTable.setColumnWidth(4, 100);  // File Date
        casesTable.setColumnWidth(5, 150);  // Client
        
        // Add double-click listener to open case details
        casesTable.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && casesTable.getSelectedRow() != -1) {
                    openCaseDetails();
                }
            }
        });
        
        // Add actions panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JButton viewCaseButton = new JButton("View Case");
        viewCaseButton.setFont(UIConstants.NORMAL_FONT);
        viewCaseButton.addActionListener(e -> openCaseDetails());
        
        actionsPanel.add(viewCaseButton);
        
        // Add components to panel
        panel.add(actionsPanel, BorderLayout.NORTH);
        panel.add(casesTable, BorderLayout.CENTER);
        
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
        
        editButton = new JButton("Edit Attorney");
        editButton.setFont(UIConstants.NORMAL_FONT);
        editButton.addActionListener(e -> editAttorney());
        
        closeButton = new JButton("Close");
        closeButton.setFont(UIConstants.NORMAL_FONT);
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(editButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(closeButton);
        
        return buttonPanel;
    }
    
    /**
     * Load attorney data into the UI
     */
    private void loadAttorneyData() {
        try {
            // Get attorney with cases
            Attorney attorneyWithCases = attorneyController.getAttorneyWithCases(attorney.getId());
            if (attorneyWithCases != null) {
                this.attorney = attorneyWithCases;
            }
            
            // Update attorney information fields
            JLabel attorneyIdValue = (JLabel) findComponentByName(attorneyInfoPanel, "attorneyId");
            JLabel nameValue = (JLabel) findComponentByName(attorneyInfoPanel, "name");
            JLabel specializationValue = (JLabel) findComponentByName(attorneyInfoPanel, "specialization");
            JLabel barNumberValue = (JLabel) findComponentByName(attorneyInfoPanel, "barNumber");
            JLabel emailValue = (JLabel) findComponentByName(attorneyInfoPanel, "email");
            JLabel phoneValue = (JLabel) findComponentByName(attorneyInfoPanel, "phone");
            JLabel hourlyRateValue = (JLabel) findComponentByName(attorneyInfoPanel, "hourlyRate");
            
            attorneyIdValue.setText(attorney.getAttorneyId());
            nameValue.setText(attorney.getFullName());
            specializationValue.setText(attorney.getSpecialization() != null ? attorney.getSpecialization() : "N/A");
            barNumberValue.setText(attorney.getBarNumber() != null ? attorney.getBarNumber() : "N/A");
            emailValue.setText(attorney.getEmail());
            phoneValue.setText(attorney.getPhone() != null ? attorney.getPhone() : "N/A");
            hourlyRateValue.setText(String.format("$%.2f", attorney.getHourlyRate()));
            
            // Load cases
            casesTable.clearTable();
            List<Case> cases = attorney.getCases();
            if (cases != null && !cases.isEmpty()) {
                for (Case cse : cases) {
                    String clientName = cse.getClient() != null ? cse.getClient().getName() : "Unknown";
                    Object[] row = {
                        cse.getCaseNumber(),
                        cse.getTitle(),
                        cse.getCaseType(),
                        cse.getStatus(),
                        cse.getFileDate() != null ? cse.getFileDate().toString() : "N/A",
                        clientName
                    };
                    casesTable.addRow(row);
                }
            }
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error loading attorney data: " + e.getMessage(),
                "Error"
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
     * Open the details dialog for the selected case
     */
    private void openCaseDetails() {
        int selectedRow = casesTable.getSelectedRow();
        if (selectedRow == -1) {
            SwingUtils.showInfoMessage(
                this,
                "Please select a case to view.",
                "No Case Selected"
            );
            return;
        }
        
        // Get the case number from the selected row
        String caseNumber = casesTable.getValueAt(selectedRow, 0).toString();
        
        try {
            // Find the case in the current attorney's case list
            Case selectedCase = null;
            for (Case cse : attorney.getCases()) {
                if (caseNumber.equals(cse.getCaseNumber())) {
                    selectedCase = cse;
                    break;
                }
            }
            
            if (selectedCase != null) {
                // Open case details dialog
                CaseDetailsDialog dialog = new CaseDetailsDialog(getOwner(), selectedCase);
                dialog.setVisible(true);
                
                // Refresh attorney data after returning from case details
                loadAttorneyData();
            }
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error opening case details: " + e.getMessage(),
                "Error"
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Edit this attorney
     */
    private void editAttorney() {
        try {
            // Open attorney editor dialog
            AttorneyEditorDialog dialog = new AttorneyEditorDialog(getOwner(), attorney);
            dialog.setVisible(true);
            
            // Refresh data if attorney was saved
            if (dialog.isAttorneySaved()) {
                // Get updated attorney
                attorney = attorneyController.getAttorneyById(attorney.getId());
                loadAttorneyData();
            }
            
        } catch (Exception e) {
            SwingUtils.showErrorMessage(
                this,
                "Error editing attorney: " + e.getMessage(),
                "Error"
            );
            e.printStackTrace();
        }
    }
}