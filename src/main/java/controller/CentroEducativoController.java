package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.CentroEducativo;
import model.CentroEducativoDAO;
import utils.LoggerUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CentroEducativoController implements Initializable {

    @FXML
    private TableView<CentroEducativo> tablaCentroEducativos;
    @FXML
    private TableColumn<CentroEducativo, String> colCodigoCentro;
    @FXML
    private TableColumn<CentroEducativo, String> colNombre;
    @FXML
    private TableColumn<CentroEducativo, String> colCalle;
    @FXML
    private TableColumn<CentroEducativo, String> colLocalidad;
    @FXML
    private TableColumn<CentroEducativo, String> colCP;
    @FXML
    private TableColumn<CentroEducativo, String> colMunicipio;
    @FXML
    private TableColumn<CentroEducativo, String> colProvincia;
    @FXML
    private TableColumn<CentroEducativo, String> colTelefono;
    @FXML
    private TableColumn<CentroEducativo, String> colEmail;
    @FXML
    private TextField txtBuscar;
    @FXML
    private Button btnNuevoCentro;
    @FXML
    private Button btnEliminarCentro;
    @FXML
    private Button btnEliminarTodosCentros;
    @FXML
    private Button btnBuscarCentro;

    private final ObservableList<CentroEducativo> listaCentros = FXCollections.observableArrayList();
    private final CentroEducativoDAO centroEducativoDAO = new CentroEducativoDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LoggerUtils.logSection("CENTROS EDUCATIVOS");
        tablaCentroEducativos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        configurarColumnas();
        cargarDatos();

        tablaCentroEducativos.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tablaCentroEducativos.getSelectionModel().isEmpty()) {
                abrirFormularioCentro(tablaCentroEducativos.getSelectionModel().getSelectedItem());
            }
        });

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                tablaCentroEducativos.setItems(listaCentros);
            } else {
                buscarCentros(newVal);
            }
        });
    }

    private void configurarColumnas() {
        colCodigoCentro.setCellValueFactory(new PropertyValueFactory<>("codigoCentro"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCalle.setCellValueFactory(new PropertyValueFactory<>("calle"));
        colLocalidad.setCellValueFactory(new PropertyValueFactory<>("localidad"));
        colCP.setCellValueFactory(new PropertyValueFactory<>("cp"));
        colMunicipio.setCellValueFactory(new PropertyValueFactory<>("municipio"));
        colProvincia.setCellValueFactory(new PropertyValueFactory<>("provincia"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private void cargarDatos() {
        List<CentroEducativo> centros = centroEducativoDAO.obtenerCentros();
        listaCentros.setAll(centros);
        tablaCentroEducativos.setItems(listaCentros);
        LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Total centros cargados: " + listaCentros.size());
    }

    @FXML
    private void btnActionNuevoCentro(ActionEvent event) {
        abrirFormularioCentro(null);
    }

    private void abrirFormularioCentro(CentroEducativo centro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CentroEducativoMantenim.fxml"));
            Parent root = loader.load();

            CentroEducativoMantenimController controller = loader.getController();
            controller.setCentro(centro);

            Stage modal = new Stage();
            modal.setScene(new Scene(root));
            modal.setTitle(centro == null ? "Nuevo Centro Educativo" : "Editar Centro Educativo");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setResizable(false);
            modal.showAndWait();

            cargarDatos();

        } catch (IOException e) {
            LoggerUtils.logError("CENTROS EDUCATIVOS", "Error al abrir formulario de centro", e);
        }
    }

    @FXML
    private void btnActionEliminarCentro() {
        CentroEducativo seleccionado = tablaCentroEducativos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            return;
        }

        boolean eliminado = centroEducativoDAO.eliminarCentro(seleccionado.getCodigoCentro());
        if (eliminado) {
            LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Centro eliminado → Código: " + seleccionado.getCodigoCentro());
            mostrarAlerta("Centro eliminado", "Se ha eliminado correctamente el centro.", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Error", "No se pudo eliminar el centro.", Alert.AlertType.ERROR);
        }
        cargarDatos();
    }

    @FXML
    private void btnActionEliminarTodosCentros() {
        int eliminados = centroEducativoDAO.eliminarTodosCentros();
        LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Centros eliminados: " + eliminados);
        mostrarAlerta("Centros eliminados", "Se eliminaron " + eliminados + " centros.", Alert.AlertType.INFORMATION);
        cargarDatos();
    }

    @FXML
    private void btnBuscarCentroAction() {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) {
            cargarDatos();
        } else {
            buscarCentros(filtro);
        }
    }

    private void buscarCentros(String filtro) {
        List<CentroEducativo> filtrados = centroEducativoDAO.buscarCentros(filtro);
        listaCentros.setAll(filtrados);
        tablaCentroEducativos.setItems(listaCentros);
        LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Filtro aplicado: " + filtro + " → Resultados: " + filtrados.size());
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
