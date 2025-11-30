package hu.szatomi.damareen.controller;

import hu.szatomi.damareen.GameEngine;
import hu.szatomi.damareen.manager.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.nio.file.Path;

public class MenuController {

    @FXML
    private VBox newGamePopup;

    @FXML
    private Pane continueButton;

    File save = new File("game.json");

    @FXML
    private void initialize() {
        if (save.exists()) {
            continueButton.setDisable(false);
            continueButton.setOpacity(1);
        }
    }

    @FXML
    private void newGame() {

        if (save.exists()) {
            newGamePopup.setVisible(true);
        } else {
            SceneManager.get().loadScene("main", new GameEngine());
        }
    }

    @FXML
    private void closeGamePopup() {
        newGamePopup.setVisible(false);
    }

    @FXML
    private void ignorePopup() {

        if (save.delete()) {
            System.out.println("Deleted the savefile");
        } else {
            System.out.println("Failed to delete the save file.");
        }

        newGamePopup.setVisible(false);
        SceneManager.get().loadScene("main", new GameEngine());
    }

    @FXML
    private void loadGame() {
        SceneManager.get().loadScene("main", new GameEngine());
    }

//    @FXML
//    private void gameMaster() {
//        SceneManager.get().loadScene("game_master", null);
//    }

    @FXML
    private void exit() {
        System.exit(0);
    }
}
