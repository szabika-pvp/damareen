module hu.szatomi.damareen {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires javafx.graphics;
    requires java.desktop;

    exports hu.szatomi.damareen;
    opens hu.szatomi.damareen to javafx.fxml, javafx.graphics;
}