package com.rikuthin.data;

import java.lang.StackWalker.StackFrame;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import com.rikuthin.entities.enemies.Enemy;
import com.rikuthin.managers.GameManager;

/**
 * Implements the **Repository pattern** as a singleton, providing centralized
 * access and management for all currently active {@link Enemy} objects.
 * <p>
 * This class ensures that animation definitions (templates) are loaded only
 * once, are uniquely identified by a key, and are reused safely across
 * different game entities to conserve memory and maintain consistency.
 */
public class EnemyRepository {

    // ----- STATIC VARIABLES -----
    /**
     * The single instance of the repository, implementing the **Singleton
     * pattern**.
     */
    private static final EnemyRepository INSTANCE = new EnemyRepository();

    /**
     * Stores references to all active enemies on screen.
     */
    private static final HashSet<Enemy> ACTIVE_ENEMIES = new HashSet<>();

    // ----- CONSTRUCTORS ------
    /**
     * Private constructor to enforce singleton pattern.
     */
    private EnemyRepository() {
    }

    // ----- GETTERS -----
    /**
     * Retrieves the singleton instance of the {@link EnemyRepository}.
     *
     * @return The {@link EnemyRepository} instance.
     */
    public static EnemyRepository getInstance() {
        return INSTANCE;
    }

    /**
     * /**
     * Returns an unmodifiable/read-only view of the stored {@link Enemy}
     * objects.
     * <p>
     * Modifications must be done via the repository's explicit mutation methods
     * (e.g., addEnemy, removeIf).
     *
     * @return The active enemies.
     */
    public Set<Enemy> getEnemies() {
        ensureRunning("getEnemies");
        // This wrapper prevents the caller (the Manager/Director) from accidentally
        // adding or removing elements, thus protecting the Repository's internal state.
        return Collections.unmodifiableSet(ACTIVE_ENEMIES);
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Adds a new {@link Enemy} instance to the repository.
     *
     * @param enemy The new enemy.
     */
    public void addEnemy(final Enemy enemy) {
        ensureRunning("addEnemy");
        ACTIVE_ENEMIES.add(enemy);
    }

    /**
     * Removes a specific {@link Enemy} instance from the repository.
     *
     * @param enemy The enemy to remove.
     * @return {@code true} if the enemy was present and successfully removed,
     * {@code false} otherwise.
     */
    public boolean removeEnemy(final Enemy enemy) {
        return ACTIVE_ENEMIES.remove(enemy);
    }

    /**
     * Removes any {@link Enemy} objects from storage which meet the given
     * filter's requirements.
     *
     * @param filter A {@code boolean-valued} function which serves as a filter.
     * @return The number of enemies removed.
     */
    public int removeIf(Predicate<Enemy> filter) {
        int initialSize = ACTIVE_ENEMIES.size();
        ACTIVE_ENEMIES.removeIf(filter);
        return initialSize - ACTIVE_ENEMIES.size();
    }

    /**
     * Removes all stored enemies from the repository.
     */
    public void clear() {
        ACTIVE_ENEMIES.clear();
    }

    /**
     * Counts the number of stored {@link Enemy} objects.
     *
     * @return The no. enemies stored.
     */
    public int countEnemies() {
        ensureRunning("getEnemies");
        return ACTIVE_ENEMIES.size();
    }

    /**
     * Checks whether the repository is empty (has no elements).
     *
     * @param methodName {@code true} if there are no elements; otherwise
     * {@code false}.
     */
    public boolean isEmpty() {
        return ACTIVE_ENEMIES.isEmpty();
    }

    // ----- HELPER METHODS -----
    // TODO: This likely needs editting too, tho I'll handle it when I rework GameManager
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
}
