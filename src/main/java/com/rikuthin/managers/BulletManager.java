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
     * Updates the state of the {@code BulletManager}.
     * <p>
     * Calls {@link #updateBullets()}, follwed by {@link #cleanupBullets()}.
     */
    @Override
    public void update() {
        ensureRunning("update");

        updateBullets();
        cleanupBullets();
    }

    // ----- HELPER METHODS -----
    /**
     * Ensures the {@link GameManager} is in the {@code RUNNING} state (i.e., a game is active).
     * 
     * @param methodName The name of the method trying to run while the manager is in the wrong state.
     * @throws IllegalStateException If the {@link GameManager} is not in the
     * {@code RUNNING} state.
     */
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
     * Orchestrates the update cycle for all bullets within the {@link BulletRepository}.
     * <p>
     * Calls the {@link Bullet#update()} method for each {@link Bullet} instance.
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
    }

    /**
     * Deletes all bullets matching predefined criteria from storage.
     * <p>
     * Defines a {@link Predicate} with the following criteria:
     * <ul>
     * <li>Bullet is fully off-screen.</li>
     * </ul>
     * <p>
     * This is then passed to {@link BulletRepository#removeIf()}, which removes
     * any/all bullets matching said criteria from itself.
     * (Note: Set criteria may be subject to change in future builds/replaced with a better system.)
     */
    private void cleanupBullets() {
        Predicate<Bullet> cleanupFilter = Entity::isFullyOutsidePanel;

        repository.removeIf(cleanupFilter);
    }
}
