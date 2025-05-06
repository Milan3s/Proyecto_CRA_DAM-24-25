package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Alumno;
import model.Sede;
import model.AlumnosDAO;
import utils.LoggerUtils;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;

public class AlumnosMantenimController implements Initializable {

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtCurso;
    @FXML
    private ComboBox<Sede> cbox_sede;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;

    private ObservableList<Sede> listaSedes = FXCollections.observableArrayList();
    private Alumno alumno;  // null = nuevo, distinto de null = edición
    private final AlumnosDAO alumnosDAO = new AlumnosDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LoggerUtils.logSection("ALUMNOS");
        cargarSedes();
        btnGuardar.setOnAction(e -> guardarAlumno());
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
        if (alumno != null) {
            txtNombre.setText(alumno.getNombre());
            txtCurso.setText(alumno.getCurso());
            seleccionarSedePorCodigo(alumno.getCodigo_sede());
        }
    }

    private void cargarSedes() {
        listaSedes.setAll(alumnosDAO.obtenerSedes());
        cbox_sede.setItems(listaSedes);
    }

    private void seleccionarSedePorCodigo(int codigoSede) {
        for (Sede sede : listaSedes) {
            if (sede.getCodigoSede() == codigoSede) {
                cbox_sede.setValue(sede);
                return;
            }
        }
    }

    private void guardarAlumno() {
        String nombre = txtNombre.getText().trim();
        String curso = txtCurso.getText().trim();
        Sede sedeSeleccionada = cbox_sede.getValue();

        if (nombre.isEmpty() || curso.isEmpty() || sedeSeleccionada == null) {
            mostrarAlerta("Campos incompletos", "Por favor, completa todos los campos.", Alert.AlertType.WARNING);
            LoggerUtils.logWarning("ALUMNOS", "Faltan campos en el formulario.");
            return;
        }

        if (alumno == null) {
            // Insertar nuevo alumno
            boolean inserted = alumnosDAO.insertarAlumno(nombre, curso, sedeSeleccionada.getCodigoSede());
            if (inserted) {
                mostrarAlerta("Éxito", "Alumno agregado con éxito.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            }
        } else {
            // Actualizar alumno existente
            boolean updated = alumnosDAO.actualizarAlumno(alumno.getCodigo(), nombre, curso, sedeSeleccionada.getCodigoSede());
            if (updated) {
                mostrarAlerta("Éxito", "Alumno actualizado correctamente.", Alert.AlertType.INFORMATION);
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
