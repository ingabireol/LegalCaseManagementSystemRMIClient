package view.components;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

import view.util.UIConstants;

public class TableFilterPanel extends JPanel {
    private JTextField searchField;
    private JComboBox<String> filterTypeCombo;
    private JPanel additionalFiltersPanel;
    private JButton searchButton;
    private JButton clearButton;
    
    private String[] filterTypes;
    private Consumer<String> searchAction;
    private Runnable clearAction;
    
    /**
     * Constructor with minimal parameters
     * 
     * @param searchAction Action to perform when search button is clicked
     * @param clearAction Action to perform when clear button is clicked
     */
    public TableFilterPanel(Consumer<String> searchAction, Runnable clearAction) {
        this(new String[]{"All"}, searchAction, clearAction);
    }
    
    /**
     * Constructor with filter types
     * 
     * @param filterTypes Array of filter type options
     * @param searchAction Action to perform when search button is clicked
     * @param clearAction Action to perform when clear button is clicked
     */
    public TableFilterPanel(String[] filterTypes, Consumer<String> searchAction, Runnable clearAction) {
        this.filterTypes = filterTypes;
        this.searchAction = searchAction;
        this.clearAction = clearAction;
        
        initializeUI();
    }
    
    /**
     * Initialize UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Create main filter panel
        JPanel mainFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        mainFilterPanel.setBackground(Color.WHITE);
        
        // Filter type combo
        JLabel filterTypeLabel = new JLabel("Filter by:");
        filterTypeLabel.setFont(UIConstants.NORMAL_FONT);
        mainFilterPanel.add(filterTypeLabel);
        
        filterTypeCombo = new JComboBox<>(filterTypes);
        filterTypeCombo.setFont(UIConstants.NORMAL_FONT);
        filterTypeCombo.addActionListener(e -> filterTypeChanged());
        mainFilterPanel.add(filterTypeCombo);
        
        // Search field
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(UIConstants.NORMAL_FONT);
        mainFilterPanel.add(searchLabel);
        
        searchField = new JTextField(20);
        searchField.setFont(UIConstants.NORMAL_FONT);
        searchField.addActionListener(e -> performSearch());
        mainFilterPanel.add(searchField);
        
        // Search button
        searchButton = new JButton("Search");
        searchButton.setFont(UIConstants.NORMAL_FONT);
        searchButton.addActionListener(e -> performSearch());
        mainFilterPanel.add(searchButton);
        
        // Clear button
        clearButton = new JButton("Clear");
        clearButton.setFont(UIConstants.NORMAL_FONT);
        clearButton.addActionListener(e -> clearFilters());
        mainFilterPanel.add(clearButton);
        
        add(mainFilterPanel, BorderLayout.NORTH);
        
        // Additional filters panel (hidden by default)
        additionalFiltersPanel = new JPanel();
        additionalFiltersPanel.setLayout(new BoxLayout(additionalFiltersPanel, BoxLayout.Y_AXIS));
        additionalFiltersPanel.setBackground(Color.WHITE);
        additionalFiltersPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        additionalFiltersPanel.setVisible(false);
        
        add(additionalFiltersPanel, BorderLayout.CENTER);
    }
    
    /**
     * Handle filter type change
     */
    private void filterTypeChanged() {
        // Subclasses can override to handle filter type changes
    }
    
    /**
     * Perform search with current filters
     */
    private void performSearch() {
        if (searchAction != null) {
            searchAction.accept(searchField.getText());
        }
    }
    
    /**
     * Clear all filters
     */
    private void clearFilters() {
        searchField.setText("");
        filterTypeCombo.setSelectedIndex(0);
        
        if (clearAction != null) {
            clearAction.run();
        }
    }
    
    /**
     * Add a component to the additional filters panel
     * 
     * @param component The component to add
     */
    public void addFilter(JComponent component) {
        additionalFiltersPanel.add(component);
        additionalFiltersPanel.setVisible(true);
        revalidate();
    }
    
    /**
     * Remove all components from the additional filters panel
     */
    public void clearAdditionalFilters() {
        additionalFiltersPanel.removeAll();
        additionalFiltersPanel.setVisible(false);
        revalidate();
    }
    
    /**
     * Get the search text
     * 
     * @return The search text
     */
    public String getSearchText() {
        return searchField.getText();
    }
    
    /**
     * Set the search text
     * 
     * @param text The search text
     */
    public void setSearchText(String text) {
        searchField.setText(text);
    }
    
    /**
     * Get the selected filter type
     * 
     * @return The selected filter type
     */
    public String getSelectedFilterType() {
        return (String) filterTypeCombo.getSelectedItem();
    }
    
    /**
     * Set the selected filter type
     * 
     * @param filterType The filter type to select
     */
    public void setSelectedFilterType(String filterType) {
        filterTypeCombo.setSelectedItem(filterType);
    }
    /**
     * Set the action to perform when clear button is clicked
     * 
     * @param clearAction The clear action
     */
    public void setClearAction(Runnable clearAction) {
        this.clearAction = clearAction;
        // Update the clear button action
        if (clearButton != null) {
            for (ActionListener al : clearButton.getActionListeners()) {
                clearButton.removeActionListener(al);
            }
            clearButton.addActionListener(e -> clearFilters());
        }
    }
}