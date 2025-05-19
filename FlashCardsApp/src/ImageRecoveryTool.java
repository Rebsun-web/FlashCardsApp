// ImageRecoveryTool.java
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageRecoveryTool extends JFrame {
    private ModuleManager moduleManager;
    private JTextArea logArea;
    private JButton scanButton;
    private JButton fixButton;
    private JButton closeButton;

    private List<Card> cardsWithMissingImages;

    public ImageRecoveryTool() {
        this.moduleManager = new ModuleManager();
        this.cardsWithMissingImages = new ArrayList<>();

        setTitle("Image Recovery Tool");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 248, 255));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("Image Recovery Tool");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);
        titlePanel.add(titleLabel);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 248, 255));

        scanButton = new JButton("Scan for Missing Images");
        scanButton.setBackground(new Color(70, 130, 180));
        scanButton.setForeground(Color.BLACK);
        scanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scanForMissingImages();
            }
        });

        fixButton = new JButton("Fix Selected Image");
        fixButton.setBackground(new Color(46, 139, 87));
        fixButton.setForeground(Color.BLACK);
        fixButton.setEnabled(false);
        fixButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fixSelectedImage();
            }
        });

        closeButton = new JButton("Close");
        closeButton.setBackground(new Color(128, 128, 128));
        closeButton.setForeground(Color.BLACK);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonPanel.add(scanButton);
        buttonPanel.add(fixButton);
        buttonPanel.add(closeButton);

        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

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

    // In ImageRecoveryTool.java - Fix the fixSelectedImage method
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
                    Module foundModule = null;
                    for (Module module : moduleManager.getModules()) {
                        if (module.getCards().contains(card)) {
                            foundModule = module;
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
                    card.loadImage(); // We need to add this method to Card class

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