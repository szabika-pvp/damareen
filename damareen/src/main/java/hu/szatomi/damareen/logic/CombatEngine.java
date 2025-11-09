package hu.szatomi.damareen.model;

import java.util.LinkedList;
import java.util.Queue;

public class CombatEngine {

    private final Player player;
    private final Dungeon dungeon;

    private Queue<Card> playerQ;
    private Queue<Card> enemyQ;

    private Card playerCard;
    private Card enemyCard;

    private boolean finished = false;
    private boolean playerWon = false;

    private Card lastPlayerCard = null;

    private int round = 0;

    public CombatEngine(Player player, Dungeon dungeon) {
        this.player = player;
        this.dungeon = dungeon;

        // reset cards
        for (Card c : player.getDeck().getCards()) c.reset();

        this.playerQ = new LinkedList<>(player.getDeck().getCards());
        this.enemyQ = new LinkedList<>(dungeon.getEnemies());

        if (dungeon.getLeader() != null)
            this.enemyQ.add(dungeon.getLeader());
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isPlayerWon() {
        return playerWon;
    }

    public int getRound() {
        return round;
    }

    public Card getLastPlayerCard() {
        return lastPlayerCard;
    }

    public CombatState nextTurn() {

        if (finished)
            return new CombatState(dungeon, null, null, true, playerWon, lastPlayerCard);

        CombatAction enemyAction = doEnemyAction();

        if (finished)
            return new CombatState(dungeon, enemyAction, null, true, playerWon, lastPlayerCard);

        CombatAction playerAction = doPlayerAction();

        round++;

        return new CombatState(dungeon, enemyAction, playerAction, finished, playerWon, lastPlayerCard);
    }

    private CombatAction doEnemyAction() {

        // új enemy kártya kijátszása
        if (enemyCard == null || enemyCard.getHealth() <= 0) {

            if (enemyCard != null && enemyCard.getHealth() <= 0)
                enemyQ.remove(); // halott kártya ki

            if (enemyQ.isEmpty()) {
                finished = true;
                playerWon = true;

                if (dungeon.getReward() == RewardType.SEBZES) {
                    lastPlayerCard.increaseBaseDamage(1);
                } else {
                    lastPlayerCard.increaseBaseHealth(2);
                }

                return new CombatAction("kazamata", "meghal", null, null, 0, 0);
            }

            enemyCard = enemyQ.peek();
            return new CombatAction("kazamata", "kijatszik", enemyCard, null,0, enemyCard.getHealth());
        }

        // támadás
        int dmg = calcDamage(enemyCard, playerCard);
        playerCard.health -= dmg;

        int newHp = Math.max(0, playerCard.getHealth());
        return new CombatAction("kazamata", "tamad",
                enemyCard, playerCard, dmg, newHp);
    }


    private CombatAction doPlayerAction() {


        // új player card kijátszása
        if (playerCard == null || playerCard.getHealth() <= 0) {

            if (playerCard != null && playerCard.getHealth() <= 0)
                playerQ.remove();

            if (playerQ.isEmpty()) {
                finished = true;
                playerWon = false;
                return new CombatAction("jatekos", "meghal", null, null, 0, 0);
            }

            playerCard = playerQ.peek();
            lastPlayerCard = playerCard;
            return new CombatAction("jatekos", "kijatszik", playerCard, null, 0, playerCard.getHealth());
        }

        // támadás
        int dmg = calcDamage(playerCard, enemyCard);
        enemyCard.health -= dmg;

        int newHp = Math.max(0, enemyCard.getHealth());
        return new CombatAction("jatekos", "tamad",
                playerCard, enemyCard, dmg, newHp);
    }


    private int calcDamage(Card a, Card d) {

        if (a.getType().isStrongAgainst(d.getType()))
            return a.getDamage() * 2;

        if (a.getType().isWeakAgainst(d.getType()))
            return a.getDamage() / 2;

        return a.getDamage();
    }
}
