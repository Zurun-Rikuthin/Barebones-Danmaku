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
import com.rikuthin.graphics.UIConstants;
import com.rikuthin.loaders.ImageLoader;
import com.rikuthin.managers.SoundManager;

import static com.rikuthin.utility.ButtonUtils.createButtonWithText;

/**
 * Main menu screen of the game. Provides options to start a new game, view
 * instructions, adjust settings, see high scores, and quit the game.
 */
public class MainMenuScreen extends Screen {

    private final JLabel titleLabel;
    private final JPanel buttonPanel;
    private final JPanel centreWrapper;
    private final JPanel titlePanel;
    private final String backgroundImageFilepath;
    private final transient BufferedImage backgroundImage;

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
        titleLabel.setFont(UIConstants.TITLE_FONT);
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
            JButton button = createButtonWithText(labels[i], UIConstants.BUTTON_FONT, 200, 40, enabled, actions[i]);
            buttonPanel.add(button);
            buttonPanel.add(Box.createVerticalStrut(10));  // Space between buttons
        }

        // Center the button panel
        centreWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centreWrapper.setOpaque(false);
        centreWrapper.add(buttonPanel);

        add(centreWrapper, BorderLayout.CENTER);

        SoundManager soundManager = SoundManager.getInstance();
        soundManager.stopAll();
        soundManager.playClip("goblinsDen", true);
    }

    @Override
    public void update() {
        // Not needed right now (no animations, button effects, etc.)
    }

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
