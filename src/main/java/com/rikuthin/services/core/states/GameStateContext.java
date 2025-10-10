package com.rikuthin.services.core.states;

import java.lang.StackWalker.StackFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the current state of the game (e.g., Running, Paused, Initializing)
 * and provides state validation/guard methods for other components.
 * <p>
 * This class is designed to be independent of the UI and game timer. It acts as
 * the core state controller, allowing other services to determine what logic
 * they can execute based on the current game mode.
 *
 * @see GameState
 * @see GameLoopCoordinator (Conceptual Facade/Mediator)
 */
public class GameStateContext {

    // ----- ENUMERATORS -----
    /**
     * Enum representing the possible game states, used by the
     * {@code GameStateManager} to control the flow and behavior of the main
     * game loop and user interface.
     */
    public enum GameState {
        /**
         * Initial state before any setup has begun. The game is essentially
         * dormant.
         */
        NOT_INITIALIZED,
        /**
         * Active state during the initial resource loading and component wiring
         * phase. Game logic should be suspended.
         */
        INITIALIZING,
        /**
         * Active state during the initial resource loading and component wiring
         * phase. Game logic should be suspended.
         */
        INITIALIZATION,
        /**
         * The game loop is suspended, but the game is still visible. Used for
         * pause menus or foreground interruptions.
         */
        PAUSED,
        /**
         * Normal active gameplay state. The game loop (update and render) is
         * fully engaged.
         */
        RUNNING,
        /**
         * The game loop is suspended, and the game is ready to return to a menu
         * or terminate. Used after a game-over or main menu transition.
         */
        STOPPED,
        /**
         * Resources are being loaded (I/O, async reads).
         */
        LOADING,
        /**
         * One-time setup and wiring of components; logic suspended.
         */
        INITIALIZING,
        /**
         * Ready for user input (menus) but not yet running gameplay.
         */
        READY,
        /**
         * Normal active gameplay; update & render loop engaged.
         */
        RUNNING,
        /**
         * Temporarily suspended gameplay (pause menus, modal dialogs).
         */
        PAUSED,
        /**
         * End-of-game state (game over) — can transition to menus or restart.
         */
        GAME_OVER,
        /**
         * Clean shutdown sequence in progress; resources being released.
         */
        SHUTTING_DOWN,
        /**
         * Terminal state after shutdown completes.
         */
        STOPPED,
        /**
         * Error state for unrecoverable failures; useful for diagnostics.
         */
        ERROR
    }

    // ----- STATIC VARIABLES -----
    /**
     * Logger for the {@code GameStateManager} class.
     */
    private static final Logger LOGGER = LogManager.getLogger(GameStateContext.class);

    // ----- INSTANCE VARIABLES -----
    /**
     * The current state of the game, initialized to
     * {@link GameState#NOT_INITIALIZED}.
     */
    private GameState currentState = GameState.NOT_INITIALIZED;

    // ----- GETTERS / STATE CHECKS -----
    /**
     * Retrieves the current state of the game.
     *
     * @return The current {@code GameState}.
     */
    public GameState getCurrentState() {
        return currentState;
    }

    /**
     * Returns {@code true} if the game is currently in the
     * {@link GameState#INITIALIZING} state.
     *
     * @return {@code true} if the current state is INITIALIZING.
     */
    public boolean isInitializing() {
        return currentState == GameState.INITIALIZING;
    }

    /**
     * Returns {@code true} if the game is currently in the
     * {@link GameState#PAUSED} state.
     *
     * @return {@code true} if the current state is PAUSED.
     */
    public boolean isPaused() {
        return currentState == GameState.PAUSED;
    }

    /**
     * Returns {@code true} if the game is currently in the
     * {@link GameState#RUNNING} state.
     *
     * @return {@code true} if the current state is RUNNING.
     */
    public boolean isRunning() {
        return currentState == GameState.RUNNING;
    }

    // ----- STATE TRANSITION METHODS -----
    /**
     * Transitions the game state to {@link GameState#INITIALIZING}.
     * <p>
     * This transition is only valid if the current state is
     * {@link GameState#NOT_INITIALIZED}.
     *
     * @throws IllegalStateException if this method is called from an invalid
     * game state.
     */
    public void startInitialization() {
        if (currentState != GameState.NOT_INITIALIZED) {
            String message = String.format(
                    "Cannot start initialization unless current state is NOT_INITIALIZED. Current state: %s",
                    currentState.name()
            );
            LOGGER.error(message);
            throw new IllegalStateException(message);
        }
        this.currentState = GameState.INITIALIZING;
        LOGGER.debug("State transitioned to INITIALIZING.");
    }

    /**
     * Transitions the game state to {@link GameState#RUNNING}.
     * <p>
     * This method is often called by the {@code GameLoopCoordinator} after
     * initialization or when resuming from the {@link GameState#PAUSED} state.
     */
    // public void startRunning() {
    //     if (currentState == GameState.RUNNING) {
    //         return;
    //     }
    //     this.currentState = GameState.RUNNING;
    //     // In a real app, this would notify a listener to start the timer.
    // }
    /**
     * Transitions the game state to {@link GameState#INITIALIZING}.
     * <p>
     * This transition is only valid if the current state is
     * {@link GameState#NOT_INITIALIZED}.
     *
     * @throws IllegalStateException if this method is called from an invalid
     * game state.
     */
    public void startRunning() {
        if (currentState != GameState.NOT_INITIALIZED) {
            String message = String.format(
                    "Cannot start initialization unless current state is NOT_INITIALIZED. Current state: %s",
                    currentState.name()
            );
            LOGGER.error(message);
            throw new IllegalStateException(message);
        }
        this.currentState = GameState.INITIALIZING;
        LOGGER.debug("State transitioned to INITIALIZING.");
    }

    /**
     * Transitions the game state to {@link GameState#PAUSED}.
     * <p>
     * This transition is only valid if the current state is
     * {@link GameState#RUNNING}.
     */
    public void pause() {
        if (currentState != GameState.RUNNING) {
            return;
        }
        this.currentState = GameState.PAUSED;
        // In a real app, this would notify a listener to stop the timer and show the UI.
    }

    /**
     * Transitions the state back to {@link GameState#NOT_INITIALIZED}.
     * <p>
     * This is typically used to reset the system for game cleanup, allowing the
     * screen to clear active entities and memory.
     */
    public void stopAndClear() {
        this.currentState = GameState.NOT_INITIALIZED;
    }

    // ----- GUARD CLAUSES -----
    /**
     * Checks if the game is fully initialized and operational (not in
     * {@link GameState#NOT_INITIALIZED} or {@link GameState#INITIALIZING}).
     * <p>
     * Use this method in service logic to prevent core functions (like entity
     * updates) from running before the game environment is ready.
     *
     * @param methodName The name of the method making the check, used for the
     * exception message.
     * @throws IllegalStateException if the game is not yet initialized or is
     * currently initializing.
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
     * Checks if the game is currently in the {@link GameState#RUNNING} state.
     * <p>
     * Use this method to prevent actions (like player movement or enemy
     * spawning) from occurring while the game is paused or stopped.
     *
     * @param methodName The name of the method making the check, used for the
     * exception message.
     * @throws IllegalStateException if the current state is not RUNNING.
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
