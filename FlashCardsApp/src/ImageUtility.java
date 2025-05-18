// ImageUtility.java
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class ImageUtility {

    /**
     * Resizes an ImageIcon to the specified width while maintaining aspect ratio
     *
     * @param icon The ImageIcon to resize
     * @param maxWidth The maximum width
     * @return A resized ImageIcon
     */
    public static ImageIcon resizeImageIcon(ImageIcon icon, int maxWidth) {
        if (icon == null) {
            return null;
        }

        if (icon.getIconWidth() <= maxWidth) {
            return icon;
        }

        double ratio = (double) maxWidth / icon.getIconWidth();
        int newHeight = (int) (icon.getIconHeight() * ratio);

        Image img = icon.getImage().getScaledInstance(maxWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    /**
     * Resizes an ImageIcon to fit within the specified dimensions while maintaining aspect ratio
     *
     * @param icon The ImageIcon to resize
     * @param maxWidth The maximum width
     * @param maxHeight The maximum height
     * @return A resized ImageIcon
     */
    public static ImageIcon resizeImageIconWithConstraints(ImageIcon icon, int maxWidth, int maxHeight) {
        if (icon == null) {
            return null;
        }

        int originalWidth = icon.getIconWidth();
        int originalHeight = icon.getIconHeight();

        if (originalWidth <= maxWidth && originalHeight <= maxHeight) {
            return icon;
        }

        double widthRatio = (double) maxWidth / originalWidth;
        double heightRatio = (double) maxHeight / originalHeight;
        double ratio = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);

        Image img = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    /**
     * Creates a high quality thumbnail of an ImageIcon
     *
     * @param icon The original ImageIcon
     * @param width The thumbnail width
     * @param height The thumbnail height
     * @return A thumbnail ImageIcon
     */
    public static ImageIcon createThumbnail(ImageIcon icon, int width, int height) {
        if (icon == null) {
            return null;
        }

        Image img = icon.getImage();

        // Create a buffered image with transparency
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Calculate the scaled dimensions while preserving aspect ratio
        int originalWidth = icon.getIconWidth();
        int originalHeight = icon.getIconHeight();

        double widthRatio = (double) width / originalWidth;
        double heightRatio = (double) height / originalHeight;
        double ratio = Math.min(widthRatio, heightRatio);

        int scaledWidth = (int) (originalWidth * ratio);
        int scaledHeight = (int) (originalHeight * ratio);

        // Center the image
        int x = (width - scaledWidth) / 2;
        int y = (height - scaledHeight) / 2;

        g2d.drawImage(img, x, y, scaledWidth, scaledHeight, null);
        g2d.dispose();

        return new ImageIcon(bufferedImage);
    }

    // Add this method to ImageUtility.java

    /**
     * Resize an image optimized for readability of text/equations
     *
     * @param icon The original ImageIcon
     * @param containerWidth The width of the container
     * @return A resized ImageIcon optimized for text readability
     */
    public static ImageIcon resizeForReadability(ImageIcon icon, int containerWidth) {
        if (icon == null) {
            return null;
        }

        int originalWidth = icon.getIconWidth();
        int originalHeight = icon.getIconHeight();

        // Use a large percentage of available width
        int targetWidth = Math.min(originalWidth, (int)(containerWidth * 0.9));

        // Ensure minimum size for readability (at least 500px or available width)
        targetWidth = Math.max(targetWidth, Math.min(500, containerWidth));

        // Calculate height maintaining aspect ratio
        int targetHeight = (int)(originalHeight * ((double)targetWidth / originalWidth));

        // Create a high-quality scaled image
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();

        // Use high-quality rendering hints
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(icon.getImage(), 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        return new ImageIcon(resizedImage);
    }
}