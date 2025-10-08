package com.rikuthin.managers;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import com.rikuthin.data.AudioRepository;
import com.rikuthin.loaders.AudioLoader;

/**
 * The {@code AudioManager} is the primary service responsible for the playback
 * behaviour of all audio {@link Clip} objects stored within the
 * {@link AudioRepository}.
 */
public class AudioManager {

    // ----- STATIC VARIABLES -----
    /**
     * Singleton instance of {@link AudioManager}.
     */
    private static AudioManager INSTANCE;

    /**
     * The repository instance used to access and mutate the collection of
     * loaded audio files.
     */
    private static final AudioRepository REPOSITORY = AudioRepository.getInstance();

    // ----- INSTANCE VARIABLES -----
    /**
     * Volume control (range: 0.0 to 1.0).
     */
    private float volume;

    // ----- CONSTRUCTORS -----
    /**
     * Private constructor to enforce the singleton pattern.
     */
    private AudioManager() {
        volume = 0.5f; // Default volume
        AudioLoader.loadAudioFromJson(); // TODO: Change this to establish a proper asset loading order
    }

    // ----- SINGLETON GETTER -----
    /**
     * Returns the singleton instance of the {@link AudioManager}.
     *
     * @return The single instance of {@link AudioManager}.
     */
    public static AudioManager getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new AudioManager();
        }
        return INSTANCE;
    }

    // ----- GETTERS -----
    /**
     * Gets the current volume level.
     *
     * @return The volume level (range: 0.0 to 1.0).
     */
    public float getVolume() {
        return volume;
    }

    /**
     * Checks if a given sound clip (by key) is currently playing.
     *
     * @param key The key of the sound clip to check.
     * @return {@code true} if the clip is currently running, {@code false}
     * otherwise.
     */
    public boolean isClipPlaying(String key) {
        Clip clip = REPOSITORY.getClip(key);
        return clip != null && clip.isRunning();
    }

    // ----- SETTERS -----
    /**
     * Sets the global volume level.
     *
     * @param volume The volume level (range: 0.0 to 1.0).
     */
    public void setVolume(float volume) {
        this.volume = Math.clamp(volume, 0.0f, 1.0f);
        applyVolumeToAllClips();
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Plays a sound clip with the current global volume..
     *
     * @param key The key of the sound clip.
     * @param looping If {@code true}, the sound will loop continuously.
     */
    public void playClip(String key, boolean looping) {
        Clip clip = REPOSITORY.getClip(key);
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
    public void playClip(String key, boolean looping, float volume) {
        Clip clip = REPOSITORY.getClip(key);
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
    public void stopClip(String key) {
        Clip clip = REPOSITORY.getClip(key);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    /**
     * Stops all currently playing sound clips.
     */
    public void stopAll() {
        for (Clip clip : REPOSITORY.getAllClips().values()) {
            if (clip.isRunning()) {
                clip.stop();
            }
        }
    }

    // ----- HELPER METHODS -----
    /**
     * Adjusts the volume for all loaded clips.
     */
    private void applyVolumeToAllClips() {
        for (Clip clip : REPOSITORY.getAllClips().values()) {
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
