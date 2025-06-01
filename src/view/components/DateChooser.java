package view.components;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import view.util.UIConstants;

/**
 * A custom date chooser component.
 */
public class DateChooser extends JPanel {
    private JTextField dateField;
    private JButton calendarButton;
    private LocalDate selectedDate;
    private DateTimeFormatter formatter;
    
    /**
     * Constructor
     */
    public DateChooser() {
        this(LocalDate.now());
    }
    
    /**
     * Constructor with initial date
     * 
     * @param initialDate The initial date
     */
    public DateChooser(LocalDate initialDate) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        this.selectedDate = initialDate;
        this.formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        
        initializeUI();
    }
    
    /**
     * Initialize UI components
     */
    private void initializeUI() {
        // Date text field
        dateField = new JTextField(12);
        dateField.setFont(UIConstants.NORMAL_FONT);
        dateField.setText(selectedDate.format(formatter));
        dateField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                tryParseDate();
            }
        });
        
        // Calendar button
        calendarButton = new JButton("...");
        calendarButton.setFont(UIConstants.NORMAL_FONT);
        calendarButton.setPreferredSize(new Dimension(30, dateField.getPreferredSize().height));
        calendarButton.addActionListener(e -> showCalendarPopup());
        
        add(dateField);
        add(calendarButton);
    }
    
    /**
     * Try to parse the date entered in the text field
     */
    private void tryParseDate() {
        try {
            LocalDate parsedDate = LocalDate.parse(dateField.getText(), formatter);
            setDate(parsedDate);
        } catch (Exception ex) {
            // Revert to the previously selected date
            dateField.setText(selectedDate.format(formatter));
        }
    }
    
    /**
     * Show the calendar popup
     */
    private void showCalendarPopup() {
        // Create and show the calendar dialog
        CalendarDialog dialog = new CalendarDialog(SwingUtilities.getWindowAncestor(this), selectedDate);
        dialog.setVisible(true);
        
        // Get the selected date from the dialog
        if (dialog.isDateSelected()) {
            setDate(dialog.getSelectedDate());
        }
    }
    
    /**
     * Set the date
     * 
     * @param date The date to set
     */
    public void setDate(LocalDate date) {
        this.selectedDate = date;
        dateField.setText(date.format(formatter));
    }
    
    /**
     * Get the selected date
     * 
     * @return The selected date
     */
    public LocalDate getDate() {
        return selectedDate;
    }
    
    /**
     * Calendar dialog for date selection
     */
    private class CalendarDialog extends JDialog {
        private LocalDate viewDate;
        private LocalDate selectedDate;
        private boolean dateSelected = false;
        
        private JPanel calendarPanel;
        private JLabel monthYearLabel;
        private JButton prevMonthButton;
        private JButton nextMonthButton;
        
        /**
         * Constructor
         * 
         * @param parent The parent window
         * @param initialDate The initial date
         */
        public CalendarDialog(Window parent, LocalDate initialDate) {
            super(parent, "Select Date", ModalityType.APPLICATION_MODAL);
            
            this.viewDate = initialDate;
            this.selectedDate = initialDate;
            
            initializeUI();
            updateCalendar();
            
            // Position the dialog near the date chooser
            Point location = DateChooser.this.getLocationOnScreen();
            setLocation(location.x, location.y + DateChooser.this.getHeight());
        }
        
        /**
         * Initialize UI components
         */
        private void initializeUI() {
            setLayout(new BorderLayout());
            setSize(300, 250);
            setResizable(false);
            
            // Month and year navigation panel
            JPanel navigationPanel = new JPanel(new BorderLayout());
            navigationPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            navigationPanel.setBackground(UIConstants.PRIMARY_COLOR);
            
            prevMonthButton = new JButton("<");
            prevMonthButton.setFont(UIConstants.NORMAL_FONT);
            prevMonthButton.setForeground(Color.WHITE);
            prevMonthButton.setBackground(UIConstants.PRIMARY_COLOR);
            prevMonthButton.setBorderPainted(false);
            prevMonthButton.setFocusPainted(false);
            prevMonthButton.addActionListener(e -> {
                viewDate = viewDate.minusMonths(1);
                updateCalendar();
            });
            
            nextMonthButton = new JButton(">");
            nextMonthButton.setFont(UIConstants.NORMAL_FONT);
            nextMonthButton.setForeground(Color.WHITE);
            nextMonthButton.setBackground(UIConstants.PRIMARY_COLOR);
            nextMonthButton.setBorderPainted(false);
            nextMonthButton.setFocusPainted(false);
            nextMonthButton.addActionListener(e -> {
                viewDate = viewDate.plusMonths(1);
                updateCalendar();
            });
            
            monthYearLabel = new JLabel("", SwingConstants.CENTER);
            monthYearLabel.setFont(UIConstants.HEADER_FONT);
            monthYearLabel.setForeground(Color.WHITE);
            
            navigationPanel.add(prevMonthButton, BorderLayout.WEST);
            navigationPanel.add(monthYearLabel, BorderLayout.CENTER);
            navigationPanel.add(nextMonthButton, BorderLayout.EAST);
            
            add(navigationPanel, BorderLayout.NORTH);
            
            // Calendar panel
            calendarPanel = new JPanel(new GridLayout(7, 7, 5, 5));
            calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            calendarPanel.setBackground(Color.WHITE);
            
            add(calendarPanel, BorderLayout.CENTER);
            
            // Buttons panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
            buttonPanel.setBackground(Color.WHITE);
            
            JButton todayButton = new JButton("Today");
            todayButton.setFont(UIConstants.NORMAL_FONT);
            todayButton.addActionListener(e -> {
                selectedDate = LocalDate.now();
                dateSelected = true;
                dispose();
            });
            
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setFont(UIConstants.NORMAL_FONT);
            cancelButton.addActionListener(e -> dispose());
            
            buttonPanel.add(todayButton);
            buttonPanel.add(cancelButton);
            
            add(buttonPanel, BorderLayout.SOUTH);
        }
        
        /**
         * Update the calendar view
         */
        private void updateCalendar() {
            // Update month and year label
            DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            monthYearLabel.setText(viewDate.format(monthYearFormatter));
            
            // Clear calendar panel
            calendarPanel.removeAll();
            
            // Add day of week headers
            String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
            for (String dayOfWeek : daysOfWeek) {
                JLabel label = new JLabel(dayOfWeek, SwingConstants.CENTER);
                label.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
                calendarPanel.add(label);
            }
            
            // Get month information
            YearMonth yearMonth = YearMonth.of(viewDate.getYear(), viewDate.getMonth());
            int daysInMonth = yearMonth.lengthOfMonth();
            int firstDayOfMonth = yearMonth.atDay(1).getDayOfWeek().getValue() % 7;
            
            // Add empty labels for days before the first day of the month
            for (int i = 0; i < firstDayOfMonth; i++) {
                calendarPanel.add(new JLabel());
            }
            
            // Add day buttons
            for (int day = 1; day <= daysInMonth; day++) {
                final int dayValue = day;
                LocalDate date = LocalDate.of(viewDate.getYear(), viewDate.getMonth(), day);
                
                JButton dayButton = new JButton(Integer.toString(day));
                dayButton.setFont(UIConstants.NORMAL_FONT);
                dayButton.setFocusPainted(false);
                
                // Highlight the selected date
                if (date.equals(selectedDate)) {
                    dayButton.setBackground(UIConstants.SECONDARY_COLOR);
                    dayButton.setForeground(Color.WHITE);
                } else {
                    dayButton.setBackground(Color.WHITE);
                }
                
                // Highlight today's date
                if (date.equals(LocalDate.now())) {
                    dayButton.setBorder(BorderFactory.createLineBorder(UIConstants.SECONDARY_COLOR, 2));
                }
                
                dayButton.addActionListener(e -> {
                    selectedDate = LocalDate.of(viewDate.getYear(), viewDate.getMonth(), dayValue);
                    dateSelected = true;
                    dispose();
                });
                
                calendarPanel.add(dayButton);
            }
            
            // Update UI
            calendarPanel.revalidate();
            calendarPanel.repaint();
        }
        
        /**
         * Check if a date was selected
         * 
         * @return true if a date was selected
         */
        public boolean isDateSelected() {
            return dateSelected;
        }
        
        /**
         * Get the selected date
         * 
         * @return The selected date
         */
        public LocalDate getSelectedDate() {
            return selectedDate;
        }
    }
}