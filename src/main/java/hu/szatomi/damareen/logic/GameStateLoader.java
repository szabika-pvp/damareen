package hu.szatomi.damareen.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.szatomi.damareen.model.*;
import hu.szatomi.damareen.model.dto.GameStateDTO;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameStateLoader {

    private final ObjectMapper mapper = new ObjectMapper();

    public GameState load(Path file) throws Exception {

        // 1. JSON → DTO
        GameStateDTO dto = mapper.readValue(file.toFile(), GameStateDTO.class);

        // 2. DTO → Environment
        Environment env = new Environment();

        // Cards
        Map<String, Card> cards = dto.env.cards.stream()
                .map(c -> new Card(c.name, c.damage, c.health, c.type))
                .collect(Collectors.toMap(Card::getName, c -> c));
        env.setCards(cards);

        // Leaders
        Map<String, LeaderCard> leaders = dto.env.leaders.stream()
                .map(b ->
                    new LeaderCard(b.name, env.getCardByName(b.baseCard).copy(), b.type))
                .collect(Collectors.toMap(LeaderCard::getName, c -> c));
        env.setLeaders(leaders);

        // Dungeons
        Map<String, Dungeon> dungeons = dto.env.dungeons.stream()
                .map(d -> {
                    List<Card> enemyCards = d.enemies.stream()
                            .map(env::getCopyByName)
                            .collect(Collectors.toList());

                    LeaderCard leader = d.leader == null
                            ? null
                            : env.getLeaderByName(d.leader).copy();

                    return new Dungeon(d.type, d.name, enemyCards, leader, d.rewardType);
                })
                .collect(Collectors.toMap(Dungeon::getName, d -> d));
        env.setDungeons(dungeons);

        // 3. DTO → Player
        Player p = new Player();

        p.setCollection(
            dto.player.collection.stream()
                .map(c -> new Card(c.name, c.damage, c.health, c.type))
                    .collect(Collectors.toList())
        );

        p.setDeck(
            new Deck(
                dto.player.deck.stream()
                    .map(env::getCopyByName)
                    .collect(Collectors.toList())
            )
        );

        // 4. összeállítás
        return new GameState(env, p);
    }
}
