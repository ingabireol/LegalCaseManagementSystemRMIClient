package view.util;

import java.awt.Color;
import java.awt.Font;

/**
 * Common UI constants for the application.
 * This class centralizes styling and appearance settings.
 */
public class UIConstants {
    // Color scheme
    public static final Color PRIMARY_COLOR = new Color(42, 58, 86);     // Dark blue
    public static final Color SECONDARY_COLOR = new Color(2, 119, 189);  // Bright blue
    public static final Color ACCENT_COLOR = new Color(245, 245, 245);   // Light gray
    public static final Color TEXT_COLOR = new Color(51, 51, 51);        // Dark gray
    public static final Color ERROR_COLOR = new Color(176, 42, 55);      // Red
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);   // Green
    public static final Color WARNING_COLOR = new Color(241, 196, 15);   // Yellow
    public static final Color INACTIVE_COLOR = new Color(149, 165, 166); // Gray
    
    // Font definitions
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    public static final Font SUBTITLE_FONT = new Font("Arial", Font.BOLD, 18);
    public static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 16);
    public static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 14);
    public static final Font NORMAL_FONT = new Font("Arial", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 12);
    
    // Padding and margins
    public static final int STANDARD_PADDING = 10;
    public static final int LARGE_PADDING = 20;
    public static final int SMALL_PADDING = 5;
    
    // Table settings
    public static final int TABLE_ROW_HEIGHT = 25;
}