package hu.szatomi.damareen.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.szatomi.damareen.model.GameState;

import java.io.IOException;
import java.nio.file.*;

public class SaveManager {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String SAVE_DIR = "src/main/resources/saves/";

    public static void save(GameState state, String saveName) throws IOException {
        Path path = Path.of(SAVE_DIR + saveName + ".json");
        MAPPER.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), state);
    }

    public static GameState load(String saveName) throws IOException {
        Path path = Path.of(SAVE_DIR + saveName + ".json");
        return MAPPER.readValue(path.toFile(), GameState.class);
    }
}
