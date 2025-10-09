package com.rikuthin.data.entities;

import java.lang.StackWalker.StackFrame;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import com.rikuthin.entities.bullets.Bullet;
import com.rikuthin.services.core.GameManager;

/**
 * The {@code BulletRepository} is a <b>static utility class</b> that acts as
 * the centralized <b>repository</b> for all active {@link Bullet} entities.
 * <p>
 * This class stores and manages the lifecycle of all in-game bullets. The
 * game's main update loop accesses the collection via {@link #getBullets()}
 * each frame for processing, while spawners use {@link #addBullet(Bullet)} and
 * expiry systems use removal methods like
 * {@link #removeIf(java.util.function.Predicate)}.
 * <p>
 * This design ensures a single, authoritative collection of entities for
 * consistent and efficient processing during the game update cycle.
 */
public final class BulletRepository {

    // ----- STATIC VARIABLES -----
    /**
     * Internal storage for all currently active {@link Bullet} instances in the
     * game.
     */
    private static final HashSet<Bullet> ACTIVE_BULLETS = new HashSet<>();

    // ----- CONSTRUCTORS ------
    /**
     * Private constructor to <b>enforce the utility class pattern</b> and
     * prevent instantiation.
     *
     * @throws UnsupportedOperationException if an attempt to call this method
     * is made.
     */
    private BulletRepository() {
        throw new UnsupportedOperationException(
                String.format(
                        "%s: This utility class cannot be instantiated. Use its methods directly.",
                        this.getClass().getName())
        );
    }

    // ----- GETTERS -----
    /**
     * Retrieves a read-only view of all stored {@link Bullet} objects.
     * <p>
     * Modifications (adding or removing bullets) must be performed via the
     * repository's explicit static mutation methods (e.g.,
     * {@link #addBullet(Bullet)}).
     *
     * @return An unmodifiable copy of a {@link Set} containing all active
     * bullets.
     */
    public static Set<Bullet> getBullets() {
        ensureRunning("getBullets");
        return Collections.unmodifiableSet(ACTIVE_BULLETS);
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Adds a new {@link Bullet} instance to the repository's collection of
     * active bullets.
     * <p>
     * The bullet is added only if the provided argument is not {@code null}.
     *
     * @param bullet The new bullet instance to store.
     * @throws IllegalStateException If the {@link GameManager} is not in the
     * required {@code RUNNING} state.
     */
    public static void addBullet(final Bullet bullet) {
        ensureRunning("addBullet");
        if (bullet != null) {
            ACTIVE_BULLETS.add(bullet);
        }
    }

    /**
     * Removes a specific {@link Bullet} instance from the repository.
     *
     * @param bullet The bullet to remove.
     * @return {@code true} if the bullet was present and successfully removed.
     */
    public static boolean removeBullet(final Bullet bullet) {
        return ACTIVE_BULLETS.remove(bullet);
    }

    /**
     * Removes any {@link Bullet} objects from storage which satisfy the given
     * predicate.
     *
     * @param filter A boolean-valued function which serves as a filter.
     * @return The number of bullets removed.
     */
    public static int removeIf(Predicate<Bullet> filter) {
        int initialSize = ACTIVE_BULLETS.size();
        ACTIVE_BULLETS.removeIf(filter);
        return initialSize - ACTIVE_BULLETS.size();
    }

    /**
     * Removes all stored bullets from the repository.
     */
    public static void clear() {
        ACTIVE_BULLETS.clear();
    }

    /**
     * Returns the number of stored {@link Bullet} objects.
     *
     * @return The number of bullets stored.
     */
    public static int size() {
        ensureRunning("getBullets");
        return ACTIVE_BULLETS.size();
    }

    /**
     * Returns {@code true} if no {@link Bullet} objects are stored.
     *
     * @return {@code true} if no bullets are stored.
     */
    public static boolean isEmpty() {
        return ACTIVE_BULLETS.isEmpty();
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
                    "%s.%s: Cannot call %s() when GameManager is not in the RUNNING state.",
                    caller != null ? caller.getClassName() : "UnknownClass",
                    caller != null ? caller.getMethodName() : "UnknownMethod",
                    methodName
            ));
        }
    }
}
