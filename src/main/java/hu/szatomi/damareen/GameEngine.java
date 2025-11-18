package hu.szatomi.damareen;

import hu.szatomi.damareen.logic.CombatFileWriter;
import hu.szatomi.damareen.logic.CombatEngine;
import hu.szatomi.damareen.model.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import static java.lang.Math.min;

public class GameEngine {

    private Player player;

    private final Map<String, Card> simpleCards = new LinkedHashMap<>();
    private final Map<String, LeaderCard> leaderCards = new HashMap<>();
    private final Map<String, Dungeon> dungeons = new LinkedHashMap<>();

    public void createPlayer() {
        this.player = new Player();
    }

    public Player getPlayer() {
        return player;
    }

    public Map<String, Card> getSimpleCards() { return simpleCards; }
    public Map<String, LeaderCard> getLeaderCards() { return leaderCards; }
    public Map<String, Dungeon> getDungeons() { return dungeons; }

    public void addCard(String name, int dmg, int hp, CardType type) {
        simpleCards.put(shorten(name, 16), new Card(shorten(name, 16), dmg, hp, type));
    }

    public void addLeader(String newName, String baseName, LeaderType leaderType) {

        Card simpleCard = getCard(baseName);
        leaderCards.put(shorten(newName, 16), new LeaderCard(shorten(newName, 16), simpleCard, leaderType));
    }

    public void addDungeon(String[] args) {

        DungeonType type = DungeonType.valueOf(args[1].toUpperCase());
        String name = shorten(args[2], 20);

        // sima kártyák
        List<Card> enemies = new ArrayList<>();
        String[] enemyNames = args[3].split(",");
        for (String n : enemyNames) {
            enemies.add(simpleCards.get(n.trim()));
        }

        // leader kártya (kicsi és nagy esetén)
        LeaderCard boss = null;
        int leaderIndex = 4;

        if (type == DungeonType.KIS || type == DungeonType.NAGY) {
            boss = leaderCards.get(args[4].trim());
            leaderIndex = 5;
        }

        // reward (egyszerű és kicsi esetén, nagy kazamatánál NINCS)
        RewardType reward = null;
        if (type != DungeonType.NAGY) {
            reward = RewardType.valueOf(args[leaderIndex].trim().toUpperCase());
        }

        // összeállítás
        Dungeon dungeon = new Dungeon(type, name, enemies, boss, reward);
        dungeons.put(name, dungeon);

    }

    public void addToCollection(String cardName) {
        player.getCollection().add(getCard(cardName));
    }

    public void createDeck(String[] deck) {

        List<Card> cards = new ArrayList<>();

        for (String card : deck) {
            cards.add(getCard(card));
            if (cards.size() == Math.ceil((double) player.getCollection().size() / 2)) break;
        }

        player.setDeck(new Deck(cards));
    }

    public void startCombat(String dungeonName, String outputPath) throws IOException {
        CombatEngine combatEngine = new CombatEngine(player, dungeons.get(dungeonName));
        CombatFileWriter combatFileWriter = new CombatFileWriter(this, Path.of(outputPath));

        while (!combatEngine.isFinished()) {
            combatFileWriter.write(combatEngine.nextTurn(), combatEngine.getRound(), dungeons.get(dungeonName));
        }

        combatFileWriter.close();
    }

    // első kártya a világban, ami még NINCS meg a playernek
    public Card getFirstNotUsedCard() {

        for (Card worldCard : simpleCards.values()) {
            if (!player.getCollection().contains(worldCard)) {
                return worldCard;
            }
        }

        return null;
    }

    public Card getCard(String cardName) {
        return simpleCards.get(cardName.trim());
    }

    // világ exportálása
    public void exportWorld(String filePath) throws IOException {

        try (BufferedWriter w = Files.newBufferedWriter(Path.of(filePath))) {

            // SIMA KÁRTYÁK
            if (!simpleCards.isEmpty()) {
                for (Card c : simpleCards.values()) {
                    w.write("kartya;" + c.getName() + ";" + c.getDamage() + ";" + c.getBaseHealth() + ";" + c
                            .getType()
                            .toString()
                            .toLowerCase());
                    w.newLine();
                }
            }

            w.newLine();

            // VEZÉRKÁRTYÁK
            if (!leaderCards.isEmpty()) {
                for (LeaderCard lc : leaderCards.values()) {
                    w.write("vezer;" + lc.getName() + ";" + lc.getDamage() + ";" + lc.getBaseHealth() + ";" + lc
                            .getType()
                            .toString()
                            .toLowerCase());
                    w.newLine();
                }
            }

            w.newLine();

            // KAZAMATÁK
            if (!dungeons.isEmpty()) {
                for (Dungeon d : dungeons.values()) {

                    StringBuilder sb = new StringBuilder();
                    sb.append("kazamata;")
                            .append(d.getType().toString().toLowerCase()).append(";")
                            .append(d.getName()).append(";");

                    // enemy kártyák listája
                    for (int i = 0; i < d.getEnemies().size(); i++) {
                        sb.append(d.getEnemies().get(i).getName());
                        if (i < d.getEnemies().size() - 1) sb.append(",");
                    }

                    if (d.getLeader() != null) {
                        sb.append(";").append(d.getLeader().getName());
                    }

                    if (d.getReward() != null) {
                        sb.append(";").append(d.getReward().toString().toLowerCase());
                    }

                    w.write(sb.toString());
                    w.newLine();
                }
            }
        }
    }

    public String shorten(String s, int n) {
        return s.substring(0, min(s.length(), n));
    }

    // player exportálása
    public void exportPlayer(String filePath) throws IOException {

        try (BufferedWriter w = Files.newBufferedWriter(Path.of(filePath))) {

            // GYŰJTEMÉNY
            if (!player.getCollection().isEmpty()) {
                for (Card c : player.getCollection()) {
                    w.write("gyujtemeny;" + c.getName() + ";" + c.getDamage() + ";" + c.getBaseHealth() + ";" + c.getType().toString().toLowerCase());
                    w.newLine();
                }

                w.newLine();
            }

            // PAKLI
            if (player.getDeck() != null) {
                for (Card c : player.getDeck().getCards()) {
                    w.write("pakli;" + c.getName());
                    w.newLine();
                }
            }
        }
    }


}
