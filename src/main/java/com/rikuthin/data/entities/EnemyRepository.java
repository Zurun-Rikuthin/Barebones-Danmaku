package com.rikuthin.data.entities;

import java.lang.StackWalker.StackFrame;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import com.rikuthin.entities.enemies.Enemy;
import com.rikuthin.services.core.GameManager;

/**
 * The {@code EnemyRepository} is a <b>static utility class</b> that acts as the
 * centralized <b>repository</b> for all active {@link Enemy} entities.
 * <p>
 * This class stores and manages the lifecycle of all in-game enemies. The
 * game's main update loop accesses the collection via {@link #getEnemies()}
 * each frame for processing, while spawners use {@link #addEnemy(Enemy)} and
 * expiry systems use removal methods like
 * {@link #removeIf(java.util.function.Predicate)}.
 * <p>
 * This design ensures a single, authoritative collection of entities for
 * consistent and efficient processing during the game update cycle.
 */
public class EnemyRepository {

    // ----- STATIC VARIABLES -----
    /**
     * Internal storage for all currently active {@link Enemy} instances in the
     * game.
     */
    private static final HashSet<Enemy> ACTIVE_ENEMIES = new HashSet<>();

    // ----- CONSTRUCTORS ------
    /**
     * Private constructor to <b>enforce the utility class pattern</b> and
     * prevent instantiation.
     *
     * @throws UnsupportedOperationException if an attempt to call this method
     * is made.
     */
    private EnemyRepository() {
        throw new UnsupportedOperationException(
                String.format(
                        "%s: This utility class cannot be instantiated. Use its methods directly.",
                        this.getClass().getName())
        );
    }

    // ----- GETTERS -----
    /**
     * Retrieves a read-only view of all stored {@link Enemy} objects.
     * <p>
     * Modifications (adding or removing enemies) must be performed via the
     * repository's explicit static mutation methods (e.g.,
     * {@link #addEnemy(Enemy)}).
     *
     * @return An unmodifiable copy of a {@link Set} containing all active
     * enemies.
     */
    public static Set<Enemy> getEnemies() {
        ensureRunning("getEnemies");
        return Collections.unmodifiableSet(ACTIVE_ENEMIES);
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Adds a new {@link Enemy} instance to the repository's collection of
     * active enemies.
     * <p>
     * The enemy is added only if the provided argument is not {@code null}.
     *
     * @param enemy The new enemy to store.
     * @throws IllegalStateException If the {@link GameManager} is not in the
     * required {@code RUNNING} state.
     */
    public static void addEnemy(final Enemy enemy) {
        ensureRunning("addEnemy");
        if (enemy != null) {
            ACTIVE_ENEMIES.add(enemy);
        }
    }

    /**
     * Removes a specific {@link Enemy} instance from the repository.
     *
     * @param enemy The enemy to remove.
     * @return {@code true} if the enemy was present and successfully removed.
     */
    public static boolean removeEnemy(final Enemy enemy) {
        return ACTIVE_ENEMIES.remove(enemy);
    }

    /**
     * Removes any {@link Enemy} objects from storage which meet the given
     * filter's requirements.
     *
     * @param filter A boolean-valued function which serves as a filter.
     * @return The number of enemies removed.
     */
    public static int removeIf(Predicate<Enemy> filter) {
        int initialSize = ACTIVE_ENEMIES.size();
        ACTIVE_ENEMIES.removeIf(filter);
        return initialSize - ACTIVE_ENEMIES.size();
    }

    /**
     * Removes all stored enemies from the repository.
     */
    public static void clear() {
        ACTIVE_ENEMIES.clear();
    }

    /**
     * Returns the number of stored {@link Enemy} objects.
     *
     * @return The number of enemies stored.
     */
    public static int size() {
        ensureRunning("getEnemies");
        return ACTIVE_ENEMIES.size();
    }

    /**
     * Returns {@code true} if no {@link Enemy} objects are stored.
     *
     * @return {@code true} if no enemies are stored.
     */
    public static boolean isEmpty() {
        return ACTIVE_ENEMIES.isEmpty();
    }

    // ----- HELPER METHODS -----
    /**
     * Checks that the {@link GameManager} is in the {@code RUNNING} state
     * before trying to run a given method.
     *
     * @param methodName The name of the method trying to run while the game is
     * in the wrong state
     * @throws IllegalStateException if the manager is in the wrong state.
     */
    // TODO: This likely needs editting too, tho I'll handle it when I rework GameManager
    private static void ensureRunning(String methodName) {
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
}
