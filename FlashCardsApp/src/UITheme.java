// UITheme.java
import java.awt.*;
import javax.swing.UIManager;

public class UITheme {
    // Main colors
    public static final Color PRIMARY_COLOR = new Color(70, 130, 180); // Steel blue
    public static final Color SECONDARY_COLOR = new Color(100, 149, 237); // Cornflower blue
    public static final Color SUCCESS_COLOR = new Color(46, 139, 87); // Sea green
    public static final Color WARNING_COLOR = new Color(205, 92, 92); // Indian red
    public static final Color NEUTRAL_COLOR = new Color(128, 128, 128); // Gray
    public static final Color BACKGROUND_COLOR = new Color(240, 248, 255); // Alice blue
    public static final Color CARD_COLOR = new Color(255, 250, 240); // Floral white

    // Fonts
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    public static final Font SUBTITLE_FONT = new Font("Arial", Font.BOLD, 18);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
    public static final Font TEXT_FONT = new Font("Arial", Font.PLAIN, 14);

    /**
     * Apply the application theme to all UI components
     */
    public static void applyTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set UI defaults
        UIManager.put("Button.background", SECONDARY_COLOR);
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("Button.font", BUTTON_FONT);
        UIManager.put("Label.font", TEXT_FONT);
        UIManager.put("TextField.font", TEXT_FONT);
        UIManager.put("TextArea.font", TEXT_FONT);
        UIManager.put("Panel.background", BACKGROUND_COLOR);
    }

    /**
     * Style a button according to the theme
     *
     * @param button The button to style
     * @param color The background color (use one of the color constants)
     */
    public static void styleButton(javax.swing.JButton button, Color color) {
        button.setFont(BUTTON_FONT);
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    /**
     * Create a styled title panel
     *
     * @param title The title text
     * @return A styled panel with the title
     */
    public static javax.swing.JPanel createTitlePanel(String title) {
        javax.swing.JPanel panel = new javax.swing.JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));

        javax.swing.JLabel titleLabel = new javax.swing.JLabel(title);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);

        panel.add(titleLabel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create a styled card panel
     *
     * @return A styled panel for cards
     */
    public static javax.swing.JPanel createCardPanel() {
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        return panel;
    }
}