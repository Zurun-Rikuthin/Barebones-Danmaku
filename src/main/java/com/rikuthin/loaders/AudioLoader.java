package com.rikuthin.loaders;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.rikuthin.data.AudioRepository;

/**
 * Utility class responsible for the low-level loading of {@link Clip} audio
 * resources from the file system or classpath.
 * <p>
 * This class ensures that audio files are correctly read, converted to a
 * supported format (if necessary), and stored in the {@link AudioRepository}.
 */
public class AudioLoader {

    // ----- STATIC VARIABLES -----
    /**
     * The repository instance used to access and mutate the collection of
     * loaded audio files.
     */
    private static final AudioRepository REPOSITORY = AudioRepository.getInstance();
    /**
     * Directory where sound files are stored, relative to the classpath.
     */
    private static final String SOUNDS_DIRECTORY = "/sounds/";
    /**
     * The extension for supported audio files (i.e., ".wav").
     */
    private static final String FILE_EXTENSION = ".wav";

    // ----- CONSTRUCTORS -----
    /**
     * Private constructor to prevent instantiation.
     */
    private AudioLoader() {
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Reads audio clip configuration from a JSON file and loads all defined
     * audio assets into the {@link AudioRepository}.
     */
    public static void loadAudioFromJson() {
        // TODO: Replace hardcoding with config file logic.
        loadAndStoreClip("goblinsDance", "Goblins_Dance_(Battle).wav");
        loadAndStoreClip("goblinsDen", "Goblins_Den_(Regular).wav");
    }

    /**
     * Loads an audio clip from a specified file path.
     * <p>
     * This method attempts to load the file as a classpath resource first (for
     * bundled JAR files) and falls back to loading it as a regular file. It
     * also handles audio format conversion if the native format is unsupported.
     *
     * @param filePath The path to the audio file (relative to classpath or file
     * system).
     * @param key The logical key used for logging purposes.
     * @return A {@link Clip} instance containing the loaded audio data.
     * @throws IllegalArgumentException If the file path is empty or
     * {@code null}.
     * @throws IOException If there is an error reading the file or the file is
     * not found.
     * @throws UnsupportedAudioFileException If the audio format is not
     * supported or cannot be converted.
     * @throws LineUnavailableException If no audio line is available for
     * creating the clip.
     */
    public static Clip loadClipFromAudioFile(final String filePath, final String key) throws IllegalArgumentException, IOException, UnsupportedAudioFileException, LineUnavailableException {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("AudioLoader: Must provide a valid file path for the audio clip.");
        }

        AudioInputStream audioIn = null;
        Clip clip;

        try {
            // Try loading as a resource (classpath)
            URL audioUrl = AudioLoader.class.getResource(filePath);
            if (audioUrl != null) {
                audioIn = AudioSystem.getAudioInputStream(audioUrl);
            } else {
                // Try loading as a normal file
                File file = new File(filePath);
                if (!file.exists() || !file.isFile()) {
                    throw new IOException("AudioLoader: Audio file not found: " + filePath);
                }
                audioIn = AudioSystem.getAudioInputStream(file);
            }

            // Get the original audio format
            AudioFormat baseFormat = audioIn.getFormat();

            // Check if the format is supported by the system
            if (isSupportedFormat(baseFormat)) {
                clip = AudioSystem.getClip();
                clip.open(audioIn);
            } else {
                // Attempt to convert to a universally supported format (16-bit PCM)
                AudioInputStream convertedAudioIn = convertToSupportedFormat(audioIn, baseFormat);
                clip = AudioSystem.getClip();
                clip.open(convertedAudioIn);
            }
        } catch (UnsupportedAudioFileException | LineUnavailableException e) {
            throw new IOException("AudioLoader: Unsupported audio format or no available audio line: " + filePath, e);
        } finally {
            if (audioIn != null) {
                audioIn.close();
            }
        }
        System.out.println("AudioLoader: Loaded audio clip <'" + key + "'>");
        return clip;
    }

    // ----- HELPER METHODS -----
    /**
     * Loads an audio clip from the specified file and stores it in the
     * {@link AudioRepository}.
     *
     * @param key The key under which the clip is stored.
     * @param fileName The file name of the audio file.
     * @throws IllegalArgumentException If either the key or file path are blank
     * or {@code null}.
     * @throws IOException If there is an error reading the file or it does not
     * exist.
     * @throws UnsupportedAudioFileException If the audio format is not
     * supported.
     * @throws LineUnavailableException If no audio line is available for
     * playback.
     */
    private static void loadAndStoreClip(String key, String fileName) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("AudioLoader: Key cannot be null or empty.");
        }

        try {
            REPOSITORY.addClip(key, loadClipFromAudioFile(SOUNDS_DIRECTORY + fileName, key));
        } catch (IOException e) {
            System.err.println("Failed to load clip (I/O error): " + fileName + " - " + e.getMessage());
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Failed to load clip (Unsupported format): " + fileName + " - " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("Failed to load clip (Audio line unavailable): " + fileName + " - " + e.getMessage());
        }
    }

    /**
     * Loads a list of all supported audio files from the sounds directory.
     *
     * @return A list of file names (e.g., "music.wav", "hit.wav"), or an
     * empty list if an error occurs.
     */
    public List<String> getAudioFiles() {
        Path soundPath = Paths.get(SOUNDS_DIRECTORY);

        // 1. Check if the directory exists and is a directory
        if (!Files.exists(soundPath) || !Files.isDirectory(soundPath)) {
            System.err.println("Sound directory not found or is not a directory: " + SOUNDS_DIRECTORY);
            return Collections.emptyList();
        }

        try (Stream<Path> walk = Files.list(soundPath)) {
            return walk
                    // 2. Filter out directories, keeping only files
                    .filter(Files::isRegularFile)
                    // 3. Convert Path to its file name (String)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    // 4. Filter for files ending with the specified extension, ignoring case
                    .filter(fileName -> fileName.toLowerCase().endsWith(FILE_EXTENSION))
                    // 5. Collect the results into a List
                    .collect(Collectors.toList());

        } catch (IOException e) {
            System.err.println("Error reading sound directory: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Checks if the given audio format is supported by the system.
     *
     * @param format The audio format to check.
     * @return true if the format is supported, false otherwise.
     */
    private static boolean isSupportedFormat(AudioFormat format) {
        // Try to find a line that supports this audio format
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        return AudioSystem.isLineSupported(info);
    }

    /**
     * Converts the provided audio stream to a supported audio format (16-bit
     * PCM).
     *
     * @param audioIn The original audio input stream.
     * @param baseFormat The original audio format.
     * @return The converted audio input stream in a supported format (16-bit
     * PCM).
     * @throws UnsupportedAudioFileException If the audio format is unsupported.
     */
    private static AudioInputStream convertToSupportedFormat(AudioInputStream audioIn, AudioFormat baseFormat) throws UnsupportedAudioFileException {
        AudioFormat targetFormat = getSupportedAudioFormat(baseFormat);

        if (targetFormat != null) {
            // Convert to supported format
            return AudioSystem.getAudioInputStream(targetFormat, audioIn);
        } else {
            throw new UnsupportedAudioFileException("AudioLoader: No supported audio format found for conversion.");
        }
    }

    /**
     * Returns a supported audio format (16-bit PCM) for a given audio format if
     * it's unsupported.
     *
     * @param baseFormat The audio format that needs to be converted.
     * @return A supported audio format (16-bit PCM) or null if no conversion is
     * possible.
     */
    private static AudioFormat getSupportedAudioFormat(AudioFormat baseFormat) {
        // Try converting to 16-bit PCM, stereo, little-endian
        if (baseFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
            return new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED, // PCM_SIGNED encoding
                    baseFormat.getSampleRate(), // Same sample rate
                    16, // 16-bit depth
                    baseFormat.getChannels(), // Same number of channels (stereo/mono)
                    baseFormat.getChannels() * 2, // 2 bytes per frame (16-bit PCM)
                    baseFormat.getSampleRate(), // Same frame rate
                    false // Little-endian
            );
        }
        return null; // If the format is already PCM_SIGNED, no conversion is needed.
    }
}
