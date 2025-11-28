package hu.szatomi.damareen.manager;

import hu.szatomi.damareen.GameEngine;
import hu.szatomi.damareen.model.EngineAware;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

import java.util.*;

public class SceneManager {

    private static SceneManager INSTANCE;
    private final Stage stage;
    private final Map<String, Parent> cache = new HashMap<>();

    private SceneManager(Stage stage) {
        this.stage = stage;
    }

    public static void init(Stage stage) {
        INSTANCE = new SceneManager(stage);
    }

    public static SceneManager get() {
        return INSTANCE;
    }

    public void loadScene(String name, GameEngine engine) {
        try {
            Parent root = cache.get(name);
            FXMLLoader loader = null;

            if (root == null) {
                loader = new FXMLLoader(
                        getClass().getResource("/hu/szatomi/damareen/ui/" + name + ".fxml")
                );
                root = loader.load();
                cache.put(name, root);
            }

            // controller injection
            if (loader != null && loader.getController() instanceof EngineAware ea) {
                ea.setEngine(engine);
            }

            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
