package controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.Espacio;
import model.EspacioDAO;
import utils.LoggerUtils;

public class EspacioController implements Initializable {

    @FXML
    private Button btnNuevoEspacio;
    @FXML
    private Button btnEliminarEspacio;
    @FXML
    private TextField txtBuscarEspacio;
    @FXML
    private Button btnBuscarEspacio;
    @FXML
    private TableView<Espacio> tablaEspacios;
    @FXML
    private TableColumn<Espacio, Integer> colCodigoEspacio;
    @FXML
    private TableColumn<Espacio, String> colNombre;
    @FXML
    private TableColumn<Espacio, String> colPabellon;
    @FXML
    private TableColumn<Espacio, Integer> colPlanta;
    @FXML
    private TableColumn<Espacio, String> colNombreSede;

    private EspacioDAO espacioDAO;
    private ObservableList<Espacio> listaEspacios;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        espacioDAO = new EspacioDAO();
        configurarColumnasTabla();
        configurarDobleClick();
        cargarEspacios();
    }

    private void configurarColumnasTabla() {
        colCodigoEspacio.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCodigoEspacio()).asObject());
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colPabellon.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPabellon()));
        colPlanta.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPlanta()).asObject());
        colNombreSede.setCellValueFactory(data -> {
            Espacio e = data.getValue();
            return new SimpleStringProperty(e.getCodigoSede() + " - " + e.getNombreSede());
        });
    }

    private void configurarDobleClick() {
        tablaEspacios.setRowFactory(tv -> {
            TableRow<Espacio> fila = new TableRow<>();
            fila.setOnMouseClicked((MouseEvent event) -> {
                if (event.getClickCount() == 2 && !fila.isEmpty()) {
                    abrirFormularioEspacio(fila.getItem());
                }
            });
            return fila;
        });
    }

    private void cargarEspacios() {
        listaEspacios = FXCollections.observableArrayList(espacioDAO.obtenerEspacios());
        tablaEspacios.setItems(listaEspacios);
    }

    @FXML
    private void btnActionNuevoEspacio(ActionEvent event) {
        abrirFormularioEspacio(null);
    }

    @FXML
    private void btnActionEliminarEspacio(ActionEvent event) {
        Espacio seleccionado = tablaEspacios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un espacio para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmación");
        confirmacion.setHeaderText("¿Eliminar espacio?");
        confirmacion.setContentText("¿Estás seguro de que deseas eliminar el espacio \"" + seleccionado.getNombre() + "\"?");
        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            boolean eliminado = espacioDAO.eliminarEspacio(seleccionado.getCodigoEspacio());
            if (eliminado) {
                listaEspacios.remove(seleccionado);
                LoggerUtils.logInfo("ESPACIOS", "Espacio eliminado → Código: " + seleccionado.getCodigoEspacio());
            } else {
                mostrarAlerta("Error al eliminar el espacio.");
            }
        }
    }

    @FXML
    private void btnActionBuscarEspacio(ActionEvent event) {
        String filtro = txtBuscarEspacio.getText().trim();
        if (!filtro.isEmpty()) {
            List<Espacio> filtrados = espacioDAO.buscarEspacios(filtro);
            tablaEspacios.setItems(FXCollections.observableArrayList(filtrados));
        } else {
            cargarEspacios();
        }
    }

    private void abrirFormularioEspacio(Espacio espacio) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EspacioMantenim.fxml"));
            Parent root = loader.load();

            EspacioMantenimController controller = loader.getController();
            if (espacio != null) {
                controller.setEspacio(espacio);
            }

            Stage stage = new Stage();
            stage.setTitle(espacio == null ? "Nuevo Espacio" : "Editar Espacio");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            cargarEspacios(); // Refresca la tabla tras cerrar el modal
        } catch (IOException e) {
            LoggerUtils.logError("ESPACIOS", "Error al abrir formulario", e);
            mostrarAlerta("No se pudo abrir el formulario.");
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
