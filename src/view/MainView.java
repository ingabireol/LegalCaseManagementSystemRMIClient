package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;

import model.User;
import controller.UserController;
import controller.CaseController;
import controller.ClientController;
import view.util.UIConstants;
import view.util.IconManager;
import view.util.SwingUtils;
import view.clients.ClientsPanel;
import view.cases.CasesPanel;
import view.attorneys.AttorneysPanel;
import view.documents.DocumentsPanel;
import view.calendar.CalendarPanel;
import view.invoices.InvoicesPanel;
import view.admin.AdminPanel;

/**
 * Main application window for the Legal Case Management System.
 * Contains the main interface and navigation between different modules.
 */
public class MainView extends JFrame {
    private User currentUser;
    private UserController userController;
    private CaseController caseController;
    private ClientController clientController;
    
    // Content panels
    private JPanel dashboardPanel;
    private ClientsPanel clientsPanel;
    private CasesPanel casesPanel;
    private AttorneysPanel attorneysPanel;
    private DocumentsPanel documentsPanel;
    private CalendarPanel calendarPanel;
    private InvoicesPanel invoicesPanel;
    private AdminPanel adminPanel;
    
    // Navigation buttons
    private JButton dashboardButton;
    private JButton casesButton;
    private JButton clientsButton;
    private JButton attorneysButton;
    private JButton documentsButton;
    private JButton calendarButton;
    private JButton invoicesButton;
    private JButton adminButton;
    private JButton logoutButton;
    
    // Card layout for switching between panels
    private CardLayout cardLayout;
    private JPanel contentPanel;
    
    // Status components
    private JLabel statusLabel;
    private JLabel dateTimeLabel;
    
    /**
     * Constructor
     * 
     * @param user The authenticated user
     */
    public MainView(User user) {
        this.currentUser = user;
        this.userController = new UserController();
        this.caseController = new CaseController();
        this.clientController = new ClientController();
        
        initializeUI();
        loadDashboardData();
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setTitle("Legal Case Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 1200);
        setMinimumSize(new Dimension(1200, 1200));
        setLocationRelativeTo(null); // Center on screen
        
        try {
            // Set application icon if available
            ImageIcon appIcon = IconManager.getIcon("app_icon");
            if (appIcon != null) {
                setIconImage(appIcon.getImage());
            }
        } catch (Exception e) {
            // Continue without icon if there's an error
        }
        
        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create sidebar navigation panel
        JPanel sidebarPanel = createSidebarPanel();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        
        // Create content panel with card layout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Create all content panels
        createContentPanels();
        
        // Add content panels to card layout
        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(clientsPanel, "clients");
        contentPanel.add(casesPanel, "cases");
        contentPanel.add(attorneysPanel, "attorneys");
        contentPanel.add(documentsPanel, "documents");
        contentPanel.add(calendarPanel, "calendar");
        contentPanel.add(invoicesPanel, "invoices");
        
        // Only add admin panel if user is admin
        if (currentUser.isAdmin()) {
            contentPanel.add(adminPanel, "admin");
        }
        
        // Show dashboard panel by default
        cardLayout.show(contentPanel, "dashboard");
        highlightSelectedButton(dashboardButton);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Add status bar at the bottom
        JPanel statusBar = createStatusBar();
        mainPanel.add(statusBar, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Add window listener to handle close event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
    }
    
    /**
     * Create the header panel with user info and system title
     * 
     * @return The header panel
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.SECONDARY_COLOR));
        
        // System logo and title
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        logoPanel.setOpaque(false);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        
        ImageIcon logoIcon = IconManager.getScaledIcon("scales", 32, 32);
        if (logoIcon != null) {
            JLabel logoLabel = new JLabel(logoIcon);
            logoPanel.add(logoLabel);
        }
        
        JLabel titleLabel = new JLabel("Legal Case Management System");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        logoPanel.add(titleLabel);
        
        headerPanel.add(logoPanel, BorderLayout.WEST);
        
        // User info panel
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        userPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        
        JLabel userIconLabel = new JLabel();
        userIconLabel.setIcon(IconManager.getScaledIcon(IconManager.ICON_USER, 24, 24));
        if (userIconLabel.getIcon() == null) {
            userIconLabel.setText("👤"); // Unicode user icon as fallback
            userIconLabel.setForeground(Color.WHITE);
            userIconLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        }
        
        JLabel userNameLabel = new JLabel(currentUser.getFullName());
        userNameLabel.setFont(UIConstants.NORMAL_FONT);
        userNameLabel.setForeground(Color.WHITE);
        
        JLabel userRoleLabel = new JLabel("(" + currentUser.getRole() + ")");
        userRoleLabel.setFont(UIConstants.SMALL_FONT);
        userRoleLabel.setForeground(Color.WHITE);
        
        JButton profileButton = new JButton("Profile");
        profileButton.setFont(UIConstants.SMALL_FONT);
        profileButton.setFocusPainted(false);
        profileButton.addActionListener(e -> showUserProfile());
        
        userPanel.add(userIconLabel);
        userPanel.add(userNameLabel);
        userPanel.add(userRoleLabel);
        userPanel.add(Box.createHorizontalStrut(10));
        userPanel.add(profileButton);
        
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * Create the sidebar panel with navigation buttons
     * 
     * @return The sidebar panel
     */
    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setBackground(new Color(52, 68, 96)); // Slightly lighter than PRIMARY_COLOR
        sidebarPanel.setPreferredSize(new Dimension(220, getHeight()));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIConstants.SECONDARY_COLOR));
        
        // Create navigation buttons
        dashboardButton = createNavButton("Dashboard", IconManager.ICON_SEARCH);
        casesButton = createNavButton("Cases", IconManager.ICON_CASE);
        clientsButton = createNavButton("Clients", IconManager.ICON_CLIENT);
        attorneysButton = createNavButton("Attorneys", IconManager.ICON_ATTORNEY);
        documentsButton = createNavButton("Documents", IconManager.ICON_DOCUMENT);
        calendarButton = createNavButton("Calendar", IconManager.ICON_CALENDAR);
        invoicesButton = createNavButton("Invoices", IconManager.ICON_INVOICE);
        adminButton = createNavButton("Administration", IconManager.ICON_SETTINGS);
        logoutButton = createNavButton("Logout", IconManager.ICON_LOGOUT);
        
        // Restrict access to admin panel
        adminButton.setVisible(currentUser.isAdmin());
        
        // Restrict access to financial information
        invoicesButton.setVisible(currentUser.canViewFinancials());
        
        // Add buttons to sidebar
        sidebarPanel.add(Box.createVerticalStrut(20));
        sidebarPanel.add(dashboardButton);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(casesButton);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(clientsButton);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(attorneysButton);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(documentsButton);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(calendarButton);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(invoicesButton);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(adminButton);
        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(logoutButton);
        sidebarPanel.add(Box.createVerticalStrut(20));
        
        // Add action listeners
        dashboardButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "dashboard");
            highlightSelectedButton(dashboardButton);
            updateStatus("Dashboard loaded");
            loadDashboardData();
        });
        
        casesButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "cases");
            highlightSelectedButton(casesButton);
            updateStatus("Cases management loaded");
        });
        
        clientsButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "clients");
            highlightSelectedButton(clientsButton);
            updateStatus("Clients management loaded");
        });
        
        attorneysButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "attorneys");
            highlightSelectedButton(attorneysButton);
            updateStatus("Attorneys management loaded");
        });
        
        documentsButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "documents");
            highlightSelectedButton(documentsButton);
            updateStatus("Documents management loaded");
        });
        
        calendarButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "calendar");
            highlightSelectedButton(calendarButton);
            updateStatus("Calendar & Events loaded");
        });
        
        invoicesButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "invoices");
            highlightSelectedButton(invoicesButton);
            updateStatus("Invoices & Payments loaded");
        });
        
        adminButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "admin");
            highlightSelectedButton(adminButton);
            updateStatus("System Administration loaded");
        });
        
        logoutButton.addActionListener(e -> logout());
        
        return sidebarPanel;
    }
    
    /**
     * Create a styled navigation button for the sidebar
     * 
     * @param text Button text
     * @param iconName Icon name
     * @return Styled JButton
     */
    private JButton createNavButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.NORMAL_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 68, 96)); // Same as sidebar
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setMaximumSize(new Dimension(220, 50));
        
        // Set icon if available
        ImageIcon icon = IconManager.getScaledIcon(iconName, 20, 20);
        if (icon != null) {
            button.setIcon(icon);
            button.setIconTextGap(10);
        }
        
        // Change appearance on hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!button.getBackground().equals(UIConstants.SECONDARY_COLOR)) {
                    button.setBackground(new Color(62, 78, 106)); // Lighter on hover
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!button.getBackground().equals(UIConstants.SECONDARY_COLOR)) {
                    button.setBackground(new Color(52, 68, 96)); // Back to normal
                }
            }
        });
        
        return button;
    }
    
    /**
     * Highlight the selected navigation button
     * 
     * @param selectedButton The button to highlight
     */
    private void highlightSelectedButton(JButton selectedButton) {
        // Reset all buttons
        JButton[] buttons = {
            dashboardButton, casesButton, clientsButton, attorneysButton,
            documentsButton, calendarButton, invoicesButton, adminButton
        };
        
        for (JButton button : buttons) {
            if (button != null && button != selectedButton) {
                button.setBackground(new Color(52, 68, 96));
                button.setFont(UIConstants.NORMAL_FONT);
            }
        }
        
        // Highlight selected button
        if (selectedButton != null) {
            selectedButton.setBackground(UIConstants.SECONDARY_COLOR);
            selectedButton.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        }
    }
    
    /**
     * Create content panels for different sections of the application
     */
    private void createContentPanels() {
        // Dashboard panel
        dashboardPanel = createDashboardPanel();
        
        // Clients panel
        clientsPanel = new ClientsPanel();
        
        // Cases panel
        casesPanel = new CasesPanel();
        
        // Attorneys panel
        attorneysPanel = new AttorneysPanel();
        
        // Documents panel
        documentsPanel = new DocumentsPanel();
        
        // Calendar panel
        calendarPanel = new CalendarPanel();
        
        // Invoices panel
        invoicesPanel = new InvoicesPanel();
        
        // Admin panel
        if (currentUser.isAdmin()) {
            adminPanel = new AdminPanel();
        }
    }
    
    /**
     * Create the dashboard panel with statistics and quick access
     * 
     * @return The dashboard panel
     */
    private JPanel createDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(Color.WHITE);
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName());
        welcomeLabel.setFont(UIConstants.TITLE_FONT);
        welcomeLabel.setForeground(UIConstants.PRIMARY_COLOR);
        
        JLabel dateLabel = new JLabel(LocalDate.now().toString());
        dateLabel.setFont(UIConstants.NORMAL_FONT);
        dateLabel.setForeground(UIConstants.TEXT_COLOR);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        
        titlePanel.add(welcomeLabel);
        titlePanel.add(dateLabel);
        dashboardPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Main content with grid of cards
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        // Create statistic cards (initial values, will be updated in loadDashboardData())
        JPanel activeCasesCard = createStatCard("Active Cases", "0", UIConstants.SECONDARY_COLOR);
        activeCasesCard.setName("activeCasesCard");
        
        JPanel clientsCard = createStatCard("Total Clients", "0", new Color(46, 204, 113));
        clientsCard.setName("clientsCard");
        
        JPanel upcomingEventsCard = createStatCard("Upcoming Events", "0", new Color(155, 89, 182));
        upcomingEventsCard.setName("upcomingEventsCard");
        
        JPanel overdueInvoicesCard = createStatCard("Overdue Invoices", "0", UIConstants.ERROR_COLOR);
        overdueInvoicesCard.setName("overdueInvoicesCard");
        
        // Add cards to grid
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(activeCasesCard, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        contentPanel.add(clientsCard, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(upcomingEventsCard, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        contentPanel.add(overdueInvoicesCard, gbc);
        
        // Add quick action buttons
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setOpaque(false);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        
        JButton newCaseButton = SwingUtils.createPrimaryButton("New Case", e -> createNewCase());
        JButton newClientButton = SwingUtils.createButton("New Client", e -> createNewClient());
        JButton newDocumentButton = SwingUtils.createButton("New Document", e -> createNewDocument());
        
        actionsPanel.add(newCaseButton);
        actionsPanel.add(Box.createHorizontalStrut(10));
        actionsPanel.add(newClientButton);
        actionsPanel.add(Box.createHorizontalStrut(10));
        actionsPanel.add(newDocumentButton);
        
        // Add actions panel to top of content panel
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.0;
        contentPanel.add(actionsPanel, gbc);
        
        dashboardPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Create recent activity panel
        JPanel recentActivityPanel = new JPanel(new BorderLayout());
        recentActivityPanel.setBackground(Color.WHITE);
        recentActivityPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 20, 20, 20),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIConstants.SECONDARY_COLOR),
                "Recent Activity",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                UIConstants.LABEL_FONT,
                UIConstants.SECONDARY_COLOR
            )
        ));
        
        // Recent activity list (placeholder for now)
        DefaultListModel<String> activityModel = new DefaultListModel<>();
        activityModel.addElement("Case #12345 - Johnson v. Smith - Updated 10 minutes ago");
        activityModel.addElement("Invoice #INV-2023-042 created for Client XYZ Corp - 25 minutes ago");
        activityModel.addElement("Meeting scheduled with Jane Doe - Tomorrow at 9:00 AM");
        activityModel.addElement("New document uploaded to Case #12345 - 2 hours ago");
        activityModel.addElement("Payment received from ABC Holdings - $5,250.00 - Today at 11:15 AM");
        
        JList<String> activityList = new JList<>(activityModel);
        activityList.setFont(UIConstants.NORMAL_FONT);
        activityList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        activityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane activityScrollPane = new JScrollPane(activityList);
        activityScrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        recentActivityPanel.add(activityScrollPane, BorderLayout.CENTER);
        
        dashboardPanel.add(recentActivityPanel, BorderLayout.SOUTH);
        
        return dashboardPanel;
    }
    
    /**
     * Create a statistic card for the dashboard
     * 
     * @param title Card title
     * @param value Statistic value
     * @param color Card color
     * @return The statistic card panel
     */
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background with slight gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, color,
                    0, getHeight(), color.darker());
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        cardPanel.setPreferredSize(new Dimension(250, 150));
        
        // Card title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.LABEL_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Card value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setName("value");
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Card top panel (title and icon)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        cardPanel.add(topPanel, BorderLayout.NORTH);
        cardPanel.add(valueLabel, BorderLayout.CENTER);
        
        return cardPanel;
    }
    
    /**
     * Update a statistic card with a new value
     * 
     * @param card The statistic card panel
     * @param value The new value
     */
    private void updateStatCard(JPanel card, String value) {
        if (card == null) return;
        
        Component[] components = card.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                // Look for value label in top panel
                JPanel panel = (JPanel) component;
                for (Component c : panel.getComponents()) {
                    if (c instanceof JLabel && "value".equals(c.getName())) {
                        ((JLabel) c).setText(value);
                        return;
                    }
                }
            } else if (component instanceof JLabel && "value".equals(component.getName())) {
                // Value label directly in card
                ((JLabel) component).setText(value);
                return;
            }
        }
    }
    
    /**
     * Create the status bar for the bottom of the main window
     * 
     * @return The status bar panel
     */
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(UIConstants.ACCENT_COLOR);
        statusBar.setPreferredSize(new Dimension(getWidth(), 25));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        
        // Status message
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(UIConstants.SMALL_FONT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        // Current date/time
        dateTimeLabel = new JLabel(LocalDate.now().toString());
        dateTimeLabel.setFont(UIConstants.SMALL_FONT);
        dateTimeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        statusBar.add(dateTimeLabel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    /**
     * Update the status message
     * 
     * @param message The status message
     */
    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
    
    /**
     * Load dashboard data from controllers
     */
    private void loadDashboardData() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private int activeCases = 0;
            private int totalClients = 0;
            private int upcomingEvents = 0;
            private int overdueInvoices = 0;
            
            @Override
            protected Void doInBackground() throws Exception {
                updateStatus("Loading dashboard data...");
                
                try {
                    // Get data from controllers
                    activeCases = caseController.findCasesByStatus("Open").size();
                    totalClients = clientController.getAllClients().size();
                    
                    // These would be populated from actual controllers in a complete implementation
                    upcomingEvents = 12; // Placeholder
                    overdueInvoices = 5; // Placeholder
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    // Update dashboard cards
                    JPanel activeCasesCard = (JPanel) SwingUtils.findComponentByName(dashboardPanel, "activeCasesCard");
                    JPanel clientsCard = (JPanel) SwingUtils.findComponentByName(dashboardPanel, "clientsCard");
                    JPanel upcomingEventsCard = (JPanel) SwingUtils.findComponentByName(dashboardPanel, "upcomingEventsCard");
                    JPanel overdueInvoicesCard = (JPanel) SwingUtils.findComponentByName(dashboardPanel, "overdueInvoicesCard");
                    
                    // Update card values using utility method
                    if (activeCasesCard != null) {
                        JLabel valueLabel = (JLabel) SwingUtils.findComponentByName(activeCasesCard, "value");
                        if (valueLabel != null) {
                            valueLabel.setText(Integer.toString(activeCases));
                        }
                    }
                    
                    if (clientsCard != null) {
                        JLabel valueLabel = (JLabel) SwingUtils.findComponentByName(clientsCard, "value");
                        if (valueLabel != null) {
                            valueLabel.setText(Integer.toString(totalClients));
                        }
                    }
                    
                    if (upcomingEventsCard != null) {
                        JLabel valueLabel = (JLabel) SwingUtils.findComponentByName(upcomingEventsCard, "value");
                        if (valueLabel != null) {
                            valueLabel.setText(Integer.toString(upcomingEvents));
                        }
                    }
                    
                    if (overdueInvoicesCard != null) {
                        JLabel valueLabel = (JLabel) SwingUtils.findComponentByName(overdueInvoicesCard, "value");
                        if (valueLabel != null) {
                            valueLabel.setText(Integer.toString(overdueInvoices));
                        }
                    }
                    
                    updateStatus("Dashboard data loaded successfully");
                } catch (Exception e) {
                    updateStatus("Error loading dashboard data");
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Show user profile dialog
     */
    private void showUserProfile() {
        // Placeholder for user profile dialog
        JOptionPane.showMessageDialog(
            this,
            "User Profile for: " + currentUser.getFullName() + "\n" +
            "Role: " + currentUser.getRole() + "\n" +
            "Email: " + currentUser.getEmail() + "\n\n" +
            "This feature will be fully implemented in the future.",
            "User Profile",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Create a new case (placeholder)
     */
    private void createNewCase() {
        // Redirect to cases panel
        cardLayout.show(contentPanel, "cases");
        highlightSelectedButton(casesButton);
        updateStatus("Cases management loaded");
        
        // Placeholder message - would be replaced with actual case creation dialog
        JOptionPane.showMessageDialog(
            this,
            "New Case Creation\n\n" +
            "This feature will allow you to create a new case.\n" +
            "It will be fully implemented in the future.",
            "Create New Case",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Create a new client (placeholder)
     */
    private void createNewClient() {
        // Redirect to clients panel
        cardLayout.show(contentPanel, "clients");
        highlightSelectedButton(clientsButton);
        updateStatus("Clients management loaded");
        
        // Trigger the add client functionality on the clients panel
        clientsPanel.addNewClient();
    }
    
    /**
     * Create a new document (placeholder)
     */
    private void createNewDocument() {
        // Redirect to documents panel
        cardLayout.show(contentPanel, "documents");
        highlightSelectedButton(documentsButton);
        updateStatus("Documents management loaded");
        
        // Placeholder message - would be replaced with actual document upload dialog
        JOptionPane.showMessageDialog(
            this,
            "New Document Upload\n\n" +
            "This feature will allow you to upload a new document.\n" +
            "It will be fully implemented in the future.",
            "Upload New Document",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Confirm exit before closing application
     */
    private void confirmExit() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit the application?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    /**
     * Perform logout and return to login screen
     */
    private void logout() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginView loginView = new LoginView();
                loginView.setVisible(true);
            });
        }
    }
    
    /**
     * Main method to start the application
     */
    public static void main(String[] args) {
        try {
            // Set look and feel to system default
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Add additional UI customizations
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("TextComponent.arc", 8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }
}