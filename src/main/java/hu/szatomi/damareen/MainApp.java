package hu.szatomi.damareen;

import hu.szatomi.damareen.manager.SceneManager;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainApp extends Application {

    private final GameEngine engine = new GameEngine();

    @Override
    public void start(Stage stage) {

        Font.loadFont(
                getClass().getResourceAsStream("/hu/szatomi/damareen/ui/font/Jersey10-Regular.ttf"),
                10
        );

        SceneManager.init(stage);
        SceneManager.get().loadScene("menu", engine);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
