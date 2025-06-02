package utils;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.Function;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.FileChooser;
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
    
    // Para formatear cómo se muestra la fecha en un DatePicker
    public static void formatearFecha(DatePicker dtp) {
        DateTimeFormatter formatFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        dtp.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? formatFecha.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, formatFecha) : null;
            }
        });
    }
    
    // Convierte una cadena de fecha en formato dd/mm/aaaa a un tipo Date de SQL
    public static Date convertirFecha(String sFecha) {
        if (sFecha == null || sFecha.isEmpty()) {
            return null;
        }

        try {
            DateTimeFormatter formatFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate fechaLocal = LocalDate.parse(sFecha, formatFecha);
            return Date.valueOf(fechaLocal);
        } catch (DateTimeParseException e) {
            return null;
        }       
    }
    
    // Para seleccionar un fichero mediante el selector de archivos del sistema
    public static File seleccFichero(String descriFiltro, String filtro, String tipo) {
        FileChooser f = new FileChooser();
        FileChooser.ExtensionFilter filtcsv = new FileChooser.ExtensionFilter(descriFiltro, filtro);
        f.getExtensionFilters().add(filtcsv);        
        File fichero = null;
        
        if (tipo.equals("w")) {
            fichero = f.showSaveDialog(null);
        } else {
            fichero = f.showOpenDialog(null);
        }
        return fichero;
    }
}
