package hu.szatomi.damareen.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.szatomi.damareen.model.dto.*;
import hu.szatomi.damareen.model.*;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class GameStateSaver {

    private final ObjectMapper mapper = new ObjectMapper();

    public void save(GameState state, Path file) throws Exception {

        GameStateDTO dto = new GameStateDTO();

        // environment DTO
        EnvironmentDTO envDto = new EnvironmentDTO();
        envDto.cards = state.getEnv().getCards().values().stream()
                .map(c -> {
                    CardDTO cd = new CardDTO();
                    cd.name = c.getName();
                    cd.damage = c.getBaseDamage();
                    cd.health = c.getBaseHealth();
                    cd.type = c.getType();
                    return cd;
                })
                .collect(Collectors.toList());

        envDto.leaders = state.getEnv().getLeaders().values().stream()
                .map(b -> {
                    LeaderDTO bd = new LeaderDTO();
                    bd.name = b.getName();
                    // FIXME: we save baseCard by baseCard name (best-effort)
                    bd.baseCard = b.getBaseCard().getName();
                    bd.type = b.getLeaderType();
                    return bd;
                })
                .collect(Collectors.toList());

        envDto.dungeons = state.getEnv().getDungeons().values().stream()
                .map(d -> {
                    DungeonDTO dd = new DungeonDTO();
                    dd.name = d.getName();
                    dd.type = d.getType();
                    dd.enemies = d.getEnemies().stream()
                            .map(Card::getName)
                            .collect(Collectors.toList());
                    dd.leader = d.getLeader() == null ? null : d.getLeader().getName();
                    dd.rewardType = d.getReward();
                    return dd;
                })
                .collect(Collectors.toList());

        dto.env = envDto;

        // player DTO
        PlayerDTO pd = new PlayerDTO();
        pd.collection = state.getPlayer().getCollection().stream()
                .map(c -> {
                    CardDTO cd = new CardDTO();
                    cd.name = c.getName();
                    cd.damage = c.getBaseDamage();
                    cd.health = c.getBaseHealth();
                    cd.type = c.getType();
                    return cd;

                }).collect(Collectors.toList());

        pd.deck = state.getPlayer().getDeck().getCards().stream()
                .map(Card::getName)
                .collect(Collectors.toList());

        dto.player = pd;

        mapper.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), dto);

        System.out.println("SAVED GAME");
    }
}
