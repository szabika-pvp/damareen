package hu.szatomi.damareen.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Environment {

    private Map<String, Card> cards = new LinkedHashMap<>();
    private Map<String, LeaderCard> bosses = new HashMap<>();
    private Map<String, Dungeon> dungeons = new LinkedHashMap<>();

    public Environment(Map<String, Card> simpleCards, Map<String, LeaderCard> leaderCards, Map<String, Dungeon> dungeons) {
        this.cards = simpleCards;
        this.bosses = leaderCards;
        this.dungeons = dungeons;
    }

    public Environment() {}

    public Card getCardByName(String name) {
        return cards.values().stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public Card getCopyByName(String name) {
        return getCardByName(name).copy();
    }

    public LeaderCard getLeaderByName(String name) {
        return bosses.values().stream()
                .filter(l -> l.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public Map<String, Card> getCards() {
        return cards;
    }

    public Map<String, LeaderCard> getLeaders() {
        return bosses;
    }

    public Map<String, Dungeon> getDungeons() {
        return dungeons;
    }

    public void setCards(Map<String, Card> cards) {
        this.cards = cards;
    }

    public void setLeaders(Map<String, LeaderCard> bosses) {
        this.bosses = bosses;
    }

    public void setDungeons(Map<String, Dungeon> dungeons) {
        this.dungeons = dungeons;
    }
}
