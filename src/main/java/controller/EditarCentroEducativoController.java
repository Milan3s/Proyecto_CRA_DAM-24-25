package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.CentroEducativo;
import utils.DataBaseConection;
import utils.LoggerUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.event.ActionEvent;

public class EditarCentroEducativoController {

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

    private CentroEducativo centro;

    public void setCentro(CentroEducativo centro, boolean disableCodigo) {
        this.centro = centro;

        LoggerUtils.logSection("CENTROS EDUCATIVOS");
        LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Editando centro → Código: " + centro.getCodigoCentro() + ", Nombre: " + centro.getNombre());

        txtCodigo.setText(centro.getCodigoCentro());
        txtNombre.setText(centro.getNombre());
        txtCalle.setText(centro.getCalle());
        txtLocalidad.setText(centro.getLocalidad());
        txtCP.setText(centro.getCp());
        txtMunicipio.setText(centro.getMunicipio());
        txtProvincia.setText(centro.getProvincia());
        txtTelefono.setText(centro.getTelefono());
        txtEmail.setText(centro.getEmail());

        txtCodigo.setDisable(disableCodigo);
    }

    public void initialize() {
        btnGuardar.setOnAction(e -> guardarCambios());
    }

    private void guardarCambios() {
        String nombre = txtNombre.getText().trim();
        String calle = txtCalle.getText().trim();
        String localidad = txtLocalidad.getText().trim();
        String cp = txtCP.getText().trim();
        String municipio = txtMunicipio.getText().trim();
        String provincia = txtProvincia.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();

        if (nombre.isEmpty() || calle.isEmpty() || localidad.isEmpty() || cp.isEmpty()
                || municipio.isEmpty() || provincia.isEmpty() || telefono.isEmpty() || email.isEmpty()) {

            mostrarAlerta("Datos incompletos", "Por favor, completa todos los campos.", Alert.AlertType.WARNING);
            LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Fallo en validación. Campos vacíos al intentar guardar centro.");
            return;
        }

        if (cp.length() > 5) {
            mostrarAlerta("Código Postal inválido", "El código postal debe tener máximo 5 caracteres.", Alert.AlertType.WARNING);
            return;
        }

        String sql = "UPDATE centros_edu SET nombre=?, calle=?, localidad=?, cp=?, municipio=?, provincia=?, telefono=?, email=? WHERE codigo_centro=?";

        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            LoggerUtils.logQuery("CENTROS EDUCATIVOS", "Actualizar centro educativo con código: " + centro.getCodigoCentro(), sql);

            stmt.setString(1, nombre);
            stmt.setString(2, calle);
            stmt.setString(3, localidad);
            stmt.setString(4, cp);
            stmt.setString(5, municipio);
            stmt.setString(6, provincia);
            stmt.setString(7, telefono);
            stmt.setString(8, email);
            stmt.setInt(9, Integer.parseInt(centro.getCodigoCentro()));

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                mostrarAlerta("Éxito", "Centro actualizado correctamente.", Alert.AlertType.INFORMATION);
                LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Centro actualizado → Código: " + centro.getCodigoCentro()
                        + ", Nombre: " + nombre + ", Localidad: " + localidad + ", Provincia: " + provincia);
                cerrarVentana();
            } else {
                mostrarAlerta("Aviso", "No se actualizó ningún registro.", Alert.AlertType.WARNING);
                LoggerUtils.logInfo("CENTROS EDUCATIVOS", "No se actualizó el centro con código: " + centro.getCodigoCentro());
            }

        } catch (SQLException e) {
            mostrarAlerta("Error SQL", "No se pudo actualizar el centro.\nDetalles: " + e.getMessage(), Alert.AlertType.ERROR);
            LoggerUtils.logError("CENTROS EDUCATIVOS", "Error al ejecutar actualización de centro", e);
        }
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    @FXML
    private void btnActionCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }
}
