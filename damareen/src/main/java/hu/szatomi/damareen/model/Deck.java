package hu.szatomi.damareen.model;

import java.util.List;

public class Deck {
    List<Card> cards;

    public Deck(List<Card> cards) {
        this.cards = cards;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void addCard(Card card) {cards.add(card);}
    public void removeCard(Card card) {cards.remove(card);}
}
