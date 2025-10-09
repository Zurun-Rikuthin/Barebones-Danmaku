package com.rikuthin;

import javax.swing.SwingUtilities;

import com.rikuthin.graphics.GameFrame;

/**
 * The main entry point of the programme. This class should not be instantiated.
 */
public class App {

    // ----- STATIC VARIABLES -----
    /**
     * How often the app should refresh the rendered frame in milliseconds.
     * <p>
     * Note that game updates occur once per frame.
     */
    public static final long FRAME_RATE_MS = (long) 16.7;  // 16.7 ms is approx. 60 FPS

    // ----- CONSTRUCTORS -----
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private App() {
        // Suppress default constructor, ensuring no instances are created.
    }
    
    // ----- BUSINESS LOGIC METHODS -----
    /**
     * The entry point for the application. This method schedules the creation
     * of the {@link GameFrame} on the Event Dispatch Thread (EDT).
     *
     * @param args The command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Schedules GameFrame creation on the EDT
        SwingUtilities.invokeLater(GameFrame::new);
    }
}
