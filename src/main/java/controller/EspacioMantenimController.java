package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import model.Espacio;
import dao.EspacioDAO;
import model.Sede;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EspacioMantenimController implements Initializable {

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtPabellon;

    @FXML
    private ComboBox<Sede> comboSede;

    @FXML
    private Spinner<Integer> spinnerPlanta;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnCancelar;

    private EspacioDAO espacioDAO;
    private Espacio espacioEditado;
    @FXML
    private TextField txtNumAbaco;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        espacioDAO = new EspacioDAO();
        cargarSedes();
        configurarSpinnerPlanta();
    }

    private void cargarSedes() {
        List<Sede> sedes = espacioDAO.obtenerSedes();
        comboSede.setItems(FXCollections.observableArrayList(sedes));
    }

    private void configurarSpinnerPlanta() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0);
        spinnerPlanta.setValueFactory(valueFactory);
        spinnerPlanta.setEditable(true);
    }

    public void setEspacio(Espacio espacio) {
        this.espacioEditado = espacio;
        txtNombre.setText(espacio.getNombre());
        txtPabellon.setText(espacio.getPabellon());
        spinnerPlanta.getValueFactory().setValue(espacio.getPlanta());
        txtNumAbaco.setText(espacio.getNumAbaco());

        for (Sede sede : comboSede.getItems()) {
            if (sede.getCodigoSede() == espacio.getCodigoSede()) {
                comboSede.getSelectionModel().select(sede);
                break;
            }
        }
    }

    @FXML
    private void btnGuardarAction(ActionEvent event) {
        String nombre = txtNombre.getText().trim();
        String pabellon = txtPabellon.getText().trim();
        Integer planta = spinnerPlanta.getValue();
        Sede sedeSeleccionada = comboSede.getSelectionModel().getSelectedItem();
        String numAbaco = txtNumAbaco.getText().trim();

        if (nombre.isEmpty() || pabellon.isEmpty() || planta == null || sedeSeleccionada == null || numAbaco.isEmpty()) {
            mostrarAlerta("Por favor completa todos los campos.");
            return;
        }

        boolean exito;
        if (espacioEditado == null) {
            exito = espacioDAO.insertarEspacio(nombre, pabellon, planta, sedeSeleccionada.getCodigoSede(), numAbaco);
        } else {
            exito = espacioDAO.actualizarEspacio(
                    espacioEditado.getCodigoEspacio(),
                    nombre, pabellon, planta,
                    sedeSeleccionada.getCodigoSede(),
                    numAbaco
            );
        }

        if (exito) {
            cerrarVentana();
        } else {
            mostrarAlerta("Ocurrió un error al guardar el espacio.");
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

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
