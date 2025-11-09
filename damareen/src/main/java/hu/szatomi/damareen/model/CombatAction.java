package hu.szatomi.damareen.model;

public record CombatAction(
        String who,     // "kazamata" | "jatekos"
        String type,    // "kijatszik" | "tamad"
        Card card,
        Card attackedCard,
        int damage,
        int targetRemainingHp
) { }
