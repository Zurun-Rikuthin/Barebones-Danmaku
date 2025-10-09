package com.rikuthin.services.core;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.Timer;

import static com.rikuthin.App.FRAME_RATE_MS;
import com.rikuthin.data.entities.BulletRepository;
import com.rikuthin.data.entities.EnemyRepository;
import com.rikuthin.entities.Player;
import com.rikuthin.entities.bullets.BulletSpawner;
import com.rikuthin.graphics.GameFrame;
import com.rikuthin.graphics.screens.subpanels.GamePanel;
import com.rikuthin.graphics.screens.subpanels.InfoPanel;
import com.rikuthin.interfaces.Updateable;
import com.rikuthin.services.core.GameStateManager.GameState;
import com.rikuthin.services.entity.BulletManager;
import com.rikuthin.services.entity.EnemyManager;
import com.rikuthin.services.ui.GameUIManager;

/**
 * This class coordinates the continuous game execution cycle (i.e., update()
 * and render()), delegating its execution to all the specialized services.
 */
public final class GameLoopCordinator implements Updateable {

    // ----- STATIC VARIABLES -----
    private static GameLoopCordinator INSTANCE;

    // ----- DEPENDENCIES & INSTANCE VARIABLES -----
    private final GameStateManager stateManager;
    private final EnemyManager enemyManager;
    private final BulletManager bulletManager;
    private final GameUIManager uiManager;

    private Timer gameplayTimer;
    private Player player; // Direct reference for game logic

    // ----- CONSTRUCTORS -----
    /**
     * Private constructor to prevent direct instantiation. Singleton pattern.
     */
    private GameLoopCordinator() {
        // Instantiate specialized managers
        this.stateManager = new GameStateManager();
        this.enemyManager = new EnemyManager();
        this.bulletManager = new BulletManager();
        this.uiManager = new GameUIManager(this.stateManager); // Inject GameStateManager dependency
    }

    // ----- SINGLETON GETTER -----
    public static GameLoopCordinator getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new GameLoopCordinator();
        }
        return INSTANCE;
    }

    // ----- GETTERS (Delegated) -----
    public Player getPlayer() {
        stateManager.ensureInitialized("getPlayer");
        return player;
    }

    public EnemyManager getEnemyManager() {
        stateManager.ensureRunning("getEnemyManager");
        return enemyManager;
    }

    public BulletManager getBulletManager() {
        stateManager.ensureRunning("getBulletManager");
        return bulletManager;
    }

    public GamePanel getGamePanel() {
        // Delegate access to UI reference
        return uiManager.getGamePanel();
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Initializes the GameLoopManager for a new game. This method delegates
     * setup to sub-managers and starts the game loop.
     *
     * @param gamePanel The panel where the game is displayed.
     * @param infoPanel The panel where the game information is displayed.
     */
    public final void init(final GamePanel gamePanel, final InfoPanel infoPanel) {
        if (stateManager.isRunning() || stateManager.isPaused() || stateManager.isInitializing()) {
            System.err.println("GameLoopManager: Cannot initialize unless the game is cleared.");
            return;
        }

        if (gamePanel == null || infoPanel == null) {
            throw new IllegalStateException("GamePanel and InfoPanel must be provided.");
        }

        stateManager.startInitialization();
        uiManager.init(gamePanel, infoPanel);

        // Core Initialization
        initialisePlayer(gamePanel); // Player creation logic is still here as it involves game loop entities
        enemyManager.init();
        bulletManager.init();

        // Start the game loop and state
        startGameplayTimer();
        stateManager.startRunning();
    }

    /**
     * Clears all game data (used when transitioning back to the main menu).
     */
    public final void clear() {
        if (stateManager.getCurrentState() != GameState.NOT_INITIALIZED) {
            stopGameplayTimer();
            stateManager.stopAndClear();
            player = null;

            // Managers handling their own repositories/cleanup
            EnemyRepository.clear();
            BulletRepository.clear();

            // UI references are set to null inside GameUIManager's init/clear logic
        }
    }

    /**
     * Handles the pause button action by delegating to the UI manager.
     */
    public void onPause(ActionEvent e) {
        uiManager.onPause(e);
    }

    // ----- OVERRIDDEN METHODS (The Game Loop) -----
    /**
     * The main update method called by the gameplay timer. Only runs if the
     * game state is RUNNING.
     */
    @Override
    public void update() {
        if (!stateManager.isRunning()) {
            // Check state manager state and handle pause/stop logic centrally
            if (stateManager.isPaused()) {
                // Delegation: Let the UI manager handle the pause display logic
                // The timer should already be stopped by setGamePaused(true), but we check state here.
            }
            return;
        }

        // Run updates only if RUNNING
        if (player != null) {
            player.update();
        }
        enemyManager.update();
        bulletManager.update();
    }

    // ----- HELPER METHODS (Player Creation Logic Remains Here) -----
    /**
     * Initialises the {@link Player} character. This logic remains in the core
     * manager as it sets up the main entity.
     */
    private void initialisePlayer(GamePanel gamePanel) {
        // Existing Player Initialization logic moved here
        HashSet<String> playerAnimationKeys = Stream.of(
                "player-death",
                "player-idle",
                "player-walk-up-left",
                "player-walk-up-right",
                "player-walk-up"
        ).collect(Collectors.toCollection(HashSet::new));

        player = new Player.PlayerBuilder(gamePanel)
                .invisibility(false)
                .collidability(true)
                .animationKeys(playerAnimationKeys)
                .currentAnimationKey("player-idle")
                .maxHitPoints(20)
                .currentHitPoints(20)
                .build();

        // Trying to do this dynamically wasn't working, so hard-coding for now
        int x = (GameFrame.FRAME_HEIGHT / 2) - (player.getSpriteWidth() / 2);
        int y = GameFrame.FRAME_HEIGHT - (2 * player.getSpriteHeight());
        player.setPosition(new Point(x, y));

        HashSet<String> playerBulletAnimationKeys = Stream.of("player-bullet").collect(Collectors.toCollection(HashSet::new));

        BulletSpawner spawner = new BulletSpawner.BulletSpawnerBuilder(gamePanel, player)
                .bulletDamage(1)
                .bulletVelocityY(20)
                .bulletAnimationKeys(playerBulletAnimationKeys)
                .currentBulletAnimationKey("player-bullet")
                .build();

        player.setBulletSpawner(spawner);
    }

    /**
     * Starts the current gameplay timer. Uses the state manager to ensure the
     * timer only runs when appropriate.
     */
    private void startGameplayTimer() {
        // Timer delay computed using FRAME_RATE_MS from App.java
        int delayMs = (int) FRAME_RATE_MS;

        if (gameplayTimer == null) {
            // Note: The timer action now calls the GameLoopManager's update method.
            gameplayTimer = new Timer(delayMs, e -> {
                // This checks the state *again* just before running the update, 
                // in case the state changed right after the timer started.
                if (stateManager.isRunning()) {
                    update();
                }
            });
        }
        gameplayTimer.start();
    }

    /**
     * Stops the gameplay timer.
     */
    private void stopGameplayTimer() {
        if (gameplayTimer != null) {
            gameplayTimer.stop();
        }
    }
}
