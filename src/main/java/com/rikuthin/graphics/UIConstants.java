package com.rikuthin.graphics;

import java.awt.Font;

import javax.swing.JButton;

/**
 * A utility class providing centralized access to various constants used by
 * user interface (UI) elements and styling throughout the game.
 * <p>
 * Note: The class cannot be instantiated, nor its members modified as **all**
 * are both {@code static} and {@code final}.
 */
public class UIConstants {

    /**
     * Private constructor to prevent instantiation.
     */
    private UIConstants() {
    }

    // TODO: Actually pick out proper fonts instead of pseudo-random placeholders.
    /**
     * The {@link Font} used for the text of the game's title on the title
     * screen.
     */
    public static final Font TITLE_FONT = new Font("Garamond", Font.BOLD, 48);
    /**
     * The {@link Font} used for general purpose text throughout the game.
     */
    public static final Font BODY_FONT = new Font("Garamond", Font.PLAIN, 20);
    /**
     * The {@link Font} used for the text on various {@link JButton} objects.
     */
    public static final Font BUTTON_FONT = new Font("Garamond", Font.BOLD, 20);
}
