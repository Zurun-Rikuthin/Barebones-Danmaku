package com.rikuthin.graphics.screens;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import com.rikuthin.entities.Player;
import com.rikuthin.graphics.GameFrame;
import com.rikuthin.graphics.screens.subpanels.GamePanel;
import com.rikuthin.graphics.screens.subpanels.InfoPanel;
import com.rikuthin.services.audio.AudioPlaybackService;
import com.rikuthin.services.core.GameManager;

/**
 * The main gameplay screen where the game logic and rendering occur. Handles
 * player input and updates the game state accordingly.
 */
public final class GameplayScreen extends Screen {

    // ----- STATIC VARIABLES -----
    /**
     * The default player movement speed in pixels/frame
     */
    private static final int BASE_SPEED = 5;

    // INSTANCE VARIABLES -----
    /**
     * Reference to the {@link GameManager}
     */
    private final transient GameManager gameManager;
    /**
     * Reference to the {@link GamePanel}
     */
    private final GamePanel gamePanel;
    /**
     * Reference to the {@link InfoPanel}
     */
    private final InfoPanel infoPanel;
    /**
     * A {@link Map} used to track the current state of keys pressed by the
     * user.
     * <p>
     * The key is the {@code Integer} key code (from {@link KeyEvent}), and the
     * value is a {@code Boolean} indicating if the key is currently held down
     * ({@code true}) or released ({@code false}).
     */
    private final Map<Integer, Boolean> keyStates;

    // ----- CONSTRUCTORS -----
    /**
     * Initializes the gameplay screen, setting up the game panels and input
     * listeners.
     *
     * @param gameFrame The parent frame containing this screen.
     */
    public GameplayScreen(GameFrame gameFrame) {
        super(gameFrame);
        setLayout(new BorderLayout());

        int frameWidth = gameFrame.getWidth();
        int frameHeight = gameFrame.getHeight();
        gamePanel = new GamePanel(frameHeight, frameHeight, "/images/backgrounds/game-panel.png");
        infoPanel = new InfoPanel(frameWidth - frameHeight, frameHeight, "/images/backgrounds/info-panel.png");

        add(gamePanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.LINE_END);

        gameManager = GameManager.getInstance();
        gameManager.init(gamePanel, infoPanel);

        keyStates = new HashMap<>();
        addKeyListener(createKeyListener());

        AudioPlaybackService.stopAll();
        AudioPlaybackService.playClip("goblinsDance", true);
    }

    // ----- GETTERS -----
    /**
     * Retrieves the {@link GamePanel}.
     *
     * @return The main game panel.
     */
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    /**
     * Retrieves the {@link InfoPanel}.
     *
     * @return The information panel.
     */
    public InfoPanel getInfoPanel() {
        return infoPanel;
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Updates the game state if it is running.
     */
    @Override
    public void update() {
        if (gameManager.isRunning()) {
            gameManager.update();
        }
    }

    /**
     * Renders the game screen.
     *
     * @param g2d The graphics context used for rendering.
     */
    @Override
    public void render(Graphics2D g2d) {
        if (gameManager.isRunning()) {
            gamePanel.safeRender(g2d);
            infoPanel.safeRender(g2d);
        }
    }

    // ----- HELPER METHODS -----
    /**
     * Creates the key listener that handles player input.
     *
     * @return A KeyAdapter instance that listens for key events.
     */
    private KeyAdapter createKeyListener() {
        return new KeyAdapter() {
            /**
             * Updates the player's movement based on the current key states.
             */
            private void updateMovement() {
                Player player = gameManager.getPlayer();
                int speed = keyStates.getOrDefault(KeyEvent.VK_SHIFT, false) ? BASE_SPEED / 2 : BASE_SPEED;
                int velocityX = 0;
                int velocityY = 0;
                String animationKey = "player-idle";

                if (keyStates.getOrDefault(KeyEvent.VK_W, false) || keyStates.getOrDefault(KeyEvent.VK_UP, false)) {
                    velocityY = speed;
                    animationKey = "player-walk-up";
                }
                if (keyStates.getOrDefault(KeyEvent.VK_S, false) || keyStates.getOrDefault(KeyEvent.VK_DOWN, false)) {
                    velocityY = -speed;
                    animationKey = "player-walk-up";
                }
                if (keyStates.getOrDefault(KeyEvent.VK_A, false) || keyStates.getOrDefault(KeyEvent.VK_LEFT, false)) {
                    velocityX = -speed;
                    animationKey = "player-walk-up-left";
                }
                if (keyStates.getOrDefault(KeyEvent.VK_D, false) || keyStates.getOrDefault(KeyEvent.VK_RIGHT, false)) {
                    velocityX = speed;
                    animationKey = "player-walk-up-right";
                }

                player.setVelocityX(velocityX);
                player.setVelocityY(velocityY);
                player.setAnimation((velocityX == 0 && velocityY == 0) ? "player-idle" : animationKey);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                keyStates.put(e.getKeyCode(), true);
                updateMovement();

                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    gameManager.getPlayer().getBulletSpawner().setIsSpawning(true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keyStates.put(e.getKeyCode(), false);
                updateMovement();

                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    gameManager.getPlayer().getBulletSpawner().setIsSpawning(false);
                }
            }
        };
    }
}
