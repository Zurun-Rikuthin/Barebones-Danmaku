package com.rikuthin.entities;

import java.util.Objects;

import javax.swing.JPanel;

import com.rikuthin.entities.bullets.BulletSpawner;

/**
 * Represents a player controlled character in the game.
 */
public class Player extends MobileEntity {

    /**
     * The player's bullet spawner.
     * <p>
     * (Must be created AFTER the player and set later due and BulletSpawner
     * needs a reference to its owner Entity.)
     */
    protected BulletSpawner bulletSpawner;
    /**
     * Whether the player is currently shooting bullets.
     */
    protected boolean isFiringBullets;

    // ----- CONSTRUCTORS -----
    /**
     * Constructor used to create a Player instance.
     *
     * @param builder The builder used to construct the player.
     */
    protected Player(PlayerBuilder builder) {
        super(builder);
        this.bulletSpawner = null;
        this.isFiringBullets = false;
    }

    // ---- GETTERS -----
    /**
     * Returns the player's {@link BulletSpawner}
     *
     * @return the bullet spawner.
     */
    public BulletSpawner getBulletSpawner() {
        return bulletSpawner;
    }

    /**
     * Returns whether the player is firing bullets.
     *
     * @return {@code true} if firing bullets, {@code false} otherwise.
     */
    public boolean isFiringBullets() {
        return isFiringBullets;
    }

    // ---- SETTERS -----
    /**
     * Sets the player's {@link BulletSpawner}
     *
     * @param bulletSpawner the bullet spawner.
     */
    public final void setBulletSpawner(final BulletSpawner bulletSpawner) {
        this.bulletSpawner = bulletSpawner;
    }

    /**
     * Sets whether the player is firing bullets.
     *
     * @param isFiringBullets {@code true} if firing bullets, {@code false}
     * otherwise.
     */
    public void setIsFiringBullets(final boolean isFiringBullets) {
        if (bulletSpawner == null) {
            this.isFiringBullets = false;
            return;
        }
        this.isFiringBullets = isFiringBullets;
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Compares this entity to another object for equality.
     *
     * @param obj The {@link Object} to compare with.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Player)) {
            return false;
        }
        Player other = (Player) obj;
        return super.equals(other)
                && Objects.equals(bulletSpawner, other.getBulletSpawner())
                && isFiringBullets == other.isFiringBullets();
    }

    /**
     * Returns a hash code for this entity.
     *
     * @return The hash code of the entity.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), bulletSpawner, isFiringBullets);
    }

    /**
     * Moves the player according to its current velocity and then corrects its
     * position to ensure it stays within the bounds of the game panel.
     */
    @Override
    public void move() {
        super.move();
        correctPosition();
    }

    // ----- STATIC BUILDER FOR PLAYER -----
    /**
     * Static builder class for creating {@link Player} instances.
     */
    public static class PlayerBuilder extends MobileEntityBuilder<PlayerBuilder> {

        // ----- CONSTRUCTOR -----
        /**
         * Constructs a new PlayerBuilder.
         *
         * @param panel The {@link JPanel} that the Player will be drawn on and
         * constrained by.
         */
        public PlayerBuilder(final JPanel panel) {
            super(panel);
        }

        // ----- BUSINESS LOGIC METHODS -----
        /**
         * Builds and returns a new {@link Player} instance.
         *
         * @return A newly constructed {@link Player} object.
         */
        public Player build() {
            return new Player(this);
        }
    }
}
