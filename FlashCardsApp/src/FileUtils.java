// FileUtils.java
import java.io.*;
import java.nio.file.*;

public class FileUtils {
    private static final String APP_DATA_DIR = "flashcards";
    private static final String IMAGES_DIR = "images";

    // Get the application data directory
    public static File getAppDataDir() {
        File dir = new File(APP_DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    // Get the main images directory
    public static File getImagesDir() {
        File dir = new File(getAppDataDir(), IMAGES_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    // Get or create module-specific image directory
    public static File getModuleImagesDir(String moduleName) {
        // Sanitize module name for file system compatibility
        String safeName = sanitizeFileName(moduleName);
        File dir = new File(getImagesDir(), safeName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    // Sanitize a string to be used as a file name
    private static String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    // Create a README file explaining the directory structure
    public static void createReadmeFile() {
        File readme = new File(getAppDataDir(), "README.txt");
        if (!readme.exists()) {
            try (PrintWriter writer = new PrintWriter(readme)) {
                writer.println("FlashCards App Directory Structure");
                writer.println("--------------------------------");
                writer.println("This directory contains your FlashCards app data.");
                writer.println();
                writer.println("flashcards/ - Main application data directory");
                writer.println("├── *.ser - Module data files");
                writer.println("├── images/ - Main images directory");
                writer.println("│   └── [ModuleName]/ - Module-specific image directories");
                writer.println();
                writer.println("Images are stored in module-specific subdirectories for better organization.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Copy an image to the module-specific directory
    public static File copyImageToModuleDir(File sourceImage, String moduleName) {
        if (sourceImage == null || !sourceImage.exists()) {
            return null;
        }

        try {
            // Get destination directory
            File moduleImagesDir = getModuleImagesDir(moduleName);

            // Create a unique filename based on timestamp and original name
            String timeStamp = String.valueOf(System.currentTimeMillis());
            String origName = sourceImage.getName();
            String extension = "";
            int dotIndex = origName.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = origName.substring(dotIndex);
            }

            String newFileName = timeStamp + "_" + sanitizeFileName(origName.substring(0,
                    dotIndex > 0 ? dotIndex : origName.length())) + extension;

            File destFile = new File(moduleImagesDir, newFileName);

            // Copy the file
            Files.copy(sourceImage.toPath(), destFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            return destFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Move an image from the old structure to the new module-specific directory
    public static File migrateImageToModuleDir(File sourceImage, String moduleName) {
        if (sourceImage == null || !sourceImage.exists()) {
            return null;
        }

        File newFile = copyImageToModuleDir(sourceImage, moduleName);

        // If it's in the old images directory, delete it after copying
        if (newFile != null && sourceImage.getParentFile().equals(getImagesDir())) {
            sourceImage.delete();
        }

        return newFile;
    }

    // Get relative path for storing in serialized objects
    public static String getRelativePath(File file) {
        if (file == null) {
            return null;
        }

        // First check if it's in a module directory
        File imagesDir = getImagesDir();
        String path = file.getAbsolutePath();
        String imagesDirPath = imagesDir.getAbsolutePath();

        if (path.startsWith(imagesDirPath)) {
            // Return relative path from images directory
            return path.substring(imagesDirPath.length() + 1);
        }

        return file.getName(); // Fallback to just the name
    }

    // Resolve a relative path to a File object
    public static File resolveRelativePath(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }

        // First check if it's a path with directories
        if (relativePath.contains("/") || relativePath.contains("\\")) {
            // It's a path relative to images directory
            return new File(getImagesDir(), relativePath);
        }

        // For backward compatibility, check if it's just a filename in the images directory
        File oldStyleFile = new File(getImagesDir(), relativePath);
        if (oldStyleFile.exists()) {
            return oldStyleFile;
        }

        // Not found
        return null;
    }

    // For compatibility with the existing code
    public static File copyImageToAppDir(File sourceImage) {
        return copyImageToModuleDir(sourceImage, "General");
    }
}