package controller;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javafx.event.ActionEvent;
import javafx.stage.Stage;
import model.AgregarAlumnos;
import model.Sede;
import utils.DataBaseConection;
import utils.LoggerUtils;

public class AgregarAlumnosController implements Initializable {

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtCurso;
    @FXML
    private Button btnGuardar;
    @FXML
    private ComboBox<Sede> cbox_sede;
    @FXML
    private Button btnCancelar;

    private ObservableList<Sede> listaSedes = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LoggerUtils.logSection("ALUMNOS");
        cargarSedes();
        btnGuardar.setOnAction(this::guardarAlumno);
    }

    private void cargarSedes() {
        Connection conn = null;
        String query = "SELECT codigo_sede, nombre FROM sedes";

        try {
            conn = DataBaseConection.getConnection();
            LoggerUtils.logQuery("ALUMNOS", "Cargar sedes para el ComboBox", query);

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Sede sede = new Sede(rs.getInt("codigo_sede"), rs.getString("nombre"));
                listaSedes.add(sede);
                LoggerUtils.logInfo("ALUMNOS", "Sede cargada → Código: " + sede.getCodigoSede() + ", Nombre: " + sede.getNombre());
            }

            cbox_sede.setItems(listaSedes);
            LoggerUtils.logInfo("ALUMNOS", "Total sedes cargadas: " + listaSedes.size());

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error cargando sedes desde la base de datos.", Alert.AlertType.ERROR);
            LoggerUtils.logError("ALUMNOS", "Error al cargar sedes", e);
        } finally {
            //DataBaseConection.closeConnection(conn);
        }
    }

    private void guardarAlumno(ActionEvent event) {
        String nombre = txtNombre.getText().trim();
        String curso = txtCurso.getText().trim();
        Sede sedeSeleccionada = cbox_sede.getValue();

        if (nombre.isEmpty() || curso.isEmpty() || sedeSeleccionada == null) {
            mostrarAlerta("Campos incompletos", "Por favor, completa todos los campos.", Alert.AlertType.WARNING);
            LoggerUtils.logInfo("ALUMNOS", "Intento fallido de guardar alumno. Faltan campos.");
            return;
        }

        AgregarAlumnos alumno = new AgregarAlumnos(0, nombre, curso, sedeSeleccionada.getCodigoSede());

        Connection conn = null;
        String insertSQL = "INSERT INTO alumnos (nombre, curso, codigo_sede) VALUES (?, ?, ?)";

        try {
            conn = DataBaseConection.getConnection();
            LoggerUtils.logQuery("ALUMNOS", "Insertar nuevo alumno", insertSQL);

            PreparedStatement stmt = conn.prepareStatement(insertSQL);
            stmt.setString(1, alumno.getNombre());
            stmt.setString(2, alumno.getCurso());
            stmt.setInt(3, alumno.getCodigo_sede());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                mostrarAlerta("Éxito", "Alumno agregado con éxito.", Alert.AlertType.INFORMATION);
                LoggerUtils.logInfo("ALUMNOS", "Alumno guardado → Nombre: " + nombre + ", Curso: " + curso + ", Código Sede: " + alumno.getCodigo_sede());
                txtNombre.clear();
                txtCurso.clear();
                cbox_sede.setValue(null);
            } else {
                mostrarAlerta("Error", "No se pudo agregar el alumno.", Alert.AlertType.ERROR);
                LoggerUtils.logInfo("ALUMNOS", "No se insertó el alumno en la base de datos.");
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar en la base de datos.", Alert.AlertType.ERROR);
            LoggerUtils.logError("ALUMNOS", "Error al insertar alumno en la base de datos", e);
        } finally {
            //DataBaseConection.closeConnection(conn);
        }
    }

    @FXML
    private void btnActionCancelar(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    // Método reutilizable para mostrar alertas
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
