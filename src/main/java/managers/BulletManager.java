package managers;

import java.lang.StackWalker.StackFrame;
import java.util.HashSet;
import java.util.Set;

import com.rikuthin.entities.bullets.Bullet;
import com.rikuthin.interfaces.Updateable;

public class BulletManager implements Updateable {
    // ----- INSTANCE VARIABLES -----
    /**
     * Stores references to all active bullets on screen.
     */
    private HashSet<Bullet> bullets;

    // ----- CONSTRUCTORS -----
    public BulletManager() {
        init();
    }

    // ----- GETTERS -----
    /**
     * Returns all active {@link Bullet} instances.
     *
     * @return The bullets.
     */
    public Set<Bullet> getBullets() {
        ensureRunning("getBullets");
        return bullets;
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Initializes the BulletManager for a new game. This method sets up all the necessary objects to manage bullets and
     * clears old bullet data.
     */
    public final void init() {
        clear();
    }

    /**
     * Clears old bullet data.
     */
    public void clear() {
        bullets = new HashSet<>();
    }

    /**
     * Adds a new {@link Bullet} instance to the managed list.
     *
     * @param bullet The new bullet.
     */
    public void addBullet(final Bullet bullet) {
        ensureRunning("addBullet");

        if (bullet != null) {
            bullets.add(bullet);
        }
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Updates all managed objects and the current game state.
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
                    "%s.%s: Cannot call %s() when GameManager is not in the RUNNING state.",
                    caller != null ? caller.getClassName() : "UnknownClass",
                    caller != null ? caller.getMethodName() : "UnknownMethod",
                    methodName
            ));
        }
    }

    /**
     * Updates the list of managed bullets and removes any defeated bullets.
     */
    private void updateBullets() {
        ensureRunning("updateBullets");

        if (bullets.isEmpty()) {
            return;
        }

        bullets.removeIf(bullet -> {
            bullet.update();
            return bullet.isFullyOutsidePanel();
        });
    }
}
