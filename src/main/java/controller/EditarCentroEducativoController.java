// EditarCentroEducativoController.java
package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.CentroEducativo;
import utils.DataBaseConection;

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

        txtCodigo.setText(centro.getCodigoCentro());
        txtNombre.setText(centro.getNombre());
        txtCalle.setText(centro.getCalle());
        txtLocalidad.setText(centro.getLocalidad());
        txtCP.setText(centro.getCp());
        txtMunicipio.setText(centro.getMunicipio());
        txtProvincia.setText(centro.getProvincia());
        txtTelefono.setText(centro.getTelefono());
        txtEmail.setText(centro.getEmail());
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
            return;
        }

        String sql = "UPDATE centroeducativo SET nombre=?, calle=?, localidad=?, cp=?, municipio=?, provincia=?, telefono=?, email=? WHERE codigo_centro=?";

        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            stmt.setString(2, calle);
            stmt.setString(3, localidad);
            stmt.setString(4, cp);
            stmt.setString(5, municipio);
            stmt.setString(6, provincia);
            stmt.setString(7, telefono);
            stmt.setString(8, email);
            stmt.setString(9, centro.getCodigoCentro());

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                mostrarAlerta("Éxito", "Centro actualizado correctamente.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            } else {
                mostrarAlerta("Aviso", "No se actualizó ningún registro.", Alert.AlertType.WARNING);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar el centro en la base de datos.", Alert.AlertType.ERROR);
            e.printStackTrace();
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