package com.rikuthin.managers;

import java.lang.StackWalker.StackFrame;
import java.util.Set;
import java.util.function.Predicate;

import com.rikuthin.data.BulletRepository;
import com.rikuthin.entities.Entity;
import com.rikuthin.entities.bullets.Bullet;
import com.rikuthin.interfaces.Updateable;

/**
 * The {@code BulletManager} is the primary service responsible for the **active
 * lifecycle** of all bullets in the game.
 * <p>
 * This manager dictates bullet behavior by handling spawning (creation rules),
 * entity updates (calling the {@link Bullet#update()} method on all active
 * bullets), and cleanup (defining the criteria for bullet removal), delegating
 * storage and mutation to the {@link BulletRepository}.
 */
public class BulletManager implements Updateable {

    // ----- INSTANCE VARIABLES -----
    /**
     * The repository instance used to access and mutate the collection of
     * active bullets.
     */
    private final BulletRepository repository;

    // ----- CONSTRUCTORS -----
    /**
     * Constructs the {@code BulletManager} and retrieves the singleton instance
     * of the {@link BulletRepository} to establish its dependency. Initializes
     * the manager for game start.
     */
    public BulletManager() {
        repository = BulletRepository.getInstance();
        init();
    }

    // ----- GETTERS -----
    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Initializes the BulletManager for a new game.
     * <p>
     * This method resets the random generator, clears the repository of old
     * bullets, and resets all cooldown tracking variables.
     */
    public final void init() {
        repository.clear();
    }

    // Commenting this out for now, but leaving here in case I want to create rules for
    // when bullets are allowed to be created, similar to how it is with enemies.
    // /**
    //  * Adds a specific, pre-built {@link Bullet} instance to the managed
    //  * collection. The bullet is added only if creation is currently allowed by
    //  * {@link #canCreateBullet()}.
    //  *
    //  * @param bullet The new bullet instance to add.
    //  * @throws IllegalStateException If the {@link GameManager} is not in the
    //  * {@code RUNNING} state.
    //  */
    // public void addBullet(final Bullet bullet) {
    //     ensureRunning("addBullet");
    //     if (bullet != null) {
    //         repository.addBullet(bullet);
    //     }
    // }
    // ----- OVERRIDDEN METHODS -----
    /**
     * The primary game loop method. Updates the bullet system by:
     * <ol>
     * <li>Tracking delta time for cooldowns.</li>
     * <li>Attempting to
     * {@link #createRandomBullet(Player) create a new bullet}.</li>
     * <li>Calling the {@link #updateBullets() update logic} for all active
     * entities.</li>
     * </ol>
     *
     * @throws IllegalStateException If the {@link GameManager} is not in the
     * {@code RUNNING} state.
     */
    @Override
    public void update() {
        ensureRunning("update");

        updateBullets();
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
     * Orchestrates the update cycle for all active bullets.
     * <p>
     * Iterates over the bullet collection to call {@link Bullet#update()} on
     * each entity, then calls {@link #cleanupBullets()} to remove defeated or
     * off-screen bullets.
     *
     * @throws IllegalStateException If the {@link GameManager} is not in the
     * {@code RUNNING} state.
     */
    private void updateBullets() {
        ensureRunning("updateBullets");

        // The individual bullet objects are mutated via their own update methods.
        Set<Bullet> activeBullets = repository.getBullets();

        for (Bullet bullet : activeBullets) {
            bullet.update();
        }

        // Removal is delegated to the repository.
        cleanupBullets();
    }

    /**
     * Defines the criteria for removing bullets from the collection and
     * delegates the mutation (removal) task to the repository.
     * <p>
     * Bullets are removed if they have
     * {@link Bullet#isFullyOutsidePanel() moved fully off-screen}.
     */
    private void cleanupBullets() {
        Predicate<Bullet> cleanupFilter = Entity::isFullyOutsidePanel;

        repository.removeIf(cleanupFilter);
    }
}
