package hu.szatomi.damareen.model;

import java.util.List;

public class Dungeon {

    private DungeonType type;
    private String name;
    private List<Card> enemies;
    private LeaderCard leader;
    private RewardType reward;

    public Dungeon(DungeonType type, String name, List<Card> enemies, LeaderCard leader, RewardType reward) {
        this.type = type;
        this.name = name;
        this.enemies = enemies;
        this.leader = leader;
        this.reward = reward;
    }

    public Dungeon() {}

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
