package hu.szatomi.damareen.controller;

import hu.szatomi.damareen.GameEngine;
import hu.szatomi.damareen.manager.SceneManager;
import hu.szatomi.damareen.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class MainController implements EngineAware {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @FXML
    private StackPane rootPane;

    @FXML
    private HBox rootHBox;
    @FXML
    private VBox leftPane;
    @FXML
    private VBox rightPane;

    @FXML
    private VBox pauseMenu;

    @FXML
    private VBox dungeonsContainer;
    @FXML
    private TilePane collectionContainer;
    @FXML
    private TilePane deckContainer;
    @FXML
    private FlowPane fightButton;

    private GameEngine engine;
    private Dungeon currentDungeon = null;

    @FXML
    public void initialize() {

        leftPane.prefWidthProperty().bind(rootHBox.widthProperty().multiply(0.5));
        rightPane.prefWidthProperty().bind(rootHBox.widthProperty().multiply(0.5));

        // későbbi futásra rakjuk, mert initialize idején még nincs scene
        Platform.runLater(() -> {
            rootPane.getScene().setOnKeyPressed(e -> {
                if (Objects.requireNonNull(e.getCode()) == KeyCode.ESCAPE) {
                    onEsc();
                }
            });
        });
    }

    @Override
    public void setEngine(GameEngine engine) {
        this.engine = engine;
        startEngine();
    }

    @FXML
    public void startEngine() {

        engine.load();


        for (Card card : engine.getPlayer().getCollection()) {
            ControllerUtils.newCardPane(collectionContainer, card, false);
        }

        for (Node card : collectionContainer.getChildren()) {

            card.cursorProperty().set(Cursor.HAND);

            card.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY &&
                    deckContainer.getChildren().size() < collectionContainer.getChildren().size() &&
                    card.getParent() != deckContainer
                ) {
                    addToDeck((VBox) card);
                }
                else if (mouseEvent.getButton() == MouseButton.SECONDARY &&
                    card.getParent() != collectionContainer
                ) {
                    removeFromDeck((VBox) card);
                }
            });
        }

        for (Card card : engine.getPlayer().getDeck().getCards()) {

            collectionContainer.getChildren().stream()
                    .filter(n -> {
                        Card c = (Card) n.getUserData();
                        return c.getName().equals(card.getName()); // vagy == ha klónozol
                    })
                    .findFirst().ifPresent(node -> deckContainer.getChildren().add(node));
        }

        for (Dungeon dungeon : engine.getDungeons().values()) {

            TilePane dungeonPane = new TilePane();

            dungeonPane.setAlignment(Pos.CENTER_LEFT);
            dungeonPane.setOrientation(Orientation.HORIZONTAL);
            dungeonPane.setHgap(5);
            dungeonPane.setVgap(5);
            dungeonPane.setPadding(new Insets(10));
            dungeonPane.setCursor(Cursor.HAND);
            dungeonPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
            dungeonPane.setPrefRows(2);
            dungeonPane.setPrefColumns(5);

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

//            Label dungeonName = new Label(dungeon.getName());
//            dungeonPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
//            dungeonName.getStyleClass().add("dungeonName");
//            dungeonName.setPrefSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
//            dungeonPane.getChildren().add(dungeonName);

            for (Card enemy : dungeon.getEnemies()) ControllerUtils.newCardPane(dungeonPane, enemy, false);
            if (dungeon.hasLeader()) ControllerUtils.newCardPane(dungeonPane, dungeon.getLeader(), true);

            dungeonsContainer.getChildren().add(dungeonPane);
        }
    }

    private void onEsc() {

        System.out.println("ASD");
        if (pauseMenu.isVisible()) closeMenu();
        else openMenu();
    }

    @FXML
    private void openMenu() {
        pauseMenu.setVisible(true);
    }

    @FXML
    private void closeMenu() {
        pauseMenu.setVisible(false);
    }

    @FXML
    private void mainMenu() {
        engine.save();
        SceneManager.get().loadScene("menu", engine);
    }

    @FXML
    private void saveExit() {
        engine.save();
        System.exit(0);
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

            cc.setEngine(engine);

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

    private void addToDeck(VBox card) {
        engine.getPlayer().getDeck().addCard((Card) card.getUserData());
        deckContainer.getChildren().add(card);

        if (fightButton.isDisabled() && currentDungeon != null) {
            fightButton.setDisable(false);
            fightButton.setOpacity(1);
        }
    }

    private void removeFromDeck(VBox card) {
        engine.getPlayer().getDeck().removeCardByName(((Card) card.getUserData()).getName());
        collectionContainer.getChildren().add(card);

        if (deckContainer.getChildren().isEmpty()) {
            fightButton.setDisable(true);
            fightButton.setOpacity(0.5);
        }
    }
}
