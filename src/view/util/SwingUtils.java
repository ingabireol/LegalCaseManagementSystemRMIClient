package view.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Utility methods for Swing components.
 */
public class SwingUtils {
    
    /**
     * Create a button with standard styling
     * 
     * @param text Button text
     * @param actionListener Action listener
     * @return Styled button
     */
    public static JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.NORMAL_FONT);
        if (actionListener != null) {
            button.addActionListener(actionListener);
        }
        return button;
    }
    
    /**
     * Create a primary button with accent color
     * 
     * @param text Button text
     * @param actionListener Action listener
     * @return Styled primary button
     */
    public static JButton createPrimaryButton(String text, ActionListener actionListener) {
        JButton button = createButton(text, actionListener);
        button.setBackground(UIConstants.SECONDARY_COLOR);
        button.setForeground(Color.WHITE);
        return button;
    }
    
    /**
     * Create a label with standard styling
     * 
     * @param text Label text
     * @return Styled label
     */
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.NORMAL_FONT);
        return label;
    }
    
    /**
     * Create a bold label with standard styling
     * 
     * @param text Label text
     * @return Styled bold label
     */
    public static JLabel createBoldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.LABEL_FONT);
        return label;
    }
    
    /**
     * Create a text field with standard styling
     * 
     * @param columns Number of columns
     * @return Styled text field
     */
    public static JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(UIConstants.NORMAL_FONT);
        return textField;
    }
    
    /**
     * Create a password field with standard styling
     * 
     * @param columns Number of columns
     * @return Styled password field
     */
    public static JPasswordField createPasswordField(int columns) {
        JPasswordField passwordField = new JPasswordField(columns);
        passwordField.setFont(UIConstants.NORMAL_FONT);
        return passwordField;
    }
    
    /**
     * Create a text area with standard styling
     * 
     * @param rows Number of rows
     * @param columns Number of columns
     * @return Styled text area
     */
    public static JTextArea createTextArea(int rows, int columns) {
        JTextArea textArea = new JTextArea(rows, columns);
        textArea.setFont(UIConstants.NORMAL_FONT);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }
    
    /**
     * Create a combo box with standard styling
     * 
     * @param items Array of items
     * @return Styled combo box
     */
    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        comboBox.setFont(UIConstants.NORMAL_FONT);
        return comboBox;
    }
    
    /**
     * Create a titled panel with a border
     * 
     * @param title Panel title
     * @return Panel with title border
     */
    public static JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.SECONDARY_COLOR),
            title,
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            UIConstants.LABEL_FONT,
            UIConstants.SECONDARY_COLOR
        ));
        return panel;
    }
    
    /**
     * Format a date for display
     * 
     * @param date The date to format
     * @return Formatted date string
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(date);
    }
    
    /**
     * Format a local date for display
     * 
     * @param date The local date to format
     * @return Formatted date string
     */
    public static String formatLocalDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return date.format(formatter);
    }
    
    /**
     * Parse a string to a local date
     * 
     * @param dateStr The date string to parse
     * @return Parsed local date, or null if invalid
     */
    public static LocalDate parseLocalDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Format a money amount for display
     * 
     * @param amount The amount to format
     * @return Formatted amount string
     */
    public static String formatMoney(double amount) {
        return String.format("$%,.2f", amount);
    }
    
    /**
     * Show an error message dialog
     * 
     * @param parent The parent component
     * @param message The error message
     * @param title The dialog title
     */
    public static void showErrorMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            title,
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Show an information message dialog
     * 
     * @param parent The parent component
     * @param message The information message
     * @param title The dialog title
     */
    public static void showInfoMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            title,
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Show a confirmation dialog
     * 
     * @param parent The parent component
     * @param message The confirmation message
     * @param title The dialog title
     * @return true if user confirms, false otherwise
     */
    public static boolean showConfirmDialog(Component parent, String message, String title) {
        int result = JOptionPane.showConfirmDialog(
            parent,
            message,
            title,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Create horizontal strut with standard padding
     * 
     * @return Component representing horizontal padding
     */
    public static Component createHorizontalSpacer() {
        return Box.createHorizontalStrut(UIConstants.STANDARD_PADDING);
    }
    
    /**
     * Create vertical strut with standard padding
     * 
     * @return Component representing vertical padding
     */
    public static Component createVerticalSpacer() {
        return Box.createVerticalStrut(UIConstants.STANDARD_PADDING);
    }
    
    /**
     * Find a component by name within a container
     * 
     * @param container The container to search in
     * @param name The component name to find
     * @return The found component or null
     */
    public static Component findComponentByName(Container container, String name) {
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
}