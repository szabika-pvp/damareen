package hu.szatomi.damareen.model;

import java.util.List;

public class Dungeon {

    private final DungeonType type;
    private final String name;
    private final List<Card> enemies;
    private final LeaderCard leader;
    private final RewardType reward;

    public Dungeon(DungeonType type, String name, List<Card> enemies, LeaderCard leader, RewardType reward) {
        this.type = type;
        this.name = name;
        this.enemies = enemies;
        this.leader = leader;
        this.reward = reward;
    }

    public boolean hasLeader() { return leader != null; }

    public LeaderCard getLeader() {
        return leader;
    }

    public DungeonType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public RewardType getReward() {
        return reward;
    }

    public List<Card> getEnemies() {
        return enemies;
    }
}
