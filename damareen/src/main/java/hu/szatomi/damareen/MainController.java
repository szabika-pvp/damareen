package hu.szatomi.damareen;

import hu.szatomi.damareen.logic.GameEngine;
import hu.szatomi.damareen.model.*;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML
    private StackPane rootPane;

    @FXML
    private FlowPane simpleCardsContainer;
    @FXML
    private FlowPane leaderCardsContainer;
    @FXML
    private FlowPane dungeonsContainer;
    @FXML
    private FlowPane collectionContainer;
    @FXML
    private FlowPane deckContainer;
    @FXML
    private FlowPane fightButton;

    private GameEngine engine = new GameEngine();
    private Dungeon currentDungeon = null;

    @FXML
    public void initialize() {

        engine.addCard("Arin", 2, 6, CardType.VIZ);
        engine.addCard("Liora", 2, 4, CardType.LEVEGO);
        engine.addCard("Nerun", 3, 3, CardType.TUZ);
        engine.addCard("Selia", 2, 6, CardType.VIZ);
        engine.addCard("Torak", 3, 4, CardType.FOLD);
        engine.addCard("Emera", 2, 5, CardType.LEVEGO);
        engine.addCard("Vorn", 2, 7, CardType.VIZ);
        engine.addCard("Kael", 3, 5, CardType.TUZ);
        engine.addCard("Myra", 2, 6, CardType.FOLD);
        engine.addCard("Thalen", 3, 5, CardType.LEVEGO);
        engine.addCard("Isara", 2, 6, CardType.VIZ);

        engine.addLeader("Lord Torak", "Torak", LeaderType.SEBZES);
        engine.addLeader("Priestess Selia", "Selia", LeaderType.ELETERO);

        engine.addDungeon(new String[] {"", "egyszeru", "Barlangi Portya", "Nerun", "sebzes"});
        engine.addDungeon(new String[] {"", "kis", "Osi Szentely", "Arin,Emera,Selia", "Lord Torak", "eletero"});
        engine.addDungeon(new String[] {"", "nagy", "A melyseg kiralynoje", "Liora,Arin,Selia,Nerun,Torak", "Priestess Selia"});

        engine.createPlayer();
        List<Card> emptyDeck = new ArrayList<>();
        engine.getPlayer().setDeck(new Deck(emptyDeck));

        engine.addToCollection("Arin");
        engine.addToCollection("Liora");
        engine.addToCollection("Selia");
        engine.addToCollection("Nerun");
        engine.addToCollection("Torak");
        engine.addToCollection("Emera");
        engine.addToCollection("Kael");
        engine.addToCollection("Myra");
        engine.addToCollection("Thalen");
        engine.addToCollection("Isara");

        for (Card card : engine.getSimpleCards().values()) {
            newCardPane(simpleCardsContainer, card, false);
        }

        for (Card card : engine.getLeaderCards().values()) {
            newCardPane(leaderCardsContainer, card, true);
        }

        for (Card card : engine.getPlayer().getCollection()) {
            newCardPane(collectionContainer, card, false);
        }

        for (Node card : collectionContainer.getChildren()) {

            ((FlowPane) card).cursorProperty().set(Cursor.HAND);

            Tooltip.install(card, new Tooltip("Bal klikk a paklihoz adáshoz"));

            card.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY &&
                    deckContainer.getChildren().size() < Math.ceil(collectionContainer.getChildren().size()) &&
                    card.getParent() != deckContainer
                ) {
                    addToDeck((FlowPane) card);
                }
                else if (mouseEvent.getButton() == MouseButton.SECONDARY &&
                    card.getParent() != collectionContainer
                ) {
                    removeFromDeck((FlowPane) card);
                }
            });
        }

        for (Dungeon dungeon : engine.getDungeons().values()) {

            FlowPane dungeonPane = new FlowPane();

            dungeonPane.setAlignment(Pos.CENTER_LEFT);
            dungeonPane.setHgap(5);
            dungeonPane.setVgap(5);
            dungeonPane.setPrefWidth(1180);
            dungeonPane.setPrefHeight(150);
            dungeonPane.setPadding(new Insets(10));
            dungeonPane.setPrefWrapLength(1300);
            dungeonPane.setCursor(Cursor.HAND);
            dungeonPane.getStyleClass().add("dungeon");

            dungeonPane.getStyleClass().add(
                dungeon.getType() == DungeonType.EGYSZERU ? "dungeon-simple" : (
                dungeon.getType() == DungeonType.KIS ? "dungeon-easy" :
                "dungeon-hard")
            );

            dungeonPane.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY && !dungeonPane.getStyleClass().contains("selected")) {
                    dungeonsContainer.getChildren().forEach(c -> c.getStyleClass().remove("selected"));
                    dungeonPane.getStyleClass().add("selected");

                    currentDungeon = dungeon;

                    if (!engine.getPlayer().getDeck().getCards().isEmpty() &&
                        fightButton.isDisabled() && currentDungeon != null
                    ) {
                        fightButton.setDisable(false);
                        fightButton.setOpacity(1);
                    }
                }
            });

            Label dungeonName = new Label(dungeon.getName());
            dungeonName.getStyleClass().add("dungeonName");
            dungeonPane.getChildren().add(dungeonName);

            for (Card enemy : dungeon.getEnemies()) newCardPane(dungeonPane, enemy, false);
            if (dungeon.hasLeader()) newCardPane(dungeonPane, dungeon.getLeader(), true);

            dungeonsContainer.getChildren().add(dungeonPane);
        }
    }

    @FXML
    private void startCombat() throws IOException {

        Combat combat = new Combat(engine);

        StackPane combatPane = new StackPane();
        combatPane.setAlignment(Pos.CENTER);
        combatPane.setPrefSize(1920, 1080);
        combatPane.getStyleClass().add("combat");

        VBox combatVBox = new VBox();
        combatVBox.setAlignment(Pos.CENTER);
        combatVBox.setSpacing(10);

        HBox dungeonHand = new HBox();
        dungeonHand.setAlignment(Pos.CENTER);
        dungeonHand.setFillHeight(false);
        dungeonHand.setSpacing(5);
        dungeonHand.setPadding(new Insets(10));
        HBox.setHgrow(dungeonHand, Priority.NEVER);
        dungeonHand.setMaxWidth(Region.USE_PREF_SIZE);
        dungeonHand.getStyleClass().add("cardHolder");

        HBox playingArea = new HBox();
        playingArea.setAlignment(Pos.CENTER);
        playingArea.setPrefSize(600, 400);
        playingArea.getStyleClass().add("cardHolder");
        HBox.setHgrow(playingArea, Priority.NEVER);
        playingArea.setMaxWidth(Region.USE_PREF_SIZE);

        HBox playerHand = new HBox();
        playerHand.setAlignment(Pos.CENTER);
        playerHand.setFillHeight(false);
        playerHand.setSpacing(5);
        playerHand.setPadding(new Insets(10));
        HBox.setHgrow(playerHand, Priority.NEVER);
        playerHand.setMaxWidth(Region.USE_PREF_SIZE);
        playerHand.getStyleClass().add("cardHolder");

        VBox.setVgrow(playerHand, Priority.NEVER);
        VBox.setVgrow(dungeonHand, Priority.NEVER);

        for (Card card : currentDungeon.getEnemies()) {
            newCardPane(dungeonHand, card, false);
        }

        if (currentDungeon.hasLeader()) newCardPane(dungeonHand, currentDungeon.getLeader(), true);

        for (Card card : engine.getPlayer().getDeck().getCards()) {
            newCardPane(playerHand, card, false);
        }

        FadeTransition ft = new FadeTransition(Duration.millis(300), combatPane);
        combatPane.setOpacity(0);
        ft.setToValue(1);
        ft.play();

        combatPane.getChildren().add(combatVBox);

        combatVBox.getChildren().add(dungeonHand);
        combatVBox.getChildren().add(playingArea);
        combatVBox.getChildren().add(playerHand);
        rootPane.getChildren().add(combatPane);

        //combat.start(currentDungeon, "");

    }

    public void closeCombat() {
        rootPane.getChildren().remove(rootPane.getChildren().size() - 1);
    }

    private void newCardPane(Pane container, Card card, boolean leader) {

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

    private void addToDeck(FlowPane card) {
        engine.getPlayer().getDeck().addCard((Card) card.getUserData());
        deckContainer.getChildren().add(card);
        Tooltip.install(card, new Tooltip("Jobb klikk a pakliból kiszedéshez"));

        if (fightButton.isDisabled() && currentDungeon != null) {
            fightButton.setDisable(false);
            fightButton.setOpacity(1);
        }
    }

    private void removeFromDeck(FlowPane card) {
        engine.getPlayer().getDeck().removeCard((Card) card.getUserData());
        collectionContainer.getChildren().add(card);
        Tooltip.install(card, new Tooltip("Bal klikk a paklihoz adáshoz"));

        if (deckContainer.getChildren().size() == 0) {
            fightButton.setDisable(true);
            fightButton.setOpacity(0.5);
        }
    }
}
