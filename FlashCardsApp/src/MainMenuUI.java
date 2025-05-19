// MainMenuUI.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 0, 5));
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

        buttonPanel.add(viewButton);
        buttonPanel.add(studyButton);

        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
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