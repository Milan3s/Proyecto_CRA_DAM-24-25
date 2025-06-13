package controller;

// Importamos las clases necesarias para la interfaz y manejo de datos
import javafx.fxml.FXML; // Para vincular el código con los elementos de la interfaz
import javafx.fxml.Initializable; // Para que esta clase se inicialice al abrirse la ventana
import javafx.scene.control.*; // Para usar botones, cuadros de texto, alertas, etc.
import javafx.stage.Stage; // Para manejar la ventana
import model.Marca; // Clase Marca que representa una marca
import dao.MarcaDAO; // Para acceder a los datos de marcas en la base de datos
import utils.LoggerUtils; // Para registrar información o errores

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

// Esta clase controla la ventana de mantenimiento (crear/editar) de marcas
public class MarcaMantenimController implements Initializable {

    // Elementos de la interfaz: un cuadro de texto y dos botones
    @FXML
    private TextField txtNombre; // Donde el usuario escribe el nombre de la marca
    @FXML
    private Button btnGuardar;   // Botón para guardar la marca
    @FXML
    private Button btnCancelar;  // Botón para cerrar sin guardar

    private Marca marca;  // Marca actual: null si es nueva, o con datos si es edición

    private final MarcaDAO marcaDAO = new MarcaDAO(); // Objeto que permite acceder a la base de datos

    // Este método se ejecuta automáticamente al abrir la ventana
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LoggerUtils.logSection("MARCAS"); // Registra en el log que se abrió la sección de marcas

        // Asigna acciones a los botones
        btnGuardar.setOnAction(e -> guardarMarca()); // Cuando se hace clic en "Guardar"
        btnCancelar.setOnAction(e -> cerrarVentana()); // Cuando se hace clic en "Cancelar"
    }

    // Este método se llama desde fuera para indicar si es una nueva marca o una existente
    public void setMarca(Marca marca) {
        this.marca = marca;
        if (marca != null) {
            // Si es una marca existente, muestra su nombre en el cuadro de texto
            txtNombre.setText(marca.getNombre());
        }
    }

    // Lógica para guardar o actualizar la marca
    private void guardarMarca() {
        String nombre = txtNombre.getText().trim(); // Obtiene el texto escrito, sin espacios al inicio/fin

        if (nombre.isEmpty()) {
            // Si no escribió nada, muestra una advertencia
            mostrarAlerta("Campo vacío", "Por favor, ingresa un nombre para la marca.", Alert.AlertType.WARNING);
            LoggerUtils.logWarning("MARCAS", "Campo nombre vacío en el formulario.");
            return;
        }

        try {
            if (marca == null) {
                // Si marca es null, es una nueva marca
                Marca nuevaMarca = new Marca(0, nombre); // Código 0, el DAO le asigna uno real
                boolean insertado = marcaDAO.insertarMarca(nuevaMarca);

                if (insertado) {
                    mostrarAlerta("Éxito", "Marca agregada con éxito.", Alert.AlertType.INFORMATION);
                    cerrarVentana();
                } else {
                    mostrarAlerta("Error", "No se pudo agregar la marca. Puede que ya exista el nombre.", Alert.AlertType.ERROR);
                }

            } else {
                // Si marca no es null, es una actualización
                boolean actualizado = marcaDAO.actualizarMarca(marca.getCodigo(), nombre);

                if (actualizado) {
                    mostrarAlerta("Éxito", "Marca actualizada correctamente.", Alert.AlertType.INFORMATION);
                    cerrarVentana();
                } else {
                    mostrarAlerta("Error", "No se pudo actualizar la marca.", Alert.AlertType.ERROR);
                }
            }

        } catch (Exception e) {
            // Si ocurre un error inesperado
            LoggerUtils.logError("MARCAS", "Error al guardar marca", e);
            mostrarAlerta("Error", "Ocurrió un error inesperado: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Cierra la ventana actual
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    // Muestra una alerta al usuario
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null); // No usamos un encabezado adicional
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
