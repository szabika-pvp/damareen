package hu.szatomi.damareen.controller;

import hu.szatomi.damareen.GameEngine;
import hu.szatomi.damareen.model.*;
import hu.szatomi.damareen.logic.CombatEngine;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CombatController {

    @FXML private HBox dungeonHand;
    @FXML private HBox playerHand;

    @FXML private StackPane dungeonPlayingArea;
    @FXML private StackPane playerPlayingArea;

    private GameEngine engine;
    private Dungeon dungeon;

    private CombatEngine combatEngine;

    private final Map<String, Node> dungeonCardNodes = new HashMap<>();
    private final Map<String, Node> playerCardNodes = new HashMap<>();

    private Node currentEnemyCardNode;
    private Node currentPlayerCardNode;

    private final Queue<CombatAction> actionQueue = new LinkedList<>();

    private ScheduledExecutorService executor;
    private Runnable onClose;

    public void setOnClose(Runnable r) {
        this.onClose = r;
    }

    public void setDungeon(Dungeon d) {
        this.dungeon = d;
    }

    @FXML
    public void initialize() {
        engine = ControllerUtils.getEngine();
        startCombatUI();
    }

    public void startCombatUI() {
        combatEngine = new CombatEngine(engine.getPlayer(), dungeon);

        initCards();       // UI kártyák létrehozása
        fillNodeMaps();    // node map-ek feltöltése
        startCombat();     // harc indítása
    }

    private void initCards() {
        dungeonHand.getChildren().clear();
        playerHand.getChildren().clear();

        // dungeon ellenségek
        for (Card card : dungeon.getEnemies()) {
            ControllerUtils.newCardPane(dungeonHand, card, false);
        }

        // leader, ha van
        if (dungeon.hasLeader()) {
            ControllerUtils.newCardPane(dungeonHand, dungeon.getLeader(), true);
        }

        // játékos kártyái
        for (Card card : engine.getPlayer().getDeck().getCards()) {
            ControllerUtils.newCardPane(playerHand, card, false);
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
        executor = Executors.newSingleThreadScheduledExecutor();

        int playSpeed = 1000;
        executor.scheduleAtFixedRate(() -> {

            if (combatEngine.isFinished()) {
                Platform.runLater(this::closeCombat);
                executor.shutdown();
                return;
            }

            if (actionQueue.isEmpty()) {
                CombatState state = combatEngine.nextTurn();

                if (state.enemyAction() != null)
                    actionQueue.add(state.enemyAction());

                if (state.playerAction() != null)
                    actionQueue.add(state.playerAction());
            }

            CombatAction next = actionQueue.poll();

            Platform.runLater(() -> {
                if (next != null && !combatEngine.isFinished()) {
                    handleAction(next, next.who().equals("kazamata"));
                }
            });

        }, playSpeed, playSpeed, TimeUnit.MILLISECONDS);
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

        //dungeonHand.getChildren().remove(n);
        dungeonPlayingArea.getChildren().add(n);

        currentEnemyCardNode = n;
    }

    private void spawnPlayerCard(String cardName) {
        Node n = playerCardNodes.get(cardName);
        if (n == null) return;

        //playerHand.getChildren().remove(n);
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

    // stat label frissítése
    private void setHpLabel(Node n, int hp, int dmg) {
        if (n == null) return;
        Label hpLabel = (Label) n.lookup(".stat"); // vagy .hpLabel ha külön van
        if (hpLabel != null)
            hpLabel.setText(dmg + "/" + hp);
    }

    // combat UI bezárása
    private void closeCombat() {
        if (onClose != null) onClose.run();
    }
}
