package com.rikuthin.services.core;

import java.lang.StackWalker.StackFrame;

/**
 * Manages the current state of the game (e.g., Running, Paused, Initializing)
 * and provides state validation/guard methods for other components.
 * <p>
 * This class is designed to be independent of the UI and Game Timer.
 */
public class GameStateManager {

    // ----- ENUMERATORS -----
    /**
     * Enum representing the possible game states.
     */
    public enum GameState {
        NOT_INITIALIZED,
        INITIALIZING,
        PAUSED,
        RUNNING,
        STOPPED
    }

    // ----- INSTANCE VARIABLES -----
    /**
     * 
     */
    private GameState currentState = GameState.NOT_INITIALIZED;

    // ----- GETTERS / STATE CHECKS -----
    public GameState getCurrentState() {
        return currentState;
    }

    public boolean isInitializing() {
        return currentState == GameState.INITIALIZING;
    }

    public boolean isPaused() {
        return currentState == GameState.PAUSED;
    }

    public boolean isRunning() {
        return currentState == GameState.RUNNING;
    }

    // ----- STATE TRANSITION METHODS -----
    /**
     * Sets the game state to INITIALIZING.
     */
    public void startInitialization() {
        if (currentState != GameState.NOT_INITIALIZED) {
            System.err.println("Cannot start initialization unless state is NOT_INITIALIZED.");
            return;
        }
        this.currentState = GameState.INITIALIZING;
    }

    /**
     * Sets the game state to RUNNING.
     */
    public void startRunning() {
        if (currentState == GameState.RUNNING) {
            return;
        }
        this.currentState = GameState.RUNNING;
        // In a real app, this would notify a listener to start the timer.
    }

    /**
     * Sets the game state to PAUSED.
     */
    public void pause() {
        if (currentState != GameState.RUNNING) {
            return;
        }
        this.currentState = GameState.PAUSED;
        // In a real app, this would notify a listener to stop the timer and show the UI.
    }

    /**
     * Resets the state for game cleanup.
     */
    public void stopAndClear() {
        this.currentState = GameState.NOT_INITIALIZED;
    }

    // ----- GUARD CLAUSES -----
    /**
     * Checks if the game is ready (not in NOT_INITIALIZED or INITIALIZING
     * states).
     *
     * @param methodName The name of the method making the check.
     */
    public void ensureInitialized(String methodName) {
        if (currentState == GameState.NOT_INITIALIZED || currentState == GameState.INITIALIZING) {
            StackWalker walker = StackWalker.getInstance();
            StackFrame caller = walker.walk(frames -> frames.skip(1).findFirst().orElse(null));

            throw new IllegalStateException(String.format(
                    "%s.%s: Cannot call %s() before initialization is complete.",
                    caller != null ? caller.getClassName() : "UnknownClass",
                    caller != null ? caller.getMethodName() : "UnknownMethod",
                    methodName
            ));
        }
    }

    /**
     * Checks if the game is currently RUNNING.
     *
     * @param methodName The name of the method making the check.
     */
    public void ensureRunning(String methodName) {
        if (currentState != GameState.RUNNING) {
            StackWalker walker = StackWalker.getInstance();
            StackFrame caller = walker.walk(frames -> frames.skip(1).findFirst().orElse(null));

            throw new IllegalStateException(String.format(
                    "%s.%s: Cannot call %s() while not in the RUNNING state. Current state: %s",
                    caller != null ? caller.getClassName() : "UnknownClass",
                    caller != null ? caller.getMethodName() : "UnknownMethod",
                    methodName,
                    currentState.name()
            ));
        }
    }
}
