package hu.szatomi.damareen.controller;

import hu.szatomi.damareen.GameEngine;
import hu.szatomi.damareen.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MainController implements EngineAware {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

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

    private GameEngine engine;
    private Dungeon currentDungeon = null;

    @Override
    public void setEngine(GameEngine engine) {
        this.engine = engine;
    }

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
            ControllerUtils.newCardPane(simpleCardsContainer, card, false);
        }

        for (Card card : engine.getLeaderCards().values()) {
            ControllerUtils.newCardPane(leaderCardsContainer, card, true);
        }

        for (Card card : engine.getPlayer().getCollection()) {
            ControllerUtils.newCardPane(collectionContainer, card, false);
        }

        for (Node card : collectionContainer.getChildren()) {

            card.cursorProperty().set(Cursor.HAND);

            Tooltip.install(card, new Tooltip("Bal klikk a paklihoz adáshoz"));

            card.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY &&
                    deckContainer.getChildren().size() < collectionContainer.getChildren().size() &&
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

            for (Card enemy : dungeon.getEnemies()) ControllerUtils.newCardPane(dungeonPane, enemy, false);
            if (dungeon.hasLeader()) ControllerUtils.newCardPane(dungeonPane, dungeon.getLeader(), true);

            dungeonsContainer.getChildren().add(dungeonPane);
        }
    }

    @FXML
    private void openCombat() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hu/szatomi/damareen/ui/combat.fxml"));
            StackPane combatRoot = loader.load();

            CombatController cc = loader.getController();
            cc.setDungeon(currentDungeon);

            cc.setOnClose(() -> {
                updateCardNodes();
                rootPane.getChildren().remove(combatRoot);
            });

            rootPane.getChildren().add(combatRoot);

        } catch (Exception e) {
            log.error("Probléma történt a combat indításakor", e);
        }
    }

    public void updateCardNodes() {
        for (Node node : rootPane.lookupAll(".card")) {
            Card curCard = (Card) node.getUserData();
            ((Label) node.lookup(".stat")).setText(curCard.getBaseDamage() + "/" + curCard.getBaseHealth());
        }
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

        if (deckContainer.getChildren().isEmpty()) {
            fightButton.setDisable(true);
            fightButton.setOpacity(0.5);
        }
    }
}
