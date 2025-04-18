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

    public void initialize() {
        cargarSedes();
        btnGuardar.setOnAction(e -> guardarCambios());
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
        LoggerUtils.logSection("ALUMNOS");
        LoggerUtils.logInfo("ALUMNOS", "Editando alumno → Código: " + alumno.getCodigo() + ", Nombre: " + alumno.getNombre());

        txtNombre.setText(alumno.getNombre());
        txtCurso.setText(alumno.getCurso());

        seleccionarSedePorCodigo(alumno.getCodigo_sede());
    }

    private void cargarSedes() {
        String sql = "SELECT codigo_sede, nombre FROM sedes";

        try (Connection conn = DataBaseConection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            LoggerUtils.logQuery("ALUMNOS", "Cargar lista de sedes", sql);

            listaSedes.clear();
            while (rs.next()) {
                Sede sede = new Sede(rs.getInt("codigo_sede"), rs.getString("nombre"));
                listaSedes.add(sede);
                LoggerUtils.logInfo("ALUMNOS", "Sede cargada → Código: " + sede.getCodigoSede() + ", Nombre: " + sede.getNombre());
            }

            cboxSede.setItems(listaSedes);

        } catch (SQLException e) {
            LoggerUtils.logError("ALUMNOS", "Error al cargar sedes en el ComboBox → " + e.getMessage(), e);
        }
    }

    private void seleccionarSedePorCodigo(int codigoSede) {
        boolean sedeEncontrada = false;
        for (Sede sede : listaSedes) {
            if (sede.getCodigoSede() == codigoSede) {
                cboxSede.setValue(sede);
                LoggerUtils.logInfo("ALUMNOS", "Sede seleccionada por defecto → " + sede.getNombre());
                sedeEncontrada = true;
                break;
            }
        }

        if (!sedeEncontrada) {
            LoggerUtils.logWarning("ALUMNOS", "No se encontró la sede con código: " + codigoSede + " en la lista cargada.");
            cboxSede.setValue(null);
        }
    }

    private void guardarCambios() {
        String nuevoNombre = txtNombre.getText().trim();
        String nuevoCurso = txtCurso.getText().trim();
        Sede sedeSeleccionada = cboxSede.getValue();

        if (nuevoNombre.isEmpty() || nuevoCurso.isEmpty() || sedeSeleccionada == null) {
            LoggerUtils.logWarning("ALUMNOS", "Fallo en validación al editar alumno. Campos vacíos.");
            return;
        }

        String sql = "UPDATE alumnos SET nombre = ?, curso = ?, codigo_sede = ? WHERE codigo_alumno = ?";

        try (Connection conn = DataBaseConection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            LoggerUtils.logQuery("ALUMNOS", "Actualizar datos del alumno ID: " + alumno.getCodigo(), sql);

            stmt.setString(1, nuevoNombre);
            stmt.setString(2, nuevoCurso);
            stmt.setInt(3, sedeSeleccionada.getCodigoSede());
            stmt.setInt(4, alumno.getCodigo());

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                LoggerUtils.logInfo("ALUMNOS", "Alumno actualizado → Código: " + alumno.getCodigo()
                        + ", Nuevo nombre: " + nuevoNombre + ", Curso: " + nuevoCurso + ", Sede: " + sedeSeleccionada.getNombre());
                cerrarVentana();
            } else {
                LoggerUtils.logWarning("ALUMNOS", "No se actualizó ningún registro para el alumno con código: " + alumno.getCodigo());
            }

        } catch (SQLException e) {
            LoggerUtils.logError("ALUMNOS", "Error al actualizar alumno en base de datos", e);
        }
    }

    @FXML
    private void btnActionCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }
}
