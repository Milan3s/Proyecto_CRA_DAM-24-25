package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Alumno;
import model.Sede;
import utils.DataBaseConection;
import utils.LoggerUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

public class EditarAlumnoController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCurso;
    @FXML private ComboBox<Sede> cboxSede;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private Alumno alumno;
    private ObservableList<Sede> listaSedes = FXCollections.observableArrayList();

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
        LoggerUtils.logSection("ALUMNOS");
        LoggerUtils.logInfo("ALUMNOS", "Editando alumno → Código: " + alumno.getCodigo() + ", Nombre: " + alumno.getNombre());

        txtNombre.setText(alumno.getNombre());
        txtCurso.setText(alumno.getCurso());

        cargarSedes(); // Cargar y seleccionar sede
    }

    @FXML
    public void initialize() {
        btnGuardar.setOnAction(e -> guardarCambios());
    }

    private void cargarSedes() {
        String sql = "SELECT codigo_sede, nombre FROM sede";

        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            LoggerUtils.logQuery("ALUMNOS", "Cargar lista de sedes", sql);

            listaSedes.clear();
            while (rs.next()) {
                Sede sede = new Sede(rs.getInt("codigo_sede"), rs.getString("nombre"));
                listaSedes.add(sede);
                LoggerUtils.logInfo("ALUMNOS", "Sede cargada → Código: " + sede.getCodigoSede() + ", Nombre: " + sede.getNombre());
            }

            cboxSede.setItems(listaSedes);

            for (Sede sede : listaSedes) {
                if (sede.getCodigoSede() == alumno.getCodigo_sede()) {
                    cboxSede.setValue(sede);
                    LoggerUtils.logInfo("ALUMNOS", "Sede seleccionada por defecto → " + sede.getNombre());
                    break;
                }
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo cargar la lista de sedes.", Alert.AlertType.ERROR);
            LoggerUtils.logError("ALUMNOS", "Error al cargar sedes en el ComboBox", e);
        }
    }

    private void guardarCambios() {
        String nuevoNombre = txtNombre.getText().trim();
        String nuevoCurso = txtCurso.getText().trim();
        Sede sedeSeleccionada = cboxSede.getValue();

        if (nuevoNombre.isEmpty() || nuevoCurso.isEmpty() || sedeSeleccionada == null) {
            mostrarAlerta("Datos incompletos", "Por favor, completa todos los campos y selecciona una sede.", Alert.AlertType.WARNING);
            LoggerUtils.logInfo("ALUMNOS", "Fallo en validación al editar alumno. Campos vacíos.");
            return;
        }

        String sql = "UPDATE alumno SET nombre = ?, curso = ?, codigo_sede = ? WHERE codigo_alumno = ?";

        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            LoggerUtils.logQuery("ALUMNOS", "Actualizar datos del alumno ID: " + alumno.getCodigo(), sql);

            stmt.setString(1, nuevoNombre);
            stmt.setString(2, nuevoCurso);
            stmt.setInt(3, sedeSeleccionada.getCodigoSede());
            stmt.setInt(4, alumno.getCodigo());

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                mostrarAlerta("Éxito", "Alumno actualizado correctamente.", Alert.AlertType.INFORMATION);
                LoggerUtils.logInfo("ALUMNOS", "Alumno actualizado → Código: " + alumno.getCodigo() +
                        ", Nuevo nombre: " + nuevoNombre + ", Curso: " + nuevoCurso + ", Sede: " + sedeSeleccionada.getNombre());

                Stage stage = (Stage) btnGuardar.getScene().getWindow();
                stage.close();
            } else {
                mostrarAlerta("Aviso", "No se actualizó ningún registro.", Alert.AlertType.WARNING);
                LoggerUtils.logInfo("ALUMNOS", "No se actualizó el alumno con código: " + alumno.getCodigo());
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Hubo un problema al actualizar el alumno.", Alert.AlertType.ERROR);
            LoggerUtils.logError("ALUMNOS", "Error al actualizar alumno en base de datos", e);
        }
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    @FXML
    private void btnActionCancelar(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
