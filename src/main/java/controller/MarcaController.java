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
import model.Marca;
import model.MarcaDAO;
import utils.LoggerUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MarcaController implements Initializable {

    @FXML
    private TableView<Marca> tablaUsuarios;
    @FXML
    private TableColumn<Marca, Integer> colCodigo;
    @FXML
    private TableColumn<Marca, String> colNombre;
    @FXML
    private Button btnNuevoMarca;
    @FXML
    private Button btnEliminarMarca;
    @FXML
    private Button btnEliminarTodos;
    @FXML
    private Button btnBuscar;
    @FXML
    private TextField txtBuscar;

    private final ObservableList<Marca> listaMarcas = FXCollections.observableArrayList();
    private final MarcaDAO marcaDAO = new MarcaDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();

        tablaUsuarios.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tablaUsuarios.getSelectionModel().isEmpty()) {
                Marca marcaSeleccionada = tablaUsuarios.getSelectionModel().getSelectedItem();
                abrirFormularioMarca(marcaSeleccionada);
            }
        });

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                tablaUsuarios.setItems(listaMarcas);
            }
        });
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
    }

    private void cargarDatos() {
        List<Marca> marcas = marcaDAO.obtenerMarcas();
        listaMarcas.setAll(marcas);
        tablaUsuarios.setItems(listaMarcas);
    }

    @FXML
    private void btnActionNuevoMarca() {
        abrirFormularioMarca(null);
    }

    private void abrirFormularioMarca(Marca marca) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MarcasMantenim.fxml"));
            Parent root = loader.load();

            MarcaMantenimController controller = loader.getController();
            controller.setMarca(marca);

            Stage modal = new Stage();
            modal.setScene(new Scene(root));
            modal.setTitle(marca == null ? "Nueva Marca" : "Editar Marca");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setResizable(false);
            modal.showAndWait();

            cargarDatos();
        } catch (IOException e) {
            LoggerUtils.logError("Marcas", "Error al abrir el formulario de marca", e);
        }
    }

    @FXML
    private void btnActionEliminarMarca() {
        Marca marca = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (marca != null && marcaDAO.eliminarMarca(marca.getCodigo())) {
            cargarDatos();
        }
    }

    @FXML
    private void btnActionEliminarTodos() {
        int filasEliminadas = marcaDAO.eliminarTodasMarcas();
        LoggerUtils.logInfo("Marcas", "Total de marcas eliminadas: " + filasEliminadas);
        cargarDatos();
    }

    @FXML
    private void btnBuscarAction() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarDatos();
        } else {
            List<Marca> filtradas = marcaDAO.buscarMarcas(filtro);
            listaMarcas.setAll(filtradas);
            tablaUsuarios.setItems(listaMarcas);
        }
    }
}