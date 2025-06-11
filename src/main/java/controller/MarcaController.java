package controller;

import dao.MarcaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Marca;
import utils.LoggerUtils;
import utils.Utilidades;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MarcaController implements Initializable {

    @FXML
    private TableView<Marca> tablaMarcas;
    @FXML
    private TableColumn<Marca, Integer> colCodigo;
    @FXML
    private TableColumn<Marca, String> colNombre;

    @FXML
    private Button btnNuevoMarca;
    @FXML
    private Button btnEliminarMarca;
    @FXML
    private Button btnBuscar;

    // Nuevos botones import/export
    @FXML
    private Button btnImportarMarca;
    @FXML
    private Button btnExportarMarca;

    @FXML
    private TextField txtBuscar;

    private final ObservableList<Marca> listaMarcas = FXCollections.observableArrayList();
    private final MarcaDAO marcaDAO = new MarcaDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();

        tablaMarcas.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tablaMarcas.getSelectionModel().isEmpty()) {
                Marca marcaSeleccionada = tablaMarcas.getSelectionModel().getSelectedItem();
                abrirFormularioMarca(marcaSeleccionada);
            }
        });

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                tablaMarcas.setItems(listaMarcas);
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
        tablaMarcas.setItems(listaMarcas);
    }

    @FXML
    private void btnNuevoMarcaAction() {
        abrirFormularioMarca(null);
    }

    private void abrirFormularioMarca(Marca marca) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MarcaMantenim.fxml"));
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
    private void btnEliminarMarcaAction() {
        Marca marca = tablaMarcas.getSelectionModel().getSelectedItem();

        if (marca == null) {
            Utilidades.mostrarAlerta2("Sin selección", "Selecciona una marca para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Seguro que quieres eliminar esta marca?");
        confirmacion.setContentText("Marca: " + marca.getNombre());

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                boolean eliminado = marcaDAO.eliminarMarca(marca.getCodigo());
                if (eliminado) {
                    cargarDatos();
                    Utilidades.mostrarAlerta2("Eliminado", "Marca eliminada correctamente.", Alert.AlertType.INFORMATION);
                    LoggerUtils.logInfo("Marcas", "Marca eliminada: " + marca.getNombre());
                } else {
                    Utilidades.mostrarAlerta2("Error", "No se pudo eliminar la marca.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    private void btnBuscarAction() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarDatos();
        } else {
            List<Marca> filtradas = marcaDAO.buscarMarcas(filtro);
            listaMarcas.setAll(filtradas);
            tablaMarcas.setItems(listaMarcas);
        }
    }

    // === MÉTODOS NUEVOS PARA IMPORTAR Y EXPORTAR ===

    @FXML
    private void btnImportarMarcaAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo para importar");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Archivos CSV", "*.csv")
        );
        File archivo = fileChooser.showOpenDialog(tablaMarcas.getScene().getWindow());
        if (archivo != null) {
            try {
                List<Marca> marcasImportadas = Utilidades.importarMarcasDesdeArchivo(archivo);
                if (!marcasImportadas.isEmpty()) {
                    int insertados = marcaDAO.insertarListaMarcas(marcasImportadas);
                    cargarDatos();
                    Utilidades.mostrarAlerta2("Importar", "Se importaron " + insertados + " marcas.", Alert.AlertType.INFORMATION);
                    LoggerUtils.logInfo("Marcas", "Importación exitosa de marcas: " + insertados);
                } else {
                    Utilidades.mostrarAlerta2("Importar", "No se encontraron marcas válidas para importar.", Alert.AlertType.WARNING);
                }
            } catch (Exception e) {
                Utilidades.mostrarAlerta2("Error", "Error al importar marcas: " + e.getMessage(), Alert.AlertType.ERROR);
                LoggerUtils.logError("Marcas", "Error importando marcas", e);
            }
        }
    }

    @FXML
    private void btnExportarMarcaAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo exportado");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Archivo CSV", "*.csv")
        );
        File archivo = fileChooser.showSaveDialog(tablaMarcas.getScene().getWindow());
        if (archivo != null) {
            try {
                List<Marca> marcasExportar = marcaDAO.obtenerMarcas();
                Utilidades.exportarMarcasAArchivo(marcasExportar, archivo);
                Utilidades.mostrarAlerta2("Exportar", "Exportación exitosa.", Alert.AlertType.INFORMATION);
                LoggerUtils.logInfo("Marcas", "Exportación exitosa a " + archivo.getAbsolutePath());
            } catch (Exception e) {
                Utilidades.mostrarAlerta2("Error", "Error al exportar marcas: " + e.getMessage(), Alert.AlertType.ERROR);
                LoggerUtils.logError("Marcas", "Error exportando marcas", e);
            }
        }
    }
}
