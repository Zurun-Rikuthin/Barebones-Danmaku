package com.rikuthin.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.rikuthin.graphics.animations.AnimationTemplate;

/**
 * Implements the **Repository pattern** as a singleton, providing centralized
 * access and management for all shared {@link AnimationTemplate} objects.
 * <p>
 * This class ensures that animation definitions (templates) are loaded only
 * once, are uniquely identified by a key, and are reused safely across
 * different game entities to conserve memory and maintain consistency.
 */
public class AnimationTemplateRepository {

    //TODO: Figure out a way to rework this thing so that animations can be unloaded as needed during runtime to free up memory.
    // Not a major issue right now, but could be if this system get developed into/used for a large enough app.

    // ----- STATIC VARIABLES -----
    /**
     * The single instance of the repository, implementing the **Singleton
     * pattern**.
     */
    private static final AnimationTemplateRepository INSTANCE = new AnimationTemplateRepository();

    /**
     * The internal, thread-safe storage for all shared
     * {@link AnimationTemplate} objects. Keys are the unique identifiers
     * (names) of the animations, and values are the {@link AnimationTemplate}
     * objects.
     */
    private static final Map<String, AnimationTemplate> STORED_ANIMATION_TEMPLATES = new HashMap<>();

    // ----- CONSTRUCTORS ------
    /**
     * Private constructor to enforce singleton pattern.
     */
    private AnimationTemplateRepository() {
    }

    // ----- GETTERS -----
    /**
     * Retrieves the singleton instance of the
     * {@link AnimationTemplateRepository}.
     *
     * @return The {@link AnimationTemplateRepository} instance.
     */
    public static AnimationTemplateRepository getInstance() {
        return INSTANCE;
    }

    /**
     * Retrieves a stored {@link AnimationTemplate} by its unique key.
     *
     * @param key The unique identifier for the animation.
     * @return The corresponding {@link AnimationTemplate} object, or
     * {@code null} if a template with the given key has not been added.
     */
    public AnimationTemplate getAnimation(String key) {
        return STORED_ANIMATION_TEMPLATES.get(key);
    }

    /**
     * Retrieves a read-only view of all stored {@link AnimationTemplate}
     * objects.
     *
     * The map where keys are animation identifiers and values are the
     * {@link AnimationTemplate} objects.
     */
    public Map<String, AnimationTemplate> getAllAnimations() {
        return Collections.unmodifiableMap(STORED_ANIMATION_TEMPLATES);
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Adds a new {@link AnimationTemplate} object to the repository, but only if one
     * with given key does not already exist.
     *
     * @param key The unique identifier for the animation.
     * @param animation The {@link AnimationTemplate} object to store.
     */
    public void addAnimation(String key, AnimationTemplate animation) {
        STORED_ANIMATION_TEMPLATES.putIfAbsent(key, animation);
    }
}
