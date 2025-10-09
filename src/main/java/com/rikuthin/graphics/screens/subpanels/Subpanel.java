package com.rikuthin.graphics.screens.subpanels;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.rikuthin.interfaces.Renderable;
import com.rikuthin.loaders.ImageLoader;

/**
 * Abstract base class for all game screens (e.g., main menu, gameplay).
 * Provides a structure for updating and rendering screens.
 */
public abstract class Subpanel extends JPanel implements Renderable {

    // ----- INSTANCE VARIABLES -----
    /**
     * The file path of the image used for the background
     */
    protected String backgroundImageFilepath;
    /**
     * The background image
     */
    protected BufferedImage backgroundImage;

    // ----- CONSTRUCTORS -----
    /**
     * General constructor to initialise shared fields for various Subpanel
     * subclasses.
     *
     * @param width Width of the panel in pixels
     * @param height Height of the panel in pixels
     * @param backgroundImageFilepath The file path of the image used for the
     * background
     */
    protected Subpanel(final int width, final int height, final String backgroundImageFilepath) {
        Dimension panelSize = new Dimension(width, height);
        setPreferredSize(panelSize);
        setMinimumSize(panelSize);
        setMaximumSize(panelSize);
        setBackgroundImage(backgroundImageFilepath);
    }

    // ----- SETTERS -----
    /**
     * Sets the background image (and its filepath) for this subpanel.
     *
     * @param backgroundImageFilepath The image filepath.
     */
    public final void setBackgroundImage(final String backgroundImageFilepath) {
        this.backgroundImageFilepath = backgroundImageFilepath;
        backgroundImage = ImageLoader.loadBufferedImage(backgroundImageFilepath);

        if (backgroundImage == null) {
            System.err.println(String.format(
                    "%s: Could not load background image <'%s'>.",
                    this.getClass().getName(),
                    backgroundImageFilepath
            ));
        }
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Override the paintComponent method to render the game on the screen. This
     * is where custom rendering will occur.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (g instanceof Graphics2D g2d) {
            safeRender(g2d);
        }
    }

    /**
     * Renders the screen's graphical components.
     *
     * By default, only renders the background image (if one is set).
     */
    @Override
    public void render(Graphics2D g2d) {
        if (backgroundImage == null) {
            System.err.println(String.format(
                    "%s: Could not load background image <'%s'>.",
                    this.getClass().getName(),
                    backgroundImageFilepath
            ));
            return;
        }
        g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
    }
}
