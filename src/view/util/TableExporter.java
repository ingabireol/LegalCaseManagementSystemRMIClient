package view.util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for exporting table data to various formats.
 * Uses only built-in Java libraries.
 */
public class TableExporter {
    
    public enum ExportFormat {
        CSV("CSV Files", "csv", ","),
        TSV("Tab-Separated Values", "tsv", "\t"),
        EXCEL("Excel Files (Tab-delimited)", "xls", "\t");
        
        private final String description;
        private final String extension;
        private final String delimiter;
        
        ExportFormat(String description, String extension, String delimiter) {
            this.description = description;
            this.extension = extension;
            this.delimiter = delimiter;
        }
        
        public String getDescription() { return description; }
        public String getExtension() { return extension; }
        public String getDelimiter() { return delimiter; }
    }
    
    /**
     * Export table data with format selection dialog
     * 
     * @param parent Parent component for dialogs
     * @param tableModel The table model to export
     * @param defaultFileName Default filename (without extension)
     * @return true if export was successful
     */
    public static boolean exportTableData(Component parent, TableModel tableModel, String defaultFileName) {
        // Show format selection dialog
        ExportFormat selectedFormat = showFormatSelectionDialog(parent);
        if (selectedFormat == null) {
            return false; // User cancelled
        }
        
        // Show file chooser
        File selectedFile = showFileSaveDialog(parent, selectedFormat, defaultFileName);
        if (selectedFile == null) {
            return false; // User cancelled
        }
        
        // Perform export
        return exportToFile(parent, tableModel, selectedFile, selectedFormat);
    }
    
    /**
     * Export table data to specific format and file
     * 
     * @param parent Parent component for dialogs
     * @param tableModel The table model to export
     * @param file The file to export to
     * @param format The export format
     * @return true if export was successful
     */
    public static boolean exportToFile(Component parent, TableModel tableModel, File file, ExportFormat format) {
        try {
            switch (format) {
                case CSV:
                case TSV:
                case EXCEL:
                    return exportDelimitedFile(tableModel, file, format);
                default:
                    SwingUtils.showErrorMessage(parent, "Unsupported export format: " + format, "Export Error");
                    return false;
            }
        } catch (Exception e) {
            SwingUtils.showErrorMessage(parent, "Error during export: " + e.getMessage(), "Export Error");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Show format selection dialog
     * 
     * @param parent Parent component
     * @return Selected format or null if cancelled
     */
    private static ExportFormat showFormatSelectionDialog(Component parent) {
        ExportFormat[] formats = ExportFormat.values();
        String[] formatNames = new String[formats.length];
        
        for (int i = 0; i < formats.length; i++) {
            formatNames[i] = formats[i].getDescription();
        }
        
        String selected = (String) JOptionPane.showInputDialog(
            parent,
            "Select export format:",
            "Export Format",
            JOptionPane.QUESTION_MESSAGE,
            null,
            formatNames,
            formatNames[0]
        );
        
        if (selected == null) {
            return null;
        }
        
        // Find the selected format
        for (ExportFormat format : formats) {
            if (format.getDescription().equals(selected)) {
                return format;
            }
        }
        
        return null;
    }
    
    /**
     * Show file save dialog
     * 
     * @param parent Parent component
     * @param format Export format
     * @param defaultFileName Default filename
     * @return Selected file or null if cancelled
     */
    private static File showFileSaveDialog(Component parent, ExportFormat format, String defaultFileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Table Data");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        // Set file filter
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            format.getDescription(), format.getExtension());
        fileChooser.setFileFilter(filter);
        
        // Set default filename with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = defaultFileName + "_" + timestamp + "." + format.getExtension();
        fileChooser.setSelectedFile(new File(fileName));
        
        int result = fileChooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            // Ensure correct extension
            if (!file.getName().toLowerCase().endsWith("." + format.getExtension())) {
                file = new File(file.getAbsolutePath() + "." + format.getExtension());
            }
            
            // Check if file exists and confirm overwrite
            if (file.exists()) {
                int confirm = JOptionPane.showConfirmDialog(
                    parent,
                    "File already exists. Do you want to overwrite it?",
                    "Confirm Overwrite",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm != JOptionPane.YES_OPTION) {
                    return showFileSaveDialog(parent, format, defaultFileName); // Show dialog again
                }
            }
            
            return file;
        }
        
        return null;
    }
    
    /**
     * Export table data to a delimited file (CSV, TSV, etc.)
     * 
     * @param tableModel The table model to export
     * @param file The file to export to
     * @param format The export format
     * @return true if export was successful
     * @throws IOException if an I/O error occurs
     */
    private static boolean exportDelimitedFile(TableModel tableModel, File file, ExportFormat format) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            String delimiter = format.getDelimiter();
            
            // Write headers
            for (int col = 0; col < tableModel.getColumnCount(); col++) {
                if (col > 0) {
                    writer.print(delimiter);
                }
                writer.print(escapeValue(tableModel.getColumnName(col), delimiter));
            }
            writer.println();
            
            // Write data rows
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                for (int col = 0; col < tableModel.getColumnCount(); col++) {
                    if (col > 0) {
                        writer.print(delimiter);
                    }
                    
                    Object value = tableModel.getValueAt(row, col);
                    String stringValue = value != null ? value.toString() : "";
                    writer.print(escapeValue(stringValue, delimiter));
                }
                writer.println();
            }
            
            return true;
        }
    }
    
    /**
     * Escape special characters in values for delimited files
     * 
     * @param value The value to escape
     * @param delimiter The delimiter being used
     * @return Escaped value
     */
    private static String escapeValue(String value, String delimiter) {
        if (value == null) {
            return "";
        }
        
        // For CSV format, we need to handle quotes and delimiters
        if (",".equals(delimiter)) {
            // CSV format: escape quotes and wrap in quotes if contains delimiter, quote, or newline
            if (value.contains("\"") || value.contains(",") || value.contains("\n") || value.contains("\r")) {
                // Escape existing quotes by doubling them
                value = value.replace("\"", "\"\"");
                // Wrap in quotes
                value = "\"" + value + "\"";
            }
        } else {
            // For other formats (TSV, etc.), replace delimiters and newlines with spaces
            value = value.replace(delimiter, " ");
            value = value.replace("\n", " ");
            value = value.replace("\r", " ");
        }
        
        return value;
    }
    
    /**
     * Generate a default filename based on current timestamp
     * 
     * @param prefix Filename prefix
     * @return Generated filename (without extension)
     */
    public static String generateDefaultFileName(String prefix) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return prefix + "_export_" + timestamp;
    }
    
    /**
     * Quick export to CSV with minimal dialogs
     * 
     * @param parent Parent component
     * @param tableModel The table model to export
     * @param fileName Default filename (without extension)
     * @return true if export was successful
     */
    public static boolean quickExportToCSV(Component parent, TableModel tableModel, String fileName) {
        File file = showFileSaveDialog(parent, ExportFormat.CSV, fileName);
        if (file != null) {
            return exportToFile(parent, tableModel, file, ExportFormat.CSV);
        }
        return false;
    }
}