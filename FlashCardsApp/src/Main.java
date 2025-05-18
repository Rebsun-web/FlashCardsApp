// Main.java
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Apply the application theme
        UITheme.applyTheme();

        // Ensure app directories exist and create README
        FileUtils.getAppDataDir();
        FileUtils.getImagesDir();
        FileUtils.createReadmeFile();

        // Start application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            ModuleManager moduleManager = new ModuleManager();
            MainMenuUI mainMenu = new MainMenuUI(moduleManager);
            mainMenu.setVisible(true);
        });
    }
}