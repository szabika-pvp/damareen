package hu.szatomi.damareen.model;

import java.util.ArrayList;
import java.util.List;

public class Player {

    List<Card> collection = new ArrayList<>();
    Deck deck;

    public void setCollection(List<Card> collection) {
        this.collection = collection;
    }

    public Card getCardByName(String cardName) {
        for (Card card : collection) {
            if (card.getName().equals(cardName)) return card;
        }

        return null;
    }

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
