package com.rikuthin.utility;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * The {@code ImageUtils} class provides a collection of static utility methods 
 * for the manipulation and processing of {@link BufferedImage} objects, such as 
 * copying and scaling.
 * <p>
 * This class does not handle image loading (I/O) or resource management.
 */
public class ImageUtils {

    /**
     * Private constructor to prevent instantiation. This is a utility class
     * containing only static methods.
     */
    private ImageUtils() {
    }

    // ----- BUSINESS LOGIC METHODS -----
    
    /**
     * Creates a deep copy of the given {@link BufferedImage}.
     * <p>
     * This method is useful when an image needs to be modified (e.g., rotated or 
     * colored) without altering the original source image.
     *
     * @param source The source {@link BufferedImage} to copy. If {@code null}, 
     * returns {@code null}.
     * @return A new, independent {@link BufferedImage} instance with the same 
     * content, or {@code null} if the source is {@code null}.
     */
    public static BufferedImage copyImage(BufferedImage source) {
        if (source == null) {
            return null;
        }

        int imageWidth = source.getWidth();
        int imageHeight = source.getHeight();

        BufferedImage copy = new BufferedImage(
                imageWidth,
                imageHeight,
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2d = copy.createGraphics();

        // Copy the image content
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();

        return copy;
    }
}