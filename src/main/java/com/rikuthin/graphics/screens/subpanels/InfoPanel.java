package com.rikuthin.graphics.screens.subpanels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import static javax.swing.Box.createHorizontalStrut;
import static javax.swing.Box.createVerticalStrut;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.rikuthin.services.core.GameManager;
import com.rikuthin.utility.ButtonUtils;
import com.rikuthin.utility.FontUtils;

/**
 * The InfoPanel displays game information (e.g., player health, elapsed time,
 * and score) pause button for the game.
 * <p>
 * This panel is displayed at the right of the gameplay screen.
 * </p>
 */
public final class InfoPanel extends Subpanel {

    // ----- INSTANCE VARIABLES -----
    /**
     * The {@link JButton} used to pause and resume the game.
     * <p>
     * It is configured to call {@link GameManager#onPause(ActionEvent)}.
     */
    private final JButton pauseButton;

    /**
     * The {@link JLabel} that displays the current wave number the player is
     * facing.
     */
    private final JLabel waveCounterLabel;

    /**
     * The {@link JLabel} that displays the formatted elapsed time since the
     * game started (e.g., HH:MM.SS).
     */
    private final JLabel gameplayTimerLabel;

    /**
     * The {@link JLabel} that displays the current high score achieved by the
     * player.
     */
    private final JLabel highscoreLabel;

    /**
     * The {@link JLabel} that displays the player's current score in the
     * ongoing game session.
     */
    private final JLabel scoreLabel;

    /**
     * A {@link JPanel} dedicated to rendering and displaying the player's
     * current health points (HP).
     */
    private final JPanel hpCounterPanel;

    /**
     * A {@link JPanel} dedicated to rendering and displaying the player's
     * remaining number of bombs.
     */
    private final JPanel bombCounterPanel;

    /**
     * The multi-line {@link JTextArea} used to display static instructional
     * information such as game controls and the primary game goal.
     */
    private final JTextArea infoTextArea;

    // ----- CONSTRUCTORS -----
    /**
     * Constructs the InfoPanel.
     * 
     * @param width width of the panel in pixels
     * @param height Height of the panel in pixels
     * @param backgroundImageFilepath file path of the backgroun image
     */
    public InfoPanel(final int width, final int height, final String backgroundImageFilepath) {
        super(width, height, backgroundImageFilepath);

        // Background colour used as a backup in case the image deosn't load.
        setBackground(new Color(87, 73, 100));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setAlignmentY(CENTER_ALIGNMENT);

        // ----- Initialise elements -----
        JPanel topRow = new JPanel();
        topRow.setLayout(new BoxLayout(topRow, BoxLayout.X_AXIS));

        JPanel topRowLabels = new JPanel();
        topRowLabels.setLayout(new BoxLayout(topRowLabels, BoxLayout.Y_AXIS));

        waveCounterLabel = new JLabel("Wave <lorem ipsum>");
        waveCounterLabel.setFont(FontUtils.BODY_FONT);

        gameplayTimerLabel = new JLabel("HH:MM.SS");
        gameplayTimerLabel.setFont(FontUtils.BODY_FONT);

        pauseButton = ButtonUtils.createButtonWithIcon("/images/icons/pause-button.png", 64, 64, false, GameManager.getInstance()::onPause);

        Dimension scoreLabelSize = new Dimension(284, 80);
        highscoreLabel = new JLabel("Highscore: <lorem ipsum>");
        highscoreLabel.setFont(FontUtils.BODY_FONT);
        highscoreLabel.setPreferredSize(scoreLabelSize);
        highscoreLabel.setMinimumSize(scoreLabelSize);
        highscoreLabel.setMaximumSize(scoreLabelSize);
        highscoreLabel.setBackground(Color.WHITE);

        scoreLabel = new JLabel("Score: <lorem ipsum>");
        scoreLabel.setFont(FontUtils.BODY_FONT);
        scoreLabel.setPreferredSize(scoreLabelSize);
        scoreLabel.setMinimumSize(scoreLabelSize);
        scoreLabel.setMaximumSize(scoreLabelSize);
        scoreLabel.setBackground(Color.WHITE);

        Dimension counterPanelSize = new Dimension(284, 80);
        hpCounterPanel = new JPanel(true);
        hpCounterPanel.setPreferredSize(counterPanelSize);
        hpCounterPanel.setMinimumSize(counterPanelSize);
        hpCounterPanel.setMaximumSize(counterPanelSize);
        hpCounterPanel.setBackground(Color.WHITE);

        bombCounterPanel = new JPanel(true);
        bombCounterPanel.setPreferredSize(counterPanelSize);
        bombCounterPanel.setMinimumSize(counterPanelSize);
        bombCounterPanel.setMaximumSize(counterPanelSize);
        bombCounterPanel.setBackground(Color.WHITE);

        StringBuilder sb = new StringBuilder("Controls:");
        sb.append(String.format("%n    - WSAD or arrow keys to move"));
        sb.append(String.format("%n    - SPACE to use a bomb (destroys all enemy bullets on screen."));
        sb.append(String.format("%n%nGoal:"));
        sb.append(String.format("%n    - Shoot enemies to gain points."));
        sb.append(String.format("%n    - Don't get hit by enemy bullets."));
        infoTextArea = new JTextArea(sb.toString());
        infoTextArea.setPreferredSize(new Dimension(getWidth() - 20, 300));
        infoTextArea.setBackground(Color.WHITE);
        infoTextArea.setForeground(Color.BLACK);
        infoTextArea.setEditable(false);
        infoTextArea.setFont(FontUtils.BODY_FONT);

        // ----- Add to topRow -----
        topRowLabels.add(waveCounterLabel);
        topRowLabels.add(createVerticalStrut(40));
        topRowLabels.add(gameplayTimerLabel);
        topRow.add(topRowLabels);
        topRow.add(createHorizontalStrut(40));
        topRow.add(pauseButton);

        // ----- Add to InfoPanel -----
        add(createVerticalStrut(20));
        add(topRow);
        add(createVerticalStrut(200));
        add(highscoreLabel);
        add(createVerticalStrut(20));
        add(scoreLabel);
        add(createVerticalStrut(20));
        validate();
        add(hpCounterPanel);
        add(createVerticalStrut(20));
        add(bombCounterPanel);
        add(createVerticalStrut(100));
        add(infoTextArea);
    }

    //     score = 0;
    //     elapsedSeconds = 0;
    //     // Create the font used for the button and labels.
    //     Font buttonFont = new Font(GameFrame.BODY_TYPEFACE, Font.PLAIN, 16);
    //     // Create the pause button.
    //     pauseMenuButton = createButton(
    //             "PAUSE", buttonFont, 100, 40, true,
    //             GameManager::onPause
    //     );
    //     // Create the score and timer labels.
    //     scoreLabel = createStatusLabel();
    //     timerLabel = createStatusLabel();
    //     // Arrange components with horizontal spacing.
    //     add(Box.createHorizontalStrut(20));
    //     add(pauseMenuButton);
    //     add(Box.createHorizontalGlue());
    //     add(scoreLabel);
    //     add(Box.createHorizontalGlue());
    //     add(timerLabel);
    //     add(Box.createHorizontalStrut(20));
    //     // Initialise the score display.
    //     updateScoreDisplay(0);
    // }
    // /**
    //  * Updates the displayed score
    //  *
    //  * @param score The new score.
    //  */
    // public final void updateScoreDisplay(final int score) {
    //     scoreLabel.setText(String.format("Score: %d", score));
    // }
    // /**
    //  * Updates the timer label with a formatted elapsed time string.
    //  *
    //  * @param elapsedSeconds The elapsed time in seconds.
    //  */
    // public void updateTimerDisplay(final int elapsedSeconds) {
    //     int minutes = elapsedSeconds / 60;
    //     int seconds = elapsedSeconds % 60;
    //     timerLabel.setText(String.format("Elapsed Time: %d:%02d", minutes, seconds));
    // }
    // /**
    //  * Creates a JLabel for displaying status information (score or timer).
    //  *
    //  * Buggy at the moment
    //  *
    //  * @return A configured JLabel.
    //  */
    // private JLabel createStatusLabel() {
    //     JLabel label = new JLabel();
    //     label.setFont(new Font(GameFrame.BODY_TYPEFACE, Font.BOLD, 16));
    //     label.setForeground(Color.WHITE);
    //     // Will try to fix for the next assignment
    //     // label.setForeground(new Color(70, 0, 50));
    //     // label.setBorder(new RoundedBorder(Color.BLACK, Color.WHITE, 10));
    //     return label;
    // }
}
