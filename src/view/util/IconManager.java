package view.util;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages icon resources for the application.
 */
public class IconManager {
    private static final Map<String, ImageIcon> iconCache = new HashMap<>();
    
    // Define common icon names
    public static final String ICON_USER = "user";
    public static final String ICON_CLIENT = "client";
    public static final String ICON_CASE = "case";
    public static final String ICON_DOCUMENT = "document";
    public static final String ICON_ATTORNEY = "attorney";
    public static final String ICON_CALENDAR = "calendar";
    public static final String ICON_EVENT = "event";
    public static final String ICON_INVOICE = "invoice";
    public static final String ICON_PAYMENT = "payment";
    public static final String ICON_SEARCH = "search";
    public static final String ICON_ADD = "add";
    public static final String ICON_EDIT = "edit";
    public static final String ICON_DELETE = "delete";
    public static final String ICON_SAVE = "save";
    public static final String ICON_CANCEL = "cancel";
    public static final String ICON_REFRESH = "refresh";
    public static final String ICON_SETTINGS = "settings";
    public static final String ICON_LOGOUT = "logout";
    
    /**
     * Private constructor to prevent instantiation
     */
    private IconManager() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Get an icon by name
     * 
     * @param iconName The icon name
     * @return The icon, or null if not found
     */
    public static ImageIcon getIcon(String iconName) {
        // Check if the icon is already cached
        if (iconCache.containsKey(iconName)) {
            return iconCache.get(iconName);
        }
        
        // Try to load the icon
        String fileName = iconName + ".png";
        URL iconUrl = IconManager.class.getResource("/icons/" + fileName);
        if (iconUrl == null) {
            System.err.println("Icon not found: " + fileName);
            return null;
        }
        
        // Create and cache the icon
        ImageIcon icon = new ImageIcon(iconUrl);
        iconCache.put(iconName, icon);
        
        return icon;
    }
    
    /**
     * Get a scaled icon by name
     * 
     * @param iconName The icon name
     * @param width The desired width
     * @param height The desired height
     * @return The scaled icon, or null if not found
     */
    public static ImageIcon getScaledIcon(String iconName, int width, int height) {
        ImageIcon original = getIcon(iconName);
        if (original == null) {
            return null;
        }
        
        // Check if the scaled icon is already cached
        String scaledName = iconName + "_" + width + "x" + height;
        if (iconCache.containsKey(scaledName)) {
            return iconCache.get(scaledName);
        }
        
        // Scale the icon
        Image scaledImage = original.getImage().getScaledInstance(
            width, height, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        
        // Cache the scaled icon
        iconCache.put(scaledName, scaledIcon);
        
        return scaledIcon;
    }
    
    /**
     * Create a button with an icon
     * 
     * @param iconName The icon name
     * @param tooltip The button tooltip
     * @return A button with the specified icon
     */
    public static JButton createIconButton(String iconName, String tooltip) {
        ImageIcon icon = getIcon(iconName);
        JButton button = new JButton();
        
        if (icon != null) {
            button.setIcon(icon);
        } else {
            button.setText(tooltip);
        }
        
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        return button;
    }
    
    /**
     * Create a label with an icon
     * 
     * @param iconName The icon name
     * @return A label with the specified icon
     */
    public static JLabel createIconLabel(String iconName) {
        ImageIcon icon = getIcon(iconName);
        JLabel label = new JLabel();
        
        if (icon != null) {
            label.setIcon(icon);
        }
        
        return label;
    }
}