package hu.szatomi.damareen.model;

import java.util.List;

public class GameState {

    private Player player;
    private List<Dungeon> dungeons;
    private String environmentName;
    private String difficulty;
    private String currentScene;

    public GameState() {}

    public Player getPlayer() { return player; }
    public void setPlayer(Player p) { this.player = p; }

    public List<Dungeon> getDungeons() { return dungeons; }
    public void setDungeons(List<Dungeon> d) { this.dungeons = d; }

    public String getEnvironmentName() { return environmentName; }
    public void setEnvironmentName(String n) { this.environmentName = n; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String d) { this.difficulty = d; }

    public String getCurrentScene() { return currentScene; }
    public void setCurrentScene(String currentScene) { this.currentScene = currentScene; }
}
