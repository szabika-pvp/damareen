package hu.szatomi.damareen.model;

import java.util.ArrayList;
import java.util.List;

public class Player {

    List<Card> collection = new ArrayList<>();
    Deck deck;

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public Deck getDeck() {
        return deck;
    }

    public List<Card> getCollection() {
        return collection;
    }
}
