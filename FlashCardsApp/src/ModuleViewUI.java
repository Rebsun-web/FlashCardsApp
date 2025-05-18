// ModuleViewUI.java
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ModuleViewUI extends JFrame {
    private JComboBox<String> topicFilterComboBox;
    private String currentTopicFilter = "All Topics";

    private Module module;
    private ModuleManager moduleManager;
    private MainMenuUI mainMenuUI;
    private JPanel cardsPanel;
    private JScrollPane scrollPane;

    public ModuleViewUI(Module module, ModuleManager moduleManager, MainMenuUI mainMenuUI) {
        this.module = module;
        this.moduleManager = moduleManager;
        this.mainMenuUI = mainMenuUI;

        setTitle("Module: " + module.getName());
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Return to main menu when closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                returnToMainMenu();
            }
        });

        initUI();
    }

    private void initUI() {
        // Main layout
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 248, 255)); // Alice blue background

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(70, 130, 180)); // Steel blue
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("Module: " + module.getName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.BLACK);

        JLabel countLabel = new JLabel(module.getCardCount() + " cards");
        countLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        countLabel.setForeground(Color.BLACK);

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(countLabel, BorderLayout.EAST);

        // Cards panel
        cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBackground(new Color(240, 248, 255));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton addTextCardButton = new JButton("Add Text Card");
        styleButton(addTextCardButton, new Color(65, 105, 225)); // Royal blue
        addTextCardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTextCard();
            }
        });

        JButton addImageCardButton = new JButton("Add Image Card");
        styleButton(addImageCardButton, new Color(46, 139, 87)); // Sea green
        addImageCardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addImageCard();
            }
        });

        JButton backButton = new JButton("Back to Main Menu");
        styleButton(backButton, new Color(128, 128, 128)); // Gray
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToMainMenu();
            }
        });

        buttonPanel.add(addTextCardButton);
        buttonPanel.add(addImageCardButton);
        buttonPanel.add(backButton);

        // Add a topic filter panel above the cards list
        JPanel topicFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topicFilterPanel.setBackground(new Color(240, 248, 255));

        JLabel filterLabel = new JLabel("Filter by Topic:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Create topic combo box with all topics plus "All Topics" option
        List<String> topics = new ArrayList<>();
        topics.add("All Topics");
        topics.addAll(module.getAllTopics());

        topicFilterComboBox = new JComboBox<>(topics.toArray(new String[0]));
        topicFilterComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        topicFilterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentTopicFilter = (String) topicFilterComboBox.getSelectedItem();
                refreshCardsList();
            }
        });

        // Add a button to manage topics
        JButton manageTopicsButton = new JButton("Manage Topics");
        styleButton(manageTopicsButton, new Color(70, 130, 180));
        manageTopicsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manageTopics();
            }
        });

        topicFilterPanel.add(filterLabel);
        topicFilterPanel.add(topicFilterComboBox);
        topicFilterPanel.add(Box.createHorizontalStrut(20));
        topicFilterPanel.add(manageTopicsButton);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titlePanel, BorderLayout.NORTH);
        headerPanel.add(topicFilterPanel, BorderLayout.CENTER);

        // And add the header panel to the main layout
        add(headerPanel, BorderLayout.NORTH);

        // Add components to frame
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load cards
        refreshCardsList();
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    private void refreshCardsList() {
        cardsPanel.removeAll();

        // Get filtered cards based on selected topic
        List<Card> filteredCards = module.getCardsByTopic(currentTopicFilter);

        if (filteredCards.isEmpty()) {
            JLabel emptyLabel = new JLabel("No cards found for this topic. Add some cards to get started!");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardsPanel.add(Box.createVerticalStrut(30));
            cardsPanel.add(emptyLabel);
        } else {
            for (int i = 0; i < filteredCards.size(); i++) {
                Card card = filteredCards.get(i);
                // Find the actual index in the full list for delete/edit operations
                int actualIndex = module.getCards().indexOf(card);
                JPanel cardPanel = createCardPanel(card, actualIndex);
                cardsPanel.add(cardPanel);
                cardsPanel.add(Box.createVerticalStrut(10));
            }
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    // Add topic management dialog
    private void manageTopics() {
        JDialog dialog = new JDialog(this, "Manage Topics", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // Get all current topics
        List<String> allTopics = module.getAllTopics();

        // Create a list model and JList to display topics
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String topic : allTopics) {
            listModel.addElement(topic);
        }

        JList<String> topicList = new JList<>(listModel);
        topicList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        topicList.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(topicList);

        // Panel for topic actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addButton = new JButton("Add Topic");
        styleButton(addButton, new Color(46, 139, 87));

        JButton renameButton = new JButton("Rename Topic");
        styleButton(renameButton, new Color(70, 130, 180));

        JButton mergeButton = new JButton("Merge Topics");
        styleButton(mergeButton, new Color(100, 149, 237));

        // Add new topic
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newTopic = JOptionPane.showInputDialog(dialog,
                        "Enter new topic name:", "Add Topic", JOptionPane.PLAIN_MESSAGE);

                if (newTopic != null && !newTopic.trim().isEmpty()) {
                    if (!listModel.contains(newTopic)) {
                        listModel.addElement(newTopic);
                        // Update filter dropdown
                        updateTopicFilter();
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                                "This topic already exists.", "Duplicate Topic",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });

        // Rename selected topic
        renameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = topicList.getSelectedIndex();
                if (selectedIndex >= 0) {
                    String oldTopic = listModel.getElementAt(selectedIndex);
                    String newTopic = JOptionPane.showInputDialog(dialog,
                            "Enter new name for topic \"" + oldTopic + "\":",
                            "Rename Topic", JOptionPane.PLAIN_MESSAGE);

                    if (newTopic != null && !newTopic.trim().isEmpty() && !newTopic.equals(oldTopic)) {
                        // Update all cards with this topic
                        for (Card card : module.getCards()) {
                            if (card.getTopic().equals(oldTopic)) {
                                card.setTopic(newTopic);
                            }
                        }

                        // Update list model
                        listModel.removeElementAt(selectedIndex);
                        listModel.addElement(newTopic);

                        // Save changes
                        moduleManager.saveModules();

                        // Update filter dropdown
                        updateTopicFilter();
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Please select a topic to rename.", "No Selection",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // Merge topics
        mergeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = topicList.getSelectedIndex();
                if (selectedIndex >= 0) {
                    String selectedTopic = listModel.getElementAt(selectedIndex);

                    // Create list of other topics for merging
                    List<String> otherTopics = new ArrayList<>();
                    for (int i = 0; i < listModel.getSize(); i++) {
                        String topic = listModel.getElementAt(i);
                        if (!topic.equals(selectedTopic)) {
                            otherTopics.add(topic);
                        }
                    }

                    if (otherTopics.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog,
                                "There are no other topics to merge with.", "No Other Topics",
                                JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    // Show dialog to select target topic
                    String targetTopic = (String) JOptionPane.showInputDialog(dialog,
                            "Merge topic \"" + selectedTopic + "\" into:",
                            "Merge Topics", JOptionPane.QUESTION_MESSAGE, null,
                            otherTopics.toArray(), otherTopics.get(0));

                    if (targetTopic != null) {
                        // Update all cards with the selected topic
                        for (Card card : module.getCards()) {
                            if (card.getTopic().equals(selectedTopic)) {
                                card.setTopic(targetTopic);
                            }
                        }

                        // Remove the merged topic
                        listModel.removeElement(selectedTopic);

                        // Save changes
                        moduleManager.saveModules();

                        // Update filter dropdown
                        updateTopicFilter();
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Please select a topic to merge.", "No Selection",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        actionPanel.add(addButton);
        actionPanel.add(renameButton);
        actionPanel.add(mergeButton);

        // Close button at bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        styleButton(closeButton, new Color(128, 128, 128));
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                refreshCardsList();
            }
        });
        bottomPanel.add(closeButton);

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(actionPanel, BorderLayout.NORTH);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // Update topic filter dropdown
    private void updateTopicFilter() {
        // Save currently selected item
        String currentSelection = (String) topicFilterComboBox.getSelectedItem();

        // Update the combo box items
        topicFilterComboBox.removeAllItems();
        topicFilterComboBox.addItem("All Topics");

        for (String topic : module.getAllTopics()) {
            topicFilterComboBox.addItem(topic);
        }

        // Restore previous selection if it still exists, otherwise select "All Topics"
        boolean selectionFound = false;
        for (int i = 0; i < topicFilterComboBox.getItemCount(); i++) {
            if (topicFilterComboBox.getItemAt(i).equals(currentSelection)) {
                topicFilterComboBox.setSelectedIndex(i);
                selectionFound = true;
                break;
            }
        }

        if (!selectionFound) {
            topicFilterComboBox.setSelectedIndex(0); // "All Topics"
        }
    }


    private JPanel createCardPanel(Card card, final int index) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(new Color(255, 250, 240)); // Floral white
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // Left side - Question
        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setBackground(new Color(255, 250, 240));
        questionPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        questionPanel.setPreferredSize(new Dimension(400, 120));

        // Add topic label
        JLabel topicLabel = new JLabel("Topic: " + card.getTopic());
        topicLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        topicLabel.setForeground(new Color(100, 100, 100));

        JLabel questionLabel = new JLabel("Question:");
        questionLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JTextArea questionText = new JTextArea(card.getQuestion());
        questionText.setFont(new Font("Arial", Font.PLAIN, 14));
        questionText.setLineWrap(true);
        questionText.setWrapStyleWord(true);
        questionText.setEditable(false);
        questionText.setBackground(new Color(255, 250, 240));

        questionPanel.add(topicLabel);
        questionPanel.add(Box.createVerticalStrut(5));
        questionPanel.add(questionLabel);
        questionPanel.add(Box.createVerticalStrut(5));
        questionPanel.add(new JScrollPane(questionText));


        // Right side - Answer Type
        JPanel answerPanel = new JPanel();
        answerPanel.setLayout(new BoxLayout(answerPanel, BoxLayout.Y_AXIS));
        answerPanel.setBackground(new Color(255, 250, 240));

        JLabel answerTypeLabel = new JLabel("Answer Type: " +
                (card.getAnswerType() == Card.AnswerType.TEXT ? "Text" : "Image"));
        answerTypeLabel.setFont(new Font("Arial", Font.BOLD, 14));

        answerPanel.add(answerTypeLabel);
        answerPanel.add(Box.createVerticalStrut(10));

        if (card.getAnswerType() == Card.AnswerType.TEXT) {
            JLabel textPreview = new JLabel("Text answer available");
            textPreview.setFont(new Font("Arial", Font.ITALIC, 14));
            answerPanel.add(textPreview);
        } else {
            JLabel imagePreview = new JLabel("Image answer available");
            imagePreview.setFont(new Font("Arial", Font.ITALIC, 14));
            answerPanel.add(imagePreview);
        }

        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(new Color(255, 250, 240));

        // Edit button
        JButton editButton = new JButton("Edit");
        editButton.setBackground(new Color(100, 149, 237)); // Cornflower blue
        editButton.setForeground(Color.BLACK);
        editButton.setFocusPainted(false);
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editCard(card, index);
            }
        });

        // Delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(220, 20, 60)); // Crimson
        deleteButton.setForeground(Color.BLACK);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(ModuleViewUI.this,
                        "Are you sure you want to delete this card?",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    module.removeCard(index);
                    moduleManager.saveModules();
                    refreshCardsList();
                }
            }
        });

        // Add spacing between buttons
        buttonsPanel.add(editButton);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(deleteButton);

        answerPanel.add(Box.createVerticalGlue());
        answerPanel.add(buttonsPanel);

        panel.add(questionPanel, BorderLayout.CENTER);
        panel.add(answerPanel, BorderLayout.EAST);

        return panel;
    }

    // Add the editCard method to handle editing of cards
    private void editCard(Card card, int index) {
        if (card.getAnswerType() == Card.AnswerType.TEXT) {
            editTextCard(card, index);
        } else {
            editImageCard(card, index);
        }
    }

    // Method to edit text cards
    private void editTextCard(Card card, int index) {
        // Create a custom dialog for editing a text card
        JDialog dialog = new JDialog(this, "Edit Text Card", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(new Color(240, 248, 255));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(240, 248, 255));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel questionLabel = new JLabel("Question:");
        questionLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JTextArea questionField = new JTextArea(5, 20);
        questionField.setFont(new Font("Arial", Font.PLAIN, 14));
        questionField.setLineWrap(true);
        questionField.setWrapStyleWord(true);
        questionField.setText(card.getQuestion());
        JScrollPane questionScroll = new JScrollPane(questionField);

        JLabel answerLabel = new JLabel("Answer:");
        answerLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JTextArea answerField = new JTextArea(8, 20);
        answerField.setFont(new Font("Arial", Font.PLAIN, 14));
        answerField.setLineWrap(true);
        answerField.setWrapStyleWord(true);
        answerField.setText(card.getTextAnswer());
        JScrollPane answerScroll = new JScrollPane(answerField);

        // Topic selection
        JLabel topicLabel = new JLabel("Topic:");
        topicLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Get existing topics for dropdown
        List<String> topics = module.getAllTopics();
        if (topics.isEmpty()) {
            topics.add("General");
        }

        JComboBox<String> topicComboBox = new JComboBox<>(topics.toArray(new String[0]));
        topicComboBox.setEditable(true); // Allow adding new topics
        topicComboBox.setFont(new Font("Arial", Font.PLAIN, 14));

        // Set current topic as selected
        topicComboBox.setSelectedItem(card.getTopic());

        formPanel.add(questionLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(questionScroll);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(answerLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(answerScroll);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(topicLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(topicComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(128, 128, 128));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        JButton saveButton = new JButton("Save Changes");
        saveButton.setBackground(new Color(46, 139, 87));
        saveButton.setForeground(Color.BLACK);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String question = questionField.getText().trim();
                String answer = answerField.getText().trim();
                String topic = topicComboBox.getSelectedItem().toString().trim();

                if (question.isEmpty() || answer.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Both question and answer are required.",
                            "Missing Information", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Create a new card with updated information
                Card updatedCard = new Card(question, answer, topic);

                // Replace the old card with the updated one
                module.getCards().set(index, updatedCard);
                moduleManager.saveModules();
                updateTopicFilter();
                refreshCardsList();
                dialog.dispose();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Method to edit image cards
    private void editImageCard(Card card, int index) {
        // Create a custom dialog for editing an image card question
        JDialog dialog = new JDialog(this, "Edit Image Card", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(new Color(240, 248, 255));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(240, 248, 255));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel questionLabel = new JLabel("Question:");
        questionLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JTextArea questionField = new JTextArea(5, 20);
        questionField.setFont(new Font("Arial", Font.PLAIN, 14));
        questionField.setLineWrap(true);
        questionField.setWrapStyleWord(true);
        questionField.setText(card.getQuestion());
        JScrollPane questionScroll = new JScrollPane(questionField);

        JLabel currentImageLabel = new JLabel("Current Image: " +
                (card.getImageAnswerFile() != null ? card.getImageAnswerFile().getName() : "None"));
        currentImageLabel.setFont(new Font("Arial", Font.ITALIC, 14));

        // Topic selection
        JLabel topicLabel = new JLabel("Topic:");
        topicLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Get existing topics for dropdown
        List<String> topics = module.getAllTopics();
        if (topics.isEmpty()) {
            topics.add("General");
        }

        JComboBox<String> topicComboBox = new JComboBox<>(topics.toArray(new String[0]));
        topicComboBox.setEditable(true); // Allow adding new topics
        topicComboBox.setFont(new Font("Arial", Font.PLAIN, 14));

        // Set current topic as selected
        topicComboBox.setSelectedItem(card.getTopic());

        JButton changeImageButton = new JButton("Change Image");
        changeImageButton.setBackground(new Color(100, 149, 237));
        changeImageButton.setForeground(Color.BLACK);

        final File[] selectedFile = new File[1]; // Array to hold the selected file
        selectedFile[0] = card.getImageAnswerFile(); // Initialize with current image file

        changeImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select New Image");
                fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif", "bmp"));

                if (card.getImageAnswerFile() != null) {
                    fileChooser.setCurrentDirectory(card.getImageAnswerFile().getParentFile());
                }

                int result = fileChooser.showOpenDialog(dialog);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile[0] = fileChooser.getSelectedFile();
                    currentImageLabel.setText("New Image: " + selectedFile[0].getName());
                }
            }
        });

        formPanel.add(questionLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(questionScroll);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(currentImageLabel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(changeImageButton);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(topicLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(topicComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(128, 128, 128));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        JButton saveButton = new JButton("Save Changes");
        saveButton.setBackground(new Color(46, 139, 87));
        saveButton.setForeground(Color.BLACK);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String question = questionField.getText().trim();
                String topic = topicComboBox.getSelectedItem().toString().trim();

                if (question.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Question is required.",
                            "Missing Information", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (selectedFile[0] == null) {
                    JOptionPane.showMessageDialog(dialog,
                            "An image file is required.",
                            "Missing Information", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (topic.isEmpty()) {
                    topic = "General";
                }

                // Create a new card with updated information
                Card updatedCard = new Card(question, selectedFile[0], topic);

                // Replace the old card with the updated one
                module.getCards().set(index, updatedCard);
                moduleManager.saveModules();
                updateTopicFilter(); // Update topic filter in case a new topic was added
                refreshCardsList();
                dialog.dispose();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addTextCard() {
        // Create a custom dialog for adding a card
        JDialog dialog = new JDialog(this, "Add Text Card", true);
        dialog.setSize(500, 450); // Increased height for topic field
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(new Color(240, 248, 255));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(240, 248, 255));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel questionLabel = new JLabel("Question:");
        questionLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JTextArea questionField = new JTextArea(5, 20);
        questionField.setFont(new Font("Arial", Font.PLAIN, 14));
        questionField.setLineWrap(true);
        questionField.setWrapStyleWord(true);
        JScrollPane questionScroll = new JScrollPane(questionField);

        JLabel answerLabel = new JLabel("Answer:");
        answerLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JTextArea answerField = new JTextArea(8, 20);
        answerField.setFont(new Font("Arial", Font.PLAIN, 14));
        answerField.setLineWrap(true);
        answerField.setWrapStyleWord(true);
        JScrollPane answerScroll = new JScrollPane(answerField);

        // Topic selection
        JLabel topicLabel = new JLabel("Topic:");
        topicLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Get existing topics for dropdown
        List<String> topics = module.getAllTopics();
        if (topics.isEmpty()) {
            topics.add("General");
        }

        JComboBox<String> topicComboBox = new JComboBox<>(topics.toArray(new String[0]));
        topicComboBox.setEditable(true); // Allow adding new topics
        topicComboBox.setFont(new Font("Arial", Font.PLAIN, 14));

        formPanel.add(questionLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(questionScroll);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(answerLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(answerScroll);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(topicLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(topicComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(128, 128, 128));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        JButton saveButton = new JButton("Save Card");
        saveButton.setBackground(new Color(46, 139, 87));
        saveButton.setForeground(Color.BLACK);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String question = questionField.getText().trim();
                String answer = answerField.getText().trim();
                String topic = topicComboBox.getSelectedItem().toString().trim();

                if (question.isEmpty() || answer.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Both question and answer are required.",
                            "Missing Information", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (topic.isEmpty()) {
                    topic = "General";
                }

                Card newCard = new Card(question, answer, topic);
                module.addCard(newCard);
                moduleManager.saveModules();
                updateTopicFilter(); // Update topic filter in case a new topic was added
                refreshCardsList();
                dialog.dispose();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addImageCard() {
        // First, get the question
        JDialog questionDialog = new JDialog(this, "Add Image Card - Question", true);
        questionDialog.setSize(500, 250);
        questionDialog.setLocationRelativeTo(this);
        questionDialog.setLayout(new BorderLayout(10, 10));
        questionDialog.getContentPane().setBackground(new Color(240, 248, 255));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(240, 248, 255));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel questionLabel = new JLabel("Question:");
        questionLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JTextArea questionField = new JTextArea(5, 20);
        questionField.setFont(new Font("Arial", Font.PLAIN, 14));
        questionField.setLineWrap(true);
        questionField.setWrapStyleWord(true);
        JScrollPane questionScroll = new JScrollPane(questionField);

        // Topic selection
        JLabel topicLabel = new JLabel("Topic:");
        topicLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Get existing topics for dropdown
        List<String> topics = module.getAllTopics();
        if (topics.isEmpty()) {
            topics.add("General");
        }

        JComboBox<String> topicComboBox = new JComboBox<>(topics.toArray(new String[0]));
        topicComboBox.setEditable(true); // Allow adding new topics
        topicComboBox.setFont(new Font("Arial", Font.PLAIN, 14));

        formPanel.add(questionLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(questionScroll);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(topicLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(topicComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(128, 128, 128));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                questionDialog.dispose();
            }
        });

        JButton nextButton = new JButton("Next");
        nextButton.setBackground(new Color(46, 139, 87));
        nextButton.setForeground(Color.BLACK);
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String question = questionField.getText().trim();
                String topic = topicComboBox.getSelectedItem().toString().trim();

                if (question.isEmpty()) {
                    JOptionPane.showMessageDialog(questionDialog,
                            "Question is required.",
                            "Missing Information", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (topic.isEmpty()) {
                    topic = "General";
                }

                questionDialog.dispose();
                selectImageForCard(question, topic);
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(nextButton);

        questionDialog.add(formPanel, BorderLayout.CENTER);
        questionDialog.add(buttonPanel, BorderLayout.SOUTH);
        questionDialog.setVisible(true);
    }

    private void selectImageForCard(String question, String topic) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Image for Answer");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif", "bmp"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            Card newCard = new Card(question, selectedFile, topic);
            module.addCard(newCard);
            moduleManager.saveModules();
            updateTopicFilter(); // Update topic filter in case a new topic was added
            refreshCardsList();
        }
    }

    private void returnToMainMenu() {
        mainMenuUI.setVisible(true);
        dispose();
    }
}