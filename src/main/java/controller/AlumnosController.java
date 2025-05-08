package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Alumno;
import model.AlumnosDAO;
import utils.LoggerUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AlumnosController implements Initializable {

    @FXML
    private TableView<Alumno> tablaUsuarios;
    @FXML
    private TableColumn<Alumno, Integer> colCodigo;
    @FXML
    private TableColumn<Alumno, String> colNombre;
    @FXML
    private TableColumn<Alumno, String> colCurso;
    @FXML
    private TableColumn<Alumno, String> colCodigoSede;
    @FXML
    private Button btnNuevoAlumno;
    @FXML
    private Button btnEliminarAlumno;
    @FXML
    private Button btnEliminarTodos;
    @FXML
    private Button btnBuscar;
    @FXML
    private TextField txtBuscar;

    private ObservableList<Alumno> listaAlumnos = FXCollections.observableArrayList();
    private final AlumnosDAO alumnosDAO = new AlumnosDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();

        tablaUsuarios.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tablaUsuarios.getSelectionModel().isEmpty()) {
                Alumno alumnoSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
                abrirFormularioAlumno(alumnoSeleccionado);
            }
        });

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                tablaUsuarios.setItems(listaAlumnos);
            }
        });
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCurso.setCellValueFactory(new PropertyValueFactory<>("curso"));
        colCodigoSede.setCellValueFactory(new PropertyValueFactory<>("nombreSede"));
    }

    private void cargarDatos() {
        List<Alumno> alumnos = alumnosDAO.obtenerAlumnos();
        listaAlumnos.setAll(alumnos);
        tablaUsuarios.setItems(listaAlumnos);
    }

    @FXML
    private void btnActionNuevoAlumno() {
        abrirFormularioAlumno(null);
    }

    private void abrirFormularioAlumno(Alumno alumno) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AlumnosMantenim.fxml"));
            Parent root = loader.load();

            AlumnosMantenimController controller = loader.getController();
            controller.setAlumno(alumno);

            Stage modal = new Stage();
            modal.setScene(new Scene(root));
            modal.setTitle(alumno == null ? "Nuevo Alumno" : "Editar Alumno");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setResizable(false);
            modal.showAndWait();

            cargarDatos();

        } catch (IOException e) {
            LoggerUtils.logError("Error al abrir el formulario de alumno", e);
        }
    }

    @FXML
    private void btnActionEliminarAlumno() {
        Alumno alumno = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (alumno != null && alumnosDAO.eliminarAlumno(alumno.getCodigo())) {
            cargarDatos();
        }
    }

    @FXML
    private void btnActionEliminarTodos() {
        int filasEliminadas = alumnosDAO.eliminarTodosAlumnos();
        LoggerUtils.logInfo("Alumnos", "Total de alumnos eliminados: " + filasEliminadas);
        cargarDatos();
    }

    @FXML
    private void btnBuscarAction() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarDatos();
        } else {
            List<Alumno> filtrados = alumnosDAO.buscarAlumnos(filtro);
            listaAlumnos.setAll(filtrados);
            tablaUsuarios.setItems(listaAlumnos);
        }
    }
}
