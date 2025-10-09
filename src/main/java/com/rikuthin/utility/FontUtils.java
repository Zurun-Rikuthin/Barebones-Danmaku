package com.rikuthin.utility;

import java.awt.Font;

/**
 * The {@code FontUtils} class provides centralized access to predefined
 * {@link Font} objects used by various user interface (UI) elements and
 * styling.
 * <p>
 * Note: The class cannot be instantiated. Likewise, all members within are both
 * {@code static} and {@code final}.
 */
public class FontUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private FontUtils() {
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
