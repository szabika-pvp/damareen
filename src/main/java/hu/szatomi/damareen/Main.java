package hu.szatomi.damareen;

public class Main {
    public static void main(String[] args) throws Exception {

        // teszt mód
        if (args.length == 1 && !args[0].equals("--ui")) {
            GameEngine engine = new GameEngine();
            new TestRunner(engine).run(args[0]);
            return;
        }

        // játék mód
        if (args.length == 1) {

            System.out.printf("%n%.2f%n", (double) 500/12);

            System.setProperty("glass.win.uiScale", "1.0");
            MainApp.main(args);
            return;
        }

        System.out.println("Használat: .\\run.bat [mappa] vagy --ui");
    }
}
