package com.rikuthin.entities.enemies;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JPanel;

import com.rikuthin.entities.bullets.BulletSpawner;
import com.rikuthin.graphics.GameFrame;

/**
 * Represents a Blue Mage enemy in the game.
 * <p>
 * This class extends {@link Enemy} and initializes attributes specific to the
 * Blue Mage.
 */
public class BlueMage extends Enemy {

    // ----- CONSTRUCTORS -----
    /**
     * Constructor used to create a BlueMage instance.
     *
     * @param builder The {@link EnemyBuilder} used to construct the BlueMage.
     */
    public BlueMage(EnemyBuilder builder) {
        super(builder);

        setUpBlueMageAnimations();
        setUpBlueMageBulletSpawner();
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Extends {@link Enemy#move()} by applying a cosine function to oscillate
     * the y-coordinate with a given amplitude.
     * <p>
     * The final y-coordinate is clamped to stay within the desired range of 1/5
     * to 3/5 of the screen height.
     */
    @Override
    public void move() {
        super.move();

        // Get the normalized cosine value in the range [-1, 1]
        double normalizedCos = Math.cos(position.y);

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
     * Set up the animations for the Blue Mage, including animation keys and
     * initial animation.
     */
    private void setUpBlueMageAnimations() {
        HashSet<String> blueMageAnimationKeys = Stream.of(
                "mage-guardian-blue"
        ).collect(Collectors.toCollection(HashSet::new));

        setAnimationKeys(blueMageAnimationKeys);
        setAnimation("mage-guardian-blue");
        setMaxHitPoints(20);
        setCurrentHitPoints(20);
    }

    /**
     * Set up the animations for the Blue Mage, including animation keys and
     * initial animation.
     */
    private void setUpBlueMageBulletSpawner() {
        HashSet<String> blueMageBulletAnimationKeys = Stream.of(
                "enemy-bullet"
        ).collect(Collectors.toCollection(HashSet::new));

        BulletSpawner spawner = new BulletSpawner.BulletSpawnerBuilder(panel, this)
                .bulletDamage(1)
                .bulletVelocityY(-20)
                .bulletAnimationKeys(blueMageBulletAnimationKeys)
                .currentBulletAnimationKey("enemy-bullet")
                .build();

        setBulletSpawner(spawner);
    }

    // ----- STATIC BUILDER FOR ENEMY -----
    public static class BlueMageBuilder extends EnemyBuilder {

        // ----- CONSTRUCTOR -----
        public BlueMageBuilder(JPanel panel) {
            super(panel);
        }

        // ----- OVERRIDDEN METHODS -----
        @Override
        public BlueMage build() {
            return new BlueMage(this);
        }
    }
}
