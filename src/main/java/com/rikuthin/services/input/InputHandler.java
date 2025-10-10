package com.rikuthin.services.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

public class InputHandler implements KeyListener {

    private Set<Integer> pressedKeys = new HashSet<>(); // Tracks pressed keys

    // TODO: Add actual reference to the (improved) game/panel/whatever that'll respond to inputs handled here
    // private Game game;
    public InputHandler() {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());
        updateActions();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private void updateActions() {
        if (pressedKeys.contains(KeyEvent.VK_UP)) {
            // game.moveUp();
        }
        if (pressedKeys.contains(KeyEvent.VK_DOWN)) {
            // game.moveDown();
        }
        if (pressedKeys.contains(KeyEvent.VK_LEFT)) {
            // game.moveLeft();
        }
        if (pressedKeys.contains(KeyEvent.VK_RIGHT)) {
            // game.moveRight();
        }
        if (pressedKeys.contains(KeyEvent.VK_SPACE)) {
            // game.performAction();
        }
    }
}
