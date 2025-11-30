package hu.szatomi.damareen.model.dto;

import hu.szatomi.damareen.model.Dungeon;
import hu.szatomi.damareen.model.DungeonType;
import hu.szatomi.damareen.model.RewardType;

import java.util.List;

public class DungeonDTO {

    public DungeonType type;
    public RewardType rewardType;
    public String name;
    public List<String> enemies;
    public String leader; // optional: can be null

    public DungeonDTO() {}
}
