package controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Proveedor;
import utils.DataBaseConection;
import utils.LoggerUtils;
import static utils.Utilidades.mostrarAlerta2;

public class ProveedoresMantenimController implements Initializable {

    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtCalle;
    @FXML
    private TextField txtLocalidad;
    @FXML
    private TextField txtCp;
    @FXML
    private TextField txtMunicipio;
    @FXML
    private TextField txtProvincia;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtEmail;

    private Proveedor proveedor;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    public void setProveedor(Proveedor proveedor) {
        if (null != proveedor) {
            this.proveedor = proveedor;
            txtNombre.setText(proveedor.getNombre());
            txtCalle.setText(proveedor.getCalle());
            txtLocalidad.setText(proveedor.getLocalidad());
            txtCp.setText(proveedor.getCp());
            txtMunicipio.setText(proveedor.getMunicipio());
            txtProvincia.setText(proveedor.getProvincia());
            txtTelefono.setText(proveedor.getTelefono());
            txtEmail.setText(proveedor.getEmail());
        }
    }

    @FXML
    private void btnGuardarAction(ActionEvent event) {
        if (null == this.proveedor) {
            insertarProv();
        } else {
            actualizarProv();
        }
    }

    @FXML
    private void btnCancelarAction(ActionEvent event) {
        cerrarVentana();
    }
    
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
    
    private void actualizarProv() {
        int codProv = this.proveedor.getCodigo();
        String nombre = txtNombre.getText().trim();
        String calle = txtCalle.getText().trim();
        String localidad = txtLocalidad.getText().trim();
        String cp = txtCp.getText().trim();
        String municipio = txtMunicipio.getText().trim();
        String provincia = txtProvincia.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();
        
        String sql = "UPDATE proveedores SET nombre = ?, calle = ?, localidad = ?, cp = ?, municipio = ?, provincia = ?, telefono = ?, email = ? WHERE codigo_proveedor = ?";
        
        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, calle);
            stmt.setString(3, localidad);
            stmt.setString(4, cp);
            stmt.setString(5, municipio);
            stmt.setString(6, provincia);
            stmt.setString(7, telefono);
            stmt.setString(8, email);
            stmt.setInt(9, codProv);
            
            int filas = stmt.executeUpdate();
            
            if (filas > 0) {
                mostrarAlerta2("Éxito", "Proveedor actualizado correctamente.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            }
        } catch (SQLException e) {
            mostrarAlerta2("Error SQL", "No se pudo actualizar el proveedor.\nDetalles: " + e.getMessage(), Alert.AlertType.ERROR);
            LoggerUtils.logError("PROVEEDORES", "Error al ejecutar actualización de proveedor", e);
        }
    }
    
    private void insertarProv() {
        String nombre = txtNombre.getText().trim();
        String calle = txtCalle.getText().trim();
        String localidad = txtLocalidad.getText().trim();
        String cp = txtCp.getText().trim();
        String municipio = txtMunicipio.getText().trim();
        String provincia = txtProvincia.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();
        
        String sql = "INSERT INTO proveedores (nombre, calle, localidad, cp, municipio, provincia, telefono, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, calle);
            stmt.setString(3, localidad);
            stmt.setString(4, cp);
            stmt.setString(5, municipio);
            stmt.setString(6, provincia);
            stmt.setString(7, telefono);
            stmt.setString(8, email);
            
            int filas = stmt.executeUpdate();
            
            if (filas > 0) {
                mostrarAlerta2("Éxito", "Proveedor guardado correctamente.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            }
        } catch (SQLException e) {
            mostrarAlerta2("Error SQL", "No se pudo guardar el proveedor.\nDetalles: " + e.getMessage(), Alert.AlertType.ERROR);
            LoggerUtils.logError("PROVEEDORES", "Error al ejecutar alta de proveedor", e);
        }
        
    }
}
