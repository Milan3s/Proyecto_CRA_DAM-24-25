package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.CentroEducativo;
import model.CentroEducativoDAO;
import utils.LoggerUtils;

import java.net.URL;
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

    private CentroEducativo centro;
    private final CentroEducativoDAO centroEducativoDAO = new CentroEducativoDAO();

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

        // Validación de los campos de entrada
        if (codigo.isEmpty() || nombre.isEmpty() || calle.isEmpty() || localidad.isEmpty()
                || cp.isEmpty() || municipio.isEmpty() || provincia.isEmpty()
                || telefono.isEmpty() || email.isEmpty()) {
            mostrarAlerta("Campos incompletos", "Por favor, completa todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        // Validación del código postal
        if (cp.length() > 5) {
            mostrarAlerta("Código Postal inválido", "El código postal debe tener máximo 5 caracteres.", Alert.AlertType.WARNING);
            return;
        }

        // Crear el objeto CentroEducativo con los datos
        CentroEducativo nuevoCentro = new CentroEducativo(codigo, nombre, calle, localidad, cp, municipio, provincia, telefono, email);

        // Verificar si es un nuevo centro o uno existente
        if (centro == null) {
            // Insertar el nuevo centro
            boolean inserted = centroEducativoDAO.insertarCentro(nuevoCentro);
            if (inserted) {
                mostrarAlerta("Éxito", "Centro educativo agregado correctamente.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            }
        } else {
            // Actualizar el centro existente
            centro.setNombre(nombre);
            centro.setCalle(calle);
            centro.setLocalidad(localidad);
            centro.setCp(cp);
            centro.setMunicipio(municipio);
            centro.setProvincia(provincia);
            centro.setTelefono(telefono);
            centro.setEmail(email);

            boolean updated = centroEducativoDAO.actualizarCentro(centro);
            if (updated) {
                mostrarAlerta("Éxito", "Centro educativo actualizado correctamente.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            }
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
