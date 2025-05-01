package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Alumno;
import model.Sede;
import utils.DataBaseConection;
import utils.LoggerUtils;

import java.net.URL;
import java.sql.*;
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
        String query = "SELECT codigo_sede, nombre FROM sedes";

        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            LoggerUtils.logQuery("ALUMNOS", "Cargar sedes para el ComboBox", query);

            while (rs.next()) {
                Sede sede = new Sede(rs.getInt("codigo_sede"), rs.getString("nombre"));
                listaSedes.add(sede);
                LoggerUtils.logInfo("ALUMNOS", "Sede cargada → Código: " + sede.getCodigoSede() + ", Nombre: " + sede.getNombre());
            }

            cbox_sede.setItems(listaSedes);

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error cargando sedes desde la base de datos.", Alert.AlertType.ERROR);
            LoggerUtils.logError("ALUMNOS", "Error al cargar sedes", e);
        }
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
            insertarAlumno(nombre, curso, sedeSeleccionada.getCodigoSede());
        } else {
            actualizarAlumno(nombre, curso, sedeSeleccionada.getCodigoSede());
        }
    }

    private void insertarAlumno(String nombre, String curso, int codigoSede) {
        String insertSQL = "INSERT INTO alumnos (nombre, curso, codigo_sede) VALUES (?, ?, ?)";

        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(insertSQL)) {

            LoggerUtils.logQuery("ALUMNOS", "Insertar nuevo alumno", insertSQL);

            stmt.setString(1, nombre);
            stmt.setString(2, curso);
            stmt.setInt(3, codigoSede);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                mostrarAlerta("Éxito", "Alumno agregado con éxito.", Alert.AlertType.INFORMATION);
                LoggerUtils.logInfo("ALUMNOS", "Alumno insertado → Nombre: " + nombre + ", Curso: " + curso + ", Sede: " + codigoSede);
                cerrarVentana();
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo insertar el alumno.", Alert.AlertType.ERROR);
            LoggerUtils.logError("ALUMNOS", "Error al insertar alumno.", e);
        }
    }

    private void actualizarAlumno(String nombre, String curso, int codigoSede) {
        String updateSQL = "UPDATE alumnos SET nombre = ?, curso = ?, codigo_sede = ? WHERE codigo_alumno = ?";

        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(updateSQL)) {

            LoggerUtils.logQuery("ALUMNOS", "Actualizar alumno", updateSQL);

            stmt.setString(1, nombre);
            stmt.setString(2, curso);
            stmt.setInt(3, codigoSede);
            stmt.setInt(4, alumno.getCodigo());

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                mostrarAlerta("Éxito", "Alumno actualizado correctamente.", Alert.AlertType.INFORMATION);
                LoggerUtils.logInfo("ALUMNOS", "Alumno actualizado → Código: " + alumno.getCodigo());
                cerrarVentana();
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo actualizar el alumno.", Alert.AlertType.ERROR);
            LoggerUtils.logError("ALUMNOS", "Error al actualizar alumno.", e);
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
