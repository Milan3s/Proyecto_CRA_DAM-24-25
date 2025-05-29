package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Proveedor;
import dao.ProveedorDAO;
import javafx.scene.control.Alert;
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
    private ProveedorDAO provDAO = new ProveedorDAO();
    
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
        guardarProveedor();
    }

    @FXML
    private void btnCancelarAction(ActionEvent event) {
        cerrarVentana();
    }
    
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void guardarProveedor() {
        int codProv;
        
        if (null == this.proveedor) {
            codProv = 0;
        } else {
            codProv = this.proveedor.getCodigo();
        }
        
        String nombre = txtNombre.getText();
        String calle = txtCalle.getText();
        String localidad = txtLocalidad.getText();
        String cp = txtCp.getText();
        String municipio = txtMunicipio.getText();
        String provincia = txtProvincia.getText();
        String telefono = txtTelefono.getText();
        String email = txtEmail.getText();
        
        if (nombre.isEmpty()) {
            mostrarAlerta2("Campos incompletos", "Por favor, completa todos los campos obligatorios.", Alert.AlertType.WARNING);
            LoggerUtils.logWarning("PROVEEDORES", "Faltan campos obligatorios en el formulario.");
            return;
        }
        
        Proveedor p = new Proveedor(codProv, nombre, calle, localidad, cp, municipio, provincia, telefono, email);
        
        if (null == this.proveedor) {
            provDAO.insertarProveedor(p);
        } else {
            provDAO.actualizarProveedor(p);
        }
        
        cerrarVentana();
    }
}
