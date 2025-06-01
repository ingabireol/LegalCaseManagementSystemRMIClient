package view.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.util.function.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import view.util.UIConstants;
import view.util.TableExporter;

/**
 * Enhanced JTable with sorting, filtering, styling, and export capabilities.
 */
public class CustomTable extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<TableModel> rowSorter;
    private List<RowFilter<Object, Object>> filters;
    private JButton exportButton;
    
    /**
     * Constructor with column names
     * 
     * @param columnNames Array of column names
     */
    public CustomTable(String[] columnNames) {
        this(columnNames, true); // Show export button by default
    }
    
    /**
     * Constructor with column names and export button option
     * 
     * @param columnNames Array of column names
     * @param showExportButton Whether to show the export button
     */
    public CustomTable(String[] columnNames, boolean showExportButton) {
        filters = new ArrayList<>();
        
        // Create table model
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Default to non-editable
            }
        };
        
        initializeUI(showExportButton);
    }
    
    /**
     * Initialize UI components
     * 
     * @param showExportButton Whether to show the export button
     */
    private void initializeUI(boolean showExportButton) {
        setLayout(new BorderLayout());
        
        // Create table
        table = new JTable(tableModel);
        table.setFont(UIConstants.NORMAL_FONT);
        table.setRowHeight(UIConstants.TABLE_ROW_HEIGHT);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        
        // Customize appearance
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        
        // Style the header
        JTableHeader header = table.getTableHeader();
        header.setFont(UIConstants.LABEL_FONT);
        header.setBackground(UIConstants.PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
        
        // Add alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 250));
                }
                
                return comp;
            }
        });
        
        // Add row sorter for filtering
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Add export button panel if requested
        if (showExportButton) {
            JPanel exportPanel = createExportPanel();
            add(exportPanel, BorderLayout.SOUTH);
        }
    }
    
    /**
     * Create the export button panel
     * 
     * @return The export panel
     */
    private JPanel createExportPanel() {
        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exportPanel.setBackground(Color.WHITE);
        exportPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        exportButton = new JButton("Export Data");
        exportButton.setFont(UIConstants.NORMAL_FONT);
        exportButton.setToolTipText("Export table data to CSV, Excel, or other formats");
        exportButton.addActionListener(e -> exportData());
        
        // Add export icon if available (you can customize this)
        try {
            // Simple text-based icon
            exportButton.setText("📊 Export Data");
        } catch (Exception e) {
            // Fallback to text only
            exportButton.setText("Export Data");
        }
        
        exportPanel.add(exportButton);
        
        return exportPanel;
    }
    
    /**
     * Export the current table data
     */
    public void exportData() {
        exportData("table_data");
    }
    
    /**
     * Export the current table data with a specific filename prefix
     * 
     * @param fileNamePrefix The prefix for the exported filename
     */
    public void exportData(String fileNamePrefix) {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                this,
                "No data to export. The table is empty.",
                "Export Warning",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        // Create a table model that reflects the current view (including sorting and filtering)
        TableModel exportModel = createExportTableModel();
        
        boolean success = TableExporter.exportTableData(
            this, 
            exportModel, 
            TableExporter.generateDefaultFileName(fileNamePrefix)
        );
        
        if (success) {
            JOptionPane.showMessageDialog(
                this,
                "Data exported successfully!",
                "Export Complete",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    /**
     * Create a table model for export that reflects current sorting and filtering
     * 
     * @return TableModel for export
     */
    private TableModel createExportTableModel() {
        // Get the current view row count (after filtering)
        int viewRowCount = table.getRowCount();
        int columnCount = table.getColumnCount();
        
        // Create data array for export
        Object[][] exportData = new Object[viewRowCount][columnCount];
        String[] columnNames = new String[columnCount];
        
        // Get column names
        for (int col = 0; col < columnCount; col++) {
            columnNames[col] = table.getColumnName(col);
        }
        
        // Get data (respecting current sorting and filtering)
        for (int viewRow = 0; viewRow < viewRowCount; viewRow++) {
            for (int col = 0; col < columnCount; col++) {
                exportData[viewRow][col] = table.getValueAt(viewRow, col);
            }
        }
        
        // Create and return the export table model
        return new DefaultTableModel(exportData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
    
    /**
     * Quick export to CSV format
     */
    public void quickExportToCSV() {
        quickExportToCSV("table_data");
    }
    
    /**
     * Quick export to CSV format with specific filename
     * 
     * @param fileNamePrefix The prefix for the exported filename
     */
    public void quickExportToCSV(String fileNamePrefix) {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                this,
                "No data to export. The table is empty.",
                "Export Warning",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        TableModel exportModel = createExportTableModel();
        boolean success = TableExporter.quickExportToCSV(
            this, 
            exportModel, 
            TableExporter.generateDefaultFileName(fileNamePrefix)
        );
        
        if (success) {
            JOptionPane.showMessageDialog(
                this,
                "Data exported to CSV successfully!",
                "Export Complete",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    /**
     * Set the visibility of the export button
     * 
     * @param visible Whether the export button should be visible
     */
    public void setExportButtonVisible(boolean visible) {
        if (exportButton != null) {
            exportButton.getParent().setVisible(visible);
        }
    }
    
    /**
     * Set the text for the export button
     * 
     * @param text The button text
     */
    public void setExportButtonText(String text) {
        if (exportButton != null) {
            exportButton.setText(text);
        }
    }
    
    /**
     * Add a custom action listener to the export button
     * 
     * @param listener The action listener to add
     */
    public void addExportButtonActionListener(ActionListener listener) {
        if (exportButton != null) {
            exportButton.addActionListener(listener);
        }
    }
    
    // [Keep all existing methods from the original CustomTable class]
    
    /**
     * Add a row of data to the table
     * 
     * @param rowData Array of row data
     */
    public void addRow(Object[] rowData) {
        tableModel.addRow(rowData);
    }
    
    /**
     * Clear all rows from the table
     */
    public void clearTable() {
        tableModel.setRowCount(0);
    }
    
    /**
     * Get the selected row index
     * 
     * @return The selected row index, or -1 if no selection
     */
    public int getSelectedRow() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            return -1;
        }
        
        // Convert view row index to model row index
        return table.convertRowIndexToModel(viewRow);
    }
    
    /**
     * Get data from a cell at the specified row and column
     * 
     * @param row The row index
     * @param column The column index
     * @return The cell data
     */
    public Object getValueAt(int row, int column) {
        return tableModel.getValueAt(row, column);
    }
    
    /**
     * Get the JTable component
     * 
     * @return The JTable instance
     */
    public JTable getTable() {
        return table;
    }
    
    /**
     * Get the table model
     * 
     * @return The table model
     */
    public DefaultTableModel getTableModel() {
        return tableModel;
    }
    
    /**
     * Add a text filter to the specified column
     * 
     * @param column The column index to filter
     * @param searchText The text to search for
     */
    public void addFilter(int column, String searchText) {
        if (searchText != null && !searchText.isEmpty()) {
            RowFilter<Object, Object> filter = RowFilter.regexFilter(
                "(?i)" + searchText, column);
            filters.add(filter);
            applyFilters();
        }
    }
    
    /**
     * Add a custom filter using a predicate
     * 
     * @param filter The row filter to add
     */
    public void addFilter(RowFilter<Object, Object> filter) {
        filters.add(filter);
        applyFilters();
    }
    
    /**
     * Clear all filters
     */
    public void clearFilters() {
        filters.clear();
        rowSorter.setRowFilter(null);
    }
    
    /**
     * Apply all filters
     */
    private void applyFilters() {
        if (filters.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else if (filters.size() == 1) {
            rowSorter.setRowFilter(filters.get(0));
        } else {
            rowSorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }
    
    /**
     * Add a selection listener
     * 
     * @param listener The list selection listener
     */
    public void addSelectionListener(ListSelectionListener listener) {
        table.getSelectionModel().addListSelectionListener(listener);
    }
    
    /**
     * Set the preferred width for a column
     * 
     * @param column The column index
     * @param width The preferred width
     */
    public void setColumnWidth(int column, int width) {
        if (column >= 0 && column < table.getColumnCount()) {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            tableColumn.setPreferredWidth(width);
        }
    }
    
    /**
     * Set a custom cell renderer for a specific column
     * 
     * @param column The column index
     * @param renderer The table cell renderer
     */
    public void setColumnRenderer(int column, TableCellRenderer renderer) {
        if (column >= 0 && column < table.getColumnCount()) {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            tableColumn.setCellRenderer(renderer);
        }
    }
    
    /**
     * Get the number of rows in the table
     * 
     * @return The row count
     */
    public int getRowCount() {
        return tableModel.getRowCount();
    }
    
    /**
     * Get the number of columns in the table
     * 
     * @return The column count
     */
    public int getColumnCount() {
        return tableModel.getColumnCount();
    }
    
    /**
     * Get the column name at the specified index
     * 
     * @param column The column index
     * @return The column name
     */
    public String getColumnName(int column) {
        return tableModel.getColumnName(column);
    }
    
    /**
     * Set whether cells are editable
     * 
     * @param editable Whether cells should be editable
     */
    public void setCellsEditable(boolean editable) {
        tableModel = new DefaultTableModel(tableModel.getDataVector(), getColumnNames()) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return editable;
            }
        };
        table.setModel(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);
    }
    
    /**
     * Get the column names as a Vector
     * 
     * @return Vector of column names
     */
    private Vector<String> getColumnNames() {
        Vector<String> columnNames = new Vector<>();
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            columnNames.add(tableModel.getColumnName(i));
        }
        return columnNames;
    }
}