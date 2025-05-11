package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Alumno;
import model.Sede;
import dao.AlumnosDAO;
import utils.LoggerUtils;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;

public class AlumnosMantenimController implements Initializable {

    // Elementos de la interfaz
    @FXML
    private TextField txtNombre;      // Campo de texto para el nombre del alumno
    @FXML
    private TextField txtCurso;       // Campo de texto para el curso
    @FXML
    private ComboBox<Sede> cbox_sede; // ComboBox para seleccionar la sede
    @FXML
    private Button btnGuardar;        // Botón de guardar
    @FXML
    private Button btnCancelar;       // Botón de cancelar

    // Lista de sedes cargada desde la base de datos
    private ObservableList<Sede> listaSedes = FXCollections.observableArrayList();

    // Alumno a editar; si es null, se está creando uno nuevo
    private Alumno alumno;

    // DAO para operaciones con la base de datos
    private final AlumnosDAO alumnosDAO = new AlumnosDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LoggerUtils.logSection("ALUMNOS");

        // Carga inicial de sedes al abrir el formulario
        cargarSedes();

        // Asigna acción al botón "Guardar"
        btnGuardar.setOnAction(e -> guardarAlumno());
    }

    // Este método es llamado desde el controlador principal al abrir el formulario
    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;

        // Si se recibe un alumno (modo edición), se rellenan los campos
        if (alumno != null) {
            txtNombre.setText(alumno.getNombre());
            txtCurso.setText(alumno.getCurso());
            seleccionarSedePorCodigo(alumno.getCodigo_sede());
        }
    }

    // Carga todas las sedes disponibles en el ComboBox
    private void cargarSedes() {
        listaSedes.setAll(alumnosDAO.obtenerSedes());
        cbox_sede.setItems(listaSedes);
    }

    // Busca y selecciona la sede actual del alumno (modo edición)
    private void seleccionarSedePorCodigo(int codigoSede) {
        for (Sede sede : listaSedes) {
            if (sede.getCodigoSede() == codigoSede) {
                cbox_sede.setValue(sede);
                return;
            }
        }
    }

    // Guarda el alumno (nuevo o modificado)
    private void guardarAlumno() {
        String nombre = txtNombre.getText().trim();
        String curso = txtCurso.getText().trim();
        Sede sedeSeleccionada = cbox_sede.getValue();

        // Validación de campos obligatorios
        if (nombre.isEmpty() || curso.isEmpty() || sedeSeleccionada == null) {
            mostrarAlerta("Campos incompletos", "Por favor, completa todos los campos.", Alert.AlertType.WARNING);
            LoggerUtils.logWarning("ALUMNOS", "Faltan campos en el formulario.");
            return;
        }

        if (alumno == null) {
            // Crear nuevo alumno
            boolean inserted = alumnosDAO.insertarAlumno(nombre, curso, sedeSeleccionada.getCodigoSede());
            if (inserted) {
                mostrarAlerta("Éxito", "Alumno agregado con éxito.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            }
        } else {
            // Actualizar alumno existente
            boolean updated = alumnosDAO.actualizarAlumno(
                    alumno.getCodigo(), nombre, curso, sedeSeleccionada.getCodigoSede()
            );
            if (updated) {
                mostrarAlerta("Éxito", "Alumno actualizado correctamente.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            }
        }
    }

    // Acción del botón "Cancelar": cierra el formulario sin guardar
    @FXML
    private void btnActionCancelar(ActionEvent event) {
        cerrarVentana();
    }

    // Cierra la ventana modal actual
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    // Muestra una alerta de información, advertencia o error
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
