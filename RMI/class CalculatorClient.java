package distributedcalculator;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CalculatorClient {
    
    private CalculatorInterface.CalculatorService server;
    private JFrame frame;
    private JTextArea resultArea;
    private JTabbedPane tabbedPane;
    private JLabel statusLabel;
    
    // Matrix components
    private JSpinner matrixARows, matrixACols, matrixBRows, matrixBCols;
    private JTextArea matrixAText, matrixBText, matrixResultText;
    
    // Array components
    private JSpinner arraySizeSpinner;
    private JTextArea arrayInputText, arrayResultText;
    
    // Prime components
    private JSpinner primeStart, primeEnd;
    private JTextArea primeResultText;
    
    // Connection components
    private JTextField hostField;
    private JSpinner portSpinner;
    private JButton connectButton;
    
    // Colors
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);     // Blue
    private final Color SECONDARY_COLOR = new Color(39, 174, 96);    // Green
    private final Color ACCENT_COLOR = new Color(142, 68, 173);      // Purple
    private final Color WARNING_COLOR = new Color(231, 76, 60);      // Red
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);     // Bright Green
    private final Color LIGHT_BG = new Color(248, 249, 250);         // Light Gray
    private final Color DARK_BG = new Color(52, 73, 94);             // Dark Blue
    private final Color TEXT_COLOR = new Color(44, 62, 80);          // Dark Text
    
    public CalculatorClient() {
        initializeGUI();
    }
    
    private void initializeGUI() {
        frame = new JFrame("Distributed Calculator System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 850);
        
        // Set application icon
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        
        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_BG);
        
        // ========== HEADER SECTION ==========
        JPanel headerPanel = createHeaderPanel();
        
        // ========== CONNECTION SECTION ==========
        JPanel connectionPanel = createConnectionPanel();
        
        // ========== TABS SECTION ==========
        tabbedPane = createStyledTabbedPane();
        tabbedPane.addTab("📊 Matrix Multiplication", createMatrixPanel());
        tabbedPane.addTab("📈 Array Sorting", createArrayPanel());
        tabbedPane.addTab("🔢 Prime Numbers", createPrimePanel());
        
        // ========== LOG SECTION ==========
        JPanel logPanel = createLogPanel();
        
        // ========== CONTROL SECTION ==========
        JPanel controlPanel = createControlPanel();
        
        // Add all sections to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(connectionPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(logPanel, BorderLayout.SOUTH);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        frame.add(mainPanel);
        centerFrame();
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(DARK_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Title
        JLabel titleLabel = new JLabel("🧮 Distributed Calculator System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("RMI-Based Parallel Processing Calculator");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 200, 200));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        return headerPanel;
    }
    
    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        // Connection icon
        JLabel connectionIcon = new JLabel("🔌");
        connectionIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        // Host input
        JLabel hostLabel = new JLabel("Server Host:");
        hostLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        hostLabel.setForeground(TEXT_COLOR);
        
        hostField = new JTextField("localhost", 12);
        styleTextField(hostField);
        
        // Port input
        JLabel portLabel = new JLabel("Port:");
        portLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        portLabel.setForeground(TEXT_COLOR);
        
        portSpinner = new JSpinner(new SpinnerNumberModel(1099, 1000, 9999, 1));
        styleSpinner(portSpinner);
        
        // Connect button
        connectButton = new JButton("🚀 Connect to Server");
        styleButton(connectButton, SECONDARY_COLOR, Color.WHITE);
        connectButton.addActionListener(e -> connectToServer());
        
        // Status label
        statusLabel = new JLabel("⏳ Not connected");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(WARNING_COLOR);
        
        // Add components
        panel.add(connectionIcon);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(hostLabel);
        panel.add(hostField);
        panel.add(portLabel);
        panel.add(portSpinner);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(connectButton);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(statusLabel);
        
        return panel;
    }
    
    private JTabbedPane createStyledTabbedPane() {
        JTabbedPane pane = new JTabbedPane();
        pane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pane.setBackground(LIGHT_BG);
        pane.setForeground(TEXT_COLOR);
        
        // Custom UI for tabs
        UIManager.put("TabbedPane.selected", PRIMARY_COLOR);
        pane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, 
                    int x, int y, int w, int h, boolean isSelected) {
                if (isSelected) {
                    g.setColor(PRIMARY_COLOR);
                    g.fillRect(x, y, w, h);
                } else {
                    g.setColor(new Color(220, 220, 220));
                    g.fillRect(x, y, w, h);
                }
            }
            
            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, 
                    int x, int y, int w, int h, boolean isSelected) {
                // No border
            }
            
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                // Custom border for content area
                g.setColor(PRIMARY_COLOR);
                g.drawRect(0, 0, pane.getWidth() - 1, pane.getHeight() - 1);
            }
        });
        
        return pane;
    }
    
    private JPanel createMatrixPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // ========== CONTROL PANEL ==========
        JPanel controlPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            "⚙️ Matrix Dimensions",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            PRIMARY_COLOR
        ));
        controlPanel.setBackground(LIGHT_BG);
        controlPanel.setForeground(TEXT_COLOR);
        
        String[] labels = {"Matrix A Rows:", "Matrix A Cols:", "Matrix B Rows:", "Matrix B Cols:"};
        JSpinner[] spinners = new JSpinner[4];
        
        for (int i = 0; i < 4; i++) {
            JLabel label = new JLabel(labels[i], SwingConstants.RIGHT);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            controlPanel.add(label);
            
            spinners[i] = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
            styleSpinner(spinners[i]);
            controlPanel.add(spinners[i]);
        }
        
        matrixARows = spinners[0];
        matrixACols = spinners[1];
        matrixBRows = spinners[2];
        matrixBCols = spinners[3];
        
        // ========== MATRIX PANEL ==========
        JPanel matrixPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        matrixPanel.setBackground(Color.WHITE);
        
        // Matrix A
        JPanel matrixAPanel = createStyledTextPanel("Matrix A 🅰️", true);
        matrixAText = ((JTextArea)((JScrollPane)matrixAPanel.getComponent(1)).getViewport().getView());
        matrixAText.setText("1 2 3\n4 5 6\n7 8 9");
        
        // Matrix B
        JPanel matrixBPanel = createStyledTextPanel("Matrix B 🅱️", true);
        matrixBText = ((JTextArea)((JScrollPane)matrixBPanel.getComponent(1)).getViewport().getView());
        matrixBText.setText("9 8 7\n6 5 4\n3 2 1");
        
        // Result Matrix
        JPanel resultPanel = createStyledTextPanel("Result Matrix 🎯", false);
        matrixResultText = ((JTextArea)((JScrollPane)resultPanel.getComponent(1)).getViewport().getView());
        matrixResultText.setBackground(new Color(240, 248, 255)); // Alice Blue
        
        matrixPanel.add(matrixAPanel);
        matrixPanel.add(matrixBPanel);
        matrixPanel.add(resultPanel);
        
        // ========== BUTTON PANEL ==========
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        String[] buttonLabels = {"🎲 Generate Random", "📋 Load Example", "🗑️ Clear Result"};
        Color[] buttonColors = {ACCENT_COLOR, PRIMARY_COLOR, WARNING_COLOR};
        
        for (int i = 0; i < 3; i++) {
            JButton button = new JButton(buttonLabels[i]);
            styleButton(button, buttonColors[i], Color.WHITE);
            
            switch (i) {
                case 0: button.addActionListener(e -> generateRandomMatrices()); break;
                case 1: button.addActionListener(e -> {
                    matrixAText.setText("1 2 3\n4 5 6\n7 8 9");
                    matrixBText.setText("9 8 7\n6 5 4\n3 2 1");
                    matrixARows.setValue(3);
                    matrixACols.setValue(3);
                    matrixBRows.setValue(3);
                    matrixBCols.setValue(3);
                    matrixResultText.setText("");
                }); break;
                case 2: button.addActionListener(e -> matrixResultText.setText("")); break;
            }
            
            buttonPanel.add(button);
        }
        
        // ========== ASSEMBLE PANEL ==========
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(matrixPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createArrayPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // ========== CONTROL PANEL ==========
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
            "⚙️ Array Configuration",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            SECONDARY_COLOR
        ));
        controlPanel.setBackground(LIGHT_BG);
        
        JLabel sizeLabel = new JLabel("Array Size (n):");
        sizeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        controlPanel.add(sizeLabel);
        
        arraySizeSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
        styleSpinner(arraySizeSpinner);
        controlPanel.add(arraySizeSpinner);
        
        JButton sizeButton = new JButton("📏 Set Size");
        styleButton(sizeButton, PRIMARY_COLOR, Color.WHITE);
        sizeButton.addActionListener(e -> updateArrayInput());
        controlPanel.add(sizeButton);
        
        JButton randomButton = new JButton("🎲 Generate Random");
        styleButton(randomButton, ACCENT_COLOR, Color.WHITE);
        randomButton.addActionListener(e -> generateRandomArray());
        controlPanel.add(randomButton);
        
        JLabel infoLabel = new JLabel("(Enter numbers separated by space or newline)");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        infoLabel.setForeground(Color.GRAY);
        controlPanel.add(infoLabel);
        
        // ========== CONTENT PANEL ==========
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBackground(Color.WHITE);
        
        // Input panel
        JPanel inputPanel = createStyledTextPanel("📥 Input Array", true);
        arrayInputText = ((JTextArea)((JScrollPane)inputPanel.getComponent(1)).getViewport().getView());
        arrayInputText.setText("5\n12\n8\n3\n15\n7\n9\n1\n20\n6");
        
        // Result panel
        JPanel resultPanel = createStyledTextPanel("📤 Sorted Result", false);
        arrayResultText = ((JTextArea)((JScrollPane)resultPanel.getComponent(1)).getViewport().getView());
        arrayResultText.setBackground(new Color(240, 255, 240)); // Honeydew
        
        contentPanel.add(inputPanel);
        contentPanel.add(resultPanel);
        
        // ========== BUTTON PANEL ==========
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton exampleButton = new JButton("📋 Load Example");
        styleButton(exampleButton, PRIMARY_COLOR, Color.WHITE);
        exampleButton.addActionListener(e -> {
            arraySizeSpinner.setValue(10);
            arrayInputText.setText("5\n12\n8\n3\n15\n7\n9\n1\n20\n6");
            arrayResultText.setText("");
        });
        
        JButton clearButton = new JButton("🗑️ Clear Result");
        styleButton(clearButton, WARNING_COLOR, Color.WHITE);
        clearButton.addActionListener(e -> arrayResultText.setText(""));
        
        buttonPanel.add(exampleButton);
        buttonPanel.add(clearButton);
        
        // ========== ASSEMBLE PANEL ==========
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createPrimePanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // ========== CONTROL PANEL ==========
        JPanel controlPanel = new JPanel(new GridLayout(2, 2, 15, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 2),
            "⚙️ Prime Number Range",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            ACCENT_COLOR
        ));
        controlPanel.setBackground(LIGHT_BG);
        
        String[] labels = {"Start Range:", "End Range:"};
        JSpinner[] spinners = new JSpinner[2];
        
        for (int i = 0; i < 2; i++) {
            JLabel label = new JLabel(labels[i], SwingConstants.RIGHT);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            controlPanel.add(label);
            
            if (i == 0) {
                spinners[i] = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
            } else {
                spinners[i] = new JSpinner(new SpinnerNumberModel(100, 2, 100000, 1));
            }
            styleSpinner(spinners[i]);
            controlPanel.add(spinners[i]);
        }
        
        primeStart = spinners[0];
        primeEnd = spinners[1];
        
        // ========== EXAMPLE BUTTONS ==========
        JPanel examplePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        examplePanel.setBackground(Color.WHITE);
        
        String[] exampleLabels = {"1-100", "1-1000", "1-10000", "1-50000"};
        int[] exampleValues = {100, 1000, 10000, 50000};
        Color exampleColor = new Color(155, 89, 182); // Light Purple
        
        for (int i = 0; i < exampleLabels.length; i++) {
            JButton button = new JButton(exampleLabels[i]);
            styleButton(button, exampleColor, Color.WHITE);
            final int value = exampleValues[i];
            button.addActionListener(e -> {
                primeStart.setValue(1);
                primeEnd.setValue(value);
            });
            examplePanel.add(button);
        }
        
        // ========== RESULT PANEL ==========
        JPanel resultPanel = createStyledTextPanel("🔍 Prime Numbers Found", false);
        primeResultText = ((JTextArea)((JScrollPane)resultPanel.getComponent(1)).getViewport().getView());
        primeResultText.setBackground(new Color(255, 250, 240)); // Floral White
        
        // ========== BUTTON PANEL ==========
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton clearButton = new JButton("🗑️ Clear Result");
        styleButton(clearButton, WARNING_COLOR, Color.WHITE);
        clearButton.addActionListener(e -> primeResultText.setText(""));
        
        buttonPanel.add(clearButton);
        
        // ========== ASSEMBLE PANEL ==========
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(examplePanel, BorderLayout.CENTER);
        mainPanel.add(resultPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "📋 Operation Log",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(52, 152, 219)
        ));
        panel.setBackground(LIGHT_BG);
        
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        resultArea.setBackground(new Color(253, 254, 254));
        resultArea.setForeground(TEXT_COLOR);
        resultArea.setMargin(new Insets(5, 5, 5, 5));
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        buttonPanel.setBackground(new Color(236, 240, 241));
        
        // Execute Task Button (Big Green Button)
        JButton executeButton = new JButton("🚀 Execute Task");
        executeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        executeButton.setBackground(SUCCESS_COLOR);
        executeButton.setForeground(Color.WHITE);
        executeButton.setFocusPainted(false);
        executeButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(39, 174, 96), 2),
            BorderFactory.createEmptyBorder(12, 30, 12, 30)
        ));
        executeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        executeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                executeButton.setBackground(new Color(39, 174, 96));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                executeButton.setBackground(SUCCESS_COLOR);
            }
        });
        executeButton.addActionListener(e -> executeTask());
        
        // Clear Log Button
        JButton clearButton = new JButton("🗑️ Clear Log");
        styleButton(clearButton, new Color(149, 165, 166), Color.WHITE);
        clearButton.addActionListener(e -> resultArea.setText(""));
        
        // Disconnect Button
        JButton disconnectButton = new JButton("🔌 Disconnect");
        styleButton(disconnectButton, WARNING_COLOR, Color.WHITE);
        disconnectButton.addActionListener(e -> disconnect());
        
        buttonPanel.add(executeButton);
        buttonPanel.add(Box.createHorizontalStrut(30));
        buttonPanel.add(clearButton);
        buttonPanel.add(disconnectButton);
        
        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setBackground(new Color(236, 240, 241));
        
        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        timeLabel.setForeground(new Color(127, 140, 141));
        
        // Update time every second
        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            timeLabel.setText("🕒 " + sdf.format(new Date()));
        });
        timer.start();
        
        statusPanel.add(timeLabel);
        
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(statusPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    // ========== HELPER METHODS FOR STYLING ==========
    
    private JPanel createStyledTextPanel(String title, boolean editable) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        textArea.setEditable(editable);
        textArea.setBackground(editable ? Color.WHITE : LIGHT_BG);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setMargin(new Insets(8, 8, 8, 8));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(120, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setBackground(Color.WHITE);
    }
    
    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI", Font.BOLD, 13));
        spinner.setPreferredSize(new Dimension(80, 35));
        JFormattedTextField tf = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        tf.setBackground(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }
    
    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }
    
    private void centerFrame() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        frame.setLocation((screenSize.width - frameSize.width) / 2,
                         (screenSize.height - frameSize.height) / 2);
    }
    
    // ========== BUSINESS LOGIC METHODS ==========
    
    private void updateArrayInput() {
        int size = (Integer) arraySizeSpinner.getValue();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append((int)(Math.random() * 1000)).append("\n");
        }
        arrayInputText.setText(sb.toString().trim());
    }
    
    private void generateRandomArray() {
        int size = (Integer) arraySizeSpinner.getValue();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append((int)(Math.random() * 1000)).append("\n");
        }
        arrayInputText.setText(sb.toString().trim());
    }
    
    private void connectToServer() {
        String host = hostField.getText();
        int port = (Integer) portSpinner.getValue();
        
        try {
            log("🔗 Connecting to server at " + host + ":" + port + "...");
            Registry registry = LocateRegistry.getRegistry(host, port);
            server = (CalculatorInterface.CalculatorService) registry.lookup("CalculatorService");
            
            if (server.isAvailable()) {
                statusLabel.setText("✅ Connected to " + server.getServerInfo());
                statusLabel.setForeground(SUCCESS_COLOR);
                log("✅ Successfully connected to server!");
                log("📊 Server info: " + server.getServerInfo());
            } else {
                statusLabel.setText("⚠ Server busy");
                statusLabel.setForeground(Color.ORANGE);
                log("⚠ Server is busy but accepting connections");
            }
        } catch (Exception e) {
            statusLabel.setText("❌ Connection failed");
            statusLabel.setForeground(WARNING_COLOR);
            log("❌ Connection failed: " + e.getMessage());
            JOptionPane.showMessageDialog(frame,
                "<html><div style='text-align: center;'>"
                + "<b>Connection Failed!</b><br><br>"
                + e.getMessage() + "<br><br>"
                + "Make sure CalculatorServer is running first!"
                + "</div></html>",
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void disconnect() {
        server = null;
        statusLabel.setText("⏳ Not connected");
        statusLabel.setForeground(WARNING_COLOR);
        log("🔌 Disconnected from server");
    }
    
    private void generateRandomMatrices() {
        int aRows = (Integer) matrixARows.getValue();
        int aCols = (Integer) matrixACols.getValue();
        int bRows = (Integer) matrixBRows.getValue();
        int bCols = (Integer) matrixBCols.getValue();
        
        // Generate matrix A
        StringBuilder matrixA = new StringBuilder();
        for(int i = 0; i < aRows; i++) {
            for(int j = 0; j < aCols; j++) {
                matrixA.append((int)(Math.random() * 10)).append(" ");
            }
            matrixA.append("\n");
        }
        matrixAText.setText(matrixA.toString().trim());
        
        // Generate matrix B
        StringBuilder matrixB = new StringBuilder();
        for(int i = 0; i < bRows; i++) {
            for(int j = 0; j < bCols; j++) {
                matrixB.append((int)(Math.random() * 10)).append(" ");
            }
            matrixB.append("\n");
        }
        matrixBText.setText(matrixB.toString().trim());
        
        // Clear result
        matrixResultText.setText("");
        
        log("🎲 Generated random matrices: " + aRows + "x" + aCols + " and " + bRows + "x" + bCols);
    }
    
    private void executeTask() {
        if (server == null) {
            JOptionPane.showMessageDialog(frame,
                "<html><div style='text-align: center;'>"
                + "<b>Not Connected!</b><br><br>"
                + "Please connect to a server first.<br>"
                + "Click the 'Connect to Server' button."
                + "</div></html>",
                "No Connection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int selectedTab = tabbedPane.getSelectedIndex();
        String[] tabNames = {"Matrix Multiplication", "Array Sorting", "Prime Numbers"};
        
        log("🚀 Starting " + tabNames[selectedTab] + " task...");
        
        // Create a simple thread to run the task
        Thread taskThread = new Thread(() -> {
            try {
                switch(selectedTab) {
                    case 0:
                        executeMatrixMultiplication();
                        break;
                    case 1:
                        executeArraySorting();
                        break;
                    case 2:
                        executePrimeFinding();
                        break;
                }
            } catch (Exception e) {
                log("❌ Error: " + e.getMessage());
            }
        });
        
        taskThread.start();
    }
    
    private void executeMatrixMultiplication() throws Exception {
        log("🔢 Parsing matrices...");
        
        // Parse matrix A
        String[] aRows = matrixAText.getText().split("\n");
        int aRowsCount = aRows.length;
        int aColsCount = aRows[0].trim().split("\\s+").length;
        
        CalculatorInterface.Matrix matrixA = new CalculatorInterface.Matrix(aRowsCount, aColsCount);
        for(int i = 0; i < aRowsCount; i++) {
            String[] values = aRows[i].trim().split("\\s+");
            for(int j = 0; j < aColsCount; j++) {
                matrixA.setValue(i, j, Integer.parseInt(values[j]));
            }
        }
        
        // Parse matrix B
        String[] bRows = matrixBText.getText().split("\n");
        int bRowsCount = bRows.length;
        int bColsCount = bRows[0].trim().split("\\s+").length;
        
        CalculatorInterface.Matrix matrixB = new CalculatorInterface.Matrix(bRowsCount, bColsCount);
        for(int i = 0; i < bRowsCount; i++) {
            String[] values = bRows[i].trim().split("\\s+");
            for(int j = 0; j < bColsCount; j++) {
                matrixB.setValue(i, j, Integer.parseInt(values[j]));
            }
        }
        
        // Check dimensions
        if(matrixA.getCols() != matrixB.getRows()) {
            throw new IllegalArgumentException("Matrix A columns (" + matrixA.getCols() + 
                    ") must equal Matrix B rows (" + matrixB.getRows() + ")");
        }
        
        CalculatorInterface.Task task = new CalculatorInterface.Task(
            CalculatorInterface.TaskType.MATRIX_MULTIPLICATION,
            new Object[]{matrixA, matrixB}
        );
        
        log("📤 Sending matrices to server...");
        CalculatorInterface.TaskResult result = server.executeTask(task);
        
        if(result.isSuccess()) {
            CalculatorInterface.Matrix resultMatrix = (CalculatorInterface.Matrix) result.getResult();
            
            // Display result in matrix result text area
            StringBuilder resultDisplay = new StringBuilder();
            resultDisplay.append("✅ Result Matrix (").append(resultMatrix.getRows())
                        .append("x").append(resultMatrix.getCols()).append("):\n\n");
            
            for(int i = 0; i < resultMatrix.getRows(); i++) {
                for(int j = 0; j < resultMatrix.getCols(); j++) {
                    resultDisplay.append(String.format("%8d", resultMatrix.getValue(i, j)));
                }
                resultDisplay.append("\n");
            }
            
            matrixResultText.setText(resultDisplay.toString());
            
            log("✅ Matrix multiplication completed successfully!");
            log("   📊 Result: " + resultMatrix.getRows() + "x" + resultMatrix.getCols());
            log("   ⏱️  Time: " + (System.currentTimeMillis() - result.getExecutionTime()) + "ms");
        } else {
            matrixResultText.setText("❌ Error: " + result.getErrorMessage());
            log("❌ Matrix multiplication failed: " + result.getErrorMessage());
        }
        log("──────────────────────────────────────────────────────");
    }
    
    private void executeArraySorting() throws Exception {
        // Parse input array
        String input = arrayInputText.getText().trim();
        if (input.isEmpty()) {
            throw new IllegalArgumentException("Array input cannot be empty");
        }
        
        String[] numberStrings = input.split("[\\s,\n]+");
        int size = numberStrings.length;
        int[] array = new int[size];
        
        log("🔢 Parsing array of " + size + " numbers...");
        for(int i = 0; i < size; i++) {
            try {
                array[i] = Integer.parseInt(numberStrings[i].trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number: " + numberStrings[i]);
            }
        }
        
        // Display original array
        StringBuilder original = new StringBuilder("📥 Original Array (" + size + " elements):\n");
        for (int i = 0; i < Math.min(size, 50); i++) {
            original.append(array[i]).append(" ");
            if ((i + 1) % 10 == 0) original.append("\n");
        }
        if (size > 50) original.append("\n... (showing first 50 of ").append(size).append(" elements)");
        log(original.toString());
        
        CalculatorInterface.Task task = new CalculatorInterface.Task(
            CalculatorInterface.TaskType.SORT_ARRAY,
            array
        );
        
        log("📤 Sending array to server for sorting...");
        CalculatorInterface.TaskResult result = server.executeTask(task);
        
        if(result.isSuccess()) {
            int[] sortedArray = (int[]) result.getResult();
            
            // Display sorted result in array result text area
            StringBuilder resultDisplay = new StringBuilder();
            resultDisplay.append("✅ Sorted Array (").append(sortedArray.length).append(" elements):\n\n");
            
            for(int i = 0; i < Math.min(sortedArray.length, 100); i++) {
                resultDisplay.append(String.format("%5d", sortedArray[i]));
                if((i + 1) % 10 == 0) resultDisplay.append("\n");
            }
            
            if(sortedArray.length > 100) {
                resultDisplay.append("\n... (showing first 100 of ").append(sortedArray.length).append(" elements)");
            }
            
            arrayResultText.setText(resultDisplay.toString());
            
            log("✅ Array sorting completed successfully!");
            log("   📊 Size: " + size + " elements");
            log("   ⏱️  Time: " + (System.currentTimeMillis() - result.getExecutionTime()) + "ms");
            
            // Verify it's sorted
            boolean isSorted = true;
            for(int i = 1; i < sortedArray.length; i++) {
                if(sortedArray[i] < sortedArray[i-1]) {
                    isSorted = false;
                    break;
                }
            }
            log("   ✓ Verified sorted: " + (isSorted ? "Yes" : "No"));
        } else {
            arrayResultText.setText("❌ Error: " + result.getErrorMessage());
            log("❌ Array sorting failed: " + result.getErrorMessage());
        }
        log("──────────────────────────────────────────────────────");
    }
    
    @SuppressWarnings("unchecked")
    private void executePrimeFinding() throws Exception {
        int start = (Integer) primeStart.getValue();
        int end = (Integer) primeEnd.getValue();
        
        if(start >= end) {
            throw new IllegalArgumentException("Start must be less than end");
        }
        
        log("🔍 Finding primes from " + start + " to " + end + "...");
        
        CalculatorInterface.Task task = new CalculatorInterface.Task(
            CalculatorInterface.TaskType.FIND_PRIMES,
            new int[]{start, end}
        );
        
        log("📤 Sending range to server...");
        CalculatorInterface.TaskResult result = server.executeTask(task);
        
        if(result.isSuccess()) {
            List<Integer> primes = (List<Integer>) result.getResult();
            
            // Display primes in prime result text area
            StringBuilder resultDisplay = new StringBuilder();
            resultDisplay.append("✅ Prime Numbers from ").append(start).append(" to ").append(end).append(":\n\n");
            resultDisplay.append("📊 Total primes found: ").append(primes.size()).append("\n\n");
            
            if(!primes.isEmpty()) {
                for(int i = 0; i < Math.min(primes.size(), 200); i++) {
                    resultDisplay.append(String.format("%6d", primes.get(i)));
                    if((i + 1) % 10 == 0) resultDisplay.append("\n");
                }
                
                if(primes.size() > 200) {
                    resultDisplay.append("\n... (showing first 200 of ").append(primes.size()).append(" primes)");
                }
            } else {
                resultDisplay.append("No prime numbers found in this range.");
            }
            
            primeResultText.setText(resultDisplay.toString());
            
            log("✅ Prime finding completed successfully!");
            log("   📊 Range: " + start + " to " + end);
            log("   📈 Found: " + primes.size() + " primes");
            log("   ⏱️  Time: " + (System.currentTimeMillis() - result.getExecutionTime()) + "ms");
        } else {
            primeResultText.setText("❌ Error: " + result.getErrorMessage());
            log("❌ Prime finding failed: " + result.getErrorMessage());
        }
        log("──────────────────────────────────────────────────────");
    }
    
    private void log(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String timestamp = sdf.format(new Date());
        
        SwingUtilities.invokeLater(() -> {
            resultArea.append("[" + timestamp + "] " + message + "\n");
            resultArea.setCaretPosition(resultArea.getDocument().getLength());
        });
    }
    
    public void show() {
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        try {
            // Set Windows look and feel if available
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Set font size globally
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("TextArea.font", new Font("Consolas", Font.PLAIN, 13));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            CalculatorClient client = new CalculatorClient();
            client.show();
        });
    }
}