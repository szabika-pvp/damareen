package hu.szatomi.damareen.model;

public record CombatState(
        Dungeon dungeon,
        CombatAction enemyAction,
        CombatAction playerAction,
        boolean combatOver,
        boolean playerWon,
        Card lastPlayerCard
) {}