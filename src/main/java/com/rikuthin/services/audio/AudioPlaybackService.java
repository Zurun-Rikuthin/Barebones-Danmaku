package com.rikuthin.services.audio;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import com.rikuthin.data.assets.AudioRegistry;

/**
 * The {@code AudioPlaybackService} controls the playback behaviour of all audio
 * {@link Clip} objects stored within the {@link AudioRegistry}.
 */
public class AudioPlaybackService {

    // ----- STATIC VARIABLES -----
    /**
     * Volume control (range: 0.0 to 1.0).
     */
    private static float volume = 0.5f;

    // ----- CONSTRUCTORS -----
    /**
     * Private constructor to <b>enforce the utility class pattern</b> and
     * prevent instantiation.
     *
     * @throws UnsupportedOperationException if an attempt to call this method
     * is made.
     */
    private AudioPlaybackService() {
        throw new UnsupportedOperationException(
                String.format(
                        "%s: This utility class cannot be instantiated. Use its methods directly.",
                        this.getClass().getName())
        );
    }

    // ----- GETTERS -----
    /**
     * Gets the current volume level.
     *
     * @return The volume level (range: 0.0 to 1.0).
     */
    public static float getVolume() {
        return volume;
    }

    /**
     * Checks if a given sound clip (by key) is currently playing.
     *
     * @param key The key of the sound clip to check.
     * @return {@code true} if the clip is currently running, {@code false}
     * otherwise.
     */
    public static boolean isClipPlaying(String key) {
        Clip clip = AudioRegistry.getClip(key);
        return clip != null && clip.isRunning();
    }

    // ----- SETTERS -----
    /**
     * Sets the global volume level.
     *
     * @param volume The volume level (range: 0.0 to 1.0).
     */
    public static void setVolume(float volume) {
        volume = Math.clamp(volume, 0.0f, 1.0f);
        applyVolumeToAllClips();
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Plays a sound clip with the current global volume..
     *
     * @param key The key of the sound clip.
     * @param looping If {@code true}, the sound will loop continuously.
     */
    public static void playClip(String key, boolean looping) {
        Clip clip = AudioRegistry.getClip(key);
        if (clip != null) {
            clip.setFramePosition(0);
            if (looping) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }
        } else {
            System.err.println("SoundManager: Cannot play clip. Key not found: " + key);
        }
    }

    /**
     * Plays a sound clip with a custom volume level.
     *
     * @param key The key of the sound clip.
     * @param looping If {@code true}, the sound will loop continuously.
     * @param volume The volume level for this playback (range: 0.0 to 1.0).
     */
    public static void playClip(String key, boolean looping, float volume) {
        Clip clip = AudioRegistry.getClip(key);
        if (clip != null) {
            clip.setFramePosition(0);
            if (looping) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }
            // Adjust volume for this specific playback
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (gainControl != null) {
                float min = gainControl.getMinimum();
                float max = gainControl.getMaximum();
                float gain = min + (Math.clamp(volume, 0.0f, 1.0f) * (max - min));
                gainControl.setValue(gain);
                System.out.println("SoundManager: Adjusted volume for clip '" + key + "' to " + gain + " dB (custom).");
            } else {
                System.out.println("SoundManager: Master gain control not found for clip '" + key + "'.");
            }
        } else {
            System.err.println("SoundManager: Cannot play clip. Key not found: " + key);
        }
    }

    /**
     * Stops a specific sound clip.
     *
     * @param key The key of the sound clip.
     */
    public static void stopClip(String key) {
        Clip clip = AudioRegistry.getClip(key);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    /**
     * Stops all currently playing sound clips.
     */
    public static void stopAll() {
        for (Clip clip : AudioRegistry.getAllClips().values()) {
            if (clip.isRunning()) {
                clip.stop();
            }
        }
    }

    // ----- HELPER METHODS -----
    /**
     * Adjusts the volume for all loaded clips.
     */
    private static void applyVolumeToAllClips() {
        for (Clip clip : AudioRegistry.getAllClips().values()) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (gainControl != null) {
                float min = gainControl.getMinimum();
                float max = gainControl.getMaximum();
                float newVolume = min + (volume * (max - min));
                gainControl.setValue(newVolume);
            } else {
                System.err.println("SoundManager: Master Gain control not supported for a clip.");
            }
        }
    }
}
