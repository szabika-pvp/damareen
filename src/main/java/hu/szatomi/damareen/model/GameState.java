package hu.szatomi.damareen.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameState {

    private Environment env;
    private Player player;

    public GameState() {}

    public GameState(Environment env, Player player) {
        this.env = env;
        this.player = player;
    }

    public Environment getEnv() { return env; }

    public void setEnv(Environment env) { this.env = env; }

    public Player getPlayer() { return player; }
    public void setPlayer(Player p) { this.player = p; }

    public List<Dungeon> getDungeons() { return new ArrayList<>(env.getDungeons().values()); }

    public void setDungeons(List<Dungeon> d) {
        this.env
            .setDungeons(d.stream()
                .collect(Collectors.toMap(Dungeon::getName, dungeon -> dungeon)));
    }
}
