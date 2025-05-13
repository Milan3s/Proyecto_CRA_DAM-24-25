package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Marca;
import model.MarcaDAO;
import utils.LoggerUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class MarcaMantenimController implements Initializable {

    @FXML
    private TextField txtNombre;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;

    private Marca marca;  // null = nuevo, distinto de null = edición
    private final MarcaDAO marcaDAO = new MarcaDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LoggerUtils.logSection("MARCAS");
        btnGuardar.setOnAction(e -> guardarMarca());
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
        if (marca != null) {
            txtNombre.setText(marca.getNombre());
        }
    }

    private void guardarMarca() {
        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta("Campo vacío", "Por favor, ingresa un nombre para la marca.", Alert.AlertType.WARNING);
            LoggerUtils.logWarning("MARCAS", "Campo nombre vacío en el formulario.");
            return;
        }

        if (marca == null) {
            // Insertar nueva marca
            boolean insertado = marcaDAO.insertarMarca(nombre);
            if (insertado) {
                mostrarAlerta("Éxito", "Marca agregada con éxito.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            }
        } else {
            // Actualizar marca existente
            boolean actualizado = marcaDAO.actualizarMarca(marca.getCodigo(), nombre);
            if (actualizado) {
                mostrarAlerta("Éxito", "Marca actualizada correctamente.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            }
        }
    }

    @FXML
    private void btnActionCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}