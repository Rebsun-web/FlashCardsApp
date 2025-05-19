// MainMenuUI.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MainMenuUI extends JFrame {
    private ModuleManager moduleManager;
    private JPanel modulesPanel;
    private JScrollPane scrollPane;

    public MainMenuUI(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;

        setTitle("Flashcard App");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        // Main layout
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 248, 255)); // Alice blue background

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180)); // Steel blue
        JLabel titleLabel = new JLabel("Flashcard Study App");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        // Modules panel
        modulesPanel = new JPanel();
        modulesPanel.setLayout(new BoxLayout(modulesPanel, BoxLayout.Y_AXIS));
        modulesPanel.setBackground(new Color(240, 248, 255));

        scrollPane = new JScrollPane(modulesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton newModuleButton = new JButton("Create New Module");
        newModuleButton.setFont(new Font("Arial", Font.BOLD, 16));
        newModuleButton.setBackground(new Color(46, 139, 87)); // Sea green
        newModuleButton.setForeground(Color.BLACK);
        newModuleButton.setFocusPainted(false);
        newModuleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewModule();
            }
        });

        buttonPanel.add(newModuleButton);

        JButton recoverButton = new JButton("Recover Missing Images");
        recoverButton.setFont(new Font("Arial", Font.BOLD, 16));
        recoverButton.setBackground(new Color(205, 92, 92)); // Indian red
        recoverButton.setForeground(Color.BLACK);
        recoverButton.setFocusPainted(false);
        recoverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openImageRecoveryTool();
            }
        });

        buttonPanel.add(recoverButton);

        JButton recoverModuleButton = new JButton("Recover Lost Module");
        recoverModuleButton.setFont(new Font("Arial", Font.BOLD, 16));
        recoverModuleButton.setBackground(new Color(70, 130, 180)); // Steel blue
        recoverModuleButton.setForeground(Color.BLACK);
        recoverModuleButton.setFocusPainted(false);
        recoverModuleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openModuleRecoveryTool();
            }
        });

        buttonPanel.add(recoverModuleButton);

        // Add all components to frame
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load modules
        refreshModulesList();
    }

    private void openImageRecoveryTool() {
        ImageRecoveryTool recoveryTool = new ImageRecoveryTool();
        recoveryTool.setVisible(true);
    }

    private void openModuleRecoveryTool() {
        ModuleRecoveryTool recoveryTool = new ModuleRecoveryTool();
        recoveryTool.setVisible(true);
    }

    private void refreshModulesList() {
        modulesPanel.removeAll();

        if (moduleManager.getModules().isEmpty()) {
            JLabel emptyLabel = new JLabel("No modules found. Create a new module to get started!");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            modulesPanel.add(Box.createVerticalStrut(30));
            modulesPanel.add(emptyLabel);
        } else {
            modulesPanel.add(Box.createVerticalStrut(10));

            for (Module module : moduleManager.getModules()) {
                JPanel moduleItemPanel = createModulePanel(module);
                modulesPanel.add(moduleItemPanel);
                modulesPanel.add(Box.createVerticalStrut(10));
            }
        }

        modulesPanel.revalidate();
        modulesPanel.repaint();
    }

    private JPanel createModulePanel(Module module) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 0));
        panel.setBackground(new Color(230, 230, 250)); // Lavender
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel nameLabel = new JLabel(module.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel countLabel = new JLabel(module.getCardCount() + " cards");
        countLabel.setFont(new Font("Arial", Font.ITALIC, 14));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(230, 230, 250));
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(countLabel);

        // Create a panel for all the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 0, 5)); // Changed to 3 rows for 3 buttons
        buttonPanel.setBackground(new Color(230, 230, 250));

        JButton viewButton = new JButton("View & Edit");
        viewButton.setBackground(new Color(100, 149, 237)); // Cornflower blue
        viewButton.setForeground(Color.BLACK);
        viewButton.setFocusPainted(false);
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openModuleView(module);
            }
        });

        JButton studyButton = new JButton("Study");
        studyButton.setBackground(new Color(60, 179, 113)); // Medium sea green
        studyButton.setForeground(Color.BLACK);
        studyButton.setFocusPainted(false);
        studyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (module.getCardCount() > 0) {
                    openStudyView(module);
                } else {
                    JOptionPane.showMessageDialog(MainMenuUI.this,
                            "This module has no cards. Add some cards before studying.",
                            "Empty Module", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // Add delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(220, 20, 60)); // Crimson
        deleteButton.setForeground(Color.BLACK);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmDeleteModule(module);
            }
        });

        buttonPanel.add(viewButton);
        buttonPanel.add(studyButton);
        buttonPanel.add(deleteButton); // Add the delete button to the panel

        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    // 2. Add a new method to handle the delete confirmation and action
    private void confirmDeleteModule(Module module) {
        // Show a confirmation dialog
        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the module '" + module.getName() + "'?\n" +
                        "This will permanently remove all cards in this module.",
                "Confirm Module Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            // User confirmed, proceed with deletion
            // First, get the module's directory path for later cleanup
            String moduleName = module.getName();
            File moduleImagesDir = FileUtils.getModuleImagesDir(moduleName);

            // Remove the module
            moduleManager.removeModuleByName(moduleName);

            // Clean up image directory (optional, uncomment if you want to delete images too)
            // This is commented out because you might want to keep images as a backup
            // deleteDirectoryContents(moduleImagesDir);

            // Refresh the modules list
            refreshModulesList();

            // Show confirmation
            JOptionPane.showMessageDialog(
                    this,
                    "Module '" + moduleName + "' has been deleted.",
                    "Module Deleted",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    // 3. Add helper method to clean up module image directory (optional)
    private void deleteDirectoryContents(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.delete()) {
                        System.out.println("Failed to delete file: " + file.getAbsolutePath());
                    }
                }
            }
            // Optionally delete the directory itself if you want
            // directory.delete();
        }
    }

    private void createNewModule() {
        String moduleName = JOptionPane.showInputDialog(this,
                "Enter the name for the new module:",
                "Create New Module", JOptionPane.PLAIN_MESSAGE);

        if (moduleName != null && !moduleName.trim().isEmpty()) {
            // Check if module with this name already exists
            if (moduleManager.getModuleByName(moduleName) != null) {
                JOptionPane.showMessageDialog(this,
                        "A module with this name already exists.",
                        "Duplicate Name", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Module newModule = new Module(moduleName);
            moduleManager.addModule(newModule);
            refreshModulesList();

            // Open the new module for editing
            openModuleView(newModule);
        }
    }

    private void openModuleView(Module module) {
        ModuleViewUI moduleView = new ModuleViewUI(module, moduleManager, this);
        moduleView.setVisible(true);
        this.setVisible(false);
    }

    private void openStudyView(Module module) {
        StudyUI studyView = new StudyUI(module, this);
        studyView.setVisible(true);
        this.setVisible(false);
    }
}