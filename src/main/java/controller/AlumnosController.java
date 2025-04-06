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

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    @FXML
    private TableView<Alumno> tablaUsuarios;

    @FXML
    private TableColumn<Alumno, Integer> colCodigo;

    @FXML
    private TableColumn<Alumno, String> colNombre;

    @FXML
    private TableColumn<Alumno, String> colCurso;

    @FXML
    private TableColumn<Alumno, String> colCodigoSede; // ahora muestra nombreSede

    private ObservableList<Alumno> listaAlumnos = FXCollections.observableArrayList();

    @FXML
    private Button btnNuevoAlumno;
    @FXML
    private Button btnEliminarAlumno;
    @FXML
    private Button btnEliminarTodos;
    @FXML
    private TextField txtBuscar;
    @FXML
    private Button btnBuscar;

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
        try (Connection connection = DataBaseConection.getConnection()) {
            String query = "SELECT a.codigo_alumno, a.nombre, a.curso, a.codigo_sede, s.nombre AS nombre_sede "
                    + "FROM alumno a "
                    + "JOIN sede s ON a.codigo_sede = s.codigo_sede";

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            listaAlumnos.clear(); // Limpia antes de cargar

            while (rs.next()) {
                int codigo = rs.getInt("codigo_alumno");
                String nombre = rs.getString("nombre");
                String curso = rs.getString("curso");
                int codigoSede = rs.getInt("codigo_sede");
                String nombreSede = rs.getString("nombre_sede");

                listaAlumnos.add(new Alumno(codigo, nombre, curso, nombreSede, codigoSede));
            }

            tablaUsuarios.setItems(listaAlumnos);

        } catch (Exception e) {
            e.printStackTrace();
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

            // Recargar datos tras la edición
            cargarDatos();

        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    @FXML
    private void btnActionEliminarAlumno(ActionEvent event) {
        Alumno alumnoSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (alumnoSeleccionado == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Selecciona un alumno");
            alerta.setHeaderText(null);
            alerta.setContentText("Debes seleccionar un alumno para eliminar.");
            alerta.showAndWait();
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
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
            int filas = stmt.executeUpdate();

            if (filas > 0) {
                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.setTitle("Éxito");
                alerta.setHeaderText(null);
                alerta.setContentText("Alumno eliminado correctamente.");
                alerta.showAndWait();

                cargarDatos(); // Recarga la tabla
            } else {
                Alert alerta = new Alert(Alert.AlertType.WARNING);
                alerta.setTitle("No se eliminó");
                alerta.setHeaderText(null);
                alerta.setContentText("No se pudo eliminar el alumno.");
                alerta.showAndWait();
            }

        } catch (SQLException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText(null);
            alerta.setContentText("Error al intentar eliminar el alumno.");
            alerta.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void btnActionEliminarTodos(ActionEvent event) {
        int totalAlumnos = listaAlumnos.size();

        if (totalAlumnos == 0) {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Sin registros");
            alerta.setHeaderText(null);
            alerta.setContentText("No hay alumnos para eliminar.");
            alerta.showAndWait();
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar todos los alumnos");
        confirmacion.setHeaderText("Se eliminarán " + totalAlumnos + " alumno(s)");
        confirmacion.setContentText("¿Estás seguro de que deseas eliminar todos los alumnos? Esta acción no se puede deshacer.");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                eliminarTodosLosAlumnos(totalAlumnos);
            }
        });
    }

    private void eliminarTodosLosAlumnos(int totalEliminables) {
        String sql = "DELETE FROM alumno";

        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            int filas = stmt.executeUpdate();

            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Eliminación completada");
            alerta.setHeaderText(null);

            if (filas > 0) {
                alerta.setContentText("Se eliminaron correctamente " + filas + " de " + totalEliminables + " alumno(s).");
            } else {
                alerta.setContentText("No se eliminó ningún alumno.");
            }

            alerta.showAndWait();
            cargarDatos();

        } catch (SQLException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText(null);
            alerta.setContentText("Ocurrió un error al eliminar los alumnos.");
            alerta.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void btnBuscarAction(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();

        if (filtro.isEmpty()) {
            tablaUsuarios.setItems(listaAlumnos); // Mostrar todos si está vacío
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
    }

}
