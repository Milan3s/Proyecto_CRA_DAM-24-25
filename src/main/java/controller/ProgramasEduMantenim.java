package controller;

import dao.ProgramasEduDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utils.LoggerUtils;
import java.net.URL;
import java.util.ResourceBundle;
import model.ProgramasEdu;

public class ProgramasEduMantenim implements Initializable {

    @FXML
    private TextField txtNombre;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;

    private ProgramasEdu programa;  // null = nuevo, distinto de null = edición
    private final ProgramasEduDAO programaDAO = new ProgramasEduDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LoggerUtils.logSection("PROGRAMAS EDUCATIVOS");
        btnGuardar.setOnAction(e -> guardarPrograma());
    }

    public void setPrograma(ProgramasEdu programa) {
        this.programa = programa;
        if (programa != null) {
            txtNombre.setText(programa.getNombre());
        }
    }

    private void guardarPrograma() {
        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta("Campo vacío", "Por favor, ingresa un nombre para el programa educativo.", Alert.AlertType.WARNING);
            LoggerUtils.logWarning("PROGRAMAS EDUCATIVOS", "Campo nombre vacío en el formulario.");
            return;
        }

        if (programa == null) {
            // Insertar nuevo programa
            boolean insertado = programaDAO.insertarPrograma(nombre);
            if (insertado) {
                mostrarAlerta("Éxito", "Programa educativo agregado con éxito.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            }
        } else {
            // Actualizar programa existente
            boolean actualizado = programaDAO.actualizarPrograma(programa.getCodigo(), nombre);
            if (actualizado) {
                mostrarAlerta("Éxito", "Programa educativo actualizado correctamente.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            }
        }
    }

    @FXML
    private void btnActionCancelar() {
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