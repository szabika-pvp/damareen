package hu.szatomi.damareen.model;

import hu.szatomi.damareen.logic.GameEngine;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Queue;

public class Combat {

    private final GameEngine engine;

    public Combat(GameEngine engine) {
        this.engine = engine;
    }

    public void start(Dungeon dungeon, String outputPath) throws IOException {

        Player player = engine.getPlayer();

        for (Card c : player.getDeck().getCards()) {
            c.reset();
        }

        Queue<Card> playerQ = new LinkedList<>(player.getDeck().getCards());
        Queue<Card> enemyQ = new LinkedList<>(dungeon.getEnemies());

        if (dungeon.getLeader() != null) enemyQ.add(dungeon.getLeader());

        try (BufferedWriter w = Files.newBufferedWriter(Path.of(outputPath))) {

            w.write("harc kezdodik;" + dungeon.getName());
            w.newLine();

            int kor = 1;

            Card enemy = null;
            Card playerCard = null;

            while (!playerQ.isEmpty() && !enemyQ.isEmpty()) {

                w.newLine();

                // ===== 1. KAZAMATA LÉP =====

                if (enemy == null) { // első kör eleje

                    enemy = enemyQ.peek();
                    assert enemy != null;
                    logKijatszik(w, kor, "kazamata", enemy);

                } else if (enemy.getHealth() <= 0) { // meghalt egy enemy

                    enemyQ.remove();

                    if (enemyQ.isEmpty()) { // meghalt az összes enemy -> player win
                        handleReward(w, dungeon, playerCard);
                        return;
                    }

                    enemy = enemyQ.peek();
                    logKijatszik(w, kor, "kazamata", enemy);

                } else { // nem halt meg senki

                    int dmg1 = calcDamage(enemy, playerCard);
                    playerCard.health -= dmg1;
                    logTamadas(w, kor, "kazamata", enemy, dmg1, playerCard);
                }

                // ===== 2. JÁTÉKOS LÉP =====

                if (playerCard == null) { // első kör eleje

                    playerCard = playerQ.peek();
                    assert playerCard != null;
                    logKijatszik(w, kor, "kazamata", playerCard);

                } else if (playerCard.getHealth() <= 0) { // meghalt egy player kártya

                    playerQ.remove();

                    if (playerQ.isEmpty()) { // meghalt a player összes kártyája -> player lose
                        w.write("jatekos vesztett");
                        w.newLine();
                        return;
                    }

                    playerCard = playerQ.peek();
                    logKijatszik(w, kor, "jatekos", playerCard);

                } else { // nem halt meg senki

                    int dmg2 = calcDamage(playerCard, enemy);
                    enemy.health -= dmg2;
                    logTamadas(w, kor, "jatekos", playerCard, dmg2, enemy);
                }

                kor++;
            }
        }
    }

    private int calcDamage(Card attacker, Card defender) {
        CardType a = attacker.getType();
        CardType d = defender.getType();

        // erős -> 2x
        if (a.isStrongAgainst(d)) {
            return attacker.getDamage() * 2;
        }

        // gyenge -> fél damage, lefelé kerekítve
        if (a.isWeakAgainst(d)) {
            return attacker.getDamage() / 2;
        }

        return attacker.getDamage(); // semleges damage
    }

    // kijátszás esetén
    private void logKijatszik(BufferedWriter w, int kor, String ki, Card c) throws IOException {

        w.write(kor
                + ".kor;"
                + ki + ";kijatszik;"
                + c.getName() + ";"
                + c.getDamage() + ";"
                + c.getHealth() + ";"
                + c.getType().toString().toLowerCase());

        w.newLine();
    }

    // támadás esetén
    private void logTamadas(BufferedWriter w, int kor, String ki,
                            Card attacker, int dmg, Card defender) throws IOException {

        int hp = Math.max(0, defender.getHealth());

        w.write(kor
                + ".kor;"
                + ki + ";tamad;"
                + attacker.getName() + ";"
                + dmg + ";"
                + defender.getName() + ";"
                + hp);

        w.newLine();
    }

    // win esetén
    private void handleReward(BufferedWriter w, Dungeon d, Card lastPlayerCard) throws IOException {

        if (d.getType() == DungeonType.NAGY) {

            // új kártya
            Card reward = engine.getFirstNotUsedCard();
            engine.addToCollection(reward.getName());
            w.write("jatekos nyert;" + reward.getName());
            w.newLine();
            return;
        }

        // egyszerű vagy kis kazamata
        RewardType r = d.getReward();

        if (r == RewardType.ELETERO) {
            lastPlayerCard.increaseBaseHealth(2);
            w.write("jatekos nyert;eletero;" + lastPlayerCard.getName());
        } else {
            lastPlayerCard.increaseBaseDamage(1);
            w.write("jatekos nyert;sebzes;" + lastPlayerCard.getName());
        }

        w.newLine();
    }
}
