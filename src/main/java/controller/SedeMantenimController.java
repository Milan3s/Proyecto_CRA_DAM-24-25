package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.CentroEducativo;
import dao.CentroEducativoDAO;
import model.Sede;
import dao.SedeDAO;
import utils.LoggerUtils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SedeMantenimController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCalle;
    @FXML private TextField txtLocalidad;
    @FXML private TextField txtCP;
    @FXML private TextField txtMunicipio;
    @FXML private TextField txtProvincia;
    @FXML private TextField txtTelefono;
    @FXML private ComboBox<Integer> comboCodigoCentro;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private Sede sede;
    private final SedeDAO sedeDAO = new SedeDAO();
    private final CentroEducativoDAO centroDAO = new CentroEducativoDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LoggerUtils.logSection("MANTENIMIENTO SEDE");

        cargarCodigosCentro();

        btnGuardar.setOnAction(e -> guardarSede());
    }

    private void cargarCodigosCentro() {
        List<CentroEducativo> centros = centroDAO.obtenerCentros();
        ObservableList<Integer> codigos = FXCollections.observableArrayList();
        for (CentroEducativo c : centros) {
            codigos.add(Integer.parseInt(c.getCodigoCentro()));
        }
        comboCodigoCentro.setItems(codigos);
    }

    public void setSede(Sede sede) {
        this.sede = sede;

        if (sede != null) {
            txtNombre.setText(sede.getNombre());
            txtCalle.setText(sede.getCalle());
            txtLocalidad.setText(sede.getLocalidad());
            txtCP.setText(sede.getCp());
            txtMunicipio.setText(sede.getMunicipio());
            txtProvincia.setText(sede.getProvincia());
            txtTelefono.setText(sede.getTelefono());
            comboCodigoCentro.setValue(sede.getCodigoCentro());
        }
    }

    private void guardarSede() {
        String nombre = txtNombre.getText().trim();
        String calle = txtCalle.getText().trim();
        String localidad = txtLocalidad.getText().trim();
        String cp = txtCP.getText().trim();
        String municipio = txtMunicipio.getText().trim();
        String provincia = txtProvincia.getText().trim();
        String telefono = txtTelefono.getText().trim();
        Integer codigoCentro = comboCodigoCentro.getValue();

        if (nombre.isEmpty() || calle.isEmpty() || localidad.isEmpty() || cp.isEmpty() ||
                municipio.isEmpty() || provincia.isEmpty() || telefono.isEmpty() || codigoCentro == null) {
            mostrarAlerta("Campos incompletos", "Por favor, completa todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        if (cp.length() > 5) {
            mostrarAlerta("Código Postal inválido", "El código postal debe tener máximo 5 caracteres.", Alert.AlertType.WARNING);
            return;
        }

        if (sede == null) {
            Sede nueva = new Sede(nombre, calle, localidad, cp, municipio, provincia, telefono, codigoCentro);
            boolean inserted = sedeDAO.insertarSede(nueva);
            if (inserted) {
                mostrarAlerta("Éxito", "Sede agregada correctamente.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            }
        } else {
            sede.setNombre(nombre);
            sede.setCalle(calle);
            sede.setLocalidad(localidad);
            sede.setCp(cp);
            sede.setMunicipio(municipio);
            sede.setProvincia(provincia);
            sede.setTelefono(telefono);
            sede.setCodigoCentro(codigoCentro);

            boolean updated = sedeDAO.actualizarSede(sede);
            if (updated) {
                mostrarAlerta("Éxito", "Sede actualizada correctamente.", Alert.AlertType.INFORMATION);
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