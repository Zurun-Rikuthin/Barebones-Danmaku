package com.rikuthin.services.ui;

import java.awt.event.ActionEvent;

import com.rikuthin.graphics.GameFrame;
import com.rikuthin.graphics.dialogue.PauseMenuDialogue;
import com.rikuthin.graphics.screens.subpanels.GamePanel;
import com.rikuthin.graphics.screens.subpanels.InfoPanel;
import com.rikuthin.services.core.states.GameStateContext;

/**
 * Manages all game-related UI components, panels, and dialogues.
 * It handles input and forwards state change requests to the GameStateManager.
 */
public class GameUIManager {

    // ----- INSTANCE VARIABLES -----
    private final GameStateContext stateManager;
    private GamePanel gamePanel;
    private InfoPanel infoPanel;

    // ----- CONSTRUCTOR (Dependency Injection) -----

    /**
     * Constructs the GameUIManager, requiring a dependency on GameStateManager.
     * @param stateManager The state management instance.
     */
    public GameUIManager(GameStateContext stateManager) {
        this.stateManager = stateManager;
    }

    // ----- INIT METHODS -----

    /**
     * Sets the references to the main game panels.
     * @param gamePanel The panel where the game is displayed.
     * @param infoPanel The panel where the game information is displayed.
     */
    public void init(final GamePanel gamePanel, final InfoPanel infoPanel) {
        this.gamePanel = gamePanel;
        this.infoPanel = infoPanel;
    }

    // ----- UI GETTERS -----

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public InfoPanel getInfoPanel() {
        return infoPanel;
    }

    // ----- UI EVENT HANDLERS (Delegates to GameStateManager) -----

    /**
     * Action handler for the pause button click. Requests a game pause.
     *
     * @param e The action event triggered by the pause button.
     */
    public void onPause(ActionEvent e) {
        if (stateManager.isRunning()) {
            stateManager.pause();
            showPauseMenu();
        }
    }

    /**
     * Action handler for the resume button click. Requests the game to run.
     */
    private void onResume() {
        stateManager.startRunning();
    }

    /**
     * Displays the pause menu dialogue.
     */
    private void showPauseMenu() {
        // Need to ensure gamePanel is not null before using its ancestor
        if (gamePanel != null) {
            PauseMenuDialogue pauseMenuDialogue = new PauseMenuDialogue(
                    (GameFrame) gamePanel.getTopLevelAncestor(),
                    this::onResume
            );
            pauseMenuDialogue.setVisible(true);
        }
    }
}
