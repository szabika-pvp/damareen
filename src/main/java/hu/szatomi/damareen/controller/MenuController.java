package hu.szatomi.damareen.controller;

import hu.szatomi.damareen.GameEngine;
import hu.szatomi.damareen.manager.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

public class MenuController {

    @FXML
    private void newGame() {
        SceneManager.get().loadScene("main", new GameEngine());
    }

    @FXML
    private void loadGame() {
        SceneManager.get().loadScene("main", new GameEngine());
    }

    @FXML
    private void gameMaster() {
        SceneManager.get().loadScene("game_master", null);
    }

    @FXML
    private void exit() {
        System.exit(0);
    }
}
