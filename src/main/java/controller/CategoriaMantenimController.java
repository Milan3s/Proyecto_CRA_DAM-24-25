package controller;

import dao.CategoriaDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utils.LoggerUtils;
import java.net.URL;
import java.util.ResourceBundle;
import model.Categoria;

public class CategoriaMantenimController implements Initializable {

    @FXML
    private TextField txtNombre;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;

    private Categoria categoria;  // null = nuevo, distinto de null = edición
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnGuardar.setOnAction(e -> guardarCategoria());
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
        if (categoria != null) {
            txtNombre.setText(categoria.getNombre());
        }
    }

    private void guardarCategoria() {
        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta("Campo vacío", "Por favor, ingresa un nombre para la categoría.", Alert.AlertType.WARNING);
            LoggerUtils.logWarning("CATEGORIAS", "Campo nombre vacío en el formulario.");
            return;
        }

        if (categoria == null) {
            // Insertar nueva categoría
            boolean insertado = categoriaDAO.insertarCategoria(nombre);
            if (insertado) {
                mostrarAlerta("Éxito", "Categoría agregada con éxito.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            }
        } else {
            // Actualizar categoría existente
            boolean actualizado = categoriaDAO.actualizarCategoria(categoria.getCodigo(), nombre);
            if (actualizado) {
                mostrarAlerta("Éxito", "Categoría actualizada correctamente.", Alert.AlertType.INFORMATION);
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