package com.rikuthin.data;

import java.util.HashMap;
import java.util.Map;

import com.rikuthin.graphics.animations.AnimationTemplate;

/**
 * Singleton manager responsible for storing and retrieving shared animation
 * templates. Ensures animations are only loaded once and reused across
 * entities.
 */
public class AnimationTemplateRepository {

    // ----- STATIC VARIABLES -----
    private static final AnimationTemplateRepository INSTANCE = new AnimationTemplateRepository();
    private static final Map<String, AnimationTemplate> animations = new HashMap<>();

    // ----- CONSTRUCTORS ------
    /**
     * Private constructor to enforce singleton pattern.
     */
    private AnimationTemplateRepository() {
    }

    // ----- GETTERS -----
    /**
     * Retrieves the singleton instance of the {@link AnimationTemplateRepository}.
     *
     * @return The {@link AnimationTemplateRepository} instance.
     */
    public static AnimationTemplateRepository getInstance() {
        return INSTANCE;
    }

    /**
     * Retrieves an animation template by its key.
     *
     * @param key The unique identifier for the animation.
     * @return The corresponding {@link AnimationTemplate}, or null if not
     * found.
     */
    public AnimationTemplate getAnimation(String key) {
        return animations.get(key);
    }

    /**
     * Retrieves all stored animations.
     *
     * @return The map of animations.
     */
    public Map<String, AnimationTemplate> getAllAnimations() {
        return animations;
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Adds a new animation template to the manager. If an animation with the
     * given key already exists, it is not replaced.
     *
     * @param key The unique identifier for the animation.
     * @param animation The AnimationTemplate to store.
     */
    public void addAnimation(String key, AnimationTemplate animation) {
        animations.putIfAbsent(key, animation);
    }
}
