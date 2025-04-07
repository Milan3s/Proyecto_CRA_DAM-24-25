module main {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;

    // Abrimos los paquetes para que FXML pueda acceder a sus propiedades públicas
    opens controller to javafx.fxml, javafx.base;
    opens model to javafx.base;
    opens utils to javafx.base; // ✅ Esto permite serializar logs si es necesario

    // Exportamos para uso desde otras partes del proyecto
    exports main;
    exports controller;
    exports model;
    exports utils; // ✅ Si otras clases necesitan acceso al logger, por ejemplo

    requires de.jensd.fx.glyphs.fontawesome; // Para íconos FontAwesome (si lo usas en vistas)
}
