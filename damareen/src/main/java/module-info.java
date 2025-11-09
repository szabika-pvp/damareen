module hu.szatomi.damareen {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires javafx.graphics;
    requires java.desktop;

    exports hu.szatomi.damareen;
    exports hu.szatomi.damareen.controller;
    opens hu.szatomi.damareen to javafx.fxml, javafx.graphics;
    opens hu.szatomi.damareen.controller to javafx.fxml, javafx.graphics;
}