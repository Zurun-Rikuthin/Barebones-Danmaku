package com.rikuthin.graphics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import static com.rikuthin.App.FRAME_RATE_MS;
import com.rikuthin.graphics.animations.AnimationLoader;
import com.rikuthin.graphics.screens.MainMenuScreen;
import com.rikuthin.graphics.screens.Screen;

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
    private final Timer gameLoopTimer;
    private final transient BufferedImage backBuffer;
    private transient Graphics2D g2d;
    private Screen currentScreen;

    // ----- CONSTRUCTORS -----
    /**
     * Constructor to initialize the game frame, set the size, title, and add
     * the main menu and gameplay panels. Also initializes the GameManager
     * instance and sets the blaster and bubble panels.
     */
    public GameFrame() {
        AnimationLoader.loadDefaultAnimations();

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
     * Stops game loop.
     */
    public void stopGameLoop() {
        gameLoopTimer.stop();
    }
}
