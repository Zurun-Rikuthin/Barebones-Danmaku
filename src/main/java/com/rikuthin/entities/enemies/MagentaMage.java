package com.rikuthin.entities.enemies;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JPanel;

import com.rikuthin.entities.bullets.BulletSpawner;

/**
 * Represents a Magenta Mage enemy in the game.
 * <p>
 * This class extends {@link Enemy} and initializes attributes specific to the
 * Magenta Mage.
 */
public class MagentaMage extends Enemy {

    // ----- CONSTRUCTORS -----
    /**
     * Constructor used to create a MagentaMage instance.
     *
     * @param builder The {@link EnemyBuilder} used to construct the MagentaMage.
     */
    public MagentaMage(EnemyBuilder builder) {
        super(builder);

        setUpMagentaMageAnimations();
        setUpMagentaMageBulletSpawner();
    }

    // ----- HELPER METHODS -----
    /**
     * Set up the animations for the Magenta Mage, including animation keys and
     * initial animation.
     */
    private void setUpMagentaMageAnimations() {
        HashSet<String> magentaMageAnimationKeys = Stream.of(
                "mage-guardian-magenta"
        ).collect(Collectors.toCollection(HashSet::new));

        setAnimationKeys(magentaMageAnimationKeys);
        setAnimation("mage-guardian-magenta");
        setMaxHitPoints(20);
        setCurrentHitPoints(20);
    }

    /**
     * Set up the animations for the Magenta Mage, including animation keys and
     * initial animation.
     */
    private void setUpMagentaMageBulletSpawner() {
        HashSet<String> magentaMageBulletAnimationKeys = Stream.of(
            "enemy-bullet"
            ).collect(Collectors.toCollection(HashSet::new));

        BulletSpawner spawner = new BulletSpawner.BulletSpawnerBuilder(panel, this)
                .bulletDamage(1)
                .bulletVelocityY(-20)
                .bulletAnimationKeys(magentaMageBulletAnimationKeys)
                .currentBulletAnimationKey("enemy-bullet")
                .build();

        setBulletSpawner(spawner);
    }


    // ----- STATIC BUILDER FOR ENEMY -----
    public static class MagentaMageBuilder extends EnemyBuilder {

        // ----- CONSTRUCTOR -----
        public MagentaMageBuilder(JPanel panel) {
            super(panel);
        }

        // ----- OVERRIDDEN METHODS -----
        @Override
        public MagentaMage build() {
            return new MagentaMage(this);
        }
    }
}
