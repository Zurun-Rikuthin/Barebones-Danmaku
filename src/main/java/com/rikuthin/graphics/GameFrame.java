package com.rikuthin.graphics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import static com.rikuthin.App.FRAME_RATE_MS;
import com.rikuthin.graphics.screens.MainMenuScreen;
import com.rikuthin.graphics.screens.Screen;
import com.rikuthin.loaders.AnimationLoader;
import com.rikuthin.loaders.AudioLoader;

/**
 * The main window that renders the entire app.
 */
public final class GameFrame extends JFrame {

    // ----- STATIC VARIABLES -----
    /**
     * The width of the app window in pixels.
     */
    public static final int FRAME_WIDTH = 1024;
    /**
     * The height of the app window in pixels.
     */
    public static final int FRAME_HEIGHT = 720;

    // ----- INSTANCE VARIABLES -----
    /**
     * The {@link Timer} responsible for triggering the main game loop updates
     * and repaints. This controls the frame rate and central timing mechanism
     * of the game.
     */
    private final Timer gameLoopTimer;
    /**
     * A transient {@link BufferedImage} used as an off-screen drawing surface
     * (back buffer) for **double buffering**. All game rendering is performed
     * onto this image first, which is then quickly copied to the screen to
     * prevent flickering.
     */
    private final transient BufferedImage backBuffer;
    /**
     * A transient reference to the {@link Graphics2D} context used for
     * high-quality drawing operations (like rendering onto the back buffer).
     * <p>
     * It is marked {@code transient} because it is a short-lived resource
     * derived from the drawing surface, and cannot be reliably serialized
     * (saved/loaded).
     */
    private transient Graphics2D g2d;
    /**
     * The current {@link Screen} being displayed by the window.
     */
    private Screen currentScreen;

    // ----- CONSTRUCTORS -----
    /**
     * Constructor to initialize the GameFrame/app window.
     * <p>
     * Sets the current {@link Screen} to the {@link MainMenuScreen}.
     */
    public GameFrame() {
        AnimationLoader.loadAnimationsFromJson();
        AudioLoader.loadAudioFromJson();

        // TODO: Rework loading order
        setTitle("<Untitled Danmaku>");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Initialize double buffering
        backBuffer = new BufferedImage(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        g2d = backBuffer.createGraphics();

        setLocationRelativeTo(null);
        setVisible(true);

        setScreen(new MainMenuScreen(this));

        gameLoopTimer = new Timer((int) FRAME_RATE_MS, e -> {
            updateGame();
            renderGame();
            currentScreen.repaint();
        });
        gameLoopTimer.start();
    }

    /**
     * Paints the back buffer onto the JFrame.
     */
    @Override
    public void paint(java.awt.Graphics g) {
        super.paint(g);
        if (backBuffer != null) {
            g.drawImage(backBuffer, 0, 0, this);
        }
    }

    /**
     * Dynamically switches to a new screen, removing the old one to free up
     * memory.
     *
     * @param newScreen The new screen to display.
     */
    public void setScreen(final Screen newScreen) {
        if (currentScreen != null) {
            remove(currentScreen);
            currentScreen.cleanup();
            currentScreen = null;
        }

        currentScreen = newScreen;
        add(currentScreen);

        currentScreen.revalidate();
        currentScreen.repaint();
        currentScreen.setFocusable(true);
        currentScreen.requestFocusInWindow();
    }

    /**
     * Updates the game logic.
     */
    public void updateGame() {
        if (currentScreen != null) {
            currentScreen.update();
        }
    }

    /**
     * Renders the game onto the back buffer.
     */
    private void renderGame() {
        if (backBuffer == null || currentScreen == null) {
            return;
        }

        g2d = backBuffer.createGraphics();
        g2d.clearRect(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        currentScreen.safeRender(g2d);
    }

    /**
     * Stops the game loop.
     */
    public void stopGameLoop() {
        gameLoopTimer.stop();
    }
}
