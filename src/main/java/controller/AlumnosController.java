package controller;

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Alumno;
import utils.DataBaseConection;
import utils.LoggerUtils;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlumnosController implements Initializable {

    @FXML private TableView<Alumno> tablaUsuarios;
    @FXML private TableColumn<Alumno, Integer> colCodigo;
    @FXML private TableColumn<Alumno, String> colNombre;
    @FXML private TableColumn<Alumno, String> colCurso;
    @FXML private TableColumn<Alumno, String> colCodigoSede;
    private ObservableList<Alumno> listaAlumnos = FXCollections.observableArrayList();

    @FXML private Button btnNuevoAlumno;
    @FXML private Button btnEliminarAlumno;
    @FXML private Button btnEliminarTodos;
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tablaUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        configurarColumnas();
        cargarDatos();

        tablaUsuarios.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tablaUsuarios.getSelectionModel().isEmpty()) {
                Alumno alumnoSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
                abrirModalEditarAlumno(alumnoSeleccionado);
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
        String query = "SELECT a.codigo_alumno, a.nombre, a.curso, a.codigo_sede, s.nombre AS nombre_sede "
                     + "FROM alumno a JOIN sede s ON a.codigo_sede = s.codigo_sede";

        try (Connection connection = DataBaseConection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            LoggerUtils.logQuery("Consulta de alumnos", query);

            listaAlumnos.clear();
            int contador = 0;

            while (rs.next()) {
                int codigo = rs.getInt("codigo_alumno");
                String nombre = rs.getString("nombre");
                String curso = rs.getString("curso");
                int codigoSede = rs.getInt("codigo_sede");
                String nombreSede = rs.getString("nombre_sede");

                Alumno alumno = new Alumno(codigo, nombre, curso, nombreSede, codigoSede);
                listaAlumnos.add(alumno);

                LoggerUtils.logInfo("Alumno cargado → Código: " + codigo +
                        ", Nombre: " + nombre +
                        ", Curso: " + curso +
                        ", Código Sede: " + codigoSede +
                        ", Nombre Sede: " + nombreSede);
                contador++;
            }

            tablaUsuarios.setItems(listaAlumnos);
            LoggerUtils.logInfo("Total alumnos cargados: " + contador);

        } catch (Exception e) {
            LoggerUtils.logError("Error al cargar alumnos desde la base de datos.", e);
        }
    }

    private void abrirModalEditarAlumno(Alumno alumno) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EditarAlumno.fxml"));
            Parent root = loader.load();

            EditarAlumnoController controller = loader.getController();
            controller.setAlumno(alumno);

            Stage modalStage = new Stage();
            modalStage.setTitle("Editar Alumno");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.showAndWait();

            cargarDatos();

        } catch (IOException e) {
            LoggerUtils.logError("Error al abrir el modal de edición de alumno.", e);
        }
    }

    @FXML
    private void btnActionNuevoAlumno(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AgregarAlumnos.fxml"));
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle("Nuevo Alumno");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.showAndWait();

            cargarDatos();

        } catch (IOException e) {
            LoggerUtils.logError("Error al abrir el modal de nuevo alumno.", e);
        }
    }

    @FXML
    private void btnActionEliminarAlumno(ActionEvent event) {
        Alumno alumnoSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (alumnoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un alumno", "Debes seleccionar un alumno para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setContentText("¿Estás seguro de que deseas eliminar al alumno: " + alumnoSeleccionado.getNombre() + "?");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                eliminarAlumno(alumnoSeleccionado);
            }
        });
    }

    private void eliminarAlumno(Alumno alumno) {
        String sql = "DELETE FROM alumno WHERE codigo_alumno = ?";

        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, alumno.getCodigo());

            LoggerUtils.logQuery("Eliminar alumno con ID: " + alumno.getCodigo(), sql);

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Alumno eliminado correctamente.");
                LoggerUtils.logInfo("Alumno eliminado → Código: " + alumno.getCodigo() +
                        ", Nombre: " + alumno.getNombre() +
                        ", Curso: " + alumno.getCurso() +
                        ", Código Sede: " + alumno.getCodigo_sede() +
                        ", Nombre Sede: " + alumno.getNombreSede());
                cargarDatos();
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "No se eliminó", "No se pudo eliminar el alumno.");
                LoggerUtils.logInfo("Fallo al eliminar alumno con código: " + alumno.getCodigo());
            }

        } catch (SQLException e) {
            LoggerUtils.logError("Error al intentar eliminar un alumno.", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al intentar eliminar el alumno.");
        }
    }

    @FXML
    private void btnActionEliminarTodos(ActionEvent event) {
        int totalAlumnos = listaAlumnos.size();

        if (totalAlumnos == 0) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sin registros", "No hay alumnos para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar todos los alumnos");
        confirmacion.setHeaderText("Se eliminarán " + totalAlumnos + " alumno(s)");
        confirmacion.setContentText("¿Estás seguro? Esta acción no se puede deshacer.");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                eliminarTodosLosAlumnos(totalAlumnos);
            }
        });
    }

    private void eliminarTodosLosAlumnos(int totalEliminables) {
        String sql = "DELETE FROM alumno";

        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            LoggerUtils.logQuery("Eliminar todos los alumnos", sql);

            int filas = stmt.executeUpdate();

            String mensaje = (filas > 0)
                    ? "Se eliminaron correctamente " + filas + " de " + totalEliminables + " alumno(s)."
                    : "No se eliminó ningún alumno.";

            mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminación completada", mensaje);
            LoggerUtils.logInfo("Eliminación masiva → Total antes: " + totalEliminables + ", Eliminados: " + filas);
            cargarDatos();

        } catch (SQLException e) {
            LoggerUtils.logError("Error al eliminar todos los alumnos.", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ocurrió un error al eliminar los alumnos.");
        }
    }

    @FXML
    private void btnBuscarAction(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();

        if (filtro.isEmpty()) {
            tablaUsuarios.setItems(listaAlumnos);
            return;
        }

        ObservableList<Alumno> filtrados = FXCollections.observableArrayList();

        for (Alumno alumno : listaAlumnos) {
            if (alumno.getNombre().toLowerCase().contains(filtro)
                    || alumno.getCurso().toLowerCase().contains(filtro)
                    || alumno.getNombreSede().toLowerCase().contains(filtro)
                    || String.valueOf(alumno.getCodigo()).contains(filtro)
                    || String.valueOf(alumno.getCodigo_sede()).contains(filtro)) {
                filtrados.add(alumno);
            }
        }

        tablaUsuarios.setItems(filtrados);
        LoggerUtils.logInfo("Búsqueda ejecutada → Filtro: \"" + filtro + "\", Resultados: " + filtrados.size());
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
