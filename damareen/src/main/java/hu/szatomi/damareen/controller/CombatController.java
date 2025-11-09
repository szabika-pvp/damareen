package hu.szatomi.damareen.controller;

import hu.szatomi.damareen.logic.GameEngine;
import hu.szatomi.damareen.model.*;
import hu.szatomi.damareen.model.CombatEngine;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class CombatController {

    @FXML private StackPane combatPane;
    @FXML private HBox dungeonHand;
    @FXML private HBox playerHand;

    @FXML private StackPane dungeonPlayingArea;
    @FXML private StackPane playerPlayingArea;

    private GameEngine engine;
    private Dungeon dungeon;

    private CombatEngine combatEngine;
    private MainController mainController;

    private final Map<String, Node> dungeonCardNodes = new HashMap<>();
    private final Map<String, Node> playerCardNodes = new HashMap<>();

    private Node currentEnemyCardNode;
    private Node currentPlayerCardNode;

    private int currentSpeed = 1500;
    private final Queue<CombatAction> actionQueue = new LinkedList<>();

    public void setEngine(GameEngine e) {
        this.engine = e;
    }

    public void setDungeon(Dungeon d) {
        this.dungeon = d;
    }

    public void setMainController(MainController c) {
        this.mainController = c;
    }

    public void startCombatUI() {
        combatEngine = new CombatEngine(engine.getPlayer(), dungeon);

        initCards();       // UI kártyák létrehozása
        fillNodeMaps();    // Node → cardName map feltöltése
        startCombat();     // harc indítása
    }

    private void initCards() {
        dungeonHand.getChildren().clear();
        playerHand.getChildren().clear();

        // dungeon ellenségek
        for (Card card : dungeon.getEnemies()) {
            newCardPane(dungeonHand, card, false);
        }

        // leader, ha van
        if (dungeon.hasLeader()) {
            newCardPane(dungeonHand, dungeon.getLeader(), true);
        }

        // játékos kártyái
        for (Card card : engine.getPlayer().getDeck().getCards()) {
            newCardPane(playerHand, card, false);
        }
    }

    private void fillNodeMaps() {

        dungeonCardNodes.clear();
        playerCardNodes.clear();

        for (Node n : dungeonHand.getChildren()) {
            Card c = (Card) n.getUserData();
            dungeonCardNodes.put(c.getName(), n);
        }

        for (Node n : playerHand.getChildren()) {
            Card c = (Card) n.getUserData();
            playerCardNodes.put(c.getName(), n);
        }
    }

    @FXML
    private void startCombat() {

        Thread t = new Thread(() -> {

            try {
                Thread.sleep(currentSpeed);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            while (!combatEngine.isFinished()) {

                // ha üres a queue, tölts egy új körből
                if (actionQueue.isEmpty()) {

                    CombatState state = combatEngine.nextTurn();

                    if (state.enemyAction() != null)
                        actionQueue.add(state.enemyAction());

                    if (state.playerAction() != null)
                        actionQueue.add(state.playerAction());
                }

                // ha már üres és a játék véget ért
                if (actionQueue.isEmpty()) break;

                // egy LÉPÉS feldolgozása
                CombatAction next = actionQueue.poll();

                Platform.runLater(() ->
                        handleAction(next, next.who().equals("kazamata"))
                );

                try {
                    Thread.sleep(currentSpeed);
                } catch (InterruptedException e) {
                    return;
                }
            }

            Platform.runLater(this::closeCombat);
        });

        t.setDaemon(true);
        t.start();

    }

    private void handleAction(CombatAction action, boolean enemySide) {

        switch (action.type()) {

            case "kijatszik" -> {
                if (enemySide)
                    spawnEnemyCard(action.card().getName());
                else
                    spawnPlayerCard(action.card().getName());
            }

            case "tamad" -> {
                if (enemySide)
                    applyDamageToPlayer(action);
                else
                    applyDamageToEnemy(action);
            }
        }
    }

    private void spawnEnemyCard(String cardName) {
        Node n = dungeonCardNodes.get(cardName);
        if (n == null) return;

        dungeonHand.getChildren().remove(n);
        dungeonPlayingArea.getChildren().add(n);

        currentEnemyCardNode = n;
    }

    private void spawnPlayerCard(String cardName) {
        Node n = playerCardNodes.get(cardName);
        if (n == null) return;

        playerHand.getChildren().remove(n);
        playerPlayingArea.getChildren().add(n);

        currentPlayerCardNode = n;
    }

    private void applyDamageToPlayer(CombatAction a) {
        setHpLabel(currentPlayerCardNode, a.targetRemainingHp(), a.attackedCard().getBaseDamage());
        if (a.targetRemainingHp() <= 0)
            playerPlayingArea.getChildren().remove(currentPlayerCardNode);
    }

    private void applyDamageToEnemy(CombatAction a) {
        setHpLabel(currentEnemyCardNode, a.targetRemainingHp(), a.attackedCard().getBaseDamage());
        if (a.targetRemainingHp() <= 0)
            dungeonPlayingArea.getChildren().remove(currentEnemyCardNode);
    }

    // ---------------------------
    // Helper: stat label frissítése
    // ---------------------------

    private void setHpLabel(Node n, int hp, int dmg) {
        if (n == null) return;
        Label hpLabel = (Label) n.lookup(".stat"); // vagy .hpLabel ha külön van
        if (hpLabel != null)
            hpLabel.setText(dmg + "/" + hp);
    }

    // ---------------------------
    // Combat UI bezárása
    // ---------------------------

    private void closeCombat() {

        mainController.updateCardNodes();
        ((StackPane) combatPane.getParent()).getChildren().remove(combatPane);
    }


    // ---------------------------
    // Card UI létrehozása (egyszerű)
    // ---------------------------

    private void newCardPane(Pane container, Card card, boolean leader) {

        VBox box = new VBox();
        box.getStyleClass().add("card");
        box.setSpacing(5);
        box.setPrefSize(100, 130);
        box.setAlignment(Pos.CENTER);

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

        if (leader) {
            name.getStyleClass().add("leader-text");
            stats.getStyleClass().add("leader-text");
            type.getStyleClass().add("leader-text");
            box.getStyleClass().add("leader");
        }

        box.getChildren().addAll(name, stats, type);

        box.setUserData(card);

        container.getChildren().add(box);
    }
}
