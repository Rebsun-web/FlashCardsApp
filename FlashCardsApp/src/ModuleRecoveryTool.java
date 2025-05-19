// ModuleRecoveryTool.java
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class ModuleRecoveryTool extends JFrame {
    private ModuleManager moduleManager;
    private JTextArea logArea;

    public ModuleRecoveryTool() {
        this.moduleManager = new ModuleManager();

        setTitle("Module Recovery Tool");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 248, 255));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("Module Recovery Tool");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);
        titlePanel.add(titleLabel);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton recoverButton = new JButton("Recover Module File");
        recoverButton.setBackground(new Color(46, 139, 87));
        recoverButton.setForeground(Color.BLACK);
        recoverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recoverModuleFile();
            }
        });

        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(128, 128, 128));
        closeButton.setForeground(Color.BLACK);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonPanel.add(recoverButton);
        buttonPanel.add(closeButton);

        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        logArea.setText("This tool can recover a flashcard module from a .ser file.\n" +
                "Click 'Recover Module File' to select a module file to restore.\n");
    }

    private void recoverModuleFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Module File to Recover");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Module files (*.ser)", "ser"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            logArea.append("\nAttempting to recover module from: " + selectedFile.getName() + "\n");

            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(selectedFile))) {
                Module recoveredModule = (Module) ois.readObject();

                // Check if a module with the same name already exists
                if (moduleManager.getModuleByName(recoveredModule.getName()) != null) {
                    // Ask user if they want to replace the existing module
                    int overwriteResult = JOptionPane.showConfirmDialog(this,
                            "A module with the name '" + recoveredModule.getName() + "' already exists. " +
                                    "Do you want to replace it?",
                            "Module Already Exists", JOptionPane.YES_NO_OPTION);

                    if (overwriteResult == JOptionPane.YES_OPTION) {
                        // Remove existing module
                        moduleManager.removeModuleByName(recoveredModule.getName());
                    } else {
                        // Ask for a new name
                        String newName = JOptionPane.showInputDialog(this,
                                "Enter a new name for the recovered module:",
                                "Rename Module", JOptionPane.PLAIN_MESSAGE);

                        if (newName != null && !newName.trim().isEmpty()) {
                            recoveredModule.setName(newName);
                        } else {
                            logArea.append("Recovery cancelled. No new name provided.\n");
                            return;
                        }
                    }
                }

                // Update all cards in the module with the module name (for images)
                for (Card card : recoveredModule.getCards()) {
                    if (card.getModuleName() == null || !card.getModuleName().equals(recoveredModule.getName())) {
                        card.setModuleName(recoveredModule.getName());

                        // If it's an image card, trigger migration to module-specific directory
                        if (card.getAnswerType() == Card.AnswerType.IMAGE) {
                            card.migrateFromOldFormat();
                        }
                    }
                }

                // Add the recovered module
                moduleManager.addModule(recoveredModule);

                logArea.append("Module '" + recoveredModule.getName() + "' successfully recovered " +
                        "with " + recoveredModule.getCardCount() + " cards.\n");

                // Confirm to the user
                JOptionPane.showMessageDialog(this,
                        "Module '" + recoveredModule.getName() + "' has been recovered successfully.",
                        "Recovery Complete", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException | ClassNotFoundException e) {
                logArea.append("Error recovering module: " + e.getMessage() + "\n");
                e.printStackTrace();

                JOptionPane.showMessageDialog(this,
                        "Failed to recover the module. The file might be corrupted or incompatible.",
                        "Recovery Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}