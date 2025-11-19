package hu.szatomi.damareen;

import hu.szatomi.damareen.model.EngineAware;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class SceneManager {

    private static SceneManager instance;

    public static SceneManager get() {
        return instance;
    }

    public static void init(Stage stage) {
        instance = new SceneManager(stage);
    }

    private final Stage stage;
    private final Map<String, Parent> cache = new HashMap<>();

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    public void loadScene(String name) {
        try {
            Parent view = cache.get(name);

            if (view == null) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/hu/szatomi/damareen/ui/" + name + ".fxml")
                );

                view = loader.load();
                cache.put(name, view);
            }

            stage.setScene(new Scene(view));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
