package com.rikuthin.graphics.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.rikuthin.graphics.GameFrame;
import com.rikuthin.loaders.ImageLoader;
import com.rikuthin.services.audio.AudioPlaybackService;
import static com.rikuthin.utility.ButtonUtils.createButtonWithText;
import com.rikuthin.utility.FontUtils;

/**
 * Main menu screen of the game. Provides options to start a new game, view
 * instructions, adjust settings, see high scores, and quit the game.
 */
public class MainMenuScreen extends Screen {

    // ----- INSTANCE VARIABLES -----
    /**
     * The {@code JLabel} displaying the main title of the game. It is placed in
     * the {@link #titlePanel} and styled using {@link FontUtils#TITLE_FONT}.
     */
    private final JLabel titleLabel;

    /**
     * The {@code JPanel} organized with a {@code BoxLayout} (Y_AXIS) that holds
     * all the main menu option {@code JButton}s.
     */
    private final JPanel buttonPanel;

    /**
     * A wrapper {@code JPanel} using {@code FlowLayout} to ensure the
     * {@link #buttonPanel} is horizontally centered within the screen's layout.
     */
    private final JPanel centreWrapper;

    /**
     * The {@code JPanel} responsible for holding the {@link #titleLabel} and
     * managing the spacing/positioning of the title at the top of the screen.
     */
    private final JPanel titlePanel;

    /**
     * The file path {@code String} pointing to the image resource used for the
     * main menu background.
     */
    private final String backgroundImageFilepath;

    /**
     * A transient {@code BufferedImage} object holding the loaded background
     * image data. It is marked {@code transient} because it is a graphics
     * resource that shouldn't be serialized.
     */
    private final transient BufferedImage backgroundImage;

    // ----- CONSTRUCTORS -----
    /**
     * Constructs the main menu screen panel with buttons for starting the game,
     * viewing how to play, settings, high scores, and quitting.
     *
     * @param gameFrame The parent {@link GameFrame} to which this screen
     * belongs.
     */
    public MainMenuScreen(GameFrame gameFrame) {
        super(gameFrame);
        setBackground(new Color(87, 73, 100));
        setLayout(new BorderLayout());

        backgroundImageFilepath = "/images/backgrounds/main-menu.png";
        backgroundImage = ImageLoader.loadBufferedImage(backgroundImageFilepath);

        // ----- Title Section (Centered at the top) -----
        titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);

        titleLabel = new JLabel(gameFrame.getTitle(), SwingConstants.CENTER);
        titleLabel.setFont(FontUtils.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);

        titlePanel.add(Box.createVerticalStrut(200));  // Add space above the title
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(80));  // Add space below the title

        add(titlePanel, BorderLayout.NORTH);

        // ----- Button Section -----
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        buttonPanel.add(Box.createVerticalStrut(100));  // Add space above buttons

        // Button labels and their corresponding actions
        final String[] labels = {"START GAME", "HIGHSCORES", "HOW TO PLAY", "SETTINGS", "EXIT GAME"};
        final ActionListener[] actions = {this::onStartGame, this::onHighscores, this::onHowToPlay, this::onSettings, this::onExitGame};

        // Create and add buttons
        for (int i = 0; i < labels.length; i++) {
            final boolean enabled = i == 0 || i == 4; // Only enable "START GAME" and "QUIT GAME" for now
            JButton button = createButtonWithText(labels[i], FontUtils.BUTTON_FONT, 200, 40, enabled, actions[i]);
            buttonPanel.add(button);
            buttonPanel.add(Box.createVerticalStrut(10));  // Space between buttons
        }

        // Center the button panel
        centreWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centreWrapper.setOpaque(false);
        centreWrapper.add(buttonPanel);

        add(centreWrapper, BorderLayout.CENTER);

        AudioPlaybackService.stopAll();
        AudioPlaybackService.playClip("goblinsDen", true);
    }

    /**
     * Updates the state of the main menu screen.
     * <p>
     * This method is **intentionally empty** (for now) as the main menu is
     * currently static. A future build however *may* contain animations.
     * <p>
     * Also, because {@link Screen} is {@code abstract} and does not define a
     * default for its own {@link Screen#update()} method, its subclasses are
     * *required* to define it in *some* manner.
     */
    @Override
    public void update() {
        // Not needed right now (no animations, button effects, etc.)
    }

    /**
     * Renders the visual elements of the main menu screen.
     * <p>
     * This method is responsible for drawing the background image to fill the
     * screen before the Swing components (buttons, labels) are drawn on top.
     *
     * @param g2d The graphics context used for rendering the background image.
     */
    @Override
    public void render(Graphics2D g2d) {
        if (backgroundImage != null && g2d != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            System.err.println(String.format("%s: Could not load background image <'%s'>.", this.getClass().getName(), backgroundImageFilepath));
        }
    }

    /**
     * Starts the game by switching to the gameplay screen.
     *
     * @param e The action event triggered by the button.
     */
    private void onStartGame(ActionEvent e) {
        gameFrame.setScreen(new GameplayScreen(gameFrame));
    }

    // TODO: Implement the functionality for high scores
    private void onHighscores(ActionEvent e) {
        System.out.println("High scores screen is not implemented yet.");
    }

    // TODO: Implement the functionality for viewing how to play
    private void onHowToPlay(ActionEvent e) {
        System.out.println("How to Play screen is not implemented yet.");
    }

    // TODO: Implement the functionality for the settings menu
    private void onSettings(ActionEvent e) {
        System.out.println("Settings menu is not implemented yet.");
    }

    /**
     * Exits the game (closes the application).
     *
     * @param e The action event triggered by the button.
     */
    private void onExitGame(ActionEvent e) {
        System.exit(0);
    }

}
