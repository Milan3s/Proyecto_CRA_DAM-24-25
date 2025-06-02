package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Sede;
import dao.SedeDAO;
import java.io.IOException;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.LoggerUtils;

public class SedeController {

    @FXML
    private TableView<Sede> tablaSedes;

    @FXML
    private TableColumn<Sede, Integer> colCodigoSede;

    @FXML
    private TableColumn<Sede, String> colNombre;

    @FXML
    private TableColumn<Sede, String> colCalle;

    @FXML
    private TableColumn<Sede, String> colLocalidad;

    @FXML
    private TableColumn<Sede, String> colCP;

    @FXML
    private TableColumn<Sede, String> colMunicipio;

    @FXML
    private TableColumn<Sede, String> colProvincia;

    @FXML
    private TableColumn<Sede, String> colTelefono;

    @FXML
    private TableColumn<Sede, Integer> colCodigoCentro;

    @FXML
    private TextField txtBuscar;

    private final SedeDAO sedeDAO = new SedeDAO();
    private final ObservableList<Sede> listaSedes = FXCollections.observableArrayList();
    @FXML
    private Button btnNuevaSede;
    @FXML
    private Button btnEliminarSede;
    @FXML
    private Button btnBuscarSede;

    public void initialize() {
        // Configurar columnas
        colCodigoSede.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCodigoSede()).asObject());
        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));
        colCalle.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCalle()));
        colLocalidad.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLocalidad()));
        colCP.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCp()));
        colMunicipio.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMunicipio()));
        colProvincia.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getProvincia()));
        colTelefono.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTelefono()));
        colCodigoCentro.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCodigoCentro()).asObject());

        cargarDatos();
    }

    private void cargarDatos() {
        List<Sede> sedes = sedeDAO.obtenerSede();
        listaSedes.setAll(sedes);
        tablaSedes.setItems(listaSedes);
    }

    @FXML
    private void btnNuevaSedeAction() {
        abrirMantenimiento(null);
    }

    @FXML
    private void btnEliminarSedeAction() {
        Sede seleccionada = tablaSedes.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            if (sedeDAO.eliminarSede(seleccionada.getCodigoSede())) {
                listaSedes.remove(seleccionada);
            }
        }
    }

    private void btnEliminarTodasSedesAction() {
        int eliminadas = sedeDAO.eliminarTodasSedes();
        if (eliminadas > 0) {
            listaSedes.clear();
        }
    }

    @FXML
    private void btnBuscarSedeAction() {
        String filtro = txtBuscar.getText();
        if (filtro.isEmpty()) {
            cargarDatos();
        } else {
            List<Sede> resultado = sedeDAO.buscarSedes(filtro);
            listaSedes.setAll(resultado);
        }
    }

    @FXML
    private void capturarClick(MouseEvent event) {
        if (event.getClickCount() == 2 && !tablaSedes.getSelectionModel().isEmpty()) {
            Sede sede = tablaSedes.getSelectionModel().getSelectedItem();
            abrirMantenimiento(sede);
        }
    }
    
    private void abrirMantenimiento(Sede sede) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SedeMantenim.fxml"));
            Parent root = loader.load();
            
            SedeMantenimController controller = loader.getController();
            controller.setSede(sede);

            Stage modalStage = new Stage();
            modalStage.setTitle("Mantenimiento de sedes");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.showAndWait();

            cargarDatos();

        } catch (IOException e) {
            LoggerUtils.logError("SEDES", "Error al abrir ventana SedeMantenim", e);
        }
    }
}
