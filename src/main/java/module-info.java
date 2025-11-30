module hu.szatomi.damareen {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires javafx.graphics;
    requires java.desktop;
    requires java.logging;
    requires org.slf4j;
    requires com.fasterxml.jackson.databind;

    exports hu.szatomi.damareen;
    exports hu.szatomi.damareen.controller;
    exports hu.szatomi.damareen.model;
    opens hu.szatomi.damareen to javafx.fxml, javafx.graphics;
    opens hu.szatomi.damareen.controller to javafx.fxml, javafx.graphics;
    exports hu.szatomi.damareen.manager;
    opens hu.szatomi.damareen.manager to javafx.fxml, javafx.graphics;

    opens hu.szatomi.damareen.model to com.fasterxml.jackson.databind;
    exports hu.szatomi.damareen.model.dto to com.fasterxml.jackson.databind;

}