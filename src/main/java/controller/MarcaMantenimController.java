
package controller;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Marca;
import dao.MarcaDAO;
import utils.LoggerUtils;

import java.net.URL;
import java.util.ResourceBundle;

// controlador de la vista 
public class MarcaMantenimController implements Initializable {

    // elementos de la interfaz 
    @FXML
    private TextField txtNombre;    // campo de texto para ingresar el nombre de la marca
    @FXML
    private Button btnGuardar;      // botón para guardar la marca
    @FXML
    private Button btnCancelar;     // botón para cancelar 

    // se está editando 
    private Marca marca;

    // DAO para acceder a la base de datos de marcas
    private final MarcaDAO marcaDAO = new MarcaDAO();

    //  método que se ejecuta automáticamente al abrir la vista
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Se registra en el log 
        LoggerUtils.logSection("MARCAS");

        // acción del botón guardar
        btnGuardar.setOnAction(e -> guardarMarca());
    }

    //  establece la marca actual 
    public void setMarca(Marca marca) {
        this.marca = marca;

        // si hay una marca existente, se muestra su nombre en el campo de texto
        if (marca != null) {
            txtNombre.setText(marca.getNombre());
        }
    }

    // sirve para guardar o actualizar la marca en la base de datos
    private void guardarMarca() {
        // Se obtiene el texto del campo de nombre y se limpia de espacios
        String nombre = txtNombre.getText().trim();

        // si el campo está vacío, se muestra una alerta y se registra en el log
        if (nombre.isEmpty()) {
            mostrarAlerta("Campo vacío", "Por favor, ingresa un nombre para la marca.", Alert.AlertType.WARNING);
            LoggerUtils.logWarning("MARCAS", "Campo nombre vacío en el formulario.");
            return;
        }

        // Si la marca es nueva , se inserta en la base de datos
        if (marca == null) {
            boolean insertado = marcaDAO.insertarMarca(nombre);
            if (insertado) {
                mostrarAlerta("Éxito", "Marca agregada con éxito.", Alert.AlertType.INFORMATION);
                cerrarVentana(); // cierra la vntana después de guardar
            }
        } else {
            // si ya existe, se actualiza en la base de datos
            boolean actualizado = marcaDAO.actualizarMarca(marca.getCodigo(), nombre);
            if (actualizado) {
                mostrarAlerta("Éxito", "Marca actualizada correctamente.", Alert.AlertType.INFORMATION);
                cerrarVentana(); // Cierra la ventana después de actualizar
            }
        }
    }

    //  botón cancelar
    @FXML
    private void btnActionCancelar() {
        cerrarVentana();
    }

    //  cierra la ventana actual
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    //  muestra una alerta con el título, mensaje y tipo especificados
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
