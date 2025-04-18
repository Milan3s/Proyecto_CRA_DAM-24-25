package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.CentroEducativo;
import utils.DataBaseConection;
import utils.LoggerUtils;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;

public class CentroEducativoMantenimController implements Initializable {

    @FXML
    private TextField txtCodigo;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtCalle;
    @FXML
    private TextField txtLocalidad;
    @FXML
    private TextField txtCP;
    @FXML
    private TextField txtMunicipio;
    @FXML
    private TextField txtProvincia;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtEmail;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;

    private CentroEducativo centro; // null si es nuevo

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LoggerUtils.logSection("CENTROS EDUCATIVOS");
        btnGuardar.setOnAction(e -> guardarCentro());
    }

    public void setCentro(CentroEducativo centro) {
        this.centro = centro;

        if (centro != null) {
            txtCodigo.setText(centro.getCodigoCentro());
            txtNombre.setText(centro.getNombre());
            txtCalle.setText(centro.getCalle());
            txtLocalidad.setText(centro.getLocalidad());
            txtCP.setText(centro.getCp());
            txtMunicipio.setText(centro.getMunicipio());
            txtProvincia.setText(centro.getProvincia());
            txtTelefono.setText(centro.getTelefono());
            txtEmail.setText(centro.getEmail());

            txtCodigo.setDisable(true); // si estamos editando, deshabilitamos el campo
        }
    }

    private void guardarCentro() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String calle = txtCalle.getText().trim();
        String localidad = txtLocalidad.getText().trim();
        String cp = txtCP.getText().trim();
        String municipio = txtMunicipio.getText().trim();
        String provincia = txtProvincia.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();

        if (codigo.isEmpty() || nombre.isEmpty() || calle.isEmpty() || localidad.isEmpty()
                || cp.isEmpty() || municipio.isEmpty() || provincia.isEmpty()
                || telefono.isEmpty() || email.isEmpty()) {
            mostrarAlerta("Campos incompletos", "Por favor, completa todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        if (cp.length() > 5) {
            mostrarAlerta("Código Postal inválido", "El código postal debe tener máximo 5 caracteres.", Alert.AlertType.WARNING);
            return;
        }

        if (centro == null) {
            insertarCentro(codigo, nombre, calle, localidad, cp, municipio, provincia, telefono, email);
        } else {
            actualizarCentro(codigo, nombre, calle, localidad, cp, municipio, provincia, telefono, email);
        }
    }

    private void insertarCentro(String codigo, String nombre, String calle, String localidad, String cp, String municipio, String provincia, String telefono, String email) {
        String sql = "INSERT INTO centros_edu (codigo_centro, nombre, calle, localidad, cp, municipio, provincia, telefono, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            stmt.setString(2, nombre);
            stmt.setString(3, calle);
            stmt.setString(4, localidad);
            stmt.setString(5, cp);
            stmt.setString(6, municipio);
            stmt.setString(7, provincia);
            stmt.setString(8, telefono);
            stmt.setString(9, email);

            LoggerUtils.logQuery("CENTROS EDUCATIVOS", "Insertar nuevo centro", sql);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Centro insertado correctamente → Código: " + codigo);
                mostrarAlerta("Éxito", "Centro educativo agregado correctamente.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            }

        } catch (SQLException e) {
            LoggerUtils.logError("CENTROS EDUCATIVOS", "Error al insertar centro", e);
            mostrarAlerta("Error", "No se pudo insertar el centro.", Alert.AlertType.ERROR);
        }
    }

    private void actualizarCentro(String codigo, String nombre, String calle, String localidad, String cp, String municipio, String provincia, String telefono, String email) {
        String sql = "UPDATE centros_edu SET nombre=?, calle=?, localidad=?, cp=?, municipio=?, provincia=?, telefono=?, email=? WHERE codigo_centro=?";

        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, calle);
            stmt.setString(3, localidad);
            stmt.setString(4, cp);
            stmt.setString(5, municipio);
            stmt.setString(6, provincia);
            stmt.setString(7, telefono);
            stmt.setString(8, email);
            stmt.setString(9, codigo);

            LoggerUtils.logQuery("CENTROS EDUCATIVOS", "Actualizar centro", sql);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Centro actualizado correctamente → Código: " + codigo);
                mostrarAlerta("Éxito", "Centro educativo actualizado correctamente.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            }

        } catch (SQLException e) {
            LoggerUtils.logError("CENTROS EDUCATIVOS", "Error al actualizar centro", e);
            mostrarAlerta("Error", "No se pudo actualizar el centro.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void btnActionCancelar(ActionEvent event) {
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
