package controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.AgregarCentroEducativo;
import utils.DataBaseConection;

public class AgregarCentroEducativoController implements Initializable {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCalle;
    @FXML private TextField txtLocalidad;
    @FXML private TextField txtCP;
    @FXML private TextField txtMunicipio;
    @FXML private TextField txtProvincia;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnGuardar.setOnAction(this::guardarCentro);
    }

    private void guardarCentro(ActionEvent event) {
        // Crear instancia del modelo usando los campos
        AgregarCentroEducativo centro = new AgregarCentroEducativo(
                txtCodigo.getText().trim(),
                txtNombre.getText().trim(),
                txtCalle.getText().trim(),
                txtLocalidad.getText().trim(),
                txtCP.getText().trim(),
                txtMunicipio.getText().trim(),
                txtProvincia.getText().trim(),
                txtTelefono.getText().trim(),
                txtEmail.getText().trim()
        );

        // Validar campos vacíos
        if (centro.getCodigoCentro().isEmpty() || centro.getNombre().isEmpty() || centro.getCalle().isEmpty()
                || centro.getLocalidad().isEmpty() || centro.getCp().isEmpty() || centro.getMunicipio().isEmpty()
                || centro.getProvincia().isEmpty() || centro.getTelefono().isEmpty() || centro.getEmail().isEmpty()) {
            mostrarAlerta("Campos incompletos", "Por favor, completa todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        Connection conn = null;
        try {
            conn = DataBaseConection.getConnection();
            String insertSQL = "INSERT INTO centroeducativo (codigo_centro, nombre, calle, localidad, cp, municipio, provincia, telefono, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertSQL);

            stmt.setString(1, centro.getCodigoCentro());
            stmt.setString(2, centro.getNombre());
            stmt.setString(3, centro.getCalle());
            stmt.setString(4, centro.getLocalidad());
            stmt.setString(5, centro.getCp());
            stmt.setString(6, centro.getMunicipio());
            stmt.setString(7, centro.getProvincia());
            stmt.setString(8, centro.getTelefono());
            stmt.setString(9, centro.getEmail());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                mostrarAlerta("Éxito", "Centro educativo agregado con éxito.", Alert.AlertType.INFORMATION);
                limpiarCampos();
            } else {
                mostrarAlerta("Error", "No se pudo agregar el centro.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar en la base de datos.", Alert.AlertType.ERROR);
            e.printStackTrace();
        } finally {
            DataBaseConection.closeConnection(conn);
        }
    }

    private void limpiarCampos() {
        txtCodigo.clear();
        txtNombre.clear();
        txtCalle.clear();
        txtLocalidad.clear();
        txtCP.clear();
        txtMunicipio.clear();
        txtProvincia.clear();
        txtTelefono.clear();
        txtEmail.clear();
    }

    @FXML
    private void btnActionCancelar(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
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
