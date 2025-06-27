// ImageRecoveryTool.java - Enhanced with bulk selection and auto-matching
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class ImageRecoveryTool extends JFrame {
    private ModuleManager moduleManager;
    private JTextArea logArea;
    private JButton scanButton;
    private JButton fixButton;
    private JButton closeButton;

    // New components for enhanced functionality
    private JTable availableImagesTable;
    private JTable missingCardsTable;
    private DefaultTableModel availableImagesModel;
    private DefaultTableModel missingCardsModel;
    private JComboBox<String> moduleComboBox;
    private JTabbedPane tabbedPane;

    private List<Card> cardsWithMissingImages;
    private List<File> availableImages;
    private List<Card> missingImageCards;

    public ImageRecoveryTool() {
        this.moduleManager = new ModuleManager();
        this.cardsWithMissingImages = new ArrayList<>();
        this.availableImages = new ArrayList<>();
        this.missingImageCards = new ArrayList<>();

        setTitle("Enhanced Image Recovery Tool");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        loadModules();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 248, 255));

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("Enhanced Image Recovery Tool");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);
        titlePanel.add(titleLabel);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();

        // Tab 1: Original simple interface
        JPanel simpleTab = createSimpleInterface();
        tabbedPane.addTab("Simple Recovery", simpleTab);

        // Tab 2: Enhanced bulk interface
        JPanel bulkTab = createBulkInterface();
        tabbedPane.addTab("Bulk Recovery", bulkTab);

        add(titlePanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createSimpleInterface() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 248, 255));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 248, 255));

        scanButton = new JButton("Scan for Missing Images");
        scanButton.setBackground(new Color(70, 130, 180));
        scanButton.setForeground(Color.BLACK);
        scanButton.addActionListener(e -> scanForMissingImages());

        fixButton = new JButton("Fix Selected Image");
        fixButton.setBackground(new Color(46, 139, 87));
        fixButton.setForeground(Color.BLACK);
        fixButton.setEnabled(false);
        fixButton.addActionListener(e -> fixSelectedImage());

        closeButton = new JButton("Close");
        closeButton.setBackground(new Color(128, 128, 128));
        closeButton.setForeground(Color.BLACK);
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(scanButton);
        buttonPanel.add(fixButton);
        buttonPanel.add(closeButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBulkInterface() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 248, 255));

        // Top panel with module selection and bulk actions
        JPanel topPanel = new JPanel(new BorderLayout());

        // Module selection
        JPanel modulePanel = new JPanel(new FlowLayout());
        modulePanel.setBackground(new Color(240, 248, 255));
        modulePanel.add(new JLabel("Select Module:"));
        moduleComboBox = new JComboBox<>();
        moduleComboBox.addActionListener(e -> loadImagesForModule());
        modulePanel.add(moduleComboBox);

        // Bulk action buttons
        JPanel bulkPanel = new JPanel(new FlowLayout());
        bulkPanel.setBackground(new Color(240, 248, 255));

        JButton autoMatchButton = new JButton("Auto-Match by Date Order");
        autoMatchButton.setBackground(new Color(46, 139, 87));
        autoMatchButton.setForeground(Color.BLACK);
        autoMatchButton.addActionListener(e -> autoMatchImagesByOrder());

        JButton bulkMatchButton = new JButton("Bulk Match Selected");
        bulkMatchButton.setBackground(new Color(70, 130, 180));
        bulkMatchButton.setForeground(Color.BLACK);
        bulkMatchButton.addActionListener(e -> bulkMatchSelected());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadImagesForModule());

        bulkPanel.add(autoMatchButton);
        bulkPanel.add(bulkMatchButton);
        bulkPanel.add(refreshButton);

        topPanel.add(modulePanel, BorderLayout.WEST);
        topPanel.add(bulkPanel, BorderLayout.EAST);

        // Main content area
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Available images panel
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.setBorder(BorderFactory.createTitledBorder("Available Images (sorted by date)"));

        String[] availableColumns = {"Select", "Image Name", "Date Modified"};
        availableImagesModel = new DefaultTableModel(availableColumns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only checkbox is editable
            }
        };

        availableImagesTable = new JTable(availableImagesModel);
        availableImagesTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        availableImagesTable.setRowHeight(30);

        // Set column widths
        availableImagesTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        availableImagesTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        availableImagesTable.getColumnModel().getColumn(2).setPreferredWidth(150);

        JScrollPane availableScrollPane = new JScrollPane(availableImagesTable);
        availablePanel.add(availableScrollPane, BorderLayout.CENTER);

        // Missing image cards panel
        JPanel missingPanel = new JPanel(new BorderLayout());
        missingPanel.setBorder(BorderFactory.createTitledBorder("Cards Missing Images"));

        String[] missingColumns = {"Select", "Question", "Topic"};
        missingCardsModel = new DefaultTableModel(missingColumns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only checkbox is editable
            }
        };

        missingCardsTable = new JTable(missingCardsModel);
        missingCardsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        missingCardsTable.setRowHeight(30);

        JScrollPane missingScrollPane = new JScrollPane(missingCardsTable);
        missingPanel.add(missingScrollPane, BorderLayout.CENTER);

        mainPanel.add(availablePanel);
        mainPanel.add(missingPanel);

        // Bottom panel with selection buttons
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(new Color(240, 248, 255));

        JButton selectAllImagesButton = new JButton("Select All Images");
        selectAllImagesButton.addActionListener(e -> selectAllImages(true));

        JButton deselectAllImagesButton = new JButton("Deselect All Images");
        deselectAllImagesButton.addActionListener(e -> selectAllImages(false));

        JButton selectAllCardsButton = new JButton("Select All Cards");
        selectAllCardsButton.addActionListener(e -> selectAllCards(true));

        JButton deselectAllCardsButton = new JButton("Deselect All Cards");
        deselectAllCardsButton.addActionListener(e -> selectAllCards(false));

        bottomPanel.add(selectAllImagesButton);
        bottomPanel.add(deselectAllImagesButton);
        bottomPanel.add(selectAllCardsButton);
        bottomPanel.add(deselectAllCardsButton);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(mainPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadModules() {
        moduleComboBox.removeAllItems();
        for (Module module : moduleManager.getModules()) {
            moduleComboBox.addItem(module.getName());
        }
    }

    private void loadImagesForModule() {
        String selectedModuleName = (String) moduleComboBox.getSelectedItem();
        if (selectedModuleName == null) return;

        Module selectedModule = moduleManager.getModuleByName(selectedModuleName);
        if (selectedModule == null) return;

        // Clear existing data
        availableImagesModel.setRowCount(0);
        missingCardsModel.setRowCount(0);
        availableImages.clear();
        missingImageCards.clear();

        // Debug: Print what we're looking for
        System.out.println("=== DEBUG: Loading images for module: " + selectedModuleName + " ===");

        // Load available images from module directory
        File moduleImagesDir = FileUtils.getModuleImagesDir(selectedModuleName);
        System.out.println("Looking in directory: " + moduleImagesDir.getAbsolutePath());
        System.out.println("Directory exists: " + moduleImagesDir.exists());

        if (moduleImagesDir.exists()) {
            File[] allFiles = moduleImagesDir.listFiles();
            System.out.println("Total files in directory: " + (allFiles != null ? allFiles.length : 0));

            if (allFiles != null) {
                for (File f : allFiles) {
                    System.out.println("Found file: " + f.getName() + " (isFile: " + f.isFile() + ")");
                }
            }

            File[] imageFiles = moduleImagesDir.listFiles((dir, name) -> {
                String lowerName = name.toLowerCase();
                boolean isImage = lowerName.endsWith(".jpg") ||
                        lowerName.endsWith(".jpeg") ||
                        lowerName.endsWith(".png") ||
                        lowerName.endsWith(".gif");
                System.out.println("Checking file: " + name + " -> isImage: " + isImage);
                return isImage;
            });

            if (imageFiles != null && imageFiles.length > 0) {
                System.out.println("Found " + imageFiles.length + " image files");

                // Sort by modification time (oldest first)
                Arrays.sort(imageFiles, Comparator.comparingLong(File::lastModified));

                for (File imageFile : imageFiles) {
                    availableImages.add(imageFile);

                    // Format date
                    String dateStr = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm")
                            .format(new Date(imageFile.lastModified()));

                    System.out.println("Adding to table: " + imageFile.getName() + " (" + dateStr + ")");

                    availableImagesModel.addRow(new Object[]{
                            Boolean.FALSE, // checkbox
                            imageFile.getName(),
                            dateStr
                    });
                }
            } else {
                System.out.println("No image files found!");
            }
        } else {
            System.out.println("Module images directory does not exist!");
        }

        // Load cards missing images (existing code)
        System.out.println("=== Checking for missing image cards ===");
        for (Card card : selectedModule.getCards()) {
            if (card.getAnswerType() == Card.AnswerType.IMAGE) {
                File imageFile = card.getImageAnswerFile();
                if (imageFile == null || !imageFile.exists()) {
                    missingImageCards.add(card);

                    String question = card.getQuestion();
                    if (question.length() > 50) {
                        question = question.substring(0, 47) + "...";
                    }

                    System.out.println("Missing image for card: " + question);

                    missingCardsModel.addRow(new Object[]{
                            Boolean.FALSE, // checkbox
                            question,
                            card.getTopic()
                    });
                }
            }
        }

        System.out.println("=== Summary ===");
        System.out.println("Available images: " + availableImages.size());
        System.out.println("Missing image cards: " + missingImageCards.size());

        // Update UI
        repaint();
    }

    private void autoMatchImagesByOrder() {
        if (availableImages.isEmpty() || missingImageCards.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No images or cards available for matching.",
                    "Auto-Match", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int matchCount = Math.min(availableImages.size(), missingImageCards.size());

        int result = JOptionPane.showConfirmDialog(this,
                String.format("This will match the first %d images (by date) with the first %d cards.\n" +
                        "Images are sorted by modification date (oldest first).\n" +
                        "Continue?", matchCount, matchCount),
                "Auto-Match Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            int matched = 0;
            StringBuilder report = new StringBuilder("Auto-Match Report:\n");

            for (int i = 0; i < matchCount; i++) {
                File imageFile = availableImages.get(i);
                Card card = missingImageCards.get(i);

                try {
                    // Set the image file for the card
                    card.setImageFile(imageFile);
                    matched++;

                    report.append(String.format("%d. %s -> %s\n",
                            i + 1,
                            imageFile.getName(),
                            card.getQuestion().substring(0, Math.min(30, card.getQuestion().length())) + "..."));

                } catch (Exception e) {
                    report.append(String.format("%d. FAILED: %s -> %s (Error: %s)\n",
                            i + 1,
                            imageFile.getName(),
                            card.getQuestion().substring(0, Math.min(30, card.getQuestion().length())) + "...",
                            e.getMessage()));
                }
            }

            // Save changes
            moduleManager.saveModules();

            // Show detailed report
            JTextArea reportArea = new JTextArea(report.toString());
            reportArea.setEditable(false);
            reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(reportArea);
            scrollPane.setPreferredSize(new Dimension(500, 300));

            JOptionPane.showMessageDialog(this, scrollPane,
                    String.format("Auto-Match Complete - %d matches", matched),
                    JOptionPane.INFORMATION_MESSAGE);

            // Refresh the display
            loadImagesForModule();
        }
    }

    private void bulkMatchSelected() {
        // Get selected images
        List<Integer> selectedImageIndices = new ArrayList<>();
        for (int i = 0; i < availableImagesModel.getRowCount(); i++) {
            if ((Boolean) availableImagesModel.getValueAt(i, 0)) {
                selectedImageIndices.add(i);
            }
        }

        // Get selected cards
        List<Integer> selectedCardIndices = new ArrayList<>();
        for (int i = 0; i < missingCardsModel.getRowCount(); i++) {
            if ((Boolean) missingCardsModel.getValueAt(i, 0)) {
                selectedCardIndices.add(i);
            }
        }

        if (selectedImageIndices.isEmpty() || selectedCardIndices.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select both images and cards to match.",
                    "Bulk Match", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedImageIndices.size() != selectedCardIndices.size()) {
            JOptionPane.showMessageDialog(this,
                    "Number of selected images (" + selectedImageIndices.size() +
                            ") must equal number of selected cards (" + selectedCardIndices.size() + ").",
                    "Bulk Match", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int matched = 0;
        StringBuilder report = new StringBuilder("Bulk Match Report:\n");

        for (int i = 0; i < selectedImageIndices.size(); i++) {
            File imageFile = availableImages.get(selectedImageIndices.get(i));
            Card card = missingImageCards.get(selectedCardIndices.get(i));

            try {
                card.setImageFile(imageFile);
                matched++;

                report.append(String.format("%d. %s -> %s\n",
                        i + 1,
                        imageFile.getName(),
                        card.getQuestion().substring(0, Math.min(30, card.getQuestion().length())) + "..."));

            } catch (Exception e) {
                report.append(String.format("%d. FAILED: %s -> %s (Error: %s)\n",
                        i + 1,
                        imageFile.getName(),
                        card.getQuestion().substring(0, Math.min(30, card.getQuestion().length())) + "...",
                        e.getMessage()));
            }
        }

        // Save changes
        moduleManager.saveModules();

        // Show detailed report
        JTextArea reportArea = new JTextArea(report.toString());
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(this, scrollPane,
                String.format("Bulk Match Complete - %d matches", matched),
                JOptionPane.INFORMATION_MESSAGE);

        // Refresh the display
        loadImagesForModule();
    }

    private void selectAllImages(boolean select) {
        for (int i = 0; i < availableImagesModel.getRowCount(); i++) {
            availableImagesModel.setValueAt(select, i, 0);
        }
    }

    private void selectAllCards(boolean select) {
        for (int i = 0; i < missingCardsModel.getRowCount(); i++) {
            missingCardsModel.setValueAt(select, i, 0);
        }
    }

    // Original methods for the simple interface
    private void scanForMissingImages() {
        logArea.setText("Scanning modules for missing images...\n");
        cardsWithMissingImages.clear();

        List<Module> modules = moduleManager.getModules();
        int moduleCount = 0;
        int cardCount = 0;
        int missingCount = 0;

        for (Module module : modules) {
            moduleCount++;
            for (Card card : module.getCards()) {
                cardCount++;

                if (card.getAnswerType() == Card.AnswerType.IMAGE) {
                    File imageFile = card.getImageAnswerFile();
                    if (imageFile == null || !imageFile.exists()) {
                        missingCount++;
                        cardsWithMissingImages.add(card);
                        logArea.append(String.format("Module: %s - Card Question: \"%s\" - Missing Image\n",
                                module.getName(), card.getQuestion()));
                    }
                }
            }
        }

        logArea.append(String.format("\nScan Complete.\nModules: %d, Cards: %d, Missing Images: %d\n",
                moduleCount, cardCount, missingCount));

        if (!cardsWithMissingImages.isEmpty()) {
            fixButton.setEnabled(true);
            logArea.append("\nClick 'Fix Selected Image' to repair the first missing image.\n");
        } else {
            fixButton.setEnabled(false);
            logArea.append("\nNo missing images found. All image cards have valid references.\n");
        }
    }

    private void fixSelectedImage() {
        if (cardsWithMissingImages.isEmpty()) {
            logArea.append("No missing images to fix.\n");
            fixButton.setEnabled(false);
            return;
        }

        Card card = cardsWithMissingImages.get(0);

        logArea.append("\nAttempting to fix image for card: \"" + card.getQuestion() + "\"\n");

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Replacement Image");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif", "bmp"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                // Get the module this card belongs to
                String moduleName = card.getModuleName();
                if (moduleName == null || moduleName.isEmpty()) {
                    // If no module name is stored in the card, try to find it
                    for (Module module : moduleManager.getModules()) {
                        if (module.getCards().contains(card)) {
                            moduleName = module.getName();
                            card.setModuleName(moduleName);
                            break;
                        }
                    }

                    if (moduleName == null || moduleName.isEmpty()) {
                        logArea.append("Error: Could not determine module for this card.\n");
                        return;
                    }
                }

                // Copy the image to the module's directory and update the card
                File copiedImage = FileUtils.copyImageToModuleDir(selectedFile, moduleName);
                if (copiedImage != null) {
                    // Update the card with the new image path
                    card.setImageFile(selectedFile);

                    // Explicitly set the image answer to load the image
                    card.loadImage();

                    // Save all modules
                    moduleManager.saveModules();

                    logArea.append("Image fixed successfully: " + copiedImage.getName() + "\n");

                    // Remove the fixed card from the list
                    cardsWithMissingImages.remove(0);

                    if (cardsWithMissingImages.isEmpty()) {
                        fixButton.setEnabled(false);
                        logArea.append("All missing images have been fixed!\n");
                    } else {
                        logArea.append("\nNext card to fix: \"" + cardsWithMissingImages.get(0).getQuestion() + "\"\n");
                    }
                } else {
                    logArea.append("Error: Failed to copy image to module directory.\n");
                }
            } catch (Exception ex) {
                logArea.append("Error fixing image: " + ex.getMessage() + "\n");
                ex.printStackTrace();
            }
        } else {
            logArea.append("Image selection cancelled.\n");
        }
    }
}