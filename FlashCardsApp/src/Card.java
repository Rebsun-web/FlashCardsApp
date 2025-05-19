// Card.java - Modified to use module-specific image paths
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import javax.swing.ImageIcon;

public class Card implements Serializable {
    private static final long serialVersionUID = 1L; // Increment serial version for updated class

    public enum AnswerType {
        TEXT,
        IMAGE
    }

    private String question;
    private String textAnswer;
    private String imageAnswerPath; // Store path relative to app data dir
    private transient ImageIcon imageAnswer;
    private AnswerType answerType;
    private String topic;
    private String moduleName; // Store the module name for organizing images

    // For backward compatibility
    private File imageAnswerFile;

    // Constructor for text answer
    public Card(String question, String textAnswer, String moduleName) {
        this.question = question;
        this.textAnswer = textAnswer;
        this.answerType = AnswerType.TEXT;
        this.topic = "General"; // Default topic
        this.moduleName = moduleName;
    }

    // Constructor for text answer with topic
    public Card(String question, String textAnswer, String topic, String moduleName) {
        this.question = question;
        this.textAnswer = textAnswer;
        this.answerType = AnswerType.TEXT;
        this.topic = topic != null && !topic.isEmpty() ? topic : "General";
        this.moduleName = moduleName;
    }

    // Constructor for image answer
    public Card(String question, File imageFile, String moduleName) {
        this.question = question;
        this.moduleName = moduleName;
        setImageFile(imageFile);
        this.answerType = AnswerType.IMAGE;
        this.topic = "General"; // Default topic
    }

    // Constructor for image answer with topic
    public Card(String question, File imageFile, String topic, String moduleName) {
        this.question = question;
        this.moduleName = moduleName;
        setImageFile(imageFile);
        this.answerType = AnswerType.IMAGE;
        this.topic = topic != null && !topic.isEmpty() ? topic : "General";
    }

    // Set image file - copies it to the module's images directory
    public void setImageFile(File imageFile) {
        if (imageFile != null && imageFile.exists()) {
            // Copy the image to our module's directory
            File copiedImage = FileUtils.copyImageToModuleDir(imageFile, moduleName);
            if (copiedImage != null) {
                // Store the relative path
                this.imageAnswerPath = FileUtils.getRelativePath(copiedImage);
                // Load the image
                loadImage();
            }
        }
    }

    public void loadImage() {
        File imageFile = getImageAnswerFile();
        if (imageFile != null && imageFile.exists()) {
            imageAnswer = new ImageIcon(imageFile.getAbsolutePath());
            // Output debug information
            System.out.println("Loaded image from: " + imageFile.getAbsolutePath());
            System.out.println("Image icon size: " + imageAnswer.getIconWidth() + "x" + imageAnswer.getIconHeight());
        } else {
            System.out.println("Failed to load image. File does not exist: " +
                    (imageFile != null ? imageFile.getAbsolutePath() : "null"));
            imageAnswer = null;
        }
    }

    // Update the getImageAnswerFile method to be more robust:
    public File getImageAnswerFile() {
        // If we have a path, use it
        if (imageAnswerPath != null && !imageAnswerPath.isEmpty()) {
            File file = FileUtils.resolveRelativePath(imageAnswerPath);
            if (file != null && file.exists()) {
                return file;
            }
        }

        // Fallback to the old direct file reference
        if (imageAnswerFile != null && imageAnswerFile.exists()) {
            return imageAnswerFile;
        }

        // If we get here, we couldn't find the image
        return null;
    }

    // Called after deserialization to reload the image
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        // Initialize topic if it's null (for backward compatibility)
        if (topic == null) {
            topic = "General";
        }

        // Initialize moduleName if it's null (for backward compatibility)
        if (moduleName == null) {
            moduleName = "General";
        }

        // Migrate old format if needed
        migrateFromOldFormat();

        // Load the image
        if (answerType == AnswerType.IMAGE) {
            loadImage();
        }
    }

    // In Card.java - Improve the migrateFromOldFormat method
    public void migrateFromOldFormat() {
        // If we have an old-style imageAnswerFile but no path
        if (answerType == AnswerType.IMAGE) {
            // First ensure we have a module name
            if (moduleName == null || moduleName.isEmpty()) {
                moduleName = "General";
                System.out.println("Setting default module name: General");
            }

            if (imageAnswerFile != null &&
                    (imageAnswerPath == null || imageAnswerPath.isEmpty())) {
                System.out.println("Migrating image: " + imageAnswerFile.getAbsolutePath());

                // First check if the file still exists at the original location
                if (imageAnswerFile.exists()) {
                    // Copy the file to our module's images directory
                    File copiedImage = FileUtils.migrateImageToModuleDir(imageAnswerFile, moduleName);
                    if (copiedImage != null) {
                        imageAnswerPath = FileUtils.getRelativePath(copiedImage);
                        System.out.println("Successfully migrated to: " + imageAnswerPath);

                        // Load the image to ensure it works
                        loadImage();
                    } else {
                        System.out.println("Failed to copy image");
                    }
                } else {
                    // The original file doesn't exist anymore, handle this case
                    System.out.println("Original image not found: " + imageAnswerFile.getAbsolutePath());

                    // Use a placeholder or default image path
                    imageAnswerPath = "missing_image";
                }
            }
            // Check if we have a path but the image is not loaded
            else if (imageAnswerPath != null && !imageAnswerPath.isEmpty() && imageAnswer == null) {
                System.out.println("Image path exists but image not loaded: " + imageAnswerPath);
                loadImage();
            }
        }
    }

    // Getters and setters
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public File getOriginalImageFile() {
        return imageAnswerFile;
    }

    public AnswerType getAnswerType() {
        return answerType;
    }

    public String getTextAnswer() {
        return textAnswer;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
        this.answerType = AnswerType.TEXT;
    }

    public ImageIcon getImageAnswer() {
        return imageAnswer;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic != null && !topic.isEmpty() ? topic : "General";
    }

    // Added getter for the relative path
    public String getImageAnswerPath() {
        return imageAnswerPath;
    }

    // Get/set module name
    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}