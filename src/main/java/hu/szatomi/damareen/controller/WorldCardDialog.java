package hu.szatomi.damareen.controller;

import hu.szatomi.damareen.model.Card;
import hu.szatomi.damareen.model.CardType;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class WorldCardDialog {

    public static Dialog<Card> build() {

        Dialog<Card> dialog = new Dialog<>();
        dialog.setTitle("Új világkártya");

        ButtonType ok = new ButtonType("Hozzáadás", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);

        TextField nameField = new TextField();
        TextField dmgField = new TextField();
        TextField hpField = new TextField();
        ComboBox<CardType> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(CardType.values());

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);

        gp.add(new Label("Név:"), 0, 0);
        gp.add(nameField, 1, 0);

        gp.add(new Label("Sebzés:"), 0, 1);
        gp.add(dmgField, 1, 1);

        gp.add(new Label("Élet:"), 0, 2);
        gp.add(hpField, 1, 2);

        gp.add(new Label("Típus:"), 0, 3);
        gp.add(typeBox, 1, 3);

        dialog.getDialogPane().setContent(gp);

        dialog.setResultConverter(btn -> {
            if (btn == ok) {
                return new Card(
                        nameField.getText(),
                        Integer.parseInt(dmgField.getText()),
                        Integer.parseInt(hpField.getText()),
                        typeBox.getValue()
                );
            }
            return null;
        });

        return dialog;
    }
}
