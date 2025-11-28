package hu.szatomi.damareen.controller;

import hu.szatomi.damareen.GameEngine;
import hu.szatomi.damareen.manager.SaveManager;
import hu.szatomi.damareen.manager.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.nio.file.*;

public class LoadGameController {

    @FXML
    private ListView<String> saveList;

    @FXML
    public void initialize() {
        try (DirectoryStream<Path> ds =
                     Files.newDirectoryStream(Path.of("src/main/resources/saves/"), "*.json")) {

            for (Path p : ds)
                saveList.getItems().add(p.getFileName().toString().replace(".json", ""));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadSelected() {
        try {
            String name = saveList.getSelectionModel().getSelectedItem();
            var state = SaveManager.load(name);

            GameEngine engine = new GameEngine();
            engine.loadState(state);
            SceneManager.get().loadScene(state.getCurrentScene(), engine);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
