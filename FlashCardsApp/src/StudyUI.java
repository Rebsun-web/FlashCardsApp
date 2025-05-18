// StudyUI.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

public class StudyUI extends JFrame {
    private JComboBox<String> topicComboBox;
    private JLabel countLabel;

    private Module module;
    private MainMenuUI mainMenuUI;
    private JPanel cardPanel;
    private JLabel questionLabel;
    private JTextArea questionText;
    private JLabel answerLabel;
    private JPanel answerPanel;
    private JTextArea textAnswerArea;
    private JLabel imageAnswerLabel;

    private List<Card> studyCards;
    private int currentCardIndex = 0;
    private boolean isShowingAnswer = false;

    private JButton toggleAnswerButton;
    private JButton prevButton;
    private JButton nextButton;
    private JButton shuffleButton;
    private JButton zoomInButton;
    private JButton zoomOutButton;
    private JButton resetZoomButton;
    private double zoomFactor = 1.0;

    private JScrollPane imageScrollPane;

    public StudyUI(Module module, MainMenuUI mainMenuUI) {
        this.module = module;
        this.mainMenuUI = mainMenuUI;

        // Make a copy of the cards for this study session
        studyCards = new ArrayList<>(module.getCards());

        setTitle("Study: " + module.getName());
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

        // Add component listener for resizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // If currently showing an image answer, resize it to fit the new window size
                if (isShowingAnswer &&
                        currentCardIndex < studyCards.size() &&
                        studyCards.get(currentCardIndex).getAnswerType() == Card.AnswerType.IMAGE) {
                    showCurrentCard();
                }
            }
        });

        initUI();
        showCurrentCard();
    }

    private void initUI() {
        // Main layout
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 248, 255)); // Alice blue background

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(70, 130, 180)); // Steel blue
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        String cardCountText = "Card " + (currentCardIndex + 1) + " of " + studyCards.size();
        JLabel titleLabel = new JLabel("Studying: " + module.getName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.BLACK);

        countLabel = new JLabel("Card 1 of " + studyCards.size());
        countLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        countLabel.setForeground(Color.WHITE);

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(countLabel, BorderLayout.EAST);

        // Add topic selector below the title
        JPanel topicPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topicPanel.setBackground(new Color(240, 248, 255));

        JLabel topicLabel = new JLabel("Study Topic:");
        topicLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Create topic combo box with all topics plus "All Topics" option
        List<String> topics = new ArrayList<>();
        topics.add("All Topics");
        topics.addAll(module.getAllTopics());

        topicComboBox = new JComboBox<>(topics.toArray(new String[0]));
        topicComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        topicComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTopic = (String) topicComboBox.getSelectedItem();
                filterCardsByTopic(selectedTopic);
            }
        });

        topicPanel.add(topicLabel);
        topicPanel.add(topicComboBox);

        // Create a header panel to hold both title and topic panels
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titlePanel, BorderLayout.NORTH);
        headerPanel.add(topicPanel, BorderLayout.CENTER);

        // Card Panel (Center)
        cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(new Color(255, 255, 255));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 3),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        // Question
        questionLabel = new JLabel("Question:");
        questionLabel.setFont(new Font("Arial", Font.BOLD,
                16));
        questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        questionText = new JTextArea(4, 20);
        questionText.setFont(new Font("Arial", Font.PLAIN, 18));
        questionText.setLineWrap(true);
        questionText.setWrapStyleWord(true);
        questionText.setEditable(false);
        questionText.setBackground(new Color(255, 255, 255));
        questionText.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane questionScrollPane = new JScrollPane(questionText);
        questionScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        questionScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Answer
        answerLabel = new JLabel("Answer:");
        answerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        answerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        answerPanel = new JPanel();
        answerPanel.setLayout(new BoxLayout(answerPanel, BoxLayout.Y_AXIS));
        answerPanel.setBackground(Color.WHITE);
        answerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textAnswerArea = new JTextArea(8, 20);
        textAnswerArea.setFont(new Font("Arial", Font.PLAIN, 18));
        textAnswerArea.setLineWrap(true);
        textAnswerArea.setWrapStyleWord(true);
        textAnswerArea.setEditable(false);
        textAnswerArea.setBackground(new Color(255, 255, 255));

        JScrollPane textAnswerScrollPane = new JScrollPane(textAnswerArea);
        textAnswerScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        textAnswerScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Create the image label
        imageAnswerLabel = new JLabel();
        imageAnswerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        imageAnswerLabel.setHorizontalAlignment(JLabel.CENTER);

        // Create a scroll pane for the image
        imageScrollPane = new JScrollPane(imageAnswerLabel);
        imageScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        imageScrollPane.setBorder(BorderFactory.createEmptyBorder());
        imageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        imageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Set the preferred size to control initial scroll pane size
        imageScrollPane.setPreferredSize(new Dimension(1600, 1000));  // Adjust size as needed

        // Add components to the answer panel
        answerPanel.add(textAnswerScrollPane);
        answerPanel.add(imageScrollPane);

        cardPanel.add(questionLabel);
        cardPanel.add(Box.createVerticalStrut(10));
        cardPanel.add(questionScrollPane);
        cardPanel.add(Box.createVerticalStrut(20));
        cardPanel.add(answerLabel);
        cardPanel.add(Box.createVerticalStrut(10));
        cardPanel.add(answerPanel);

        // Controls panel
        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.setBackground(new Color(240, 248, 255));
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel zoomControls = createZoomControls();
        controlsPanel.add(zoomControls, BorderLayout.NORTH);

        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        navPanel.setBackground(new Color(240, 248, 255));

        prevButton = new JButton("Previous");
        styleButton(prevButton, new Color(70, 130, 180));
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPreviousCard();
            }
        });

        nextButton = new JButton("Next");
        styleButton(nextButton, new Color(70, 130, 180));
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNextCard();
            }
        });

        toggleAnswerButton = new JButton("Show Answer");
        styleButton(toggleAnswerButton, new Color(46, 139, 87));
        toggleAnswerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleAnswer();
            }
        });

        shuffleButton = new JButton("Shuffle Cards");
        styleButton(shuffleButton, new Color(205, 92, 92));
        shuffleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shuffleCards();
            }
        });

        navPanel.add(prevButton);
        navPanel.add(toggleAnswerButton);
        navPanel.add(nextButton);

        // Back button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(240, 248, 255));

        JButton backButton = new JButton("Back to Main Menu");
        styleButton(backButton, new Color(128, 128, 128));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToMainMenu();
            }
        });

        bottomPanel.add(shuffleButton, BorderLayout.WEST);
        bottomPanel.add(backButton, BorderLayout.EAST);

        controlsPanel.add(navPanel, BorderLayout.CENTER);
        controlsPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add all components to frame
        add(headerPanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        add(controlsPanel, BorderLayout.SOUTH);
    }

    private void filterCardsByTopic(String topic) {
        if (topic == null || topic.equals("All Topics")) {
            studyCards = new ArrayList<>(module.getCards());
        } else {
            studyCards = module.getCardsByTopic(topic);
        }

        // Reset to first card and update display
        if (!studyCards.isEmpty()) {
            currentCardIndex = 0;
            showCurrentCard();
        } else {
            // No cards for this topic
            JOptionPane.showMessageDialog(this,
                    "No cards available for the selected topic.",
                    "Empty Topic", JOptionPane.INFORMATION_MESSAGE);

            // Reset to "All Topics"
            topicComboBox.setSelectedIndex(0);
        }
    }


    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    // In StudyUI.java, update the showCurrentCard method:

    private void showCurrentCard() {
        if (studyCards.isEmpty()) {
            // Display a message in the card panel
            questionText.setText("No cards available for the selected topic.");
            answerLabel.setVisible(false);
            answerPanel.setVisible(false);
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
            toggleAnswerButton.setEnabled(false);
            return;
        }

        toggleAnswerButton.setEnabled(true);

        if (studyCards.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No cards available in this module.",
                    "Empty Module", JOptionPane.INFORMATION_MESSAGE);
            returnToMainMenu();
            return;
        }

        // Update title with current card number
        String cardCountText = "Card " + (currentCardIndex + 1) + " of " + studyCards.size();
        countLabel.setText(cardCountText);

        // Get current card
        Card currentCard = studyCards.get(currentCardIndex);

        // Update question
        questionText.setText(currentCard.getQuestion());

        // Reset answer view
        isShowingAnswer = false;
        toggleAnswerButton.setText("Show Answer");
        answerLabel.setVisible(false);
        answerPanel.setVisible(false);

        // Update answer content but don't show it yet
        if (currentCard.getAnswerType() == Card.AnswerType.TEXT) {
            textAnswerArea.setText(currentCard.getTextAnswer());
            textAnswerArea.setVisible(true);
            imageScrollPane.setVisible(false);
        } else {
            textAnswerArea.setVisible(false);

            // Reset zoom factor when showing a new card
            zoomFactor = 1.0;

            // Enhanced image handling for better initial display
            if (currentCard.getImageAnswer() != null) {
                // Get original image
                ImageIcon originalIcon = currentCard.getImageAnswer();

                // Get viewport size
                int viewportWidth = imageScrollPane.getViewport().getWidth();
                int viewportHeight = imageScrollPane.getViewport().getHeight();

                // Use default sizes if not yet laid out
                if (viewportWidth < 100) viewportWidth = 600;
                if (viewportHeight < 100) viewportHeight = 300;

                // Get original dimensions
                int originalWidth = originalIcon.getIconWidth();
                int originalHeight = originalIcon.getIconHeight();

                double scale = 1.0; // Use original size as baseline

                // If original is very small, scale it up a bit
                if (originalWidth < 800) {
                    scale = 1.5; // Make small images 50% larger
                }

//                // Calculate scale to fit the image entirely within the viewport
//                // with a small margin (90% of viewport)
//                double widthScale = (viewportWidth * 0.9) / originalWidth;
//                double heightScale = (viewportHeight * 0.9) / originalHeight;
//                double scale = Math.min(widthScale, heightScale);
//
//                // If image is smaller than viewport, don't scale up
//                if (scale > 1.0) {
//                    scale = 1.0;
//                }

                // Calculate target dimensions
                int targetWidth = (int)(originalWidth * scale);
                int targetHeight = (int)(originalHeight * scale);

                // Create resized image with high quality
                Image resizedImage = originalIcon.getImage().getScaledInstance(
                        targetWidth, targetHeight, Image.SCALE_SMOOTH);
                imageAnswerLabel.setIcon(new ImageIcon(resizedImage));

                // Set the preferred size of the label to match the image size
                imageAnswerLabel.setPreferredSize(new Dimension(targetWidth, targetHeight));
            } else {
                imageAnswerLabel.setIcon(null);
                imageAnswerLabel.setPreferredSize(null);
            }

            imageScrollPane.setVisible(true);

            // Reset the scroll position to the top-left corner
            SwingUtilities.invokeLater(() -> {
                JScrollBar horizontal = imageScrollPane.getHorizontalScrollBar();
                JScrollBar vertical = imageScrollPane.getVerticalScrollBar();
                horizontal.setValue(0);
                vertical.setValue(0);
            });
        }

        // Update navigation buttons
        prevButton.setEnabled(currentCardIndex > 0);
        nextButton.setEnabled(currentCardIndex < studyCards.size() - 1);
    }


    private JPanel createZoomControls() {
        JPanel zoomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        zoomPanel.setBackground(new Color(240, 248, 255));

        zoomInButton = new JButton("+");
        zoomInButton.setToolTipText("Zoom In");
        zoomInButton.setEnabled(false);
        zoomInButton.addActionListener(e -> {
            zoomFactor *= 1.25;
            updateImageWithZoom();
        });

        zoomOutButton = new JButton("-");
        zoomOutButton.setToolTipText("Zoom Out");
        zoomOutButton.setEnabled(false);
        zoomOutButton.addActionListener(e -> {
            zoomFactor *= 0.8;
            updateImageWithZoom();
        });

        resetZoomButton = new JButton("Reset Zoom");
        resetZoomButton.setToolTipText("Reset Zoom");
        resetZoomButton.setEnabled(false);
        resetZoomButton.addActionListener(e -> {
            zoomFactor = 1.0;
            updateImageWithZoom();
        });

        zoomPanel.add(zoomInButton);
        zoomPanel.add(resetZoomButton);
        zoomPanel.add(zoomOutButton);

        return zoomPanel;
    }

    private void updateImageWithZoom() {
        Card currentCard = studyCards.get(currentCardIndex);
        if (currentCard.getAnswerType() == Card.AnswerType.IMAGE && currentCard.getImageAnswer() != null) {
            ImageIcon originalIcon = currentCard.getImageAnswer();
            int originalWidth = originalIcon.getIconWidth();
            int originalHeight = originalIcon.getIconHeight();

            // Apply zoom factor
            int targetWidth = (int)(originalWidth * zoomFactor);
            int targetHeight = (int)(originalHeight * zoomFactor);

            // Create high-quality scaled image
            Image resizedImage = originalIcon.getImage().getScaledInstance(
                    targetWidth, targetHeight, Image.SCALE_SMOOTH);
            imageAnswerLabel.setIcon(new ImageIcon(resizedImage));

            // Important: update the preferred size so scrollbars adjust
            imageAnswerLabel.setPreferredSize(new Dimension(targetWidth, targetHeight));

            // Refresh the scroll pane to update scrollbars
            imageScrollPane.revalidate();
        }
    }

    private void toggleAnswer() {
        isShowingAnswer = !isShowingAnswer;

        Card currentCard = studyCards.get(currentCardIndex);
        boolean isImageCard = currentCard.getAnswerType() == Card.AnswerType.IMAGE;

        if (isShowingAnswer) {
            answerLabel.setVisible(true);
            answerPanel.setVisible(true);
            toggleAnswerButton.setText("Hide Answer");

            // Enable zoom controls for images
            zoomInButton.setEnabled(isImageCard);
            zoomOutButton.setEnabled(isImageCard);
            resetZoomButton.setEnabled(isImageCard);
        } else {
            answerLabel.setVisible(false);
            answerPanel.setVisible(false);
            toggleAnswerButton.setText("Show Answer");

            // Disable zoom controls
            zoomInButton.setEnabled(false);
            zoomOutButton.setEnabled(false);
            resetZoomButton.setEnabled(false);
        }
    }

    private void showNextCard() {
        if (currentCardIndex < studyCards.size() - 1) {
            currentCardIndex++;
            showCurrentCard();
        }
    }

    private void showPreviousCard() {
        if (currentCardIndex > 0) {
            currentCardIndex--;
            showCurrentCard();
        }
    }

    private void shuffleCards() {
        java.util.Collections.shuffle(studyCards);
        currentCardIndex = 0;
        showCurrentCard();
        JOptionPane.showMessageDialog(this,
                "Cards have been shuffled!",
                "Shuffle Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    private void returnToMainMenu() {
        mainMenuUI.setVisible(true);
        dispose();
    }
}