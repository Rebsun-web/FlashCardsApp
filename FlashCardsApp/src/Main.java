// Main.java
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set some nicer UI defaults
        UIManager.put("Button.background", new Color(100, 149, 237)); // Cornflower blue
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 14));
        UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 14));
        UIManager.put("TextField.font", new Font("Arial", Font.PLAIN, 14));
        UIManager.put("TextArea.font", new Font("Arial", Font.PLAIN, 14));

        // Start application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            ModuleManager moduleManager = new ModuleManager();
            MainMenuUI mainMenu = new MainMenuUI(moduleManager);
            mainMenu.setVisible(true);
        });
    }
}