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
import model.Sede;
import utils.DataBaseConection;
import utils.LoggerUtils;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class AlumnosController implements Initializable {

    // Tabla y columnas
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

    // Botones
    @FXML
    private Button btnNuevoAlumno;
    @FXML
    private Button btnEliminarAlumno;
    @FXML
    private Button btnEliminarTodos;
    @FXML
    private Button btnBuscar;

    // Entrada de texto
    @FXML
    private TextField txtBuscar;

    private ObservableList<Alumno> listaAlumnos = FXCollections.observableArrayList();

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
        String query = "SELECT a.codigo_alumno, a.nombre, a.curso, a.codigo_sede, s.nombre AS nombre_sede "
                + "FROM alumnos a JOIN sedes s ON a.codigo_sede = s.codigo_sede";

        try (Connection connection = DataBaseConection.getConnection(); Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            listaAlumnos.clear();

            while (rs.next()) {
                Alumno alumno = new Alumno(
                        rs.getInt("codigo_alumno"),
                        rs.getString("nombre"),
                        rs.getString("curso"),
                        rs.getString("nombre_sede"),
                        rs.getInt("codigo_sede")
                );
                listaAlumnos.add(alumno);
            }

            tablaUsuarios.setItems(listaAlumnos);

        } catch (Exception e) {
            LoggerUtils.logError("Error al cargar alumnos.", e);
        }
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
            LoggerUtils.logError("Error al abrir el formulario de alumno.", e);
        }
    }

    @FXML
    private void btnActionEliminarAlumno() {
        Alumno alumno = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (alumno == null) {
            return;
        }

        String sql = "DELETE FROM alumnos WHERE codigo_alumno = ?";

        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, alumno.getCodigo());
            stmt.executeUpdate();
            cargarDatos();

        } catch (SQLException e) {
            LoggerUtils.logError("Error al eliminar alumno.", e);
        }
    }

    @FXML
    private void btnActionEliminarTodos() {
        String sql = "DELETE FROM alumnos";

        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();
            cargarDatos();

        } catch (SQLException e) {
            LoggerUtils.logError("Error al eliminar todos los alumnos.", e);
        }
    }

    @FXML
    private void btnBuscarAction() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            tablaUsuarios.setItems(listaAlumnos);
            return;
        }

        ObservableList<Alumno> filtrados = FXCollections.observableArrayList();

        for (Alumno a : listaAlumnos) {
            if (a.getNombre().toLowerCase().contains(filtro)
                    || a.getCurso().toLowerCase().contains(filtro)
                    || a.getNombreSede().toLowerCase().contains(filtro)
                    || String.valueOf(a.getCodigo()).contains(filtro)) {
                filtrados.add(a);
            }
        }

        tablaUsuarios.setItems(filtrados);
    }
}
