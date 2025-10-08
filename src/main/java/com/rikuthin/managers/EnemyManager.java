package com.rikuthin.managers;

import java.awt.Point;
import java.lang.StackWalker.StackFrame;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

import com.rikuthin.data.EnemyRepository;
import com.rikuthin.entities.Player;
import com.rikuthin.entities.enemies.BlueMage;
import com.rikuthin.entities.enemies.Enemy;
import com.rikuthin.entities.enemies.MagentaMage;
import com.rikuthin.entities.enemies.RedMage;
import com.rikuthin.graphics.GameFrame;
import com.rikuthin.graphics.screens.subpanels.GamePanel;
import com.rikuthin.interfaces.Updateable;

/**
 * The {@code EnemyManager} is the primary service responsible for the **active
 * lifecycle** of all enemies in the game.
 * <p>
 * This manager dictates enemy behavior by handling spawning (creation rules),
 * entity updates (calling the {@link Enemy#update()} method on all active
 * enemies), and cleanup (defining the criteria for enemy removal), delegating
 * storage and mutation to the {@link EnemyRepository}.
 */
public class EnemyManager implements Updateable {

    /**
     * The maximum number of enemies that can exist simultaneously.
     */
    private static final int MAX_ENEMY_COUNT = 10;
    /**
     * The cooldown duration (in milliseconds) before another {@link Enemy} can
     * be created.
     */
    private static final long ENEMY_CREATION_COOLDOWN_MS = 5000; // 5 seconds

    // ----- INSTANCE VARIABLES -----
    /**
     * The repository instance used to access and mutate the collection of
     * active enemies.
     */
    private final EnemyRepository repository;
    /**
     * Random generator used by various methods.
     */
    private Random random;
    /**
     * Flag indicating whether the enemy creation cooldown timer is currently
     * active.
     */
    private boolean isOnCreationCooldown;
    /**
     * The elapsed time (in milliseconds) since the last enemy was created.
     */
    private long elapsedCreationCooldownMs;
    /**
     * The system time (in milliseconds) when the manager was last updated. Used
     * to calculate delta time for cooldown tracking.
     */
    private long lastUpdateTime;

    // ----- CONSTRUCTORS -----
    /**
     * Constructs the {@code EnemyManager} and retrieves the singleton instance
     * of the {@link EnemyRepository} to establish its dependency. Initializes
     * the manager for game start.
     */
    public EnemyManager() {
        repository = EnemyRepository.getInstance();
        init();
    }

    // ----- GETTERS -----
    /**
     * Checks if the enemy creation cooldown is currently active.
     *
     * @return {@code true} if the cooldown is active, otherwise {@code false}.
     */
    public boolean isOnCreationCooldown() {
        return isOnCreationCooldown;
    }

    /**
     * Gets the required cooldown time in milliseconds between enemy creations.
     *
     * @return The cooldown time in millisecods.
     */
    public long getEnemyCreationCooldownMs() {
        return ENEMY_CREATION_COOLDOWN_MS;
    }

    /**
     * Returns how many milliseconds of the enemy creation cooldown have passed
     * since the last enemy was created.
     *
     * @return The elasped time in milliseconds.
     */
    public long getElapsedCreationCooldownMs() {
        return elapsedCreationCooldownMs;
    }

    /**
     * Returns the system timestamp (in milliseconds) of the manager's last
     * {@link #update()} call.
     *
     * @return The last update time.
     */
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Initializes the EnemyManager for a new game.
     * <p>
     * This method resets the random generator, clears the repository of old
     * enemies, and resets all cooldown tracking variables.
     */
    public final void init() {
        random = new Random();
        repository.clear();
        isOnCreationCooldown = false;
        elapsedCreationCooldownMs = 0;
        lastUpdateTime = 0;
    }

    /**
     * Checks if a new enemy can be created based on the maximum enemy count and
     * the creation cooldown status.
     *
     * @return {@code true} if a new {@link Enemy} can be created, otherwise
     * {@code false}.
     * @throws IllegalStateException If the {@link GameManager} is not in the
     * {@code RUNNING} state.
     */
    public boolean canCreateEnemy() {
        ensureRunning("canCreateEnemy");

        int enemyCount = repository.countEnemies();

        System.out.println("Enemies: " + enemyCount
                + " | Elapsed Cooldown Time: " + elapsedCreationCooldownMs
                + " | On Cooldown: " + isOnCreationCooldown());

        return enemyCount < MAX_ENEMY_COUNT && !isOnCreationCooldown();
    }

    /**
     * Adds a specific, pre-built {@link Enemy} instance to the managed
     * collection. The enemy is added only if creation is currently allowed by
     * {@link #canCreateEnemy()}.
     *
     * @param enemy The new enemy instance to add.
     * @throws IllegalStateException If the {@link GameManager} is not in the
     * {@code RUNNING} state.
     */
    public void addEnemy(final Enemy enemy) {
        ensureRunning("addEnemy");
        updateEnemyCreationCooldownTimer();

        if (canCreateEnemy()) {
            repository.addEnemy(enemy);
        }
    }

    // TODO: Rework this
    /**
     * Creates a new random {@link Enemy} (type and position) if allowed by
     * {@link #canCreateEnemy()} and adds it to the collection.
     * <p>
     * After successful creation, the enemy cooldown is activated.
     *
     * @param player The {@link Player} entity that the newly created
     * {@link Enemy} will target.
     * @throws IllegalStateException If the {@link GameManager} is not in the
     * {@code RUNNING} state.
     */
    public void createRandomEnemy(final Player player) {
        ensureRunning("createRandomEnemy");
        updateEnemyCreationCooldownTimer();

        if (canCreateEnemy()) {
            GamePanel gamePanel = GameManager.getInstance().getGamePanel();
            Enemy newEnemy;
            int enemyType = random.nextInt(3);

            switch (enemyType) {
                case 0 ->
                    newEnemy = new RedMage.RedMageBuilder(gamePanel).build();
                case 1 ->
                    newEnemy = new BlueMage.BlueMageBuilder(gamePanel).build();
                case 2 ->
                    newEnemy = new MagentaMage.MagentaMageBuilder(gamePanel).build();
                default ->
                    throw new IllegalStateException("Switch-case recieved unexpected value: " + enemyType);
            }

            newEnemy.setPosition(getRandomSpawnPoint());
            newEnemy.setTarget(player.getPosition());

            int xMoveSpeed = random.nextInt(5);
            boolean moveLeft = random.nextBoolean();

            newEnemy.setVelocityX(moveLeft ? -xMoveSpeed : xMoveSpeed);

            repository.addEnemy(newEnemy);

            isOnCreationCooldown = true;
            elapsedCreationCooldownMs = 0;
        }
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * The primary game loop method. Updates the enemy system by:
     * <ol>
     * <li>Tracking delta time for cooldowns.</li>
     * <li>Attempting to
     * {@link #createRandomEnemy(Player) create a new enemy}.</li>
     * <li>Calling the {@link #updateEnemies() update logic} for all active
     * entities.</li>
     * </ol>
     *
     * @throws IllegalStateException If the {@link GameManager} is not in the
     * {@code RUNNING} state.
     */
    @Override
    public void update() {
        ensureRunning("update");

        lastUpdateTime = System.currentTimeMillis();

        createRandomEnemy(GameManager.getInstance().getPlayer());
        updateEnemies();
    }

    // ----- HELPER METHODS -----
    private void ensureRunning(String methodName) {
        if (!GameManager.getInstance().isRunning()) {
            StackWalker walker = StackWalker.getInstance();
            StackFrame caller = walker.walk(frames -> frames.skip(1).findFirst().orElse(null));

            throw new IllegalStateException(String.format(
                    "%s.%s: Cannot call %s() when GameMaager is not in the RUNNING state.",
                    caller != null ? caller.getClassName() : "UnknownClass",
                    caller != null ? caller.getMethodName() : "UnknownMethod",
                    methodName
            ));
        }
    }

    /**
     * Updates the enemy creation cooldown timer based on the elapsed time since
     * the last frame ({@code deltaTime}).
     * <p>
     * This method tracks how much time has passed and disables the
     * {@code isOnCreationCooldown} flag when the full duration is reached.
     */
    private void updateEnemyCreationCooldownTimer() {
        if (isOnCreationCooldown) {
            long currentTime = System.currentTimeMillis();
            long deltaTime = currentTime - lastUpdateTime;

            // Increment the cooldown timer
            elapsedCreationCooldownMs += deltaTime;

            // If the cooldown time is passed, reset the flag and elapsed time
            if (elapsedCreationCooldownMs >= ENEMY_CREATION_COOLDOWN_MS) {
                isOnCreationCooldown = false;
                elapsedCreationCooldownMs = 0;
            }
        }
    }

    /**
     * Orchestrates the update cycle for all active enemies.
     * <p>
     * Iterates over the enemy collection to call {@link Enemy#update()} on each
     * entity, then calls {@link #cleanupEnemies()} to remove defeated or
     * off-screen enemies.
     *
     * @throws IllegalStateException If the {@link GameManager} is not in the
     * {@code RUNNING} state.
     */
    private void updateEnemies() {
        ensureRunning("updateEnemies");

        // The individual enemy objects are mutated via their own update methods.
        Set<Enemy> activeEnemies = repository.getEnemies();

        for (Enemy enemy : activeEnemies) {
            enemy.update();
        }

        // Removal is delegated to the repository.
        cleanupEnemies();
    }

    /**
     * Defines the criteria for removing enemies from the collection and
     * delegates the mutation (removal) task to the repository.
     * <p>
     * Enemies are removed if they have
     * {@link Enemy#isFullyOutsidePanel() moved fully off-screen} or are
     * {@link Enemy#getCurrentHitPoints() defeated} (HP <= 0).
     */
    private void cleanupEnemies() {
        Predicate<Enemy> cleanupFilter = enemy
                -> enemy.isFullyOutsidePanel()
                || enemy.getCurrentHitPoints() <= 0;

        repository.removeIf(cleanupFilter);
    }

    /**
     * Generates a random {@link Point} within the defined spawn boundaries of
     * the game frame.
     *
     * @return A random {@link Point} where a new enemy can be spawned.
     */
    private Point getRandomSpawnPoint() {
        int x = random.nextInt(GameFrame.FRAME_HEIGHT);
        int y = random.nextInt(GameFrame.FRAME_HEIGHT * 1 / 5, GameFrame.FRAME_HEIGHT * 3 / 5);
        return new Point(x, y);
    }
}
