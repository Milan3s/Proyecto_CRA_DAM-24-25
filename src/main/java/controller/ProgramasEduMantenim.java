package controller;

import dao.ProgramasEduDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utils.LoggerUtils;
import java.net.URL;
import java.util.ResourceBundle;
import model.ProgramasEdu;

/**
 * Controlador para el formulario de mantenimiento de Programas Educativos.
 * Permite agregar o editar un programa educativo.
 */
public class ProgramasEduMantenim implements Initializable {

    @FXML
    private TextField txtNombre; // Campo de texto para el nombre del programa

    @FXML
    private Button btnGuardar;   // Botón para guardar los datos

    @FXML
    private Button btnCancelar;  // Botón para cancelar y cerrar el formulario

    private ProgramasEdu programa; // Si es null: nuevo programa. Si no: programa existente en edición

    private final ProgramasEduDAO programaDAO = new ProgramasEduDAO(); // DAO para operaciones de base de datos

    /**
     * Método que se ejecuta al inicializar la ventana.
     * Configura los manejadores de eventos y registra el inicio en el log.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LoggerUtils.logSection("PROGRAMAS EDUCATIVOS"); // Marca inicio de la sección en el log
        btnGuardar.setOnAction(e -> guardarPrograma()); // Asigna acción al botón de guardar
    }

    /**
     * Establece el programa que se va a editar (o null si es nuevo).
     * Llena el formulario si es un programa existente.
     *
     * @param programa El programa a editar o null para uno nuevo.
     */
    public void setPrograma(ProgramasEdu programa) {
        this.programa = programa;
        if (programa != null) {
            txtNombre.setText(programa.getNombre()); // Carga el nombre en el campo de texto
        }
    }

    /**
     * Método para guardar el programa.
     * Valida el campo y decide si insertar o actualizar según corresponda.
     */
    private void guardarPrograma() {
        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta("Campo vacío", "Por favor, ingresa un nombre para el programa educativo.", Alert.AlertType.WARNING);
            LoggerUtils.logWarning("PROGRAMAS EDUCATIVOS", "Campo nombre vacío en el formulario.");
            return;
        }

        if (programa == null) {
            // Insertar nuevo programa
            boolean insertado = programaDAO.insertarPrograma(nombre);
            if (insertado) {
                mostrarAlerta("Éxito", "Programa educativo agregado con éxito.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            } else {
                mostrarAlerta("Error", "No se pudo agregar el programa educativo.", Alert.AlertType.ERROR);
            }
        } else {
            // Actualizar programa existente
            boolean actualizado = programaDAO.actualizarPrograma(programa.getCodigo(), nombre);
            if (actualizado) {
                mostrarAlerta("Éxito", "Programa educativo actualizado correctamente.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el programa educativo.", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Acción del botón cancelar: cierra la ventana actual.
     */
    @FXML
    private void btnActionCancelar() {
        cerrarVentana();
    }

    /**
     * Cierra la ventana actual.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    /**
     * Muestra una alerta al usuario.
     *
     * @param titulo  Título de la alerta.
     * @param mensaje Mensaje de la alerta.
     * @param tipo    Tipo de alerta.
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
