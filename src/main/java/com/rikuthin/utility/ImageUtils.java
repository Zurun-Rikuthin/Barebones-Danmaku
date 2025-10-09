package com.rikuthin.utility;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * The {@code ImageUtils} class provides a collection of {@code static} utility methods
 * for the manipulation and processing of {@link BufferedImage} objects, such as
 * copying and scaling.
 * <p>
 * This class does not handle image loading (I/O) or resource management, nor
 * can it be instantiated.
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
     * This method is useful when an image needs to be modified (e.g., rotated
     * or colored) without altering the original source image.
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

    /**
     * Creates a new BufferedImage that is a scaled version of the original,
     * with the original image drawn onto it, scaled by the provided factor.
     * <p>
     * The resulting dimensions (width and height) are rounded to the nearest
     * integer.
     *
     * @param originalImage The BufferedImage to scale.
     * @param scaleX The factor by which to scale the image's width.
     * @param scaleY The factor by which to scale the image's height. Use
     * absolute values.
     * @return A new BufferedImage that is scaled by the provided factors, or
     * null if the original image is null.
     */
    public static BufferedImage scaleBufferedImageSize(final BufferedImage originalImage, double scaleX, double scaleY) {
        if (originalImage == null) {
            return null;
        }

        scaleX = Math.abs(scaleX);
        scaleY = Math.abs(scaleY);

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int scaledWidth = (int) Math.round(originalWidth * scaleX); // Use Math.round for better scaling
        int scaledHeight = (int) Math.round(originalHeight * scaleY); // Use Math.round for better scaling

        BufferedImage scaled = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaled.createGraphics();
        g2d.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        return scaled;
    }

    /**
     * Creates a new BufferedImage that is a uniformly scaled version of the
     * original, with the original image drawn onto it, scaled by the provided
     * factor.
     * <p>
     * The resulting dimensions (width and height) are rounded to the nearest
     * integer.
     *
     * @param originalImage The BufferedImage to scale.
     * @param scaleFactor The factor by which to scale both the width and
     * height. Use absolute value.
     * @return A new BufferedImage that is scaled by the provided factor, or
     * null if the original image is null.
     */
    public static BufferedImage scaleBufferedImageSize(final BufferedImage originalImage, double scaleFactor) {
        return scaleBufferedImageSize(originalImage, scaleFactor, scaleFactor);
    }
}
