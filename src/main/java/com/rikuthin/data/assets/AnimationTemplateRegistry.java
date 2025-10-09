package com.rikuthin.data.assets;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.rikuthin.graphics.animations.AnimationTemplate;
import com.rikuthin.loaders.AnimationLoader;

/**
 * The {@code AnimationTemplateRegistry} is a <b>static utility class</b> that
 * acts as the centralized <b>repository</b> for all shared
 * {@link AnimationTemplate} assets.
 * <p>
 * Assets loaded by the {@link AnimationLoader} are registered here using a
 * unique identifier, making them readily available to the game's rendering and
 * entity systems.
 * <p>
 * This design ensures that all components access a single, consistent set of
 * shared assets, which <b>conserves memory</b> and avoids redundant I/O
 * operations during runtime.
 */
public final class AnimationTemplateRegistry {

    //TODO: Figure out a way to rework this thing to use dynamic storage instead of static.
    // Not a major issue right now, but could be if this system get developed into/used for a large enough app.
    // ----- STATIC VARIABLES -----
    /**
     * Internal storage for shared {@link AnimationTemplate} instances.
     * <p>
     * <b>Note:</b> The key is the template's unique identifier (name); the
     * value is the corresponding instance.
     */
    private static final Map<String, AnimationTemplate> ANIMATION_TEMPLATES = new HashMap<>();

    // ----- CONSTRUCTORS ------
    /**
     * Private constructor to <b>enforce the utility class pattern</b> and
     * prevent instantiation.
     *
     * @throws UnsupportedOperationException if an attempt to call this method
     * is made.
     */
    private AnimationTemplateRegistry() {
        throw new UnsupportedOperationException(
                String.format(
                        "%s: This utility class cannot be instantiated. Use its methods directly.",
                        this.getClass().getName())
        );
    }

    // ----- GETTERS -----
    /**
     * Retrieves a registered {@link AnimationTemplate} object by its unique
     * key.
     *
     * @param key The unique identifier for the template.
     * @return The corresponding template, or {@code null} if one with the given
     * key has not been registered.
     */
    public static AnimationTemplate getTemplate(String key) {
        return ANIMATION_TEMPLATES.get(key);
    }

    /**
     * Retrieves a read-only view of all registered {@link AnimationTemplate}
     * objects.
     * <p>
     * Modifications (registering templates) must be performed via the
     * registry's explicit static mutation methods (e.g.,
     * {@link #registerTemplate(String, AnimationTemplate)}).
     *
     * @return An unmodifiable copy of a {@link Map} containing all templates
     * and their unique identifiers.
     */
    public static Map<String, AnimationTemplate> getAllTemplates() {
        return Collections.unmodifiableMap(ANIMATION_TEMPLATES);
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Registers a new {@link AnimationTemplate}, so long as the given key does
     * not already exist.
     *
     * @param key The unique identifier for the template.
     * @param animation The template to store.
     */
    public static void registerTemplate(String key, AnimationTemplate animation) {
        ANIMATION_TEMPLATES.putIfAbsent(key, animation);
    }

    /**
     * Returns the number of registered {@link AnimationTemplate} objects.
     *
     * @return The number of registered templates.
     */
    public static int size() {
        return ANIMATION_TEMPLATES.size();
    }

    /**
     * Returns {@code true} if no {@link AnimationTemplate} objects are
     * registered.
     *
     * @return {@code true} if no templates are registered.
     */
    public static boolean isEmpty() {
        return ANIMATION_TEMPLATES.isEmpty();
    }
}
