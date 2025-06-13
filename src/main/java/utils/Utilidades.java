package utils;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

/**
 * Clase que contiene varios métodos o utilidades que se utilizan en distintos puntos de la aplicación.
 * 
 */
public class Utilidades {
    
    /**
     * Muestra avisos por pantalla al usuario.
     * 
     * @param titulo String
     * @param contenido String
     * @param tipo Alert.AlertType
     */
    public static void mostrarAlerta2(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
    
    /**
     * Método genérico para cargar objetos en comboBox mostrando, normalmente,
     * el nombre del objeto.
     * 
     * @param <T>
     * @param comboBox  ComboBox<T>
     * @param lista ObservableList<T>
     * @param nombreExtractor 
     */
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
    
    /**
     * Muestra la fecha en un DatePicker en formato dd/MM/yyyy
     * 
     * @param dtp DatePicker
     */
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
    
    /**
     * Convierte una cadena de fecha en formato dd/mm/aaaa a un tipo Date de SQL
     * 
     * @param sFecha String
     * @return Date
     */
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
    
    /**
     * Selecciona un fichero mediante el selector de archivos del sistema
     * 
     * @param descriFiltro String
     * @param filtro String
     * @param tipo String 
     * @return File
     */
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
    
    /**
     * Valida una cadena de tipo email
     * 
     * @param email String
     * @return boolean
     */
    public static boolean validarEmail(String email) {
        String regex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
               
        return validarDato(email, regex);
    }
    
    /**
     * Valida una cadena de tipo telefono
     * 
     * @param telefono
     * @return 
     */
    public static boolean validarTelefono(String telefono) {
        // 9 dígitos con espacios opcionales entre bloques de 3
        String regex = "^\\d{3}\\s?\\d{3}\\s?\\d{3}$";
        
        return validarDato(telefono, regex);
    }
    
    /**
     * Valida una cadena de tipo código postal
     * 
     * @param cp String
     * @return boolean
     */
    public static boolean validarCP(String cp) {
        // 5 dígitos
        String regex = "^\\d{5}$";
        
        return validarDato(cp, regex);
    }
    
    /**
     * Valida una cadena de tipo código postal
     * 
     * @param cp String
     * @return boolean
     */
    public static boolean validarNumero(String numero) {
        // 5 dígitos
        String regex = "^\\d$";
        
        return validarDato(numero, regex);
    }
    
    /**
     * Valida un dato a partir de una expresión regular
     * 
     * @param dato String
     * @param regex String
     * @return boolean
     */
    public static boolean validarDato(String dato, String regex) {               
        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(dato);
        return mat.matches();
    }
}
