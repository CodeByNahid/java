package distributedcalculator;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CalculatorClient extends DistributedCalculator {
    
    private Map<String, CalculatorService> servers = new HashMap<>();
    private JFrame frame;
    private JComboBox<String> serverComboBox;
    private JTextArea resultArea;
    private JTabbedPane tabbedPane;
    
    // Matrix components
    private JSpinner matrixARows, matrixACols, matrixBRows, matrixBCols;
    private JTextArea matrixAText, matrixBText;
    
    // Array components
    private JSpinner arraySizeSpinner;
    
    // Prime components
    private JSpinner primeStart, primeEnd;
    
    public CalculatorClient() {
        createGUI();
    }
    
    private void createGUI() {
        frame = new JFrame("Distributed Calculator Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());
        
        // Server panel
        JPanel serverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        serverComboBox = new JComboBox<>();
        JButton refreshButton = new JButton("Refresh Servers");
        refreshButton.addActionListener(e -> discoverServers());
        
        serverPanel.add(new JLabel("Available Servers:"));
        serverPanel.add(serverComboBox);
        serverPanel.add(refreshButton);
        
        // Tabbed pane for different operations
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Matrix Multiplication", createMatrixPanel());
        tabbedPane.addTab("Array Sorting", createArrayPanel());
        tabbedPane.addTab("Prime Numbers", createPrimePanel());
        
        // Result area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setBorder(BorderFactory.createTitledBorder("Results"));
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton executeButton = new JButton("Execute Task");
        executeButton.addActionListener(e -> executeTask());
        
        JButton clearButton = new JButton("Clear Results");
        clearButton.addActionListener(e -> resultArea.setText(""));
        
        controlPanel.add(executeButton);
        controlPanel.add(clearButton);
        
        // Add components to frame
        frame.add(serverPanel, BorderLayout.NORTH);
        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.add(resultScroll, BorderLayout.SOUTH);
        frame.add(controlPanel, BorderLayout.SOUTH);
        
        // Auto-discover servers on startup
        discoverServers();
    }
    
    private JPanel createMatrixPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Matrix A controls
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Matrix A Rows:"), gbc);
        
        gbc.gridx = 1;
        matrixARows = new JSpinner(new SpinnerNumberModel(3, 1, 50, 1));
        panel.add(matrixARows, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Columns:"), gbc);
        
        gbc.gridx = 3;
        matrixACols = new JSpinner(new SpinnerNumberModel(3, 1, 50, 1));
        panel.add(matrixACols, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Matrix B Rows:"), gbc);
        
        gbc.gridx = 1;
        matrixBRows = new JSpinner(new SpinnerNumberModel(3, 1, 50, 1));
        panel.add(matrixBRows, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Columns:"), gbc);
        
        gbc.gridx = 3;
        matrixBCols = new JSpinner(new SpinnerNumberModel(3, 1, 50, 1));
        panel.add(matrixBCols, gbc);
        
        // Matrix text areas
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        matrixAText = new JTextArea(8, 15);
        matrixAText.setText("1 2 3\n4 5 6\n7 8 9");
        JScrollPane matrixAScroll = new JScrollPane(matrixAText);
        matrixAScroll.setBorder(BorderFactory.createTitledBorder("Matrix A"));
        panel.add(matrixAScroll, gbc);
        
        gbc.gridx = 2; gbc.gridwidth = 2;
        matrixBText = new JTextArea(8, 15);
        matrixBText.setText("9 8 7\n6 5 4\n3 2 1");
        JScrollPane matrixBScroll = new JScrollPane(matrixBText);
        matrixBScroll.setBorder(BorderFactory.createTitledBorder("Matrix B"));
        panel.add(matrixBScroll, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        JPanel buttonPanel = new JPanel();
        JButton generateButton = new JButton("Generate Random Matrices");
        generateButton.addActionListener(e -> generateRandomMatrices());
        buttonPanel.add(generateButton);
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private JPanel createArrayPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 5, 20, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Array Size:"), gbc);
        
        gbc.gridx = 1;
        arraySizeSpinner = new JSpinner(new SpinnerNumberModel(10000, 100, 1000000, 1000));
        panel.add(arraySizeSpinner, gbc);
        
        gbc.gridx = 2;
        JLabel infoLabel = new JLabel("(Random numbers 0-9999)");
        infoLabel.setForeground(Color.GRAY);
        panel.add(infoLabel, gbc);
        
        return panel;
    }
    
    private JPanel createPrimePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 5, 20, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Start:"), gbc);
        
        gbc.gridx = 1;
        primeStart = new JSpinner(new SpinnerNumberModel(1, 1, 10000000, 1));
        panel.add(primeStart, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("End:"), gbc);
        
        gbc.gridx = 3;
        primeEnd = new JSpinner(new SpinnerNumberModel(10000, 10, 10000000, 1000));
        panel.add(primeEnd, gbc);
        
        return panel;
    }
    
    private void discoverServers() {
        servers.clear();
        serverComboBox.removeAllItems();
        
        // Try multiple ports
        int[] ports = {1099, 1100, 1101, 1102};
        
        for(int port : ports) {
            try {
                Registry registry = LocateRegistry.getRegistry("localhost", port);
                CalculatorService service = (CalculatorService) registry.lookup("CalculatorService");
                
                if(service.isAvailable()) {
                    String serverInfo = "localhost:" + port + " - " + service.getServerInfo();
                    servers.put("localhost:" + port, service);
                    serverComboBox.addItem(serverInfo);
                    System.out.println("Found server at localhost:" + port);
                }
            } catch (Exception e) {
                // Server not available at this port
            }
        }
        
        if(serverComboBox.getItemCount() == 0) {
            serverComboBox.addItem("No servers found - start a server first!");
        }
    }
    
    private void generateRandomMatrices() {
        int aRows = (int) matrixARows.getValue();
        int aCols = (int) matrixACols.getValue();
        int bRows = (int) matrixBRows.getValue();
        int bCols = (int) matrixBCols.getValue();
        
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
    }
    
    private void executeTask() {
        String selectedItem = (String) serverComboBox.getSelectedItem();
        if(selectedItem == null || selectedItem.contains("No servers")) {
            JOptionPane.showMessageDialog(frame, "No server selected or available!");
            return;
        }
        
        // Extract server address from combo box item
        String serverAddress = selectedItem.split(" ")[0];
        
        int selectedTab = tabbedPane.getSelectedIndex();
        
        new Thread(() -> {
            try {
                switch(selectedTab) {
                    case 0: // Matrix multiplication
                        executeMatrixMultiplication(serverAddress);
                        break;
                    case 1: // Array sorting
                        executeArraySorting(serverAddress);
                        break;
                    case 2: // Prime finding
                        executePrimeFinding(serverAddress);
                        break;
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    resultArea.append("Error: " + e.getMessage() + "\n");
                    JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
                });
            }
        }).start();
    }
    
    private void executeMatrixMultiplication(String serverAddress) throws Exception {
        // Parse matrix A
        String[] aRows = matrixAText.getText().split("\n");
        int aRowsCount = aRows.length;
        int aColsCount = aRows[0].trim().split("\\s+").length;
        
        Matrix matrixA = new Matrix(aRowsCount, aColsCount);
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
        
        Matrix matrixB = new Matrix(bRowsCount, bColsCount);
        for(int i = 0; i < bRowsCount; i++) {
            String[] values = bRows[i].trim().split("\\s+");
            for(int j = 0; j < bColsCount; j++) {
                matrixB.setValue(i, j, Integer.parseInt(values[j]));
            }
        }
        
        // Check dimensions
        if(matrixA.getCols() != matrixB.getRows()) {
            throw new IllegalArgumentException("Matrix A columns must equal Matrix B rows");
        }
        
        Task task = new Task(TaskType.MATRIX_MULTIPLICATION, 
                           new Object[]{matrixA, matrixB});
        
        SwingUtilities.invokeLater(() -> {
            resultArea.append("Starting matrix multiplication...\n");
        });
        
        TaskResult result = servers.get(serverAddress).executeTask(task);
        
        if(result.isSuccess()) {
            Matrix resultMatrix = (Matrix) result.getResult();
            SwingUtilities.invokeLater(() -> {
                resultArea.append("Matrix multiplication completed successfully!\n");
                resultArea.append("Result matrix (" + resultMatrix.getRows() + "x" + 
                                resultMatrix.getCols() + "):\n");
                resultArea.append(resultMatrix.toString() + "\n---\n");
            });
        } else {
            SwingUtilities.invokeLater(() -> {
                resultArea.append("Matrix multiplication failed: " + 
                                result.getErrorMessage() + "\n---\n");
            });
        }
    }
    
    private void executeArraySorting(String serverAddress) throws Exception {
        int size = (int) arraySizeSpinner.getValue();
        int[] array = new int[size];
        
        for(int i = 0; i < size; i++) {
            array[i] = (int)(Math.random() * 10000);
        }
        
        Task task = new Task(TaskType.SORT_ARRAY, array);
        
        SwingUtilities.invokeLater(() -> {
            resultArea.append("Starting array sorting (" + size + " elements)...\n");
        });
        
        TaskResult result = servers.get(serverAddress).executeTask(task);
        
        if(result.isSuccess()) {
            int[] sortedArray = (int[]) result.getResult();
            SwingUtilities.invokeLater(() -> {
                resultArea.append("Array sorting completed successfully!\n");
                resultArea.append("First 20 elements: ");
                for(int i = 0; i < Math.min(20, sortedArray.length); i++) {
                    resultArea.append(sortedArray[i] + " ");
                }
                resultArea.append("\n---\n");
            });
        } else {
            SwingUtilities.invokeLater(() -> {
                resultArea.append("Array sorting failed: " + 
                                result.getErrorMessage() + "\n---\n");
            });
        }
    }
    
    private void executePrimeFinding(String serverAddress) throws Exception {
        int start = (int) primeStart.getValue();
        int end = (int) primeEnd.getValue();
        
        if(start >= end) {
            throw new IllegalArgumentException("Start must be less than end");
        }
        
        Task task = new Task(TaskType.FIND_PRIMES, new int[]{start, end});
        
        SwingUtilities.invokeLater(() -> {
            resultArea.append("Finding primes from " + start + " to " + end + "...\n");
        });
        
        TaskResult result = servers.get(serverAddress).executeTask(task);
        
        if(result.isSuccess()) {
            @SuppressWarnings("unchecked")
            List<Integer> primes = (List<Integer>) result.getResult();
            SwingUtilities.invokeLater(() -> {
                resultArea.append("Prime finding completed successfully!\n");
                resultArea.append("Found " + primes.size() + " prime numbers\n");
                resultArea.append("First 20 primes: ");
                for(int i = 0; i < Math.min(20, primes.size()); i++) {
                    resultArea.append(primes.get(i) + " ");
                }
                resultArea.append("\n---\n");
            });
        } else {
            SwingUtilities.invokeLater(() -> {
                resultArea.append("Prime finding failed: " + 
                                result.getErrorMessage() + "\n---\n");
            });
        }
    }
    
    public void show() {
        frame.setVisible(true);
    }
    
    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalculatorClient client = new CalculatorClient();
            client.show();
        });
    }
}