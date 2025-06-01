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

/**
 * Enhanced JTable with sorting, filtering, and styling capabilities.
 */
public class CustomTable extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<TableModel> rowSorter;
    private List<RowFilter<Object, Object>> filters;
    
    /**
     * Constructor with column names
     * 
     * @param columnNames Array of column names
     */
    public CustomTable(String[] columnNames) {
        filters = new ArrayList<>();
        
        // Create table model
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Default to non-editable
            }
        };
        
        initializeUI();
    }
    
    /**
     * Initialize UI components
     */
    private void initializeUI() {
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
    }
    
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