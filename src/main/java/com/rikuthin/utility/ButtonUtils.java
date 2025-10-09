package com.rikuthin.utility;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;

import com.rikuthin.loaders.ImageLoader;

/**
 * Utility class for creating various buttons in the UI.
 */
public class ButtonUtils {

    private ButtonUtils() {
        // Private constructor to prevent instantiation.
    }

    /**
     * Creates a new {@link JButton} that's labelled with/displays an image/icon.
     *
     * @param imageFilepath The file path for the image used for the button's icon.
     * @param buttonWidth The width of the button in pixels.
     * @param buttonHeight The height of the button in pixels.
     * @param enabled {@code true} if the button is enabled by default, {@code false} otherwise.
     * @param actionListener The {@link ActionListener} to be added to the button.
     * @return The configured button.
     */
    public static JButton createButtonWithIcon(
            final String imageFilepath,
            final int buttonWidth,
            final int buttonHeight,
            boolean enabled,
            final ActionListener actionListener
        ) {
        JButton button = createGenericButton(new JButton(), buttonWidth, buttonHeight, enabled, actionListener);
        try {
            BufferedImage image = ImageLoader.loadBufferedImage(imageFilepath);
            button.setIcon(new ImageIcon (image));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return button;
    }

    /**
     * Creates a new {@link JButton} that's labelled with the specified text and font.
     *
     * @param text The text displayed on the button.
     * @param font The {@link Font} to be used on the button.
     * @param buttonWidth The width of the button in pixels.
     * @param buttonHeight The height of the button in pixels.
     * @param enabled {@code true} if the button is enabled by default, {@code false} otherwise.
     * @param actionListener The {@link ActionListener} to be added to the button.
     * @return The configured button.
     */
    public static JButton createButtonWithText(final String text, final Font font, final int buttonWidth, final int buttonHeight, boolean enabled, final ActionListener actionListener) {
        if (font == null) {
            throw new IllegalArgumentException("Font must not be null");
        }

        JButton button = createGenericButton(new JButton(text), buttonWidth, buttonHeight, enabled, actionListener);
        button.setFont(font);

        return button;
    }

    /**
     * Creates a new {@link JToggleButton} that's labelled with the specified text.
     *
     * @param text The text displayed on the button.
     * @param font The {@link Font} to be used on the button.
     * @param buttonWidth The width of the button in pixels.
     * @param buttonHeight The height of the button in pixels.
     * @param enabled {@code true} if the button is enabled by default, {@code false} otherwise.
     * @param actionListener The {@link ActionListener} to be added to the button.
     * @return The configured button.
     */
    public static JToggleButton createToggleButtonWithText(final String text, final Font font, final int buttonWidth, final int buttonHeight, boolean enabled, final ActionListener actionListener) {
        if (font == null) {
            throw new IllegalArgumentException("Font must not be null");
        }

        JToggleButton button = createGenericButton(new JToggleButton(text), buttonWidth, buttonHeight, enabled, actionListener);
        button.setFont(font);

        return button;
    }

    /**
     * Creates a new generic button with the specified properties.
     * <p>
     * Used internally for button creation.
     *
     * @param button The type of button to be created ({@link JButton} or
     * {@link JToggleButton}).
     * @param buttonWidth The width of the button in pixels.
     * @param buttonHeight The height of the button in pixles.
     * @param enabled {@code true} if the button is enabled by default, {@code false} otherwise.
     * @param actionListener The {@link ActionListener} to be added to the button.
     * @return The configured button.
     */
    private static <T extends AbstractButton> T createGenericButton(T button, int buttonWidth, int buttonHeight, boolean enabled, ActionListener actionListener) {
        if (actionListener == null) {
            throw new IllegalArgumentException("Action listener must not be null");
        }

        // Validate dimensions
        if (buttonWidth <= 0 || buttonHeight <= 0) {
            throw new IllegalArgumentException("Button width and height must be positive values.");
        }

        Dimension buttonSize = new Dimension(buttonWidth, buttonHeight);

        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);
        button.setEnabled(enabled);
        button.addActionListener(actionListener);

        return button;
    }

}
