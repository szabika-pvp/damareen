package hu.szatomi.damareen;

import hu.szatomi.damareen.logic.GameEngine;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class Main {
    public static void main(String[] args) throws Exception {

        GameEngine engine = new GameEngine();

        // teszt mód
        if (args.length == 1 && !args[0].equals("--ui")) {
            new TestRunner(engine).run(args[0]);
            return;
        }

        // játék mód
        if (args.length == 1 && args[0].equals("--ui")) {

            MainApp.main(args);
            return;
        }

        System.out.println("Használat: .\\run.bat [mappa] vagy --ui");
    }
}
