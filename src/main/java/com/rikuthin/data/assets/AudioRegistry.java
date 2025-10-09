package com.rikuthin.data.assets;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.Clip;

import com.rikuthin.loaders.AudioLoader;

/**
 * The {@code AudioRegistry} is a <b>static utility class</b> that acts as the
 * centralized <b>repository</b> for all shared {@link Clip} assets.
 * <p>
 * Assets loaded by the {@link AudioLoader} are registered here using a unique
 * identifier, making them readily available to the game's rendering and entity
 * systems.
 * <p>
 * This design ensures that all components access a single, consistent set of
 * shared assets, which <b>conserves memory</b> and avoids redundant I/O
 * operations during runtime.
 */
public final class AudioRegistry {

    //TODO: Figure out a way to rework this thing to use dynamic storage instead of static.
    // Not a major issue right now, but could be if this system get developed into/used for a large enough app.
    // ----- STATIC VARIABLES -----
    /**
     * Internal storage for shared {@link Clip} instances.
     * <p>
     * <b>Note:</b> The key is the clip's unique identifier (name); the value is
     * the corresponding instance.
     */
    private static final Map<String, Clip> AUDIO_CLIPS = new HashMap<>();

    // ----- CONSTRUCTORS ------
    /**
     * Private constructor to <b>enforce the utility class pattern</b> and
     * prevent instantiation.
     *
     * @throws UnsupportedOperationException if an attempt to call this method
     * is made.
     */
    private AudioRegistry() {
        throw new UnsupportedOperationException(
                String.format(
                        "%s: This utility class cannot be instantiated. Use its methods directly.",
                        this.getClass().getName())
        );
    }

    // ----- GETTERS -----
    /**
     * Retrieves a registered {@link Clip} object by its unique key.
     *
     * @param key The unique identifier for the audio clip.
     * @return The corresponding audio clip, or {@code null} if one with the
     * given key has not been registered.
     */
    public static Clip getClip(String key) {
        return AUDIO_CLIPS.get(key);
    }

    /**
     * Retrieves a read-only view of all registered {@link Clip} objects.
     * <p>
     * Modifications (registering clips) must be performed via the registry's
     * explicit static mutation methods (e.g.,
     * {@link #registerClip(String, Clip)}).
     *
     * @return An unmodifiable copy of a {@link Map} containing all audio clips
     * and their unique identifiers.
     */
    public static Map<String, Clip> getAllClips() {
        return Collections.unmodifiableMap(AUDIO_CLIPS);
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Registers a new {@link Clip}, so long as one with given key does not
     * already exist.
     *
     * @param key The unique identifier for the audio clip.
     * @param clip The audio clip object to store.
     */
    public static void registerClip(String key, Clip clip) {
        AUDIO_CLIPS.putIfAbsent(key, clip);
    }

    /**
     * Returns the number of registered {@link Clip} objects.
     *
     * @return The number of registered clips.
     */
    public static int size() {
        return AUDIO_CLIPS.size();
    }

    /**
     * Returns {@code true} if no {@link Clip} objects are registered.
     *
     * @return {@code true} if no {@link Clip} objects are registered.
     */
    public static boolean isEmpty() {
        return AUDIO_CLIPS.isEmpty();
    }
}
