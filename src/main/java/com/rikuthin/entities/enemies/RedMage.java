package com.rikuthin.entities.enemies;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JPanel;

import com.rikuthin.entities.bullets.BulletSpawner;
import com.rikuthin.graphics.GameFrame;

/**
 * Represents a Red Mage enemy in the game.
 * <p>
 * This class extends {@link Enemy} and initializes attributes specific to the
 * Red Mage.
 */
public class RedMage extends Enemy {

    // ----- CONSTRUCTORS -----
    /**
     * Constructor used to create a RedMage instance.
     *
     * @param builder The {@link EnemyBuilder} used to construct the RedMage.
     */
    public RedMage(EnemyBuilder builder) {
        super(builder);

        setUpRedMageAnimations();
        setUpRedMageBulletSpawner();
    }

    /**
     * Extends {@link Enemy#move()} by applying a sine function to oscillate the
     * y-coordinate with a given amplitude.
     * <p>
     * The final y-coordinate is clamped to stay within the desired range of 1/5
     * to 3/5 of the screen height.
     */
    @Override
    public void move() {
        super.move();

        // Get the normalized cosine value in the range [-1, 1]
        double normalizedCos = Math.sin(position.y);

        double amplitude = 50;

        // Oscillate within the range, using the amplitude to determine the oscillation
        double oscillatedY = amplitude * GameFrame.FRAME_HEIGHT * normalizedCos;

        // Clamp the result to stay within the limits [1/5 * GameFrame.FRAME_HEIGHT, 3/5 * GameFrame.FRAME_HEIGHT]
        position.y = (int) Math.max(
                Math.min(
                        oscillatedY + (1.0 / 5.0) * GameFrame.FRAME_HEIGHT,
                        (1.0 / 5.0) * GameFrame.FRAME_HEIGHT
                ),
                (3.0 / 5.0) * GameFrame.FRAME_HEIGHT
        );
    }

    // ----- HELPER METHODS -----
    /**
     * Set up the animations for the Red Mage, including animation keys and
     * initial animation.
     */
    private void setUpRedMageAnimations() {
        HashSet<String> redMageAnimationKeys = Stream.of(
                "mage-guardian-red"
        ).collect(Collectors.toCollection(HashSet::new));

        setAnimationKeys(redMageAnimationKeys);
        setAnimation("mage-guardian-red");
        setMaxHitPoints(20);
        setCurrentHitPoints(20);
    }

    /**
     * Set up the animations for the Red Mage, including animation keys and
     * initial animation.
     */
    private void setUpRedMageBulletSpawner() {
        HashSet<String> redMageBulletAnimationKeys = Stream.of(
                "enemy-bullet"
        ).collect(Collectors.toCollection(HashSet::new));

        BulletSpawner spawner = new BulletSpawner.BulletSpawnerBuilder(panel, this)
                .bulletDamage(1)
                .bulletVelocityY(-20)
                .bulletAnimationKeys(redMageBulletAnimationKeys)
                .currentBulletAnimationKey("enemy-bullet")
                .build();

        setBulletSpawner(spawner);
    }

    // ----- STATIC BUILDER FOR ENEMY -----
    public static class RedMageBuilder extends EnemyBuilder {

        // ----- CONSTRUCTOR -----
        public RedMageBuilder(JPanel panel) {
            super(panel);
        }

        // ----- OVERRIDDEN METHODS -----
        @Override
        public RedMage build() {
            return new RedMage(this);
        }
    }
}
