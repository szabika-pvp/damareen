package hu.szatomi.damareen.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.szatomi.damareen.model.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class EnvironmentManager {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String ENV_DIR = "src/main/resources/environments/";

    public static List<String> listEnvironments() throws IOException {
        List<String> names = new ArrayList<>();

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(Path.of(ENV_DIR), "*.json")) {
            for (Path p : ds) {
                names.add(p.getFileName().toString().replace(".json", ""));
            }
        }
        return names;
    }

    public static Environment loadEnvironment(String name) throws IOException {
        Path path = Path.of(ENV_DIR + name + ".json");
        return MAPPER.readValue(path.toFile(), Environment.class);
    }
}
