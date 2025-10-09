package com.rikuthin.entities.bullets;

import java.util.Objects;

import javax.swing.JPanel;

import com.rikuthin.entities.Entity;
import com.rikuthin.entities.MobileEntity;
import com.rikuthin.entities.enemies.Enemy;

/**
 * Represents a generic bullet that moves within a JPanel.
 */
public class Bullet extends MobileEntity {

    // ----- INSTANCE VARIABLES -----
    /**
     * The {@link Entity} that owns/controls the {@link BulletSpawner} that
     * created this bullet.
     */
    protected Entity owner;
    /**
     * How many points of damage the bullet should deal to entities it collides
     * with.
     */
    protected int damage;

    // ----- CONSTRUCTORS -----
    /**
     * Constructor used to create a BulletSpawner instance.
     *
     * @param builder The builder used to construct the player.
     */
    public Bullet(BulletBuilder builder) {
        super(builder);
        this.owner = builder.owner;
        setDamage(builder.damage);
    }

    // ---- GETTERS -----
    /**
     * Returns a reference to the {@link Entity} that owns/controls this
     * spawner.
     *
     * @return The owner entity.
     */
    public Entity getOwner() {
        return owner;
    }

    /**
     * Returns how many points of damage are dealt to entities this
     * bullet collides with.
     *
     * @return The damage dealt.
     */
    public int getDamage() {
        return damage;
    }

    // ---- SETTERS -----
    /**
     * Sets how many points of damage are dealt to entities this
     * bullet collides with.
     * <p>
     * Value cannot be less than zero (0).
     *
     * @param damage The damage dealt.
     * @throws IllegalArgumentException if the value is less than zero (0).
     */
    public final void setDamage(final int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException(String.format(
                    "%s: Damage cannot be less than zero (0).",
                    this.getClass().getName()
            ));
        }
        this.damage = damage;
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
        if (!(obj instanceof Bullet)) {
            return false;
        }
        Bullet other = (Bullet) obj;
        return super.equals(other)
                && damage == other.getDamage();
    }

    /**
     * Returns a hash code for this entity.
     *
     * @return The hash code of the entity.
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                damage
        );
    }

    // ----- STATIC BUILDER FOR BULLET -----
    /**
     * Builder class for constructing and initializing an {@link Bullet} instance.
     * <p>
     * This class extends {@code MobileEntityBuilder} by providing functionality unique to bullets,
     * such as damage dealt and the {@link Entity} that fired the bullet.
     */
    protected static class BulletBuilder extends MobileEntityBuilder<BulletBuilder> {

        // ----- INSTANCE VARIABLES -----
        /**
         * The {@link Entity} that fired the bullet.
         * <p>
         * To be used to determine who gets credit for dealing damage.
         */
        protected Entity owner = null;

        /**
         * How many points of damage the spawned bullets should do.
         */
        protected int damage = 0;

        // ------ CONSTRUCTORS -----
        /**
        * Builder class for constructing and initializing an {@link Bullet} instance.
        * 
        * @param panel The {@link JPanel} that will contain the bullet.
        * @param owner The {@link Entity} that fired the bullet.
        */
        public BulletBuilder(final JPanel panel, final Entity owner) {
            super(panel);

            if (owner == null) {
                throw new IllegalArgumentException(String.format(
                        "%s: Owner cannot be null.",
                        this.getClass().getName()
                ));
            }
            this.owner = owner;
        }

        // ---- SETTERS -----
        /**
         * Sets the amount of damage the bullet will deal.
         *
         * @param damage The amount of damage.
         * @return The builder instance.
         */
        public BulletBuilder damage(final int damage) {
            this.damage = damage;
            return this;
        }

        // ----- BUSINESS LOGIC METHODS -----
        /**
         * Builds and returns a new Bullet instance with the specified
         * attributes.
         *
         * @return A new Bullet instance.
         */
        public Bullet build() {
            return new Bullet(this);
        }
    }
}
