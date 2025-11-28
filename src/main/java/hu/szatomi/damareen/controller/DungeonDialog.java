package hu.szatomi.damareen.controller;

import hu.szatomi.damareen.model.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class DungeonDialog {

    public static Dialog<Dungeon> build() {

        Dialog<Dungeon> dialog = new Dialog<>();
        dialog.setTitle("Új kazamata");

        ButtonType ok = new ButtonType("Hozzáadás", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);

        TextField nameField = new TextField();
        ComboBox<DungeonType> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(DungeonType.values());

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);

        gp.add(new Label("Név:"), 0, 0);
        gp.add(nameField, 1, 0);

        gp.add(new Label("Típus:"), 0, 1);
        gp.add(typeBox, 1, 1);

        dialog.getDialogPane().setContent(gp);

        /*dialog.setResultConverter(btn -> {
            if (btn == ok) {
                Dungeon d = new Dungeon(DungeonType.KIS, "asd", );
                return d;
            }
            return null;
        });*/

        return dialog;
    }
}
