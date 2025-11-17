package hu.szatomi.damareen;

import hu.szatomi.damareen.logic.GameEngine;
import hu.szatomi.damareen.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TestRunner {

    private final GameEngine engine;

    public TestRunner(GameEngine engine) {
        this.engine = engine;
    }

    // in.txt fájl beolvasása
    public void run(String inputFolder) throws IOException {
        Path inFile = Path.of(inputFolder, "in.txt");

        List<String> lines = Files.readAllLines(inFile);

        for (String rawLine : lines) {
            String line = rawLine.trim();

            if (line.isEmpty()) continue;

            processLine(line, inputFolder);
        }
    }

    // parancsok
    private void processLine(String line, String folder) throws IOException {

        String[] args = line.split(";");
        String command = args[0].trim();

        switch (command) {
            case "uj kartya":
                engine.addCard(args[1],
                        Integer.parseInt(args[2]),
                        Integer.parseInt(args[3]),
                        CardType.valueOf(args[4].toUpperCase()));
                break;

            case "uj vezer":
                engine.addLeader(args[1], args[2], LeaderType.valueOf(args[3].toUpperCase()));
                break;

            case "uj kazamata":
                engine.addDungeon(args);
                break;

            case "uj jatekos":
                engine.createPlayer();
                break;

            case "felvetel gyujtemenybe":
                engine.addToCollection(args[1]);
                break;

            case "uj pakli":
                engine.createDeck(args[1].split(","));
                break;

            case "harc":
                engine.startCombat(args[1], folder + "/" + args[2]);
                break;

            case "export vilag":
                engine.exportWorld(folder + "/" + args[1]);
                break;

            case "export jatekos":
                engine.exportPlayer(folder + "/" + args[1]);
                break;

            default:
                System.out.println("Ismeretlen parancs: " + command);
        }
    }
}
