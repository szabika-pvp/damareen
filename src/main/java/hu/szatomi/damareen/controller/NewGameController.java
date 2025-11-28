package hu.szatomi.damareen.controller;

import hu.szatomi.damareen.GameEngine;
import hu.szatomi.damareen.manager.EnvironmentManager;
import hu.szatomi.damareen.manager.SaveManager;
import hu.szatomi.damareen.manager.SceneManager;
import hu.szatomi.damareen.model.EnvironmentConfig;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class NewGameController {

    @FXML private ComboBox<String> environmentBox;
    @FXML private Spinner<Integer> difficultyBox;
    @FXML private TextField playerNameField;
    @FXML private Button startButton;

    private EnvironmentConfig selectedEnv;

    @FXML
    public void initialize() {

        // 1) ENVIRONMENT LIST
        try {
            environmentBox.getItems().addAll(
                    EnvironmentManager.listEnvironments()
            );

            environmentBox.setOnAction(e -> {
                try {
                    selectedEnv = EnvironmentManager.loadEnvironment(
                            environmentBox.getSelectionModel().getSelectedItem()
                    );
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void startGame() {

        String env = environmentBox.getValue();
        Integer diff = difficultyBox.getValue();
        String name = playerNameField.getText();

        if (env == null || diff == null || name.isBlank()) {
            showError("Minden mezőt tölts ki!");
            return;
        }

        try {
            // játék motor előkészítése
            GameEngine engine = new GameEngine();
            engine.loadState(SaveManager.load(selectedEnv.environmentName));
            engine.setDifficulty(diff);

            // player létrehozása
            engine.createPlayer();

            // főjátékra váltunk
            SceneManager.get().loadScene("main", engine);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Hiba történt az új játék indításakor.");
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(msg);
        a.showAndWait();
    }
}
