package com.rikuthin.loaders;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * The {@code ImageLoader} class is a ctility class responsible for loading
 * image resources from files or the application's classpath.
 */
public class ImageLoader {

    // ----- CONSTRUCTORS -----
    /**
     * Private constructor to prevent instantiation.
     */
    private ImageLoader() {
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Creates a new {@link Image} object from a provided image file.
     *
     * @param fileName The path to the image.
     * @return The Image object.
     */
    public static Image loadImage(String fileName) {
        return new ImageIcon(fileName).getImage();
    }

    /**
     * Creates a new {@link BufferedImage} object from the provided file path.
     * <p>
     * Note: If the image is bundled in the JAR (or the classpath), use a URL.
     * Otherwise, load it as a regular file.
     *
     * @param filepath The file path (relative or absolute).
     * @return The loaded image object (if possible); {@code null} if a
     * null/empty filepath is provided or the image cannot be found.
     */
    public static BufferedImage loadBufferedImage(final String filepath) {
        if (filepath == null || filepath.isEmpty()) {
            return null;
        }
        BufferedImage bufferedImage = null;

        try {
            // Try loading as a resource (for classpath resources, e.g., inside JAR file)
            URL imageUrl = ImageLoader.class.getResource(filepath);
            if (imageUrl != null) {
                // If URL is found (i.e., resource exists in classpath), load it
                bufferedImage = ImageIO.read(imageUrl);
            } else {
                // If URL is not found, try loading as a normal file (e.g., file system)
                File file = new File(filepath);
                if (file.exists()) {
                    bufferedImage = ImageIO.read(file);
                }
            }
        } catch (IOException e) {
            System.err.println("Error opening file " + filepath + ": " + e.getMessage());
        }

        return bufferedImage;
    }
}
