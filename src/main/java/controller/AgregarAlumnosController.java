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
        cargarSedes();
        btnGuardar.setOnAction(this::guardarAlumno);
    }

    private void cargarSedes() {
        Connection conn = null;
        try {
            conn = DataBaseConection.getConnection();
            String query = "SELECT codigo_sede, nombre FROM sede";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Sede sede = new Sede(rs.getInt("codigo_sede"), rs.getString("nombre"));
                listaSedes.add(sede);
            }

            cbox_sede.setItems(listaSedes);

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error cargando sedes desde la base de datos.", Alert.AlertType.ERROR);
            e.printStackTrace();
        } finally {
            DataBaseConection.closeConnection(conn);
        }
    }

    private void guardarAlumno(ActionEvent event) {
        String nombre = txtNombre.getText().trim();
        String curso = txtCurso.getText().trim();
        Sede sedeSeleccionada = cbox_sede.getValue();

        if (nombre.isEmpty() || curso.isEmpty() || sedeSeleccionada == null) {
            mostrarAlerta("Campos incompletos", "Por favor, completa todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        AgregarAlumnos alumno = new AgregarAlumnos(0, nombre, curso, sedeSeleccionada.getCodigoSede());

        Connection conn = null;
        try {
            conn = DataBaseConection.getConnection();
            String insertSQL = "INSERT INTO alumno (nombre, curso, codigo_sede) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertSQL);

            stmt.setString(1, alumno.getNombre());
            stmt.setString(2, alumno.getCurso());
            stmt.setInt(3, alumno.getCodigo_sede());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                mostrarAlerta("Éxito", "Alumno agregado con éxito.", Alert.AlertType.INFORMATION);
                txtNombre.clear();
                txtCurso.clear();
                cbox_sede.setValue(null);
            } else {
                mostrarAlerta("Error", "No se pudo agregar el alumno.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al guardar en la base de datos.", Alert.AlertType.ERROR);
            e.printStackTrace();
        } finally {
            DataBaseConection.closeConnection(conn);
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
