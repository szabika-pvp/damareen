package hu.szatomi.damareen.logic;

import hu.szatomi.damareen.GameEngine;
import hu.szatomi.damareen.model.CombatAction;
import hu.szatomi.damareen.model.CombatState;
import hu.szatomi.damareen.model.Dungeon;
import hu.szatomi.damareen.model.DungeonType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CombatFileWriter {

    private final BufferedWriter w;
    private final GameEngine engine;

    public CombatFileWriter(GameEngine engine, Path path) throws IOException {
        this.engine = engine;
        w = Files.newBufferedWriter(path);
    }

    public void write(CombatState st, int round, Dungeon dungeon) throws IOException {

        if (round == 1) {
            w.write("harc kezdodik;" + dungeon.getName() + "\n");
        }

        if (!st.combatOver()) {
            w.newLine();
        }

        if (st.enemyAction() != null)
            writeAction(round, st.enemyAction());

        if (st.playerAction() != null)
            writeAction(round, st.playerAction());

        if (st.combatOver()) {
            if (st.playerWon()) {
                if (st.dungeon().getType() != DungeonType.NAGY) {
                    w.write(
                        "jatekos nyert;" +
                        st.dungeon()
                            .getReward()
                            .toString()
                            .toLowerCase() + ";" +
                        st.lastPlayerCard().getName()
                    );
                } else {
                    w.write(
                        "jatekos nyert;" +
                        engine.getFirstNotUsedCard()
                    );
                }
            }
            else
                w.write("jatekos vesztett");
        }
    }

    public void close() throws IOException {
        w.close();
    }

    private void writeAction(int round, CombatAction a) throws IOException {

        if (a.type().equals("kijatszik")) {
            w.write(
            round + ".kor;" +
                a.who() +
                ";kijatszik;" +
                a.card().getName() + ";" +
                a.card().getBaseDamage() + ";" +
                a.card().getHealth() + ";" +
                a.card().getType().toString().toLowerCase()
            );

        } else if (a.type().equals("tamad")) {
            w.write(
            round + ".kor;" +
                a.who() +
                ";tamad;" +
                a.card().getName() + ";" +
                a.damage() + ";" +
                a.attackedCard().getName() + ";" +
                a.targetRemainingHp()
            );
        }

        w.newLine();
    }
}
