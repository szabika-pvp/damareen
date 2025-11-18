package hu.szatomi.damareen.controller;

import hu.szatomi.damareen.model.Card;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;

public class ControllerUtils {

    public static void newCardPane(Pane container, Card card, boolean leader) {

        FlowPane cardPane = new FlowPane();
        Label name = new Label(card.getName());
        Label stats = new Label(card.getBaseDamage() + "/" + card.getBaseHealth());
        Label type = new Label(card.getType().toString().toLowerCase());

        name.getStyleClass().add("name");
        stats.getStyleClass().add("stat");
        type.getStyleClass().add("type");

        name.setPadding(new Insets(0, 5, 0, 5));
        stats.setPadding(new Insets(0, 5, 0, 5));
        type.setPadding(new Insets(0, 5, 0, 5));

        name.setWrapText(true);
        name.setAlignment(Pos.CENTER);
        name.setTextAlignment(TextAlignment.CENTER);

        cardPane.getStyleClass().add("card");
        cardPane.setPrefWidth(100);
        cardPane.setPrefHeight(130);
        cardPane.setOrientation(Orientation.VERTICAL);
        cardPane.setAlignment(Pos.CENTER);

        cardPane.setUserData(card);

        if (leader) {
            name.getStyleClass().add("leader-text");
            stats.getStyleClass().add("leader-text");
            type.getStyleClass().add("leader-text");
            cardPane.getStyleClass().add("leader");
        }

        cardPane.getChildren().addAll(name, stats, type);

        container.getChildren().add(cardPane);
    }
}
