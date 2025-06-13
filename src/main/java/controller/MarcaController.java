package controller;

// Importamos las clases necesarias
import dao.MarcaDAO; // Para acceder a los datos de marcas en la base de datos
import javafx.collections.FXCollections; // Para crear listas que se actualizan solas en la interfaz
import javafx.collections.ObservableList;
import javafx.fxml.FXML; // Para vincular el código con los elementos de la interfaz
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*; // Para usar botones, tablas, cuadros de texto, etc.
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser; // Para abrir el explorador de archivos
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Marca; // Clase Marca que representa cada marca
import utils.LoggerUtils; // Para registrar información o errores
import utils.Utilidades; // Para mostrar mensajes de alerta

import java.io.*; // Para leer y escribir archivos
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

// Esta es la clase que controla la pantalla de marcas
public class MarcaController implements Initializable {

    // Elementos de la interfaz (tabla, columnas, botones, campo de búsqueda)
    @FXML private TableView<Marca> tablaMarcas;
    @FXML private TableColumn<Marca, Integer> colCodigo;
    @FXML private TableColumn<Marca, String> colNombre;

    @FXML private Button btnNuevoMarca;
    @FXML private Button btnEliminarMarca;
    @FXML private Button btnBuscar;
    @FXML private Button btnImportarMarca;
    @FXML private Button btnExportarMarca;
    @FXML private TextField txtBuscar;

    // Lista de marcas que se muestra en la tabla
    private final ObservableList<Marca> listaMarcas = FXCollections.observableArrayList();

    // Objeto que permite acceder a la base de datos
    private final MarcaDAO marcaDAO = new MarcaDAO();

    // Este método se ejecuta automáticamente al abrir la ventana
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas(); // Configura las columnas de la tabla
        cargarDatos(); // Carga las marcas desde la base de datos

        // Si el usuario hace doble clic en una fila de la tabla, abre el formulario para editarla
        tablaMarcas.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tablaMarcas.getSelectionModel().isEmpty()) {
                Marca marcaSeleccionada = tablaMarcas.getSelectionModel().getSelectedItem();
                abrirFormularioMarca(marcaSeleccionada);
            }
        });

        // Si el campo de búsqueda queda vacío, recarga los datos completos
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                cargarDatos();
            }
        });
    }

    // Configura qué dato se muestra en cada columna de la tabla
    private void configurarColumnas() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
    }

    // Carga la lista de marcas desde la base de datos y las muestra en la tabla
    private void cargarDatos() {
        List<Marca> marcas = marcaDAO.obtenerMarcas();
        listaMarcas.setAll(marcas);
        tablaMarcas.setItems(listaMarcas);
    }

    // Acción del botón "Nueva Marca": abre el formulario vacío para crear una marca
    @FXML
    private void btnNuevoMarcaAction() {
        abrirFormularioMarca(null);
    }

    // Abre el formulario para crear o editar una marca
    private void abrirFormularioMarca(Marca marca) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MarcaMantenim.fxml"));
            Parent root = loader.load();

            MarcaMantenimController controller = loader.getController();
            controller.setMarca(marca); // Si es null es nueva, si no es edición

            Stage modal = new Stage();
            modal.setScene(new Scene(root));
            modal.setTitle(marca == null ? "Nueva Marca" : "Editar Marca");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setResizable(false);
            modal.showAndWait();

            cargarDatos(); // Al cerrar el formulario, recarga los datos
        } catch (IOException e) {
            LoggerUtils.logError("Marcas", "Error al abrir el formulario de marca" + e.getMessage(), e);
        }
    }

    // Acción del botón "Eliminar Marca"
    @FXML
    private void btnEliminarMarcaAction() {
        Marca marca = tablaMarcas.getSelectionModel().getSelectedItem();

        if (marca == null) {
            Utilidades.mostrarAlerta2("Sin selección", "Selecciona una marca para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        // Muestra una ventana de confirmación antes de eliminar
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
                    LoggerUtils.logWarning("Marcas", "Error al eliminar marca: " + marca.getNombre());
                }
            }
        });
    }

    // Acción del botón "Buscar"
    @FXML
    private void btnBuscarAction() {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) {
            cargarDatos();
            return;
        }

        List<Marca> resultados = marcaDAO.buscarMarcas(filtro);
        listaMarcas.setAll(resultados);
        tablaMarcas.setItems(listaMarcas);
    }

    // Acción del botón "Importar Marca"
    @FXML
    private void btnImportarMarcaAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar Marcas");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        File archivo = fileChooser.showOpenDialog(tablaMarcas.getScene().getWindow());

        if (archivo != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                String linea;
                List<Marca> listaParaImportar = new ArrayList<>();
                while ((linea = reader.readLine()) != null) {
                    String[] campos = linea.split(";");
                    if (campos.length == 2) {
                        try {
                            int codigo = Integer.parseInt(campos[0].trim());
                            String nombre = campos[1].trim();
                            listaParaImportar.add(new Marca(codigo, nombre));
                        } catch (NumberFormatException ignored) {}
                    }
                }

                if (listaParaImportar.isEmpty()) {
                    Utilidades.mostrarAlerta2("Importar", "No hay datos válidos para importar.", Alert.AlertType.WARNING);
                    return;
                }

                List<Marca> marcasNoExistentes = new ArrayList<>();
                List<Marca> marcasExistentes = marcaDAO.obtenerMarcas();

                for (Marca mImport : listaParaImportar) {
                    boolean existe = marcasExistentes.stream()
                        .anyMatch(m -> m.getNombre().equalsIgnoreCase(mImport.getNombre()));
                    if (!existe) {
                        marcasNoExistentes.add(mImport);
                    }
                }

                if (marcasNoExistentes.isEmpty()) {
                    Utilidades.mostrarAlerta2("Importar", "Todos los datos importados ya existen en la tabla. No se insertó nada.", Alert.AlertType.ERROR);
                    LoggerUtils.logWarning("Marcas", "Intento de importar datos duplicados: no se insertó ninguna marca.");
                } else {
                    boolean insertados = marcaDAO.insertarListaMarcas(marcasNoExistentes);
                    if (insertados) {
                        cargarDatos();
                        Utilidades.mostrarAlerta2("Importar", "Importación completada con éxito.", Alert.AlertType.INFORMATION);
                        LoggerUtils.logInfo("Marcas", "Importación exitosa de marcas.");
                    } else {
                        Utilidades.mostrarAlerta2("Importar", "Error al insertar nuevas marcas.", Alert.AlertType.ERROR);
                        LoggerUtils.logWarning("Marcas", "Error al insertar nuevas marcas durante la importación.");
                    }
                }

            } catch (IOException | SQLException e) {
                Utilidades.mostrarAlerta2("Error", "Error al importar marcas: " + e.getMessage(), Alert.AlertType.ERROR);
                LoggerUtils.logError("Marcas", "Error importando marcas", e);
            }
        }
    }

    // Acción del botón "Exportar Marca"
    @FXML
    private void btnExportarMarcaAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar Marcas");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        File archivo = fileChooser.showSaveDialog(tablaMarcas.getScene().getWindow());

        if (archivo != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
                for (Marca marca : listaMarcas) {
                    String linea = marca.getCodigo() + ";" + marca.getNombre();
                    writer.write(linea);
                    writer.newLine();
                }
                writer.flush();
                Utilidades.mostrarAlerta2("Exportar", "Exportación completada con éxito.", Alert.AlertType.INFORMATION);
                LoggerUtils.logInfo("Marcas", "Exportación exitosa de marcas.");
            } catch (IOException e) {
                Utilidades.mostrarAlerta2("Error", "Error al exportar marcas: " + e.getMessage(), Alert.AlertType.ERROR);
                LoggerUtils.logError("Marcas", "Error exportando marcas", e);
            }
        }
    }
}
