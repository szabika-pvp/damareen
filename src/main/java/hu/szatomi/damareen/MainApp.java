package hu.szatomi.damareen;

import hu.szatomi.damareen.controller.ControllerUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    private final GameEngine engine = new GameEngine();

    @Override
    public void start(Stage stage) {
        ControllerUtils.setEngine(engine);
        SceneManager.init(stage);
        SceneManager.get().loadScene("menu");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
