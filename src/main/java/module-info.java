module main {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;

    opens controller to javafx.fxml, javafx.base;
    opens model to javafx.base;

    exports main;
    exports controller;
    exports model;

    requires de.jensd.fx.glyphs.fontawesome;
}
