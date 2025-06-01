package view.util;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import view.components.CustomTable;

/**
 * Helper class for integrating export functionality into menus and toolbars.
 */
public class ExportMenuHelper {
    
    /**
     * Create an export menu with multiple format options
     * 
     * @param customTable The table to export from
     * @param fileNamePrefix Prefix for exported files
     * @return JMenu with export options
     */
    public static JMenu createExportMenu(CustomTable customTable, String fileNamePrefix) {
        JMenu exportMenu = new JMenu("Export");
        exportMenu.setMnemonic(KeyEvent.VK_E);
        
        // Export with format selection
        JMenuItem exportWithDialog = new JMenuItem("Export Data...");
        exportWithDialog.setMnemonic(KeyEvent.VK_E);
        exportWithDialog.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        exportWithDialog.addActionListener(e -> customTable.exportData(fileNamePrefix));
        
        // Quick CSV export
        JMenuItem quickCSV = new JMenuItem("Quick Export to CSV");
        quickCSV.setMnemonic(KeyEvent.VK_C);
        quickCSV.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        quickCSV.addActionListener(e -> customTable.quickExportToCSV(fileNamePrefix));
        
        // Direct format exports
        JMenuItem exportCSV = new JMenuItem("Export as CSV");
        exportCSV.addActionListener(e -> exportToFormat(customTable, fileNamePrefix, TableExporter.ExportFormat.CSV));
        
        JMenuItem exportTSV = new JMenuItem("Export as TSV");
        exportTSV.addActionListener(e -> exportToFormat(customTable, fileNamePrefix, TableExporter.ExportFormat.TSV));
        
        JMenuItem exportExcel = new JMenuItem("Export as Excel");
        exportExcel.addActionListener(e -> exportToFormat(customTable, fileNamePrefix, TableExporter.ExportFormat.EXCEL));
        
        exportMenu.add(exportWithDialog);
        exportMenu.addSeparator();
        exportMenu.add(quickCSV);
        exportMenu.addSeparator();
        exportMenu.add(exportCSV);
        exportMenu.add(exportTSV);
        exportMenu.add(exportExcel);
        
        return exportMenu;
    }
    
    /**
     * Create export toolbar buttons
     * 
     * @param toolbar The toolbar to add buttons to
     * @param customTable The table to export from
     * @param fileNamePrefix Prefix for exported files
     */
    public static void addExportButtons(JToolBar toolbar, CustomTable customTable, String fileNamePrefix) {
        // Main export button
        JButton exportButton = new JButton("Export");
        exportButton.setToolTipText("Export table data to various formats");
        exportButton.addActionListener(e -> customTable.exportData(fileNamePrefix));
        
        // Quick CSV button
        JButton csvButton = new JButton("CSV");
        csvButton.setToolTipText("Quick export to CSV format");
        csvButton.addActionListener(e -> customTable.quickExportToCSV(fileNamePrefix));
        
        toolbar.add(exportButton);
        toolbar.add(csvButton);
    }
    
    /**
     * Export to a specific format directly
     * 
     * @param customTable The table to export from
     * @param fileNamePrefix Prefix for exported files
     * @param format The export format
     */
    private static void exportToFormat(CustomTable customTable, String fileNamePrefix, TableExporter.ExportFormat format) {
        if (customTable.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                customTable,
                "No data to export. The table is empty.",
                "Export Warning",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        // Create export model
        TableModel exportModel = createExportTableModel(customTable);
        
        // Show file chooser for the specific format
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export to " + format.getDescription());
        
        String fileName = TableExporter.generateDefaultFileName(fileNamePrefix) + "." + format.getExtension();
        fileChooser.setSelectedFile(new java.io.File(fileName));
        
        int result = fileChooser.showSaveDialog(customTable);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            
            // Ensure correct extension
            if (!file.getName().toLowerCase().endsWith("." + format.getExtension())) {
                file = new java.io.File(file.getAbsolutePath() + "." + format.getExtension());
            }
            
            boolean success = TableExporter.exportToFile(customTable, exportModel, file, format);
            
            if (success) {
                JOptionPane.showMessageDialog(
                    customTable,
                    "Data exported successfully to " + format.getDescription() + "!",
                    "Export Complete",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    }
    
    /**
     * Create a table model for export that reflects current sorting and filtering
     * 
     * @param customTable The custom table
     * @return TableModel for export
     */
    private static TableModel createExportTableModel(CustomTable customTable) {
        JTable table = customTable.getTable();
        
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
        return new javax.swing.table.DefaultTableModel(exportData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
    
    /**
     * Create a popup menu with export options for right-click context
     * 
     * @param customTable The table to export from
     * @param fileNamePrefix Prefix for exported files
     * @return JPopupMenu with export options
     */
    public static JPopupMenu createExportPopupMenu(CustomTable customTable, String fileNamePrefix) {
        JPopupMenu popup = new JPopupMenu();
        
        JMenuItem exportItem = new JMenuItem("Export Data...");
        exportItem.addActionListener(e -> customTable.exportData(fileNamePrefix));
        
        JMenuItem csvItem = new JMenuItem("Export to CSV");
        csvItem.addActionListener(e -> customTable.quickExportToCSV(fileNamePrefix));
        
        popup.add(exportItem);
        popup.add(csvItem);
        
        return popup;
    }
    
    /**
     * Add right-click export functionality to a table
     * 
     * @param customTable The table to add functionality to
     * @param fileNamePrefix Prefix for exported files
     */
    public static void addRightClickExport(CustomTable customTable, String fileNamePrefix) {
        JPopupMenu popup = createExportPopupMenu(customTable, fileNamePrefix);
        
        customTable.getTable().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }
            
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }
            
            private void showPopup(java.awt.event.MouseEvent e) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });
    }
    
    /**
     * Create keyboard shortcuts for export functionality
     * 
     * @param customTable The table to add shortcuts to
     * @param fileNamePrefix Prefix for exported files
     */
    public static void addExportKeyboardShortcuts(CustomTable customTable, String fileNamePrefix) {
        JTable table = customTable.getTable();
        
        // Ctrl+E for export dialog
        table.getInputMap(JComponent.WHEN_FOCUSED).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK), "export");
        table.getActionMap().put("export", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customTable.exportData(fileNamePrefix);
            }
        });
        
        // Ctrl+Shift+E for quick CSV export
        table.getInputMap(JComponent.WHEN_FOCUSED).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK), "quickCsv");
        table.getActionMap().put("quickCsv", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customTable.quickExportToCSV(fileNamePrefix);
            }
        });
    }
    
    /**
     * Configure export settings for a table
     * 
     * @param customTable The table to configure
     * @param fileNamePrefix Prefix for exported files
     * @param showButton Whether to show the export button
     * @param enableRightClick Whether to enable right-click export
     * @param enableKeyboardShortcuts Whether to enable keyboard shortcuts
     */
    public static void configureTableExport(CustomTable customTable, String fileNamePrefix, 
                                          boolean showButton, boolean enableRightClick, 
                                          boolean enableKeyboardShortcuts) {
        // Configure export button visibility
        customTable.setExportButtonVisible(showButton);
        
        // Add right-click functionality if requested
        if (enableRightClick) {
            addRightClickExport(customTable, fileNamePrefix);
        }
        
        // Add keyboard shortcuts if requested
        if (enableKeyboardShortcuts) {
            addExportKeyboardShortcuts(customTable, fileNamePrefix);
        }
    }
}