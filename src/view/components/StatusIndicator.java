package view.components;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import view.util.UIConstants;

/**
 * A colored status indicator component.
 * Shows status with appropriate color coding.
 */
public class StatusIndicator extends JPanel {
    private JLabel statusLabel;
    private String status;
    private Color statusColor;
    
    /**
     * Constructor
     * 
     * @param status The status text to display
     */
    public StatusIndicator(String status) {
        this.status = status;
        this.statusColor = determineColorForStatus(status);
        initializeUI();
    }
    
    /**
     * Constructor with custom color
     * 
     * @param status The status text to display
     * @param statusColor The color to use
     */
    public StatusIndicator(String status, Color statusColor) {
        this.status = status;
        this.statusColor = statusColor;
        initializeUI();
    }
    
    /**
     * Initialize UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setOpaque(false);
        
        statusLabel = new JLabel(status);
        statusLabel.setFont(UIConstants.NORMAL_FONT);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        
        add(statusLabel, BorderLayout.CENTER);
        
        setPreferredSize(new Dimension(100, 25));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(statusColor);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
    }
    
    /**
     * Update the status
     * 
     * @param status New status text
     */
    public void setStatus(String status) {
        this.status = status;
        this.statusColor = determineColorForStatus(status);
        statusLabel.setText(status);
        repaint();
    }
    
    /**
     * Set a custom color for the status
     * 
     * @param color The color to use
     */
    public void setStatusColor(Color color) {
        this.statusColor = color;
        repaint();
    }
    
    /**
     * Get the current status text
     * 
     * @return The status text
     */
    public String getStatus() {
        return this.status;
    }
    
    /**
     * Get the current status color
     * 
     * @return The status color
     */
    public Color getStatusColor() {
        return this.statusColor;
    }
    
    /**
     * Determine the appropriate color for a status
     * 
     * @param status The status text
     * @return Color corresponding to the status
     */
    private Color determineColorForStatus(String status) {
        if (status == null) return UIConstants.INACTIVE_COLOR;
        
        switch (status.toLowerCase()) {
            case "open":
            case "active":
            case "paid":
            case "completed":
            case "approved":
            case "success":
                return UIConstants.SUCCESS_COLOR;
                
            case "closed":
            case "cancelled":
            case "rejected":
            case "error":
            case "failed":
                return UIConstants.ERROR_COLOR;
                
            case "pending":
            case "in progress":
            case "partially paid":
            case "in review":
            case "waiting":
                return UIConstants.WARNING_COLOR;
                
            case "archived":
            case "inactive":
            case "draft":
                return UIConstants.INACTIVE_COLOR;
                
            default:
                return UIConstants.SECONDARY_COLOR;
        }
    }
    
    /**
     * Set the font for the status label
     * 
     * @param font The font to use
     */
    @Override
    public void setFont(Font font) {
        if (statusLabel != null) {
            statusLabel.setFont(font);
        }
    }
    
    /**
     * Set preferred width for the component
     * 
     * @param width Preferred width
     */
    public void setPreferredWidth(int width) {
        setPreferredSize(new Dimension(width, getPreferredSize().height));
    }
}