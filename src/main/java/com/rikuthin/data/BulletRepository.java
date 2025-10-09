package com.rikuthin.data;

import java.lang.StackWalker.StackFrame;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import com.rikuthin.entities.bullets.Bullet;
import com.rikuthin.managers.GameManager;

/**
 * Implements the **Repository pattern** as a singleton, providing centralized
 * access and management for all currently active {@link Bullet} objects in the
 * game world.
 * <p>
 * This class serves as the single source of truth for the game's active bullet
 * population. It protects the integrity of the collection by exposing a
 * read-only view via {@link #getBullets()} and requiring explicit mutation
 * methods (like {@link #addBullet(Bullet)} and {@link #removeIf(Predicate)})
 * for modification.
 */
public class BulletRepository {

    // ----- STATIC VARIABLES -----
    /**
     * The single instance of the repository, implementing the **Singleton
     * pattern**.
     */
    private static final BulletRepository INSTANCE = new BulletRepository();

    /**
     * Stores references to all active bullets on screen.
     */
    private static final HashSet<Bullet> ACTIVE_BULLETS = new HashSet<>();

    // ----- CONSTRUCTORS ------
    /**
     * Private constructor to enforce singleton pattern.
     */
    private BulletRepository() {
    }

    // ----- GETTERS -----
    /**
     * Retrieves the singleton instance of the {@link BulletRepository}.
     *
     * @return The {@link BulletRepository} instance.
     */
    public static BulletRepository getInstance() {
        return INSTANCE;
    }

    /**
     * /**
     * Returns an unmodifiable/read-only view of the stored {@link Bullet}
     * objects.
     * <p>
     * Modifications must be done via the repository's explicit mutation methods
     * (e.g., addBullet, removeIf).
     *
     * @return The active bullets.
     */
    public Set<Bullet> getBullets() {
        ensureRunning("getBullets");
        // This wrapper prevents the caller from accidentally
        // adding or removing elements, thus protecting the repository's internal state.
        return Collections.unmodifiableSet(ACTIVE_BULLETS);
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Adds a new {@link Bullet} instance to the repository's collection of
     * active bullets.
     * <p>
     * The bullet is added only if the provided argument is not {@code null}.
     *
     * @param bullet The new {@link Bullet} instance to store.
     * @throws IllegalStateException If the {@link GameManager} is not in the
     * required {@code RUNNING} state.
     */
    public void addBullet(final Bullet bullet) {
        ensureRunning("addBullet");
        if (bullet != null) {
            ACTIVE_BULLETS.add(bullet);
        }
    }

    /**
     * Removes a specific {@link Bullet} instance from the repository.
     *
     * @param bullet The bullet to remove.
     * @return {@code true} if the bullet was present and successfully removed,
     * {@code false} otherwise.
     */
    public boolean removeBullet(final Bullet bullet) {
        return ACTIVE_BULLETS.remove(bullet);
    }

    /**
     * Removes any {@link Bullet} objects from storage which meet the given
     * filter's requirements.
     *
     * @param filter A {@code boolean-valued} function which serves as a filter.
     * @return The number of bullets removed.
     */
    public int removeIf(Predicate<Bullet> filter) {
        int initialSize = ACTIVE_BULLETS.size();
        ACTIVE_BULLETS.removeIf(filter);
        return initialSize - ACTIVE_BULLETS.size();
    }

    /**
     * Removes all stored bullets from the repository.
     */
    public void clear() {
        ACTIVE_BULLETS.clear();
    }

    /**
     * Counts the number of stored {@link Bullet} objects.
     *
     * @return The number of bullets stored.
     */
    public int countBullets() {
        ensureRunning("getBullets");
        return ACTIVE_BULLETS.size();
    }

    /**
     * Checks whether the repository is empty (has no elements).
     *
     * @return {@code true} if there are no elements; otherwise {@code false}.
     */
    public boolean isEmpty() {
        return ACTIVE_BULLETS.isEmpty();
    }

    // ----- HELPER METHODS -----
    /**
     * Checks that the {@link GameManager} is in the {@code RUNNING} state before trying to run a given method.
     * 
     * @param methodName The name of the method trying to run while the game is in the wrong state
     * @throws IllegalStateException if the manager is in the wrong state.
     */
    // TODO: This likely needs editting too, tho I'll handle it when I rework GameManager
    private void ensureRunning(String methodName) {
        if (!GameManager.getInstance().isRunning()) {
            StackWalker walker = StackWalker.getInstance();
            StackFrame caller = walker.walk(frames -> frames.skip(1).findFirst().orElse(null));

            throw new IllegalStateException(String.format(
                    "%s.%s: Cannot call %s() when GameManager is not in the RUNNING state.",
                    caller != null ? caller.getClassName() : "UnknownClass",
                    caller != null ? caller.getMethodName() : "UnknownMethod",
                    methodName
            ));
        }
    }
}
