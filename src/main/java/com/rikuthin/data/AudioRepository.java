package com.rikuthin.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.Clip;

/**
 * Implements the **Repository pattern** as a singleton, providing centralized
 * access and management for all shared {@link Clip} objects.
 * <p>
 * This class ensures that audio files are loaded only once, are uniquely
 * identified by a key, and are reused safely across different game entities to
 * conserve memory and maintain consistency.
 */
public class AudioRepository {

    //TODO: Figure out a way to rework this thing so that audio clips can be unloaded as needed during runtime to free up memory.
    // Not a major issue right now, but could be if this system get developed into/used for a large enough app.

    // ----- STATIC VARIABLES -----
    /**
     * The single instance of the repository, implementing the **Singleton
     * pattern**.
     */
    private static final AudioRepository INSTANCE = new AudioRepository();

    /**
     * The internal, thread-safe storage for all shared {@link Clip} objects.
     * Keys are the unique identifiers (names) of the animations, and values are
     * the {@link Clip} objects.
     */
    private static final Map<String, Clip> STORED_CLIPS = new HashMap<>();

    // ----- CONSTRUCTORS ------
    /**
     * Private constructor to enforce singleton pattern.
     */
    private AudioRepository() {
    }

    // ----- GETTERS -----
    /**
     * Retrieves the singleton instance of the {@link AudioRepository}.
     *
     * @return The {@link AudioRepository} instance.
     */
    public static AudioRepository getInstance() {
        return INSTANCE;
    }

    /**
     * Retrieves a stored {@link Clip} by its unique key.
     *
     * @param key The unique identifier for the animation.
     * @return The corresponding {@link Clip} object, or {@code null} if a clip
     * with the given key has not been added.
     */
    public Clip getClip(String key) {
        return STORED_CLIPS.get(key);
    }

    /**
     * Retrieves a read-only view of all stored {@link Clip} objects.
     *
     * The map where keys are clip identifiers and values are the {@link Clip}
     * objects.
     */
    public Map<String, Clip> getAllClips() {
        return Collections.unmodifiableMap(STORED_CLIPS);
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Adds a new {@link Clip} object to the repository, but only if one with
     * given key does not already exist.
     *
     * @param key The unique identifier for the animation.
     * @param clip The {@link Clip} object to store.
     */
    public void addClip(String key, Clip clip) {
        STORED_CLIPS.putIfAbsent(key, clip);
    }
}
