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

    // Campos de texto del formulario
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCalle;
    @FXML private TextField txtLocalidad;
    @FXML private TextField txtCP;
    @FXML private TextField txtMunicipio;
    @FXML private TextField txtProvincia;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;

    // Botones de acción
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    // Centro educativo en edición (null si es nuevo)
    private CentroEducativo centro;

    // Acceso a datos
    private final CentroEducativoDAO centroEducativoDAO = new CentroEducativoDAO();

    // Inicialización del formulario
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LoggerUtils.logSection("CENTROS EDUCATIVOS");

        // Asignar acción al botón de guardar
        btnGuardar.setOnAction(e -> guardarCentro());
    }

    // Método para cargar un centro al formulario (modo edición)
    public void setCentro(CentroEducativo centro) {
        this.centro = centro;

        // Si el centro no es null, rellenar los campos y bloquear el código
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

            txtCodigo.setDisable(true); // El código no se puede modificar en edición
        }
    }

    // Lógica principal para guardar (insertar o actualizar)
    private void guardarCentro() {
        // Obtener valores desde los campos
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String calle = txtCalle.getText().trim();
        String localidad = txtLocalidad.getText().trim();
        String cp = txtCP.getText().trim();
        String municipio = txtMunicipio.getText().trim();
        String provincia = txtProvincia.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();

        // Validar campos obligatorios
        if (codigo.isEmpty() || nombre.isEmpty() || calle.isEmpty() || localidad.isEmpty()
                || cp.isEmpty() || municipio.isEmpty() || provincia.isEmpty()
                || telefono.isEmpty() || email.isEmpty()) {
            mostrarAlerta("Campos incompletos", "Por favor, completa todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        // Validar longitud del código postal
        if (cp.length() > 5) {
            mostrarAlerta("Código Postal inválido", "El código postal debe tener máximo 5 caracteres.", Alert.AlertType.WARNING);
            return;
        }

        // Crear nuevo objeto CentroEducativo con los datos del formulario
        CentroEducativo nuevoCentro = new CentroEducativo(
                codigo, nombre, calle, localidad, cp, municipio, provincia, telefono, email
        );

        // Si el centro es nuevo
        if (centro == null) {
            boolean inserted = centroEducativoDAO.insertarCentro(nuevoCentro);
            if (inserted) {
                mostrarAlerta("Éxito", "Centro educativo agregado correctamente.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            }
        } else {
            // Si estamos editando un centro ya existente, actualizar campos
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

    // Acción del botón Cancelar: cerrar la ventana
    @FXML
    private void btnActionCancelar(ActionEvent event) {
        cerrarVentana();
    }

    // Cierra la ventana modal actual
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    // Muestra una alerta con mensaje personalizado
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
