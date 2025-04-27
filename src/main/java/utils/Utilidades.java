package utils;

import java.util.function.Function;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public class Utilidades {
    public static void mostrarAlerta2(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
    
    // Método genérico para cargar objetos en comboBox
    public static <T> void cargarComboBox(ComboBox<T> comboBox, ObservableList<T> lista, Function<T, String> nombreExtractor) {
        comboBox.setItems(lista);

        comboBox.setConverter(new StringConverter<T>() {
            @Override
            public String toString(T objeto) {
                return (objeto != null) ? nombreExtractor.apply(objeto) : "";
            }

            @Override
            public T fromString(String string) {
                return null; // No se usa en este caso
            }
        });
    }
}
