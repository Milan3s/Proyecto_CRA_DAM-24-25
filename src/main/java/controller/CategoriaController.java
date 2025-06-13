// Paquete donde está el controlador
package controller;

// Importación de clases necesarias para la lógica y la interfaz
import dao.CategoriaDAO;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Categoria;
import utils.LoggerUtils;
import utils.Utilidades;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

// Controlador de la ventana de categorías
public class CategoriaController implements Initializable {

    // Componentes de la interfaz gráfica (enlazados con el FXML)
    @FXML private Button btnNuevaCat;
    @FXML private Button btnEliminarCat;
    @FXML private Button btnImportar;
    @FXML private Button btnExportar;
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private TableView<Categoria> tablaCat;
    @FXML private TableColumn<Categoria, Integer> colCodigo;
    @FXML private TableColumn<Categoria, String> colNombre;

    // Lista de categorías para la tabla
    private final ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
    // Acceso a la base de datos
    private final CategoriaDAO catDAO = new CategoriaDAO();

    // Este método se llama automáticamente al cargar la ventana
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();  // Configura las columnas de la tabla
        cargarDatos();         // Carga las categorías desde la base de datos

        // Doble clic en una fila abre el formulario de edición
        tablaCat.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tablaCat.getSelectionModel().isEmpty()) {
                Categoria seleccionada = tablaCat.getSelectionModel().getSelectedItem();
                abrirFormularioCategoria(seleccionada);
            }
        });

        // Si el campo de búsqueda se vacía, recarga todas las categorías
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                tablaCat.setItems(listaCategorias);
            }
        });
    }

    // Asocia las propiedades del objeto Categoria con las columnas de la tabla
    private void configurarColumnas() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
    }

    // Carga las categorías desde el DAO y las muestra en la tabla
    private void cargarDatos() {
        List<Categoria> categorias = catDAO.obtenerCategorias();
        listaCategorias.setAll(categorias);
        tablaCat.setItems(listaCategorias);
    }

    // Acción para el botón "Nueva Categoría"
    @FXML
    private void btnActionNuevaCat(ActionEvent event) {
        abrirFormularioCategoria(null); // null indica que es nueva categoría
    }

    // Acción para eliminar una categoría seleccionada
    @FXML
    private void btnActionEliminarCat(ActionEvent event) {
        Categoria seleccionada = tablaCat.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            // Muestra un mensaje si no se seleccionó nada
            Utilidades.mostrarAlerta2("Sin selección", "Selecciona una categoría para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        // Pide confirmación antes de eliminar
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Seguro que quieres eliminar esta categoría?");
        confirmacion.setContentText("Categoría: " + seleccionada.getNombre() + "\nCódigo: " + seleccionada.getCodigo());

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                boolean eliminada = catDAO.eliminarCategoria(seleccionada.getCodigo());
                if (eliminada) {
                    cargarDatos();
                    Utilidades.mostrarAlerta2("Eliminada", "Categoría eliminada correctamente.", Alert.AlertType.INFORMATION);
                } else {
                    Utilidades.mostrarAlerta2("Error", "No se pudo eliminar la categoría.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    // Abre el formulario para añadir o editar una categoría
    private void abrirFormularioCategoria(Categoria categoria) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CategoriaMantenim.fxml"));
            Parent root = loader.load();

            CategoriaMantenimController controller = loader.getController();
            controller.setCategoria(categoria);

            Stage modal = new Stage();
            modal.setScene(new Scene(root));
            modal.setTitle(categoria == null ? "Nueva Categoría" : "Editar Categoría");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setResizable(false);
            modal.showAndWait();

            cargarDatos();
        } catch (IOException e) {
            LoggerUtils.logError("Categorías", "Error al abrir el formulario de categoría: " + e.getMessage(), e);
        }
    }

    // Realiza la búsqueda de categorías por texto
    @FXML
    private void btnBuscarAction(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarDatos();
        } else {
            List<Categoria> filtradas = catDAO.buscarCategorias(filtro);
            listaCategorias.setAll(filtradas);
            tablaCat.setItems(listaCategorias);
        }
    }

    // Importa categorías desde un archivo CSV
    @FXML
    private void btnImportarAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar Categorías");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        File archivo = fileChooser.showOpenDialog(null);

        if (archivo != null) {
            int exitos = 0;
            int errores = 0;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), "UTF-8"))) {
                String linea;
                int lineaActual = 0;

                LoggerUtils.logInfo("Categorías", "Iniciando importación desde: " + archivo.getAbsolutePath());

                while ((linea = reader.readLine()) != null) {
                    lineaActual++;

                    if (lineaActual == 1 && linea.toLowerCase().contains("nombre")) {
                        continue; // Salta la cabecera
                    }

                    String[] campos = linea.split(";");
                    if (campos.length < 1) {
                        LoggerUtils.logWarning("Categorías", "Línea incompleta ignorada (línea " + lineaActual + "): " + linea);
                        errores++;
                        continue;
                    }

                    try {
                        String nombre = campos[0].trim();
                        boolean insertado = catDAO.insertarCategoria(new Categoria(0, nombre));
                        if (insertado) {
                            LoggerUtils.logInfo("Categorías", "Categoría importada (línea " + lineaActual + "): " + nombre);
                            exitos++;
                        } else {
                            LoggerUtils.logWarning("Categorías", "No se pudo insertar la categoría (línea " + lineaActual + "): " + nombre);
                            errores++;
                        }
                    } catch (Exception e) {
                        LoggerUtils.logError("Categorías", "Error al procesar línea " + lineaActual + ": " + linea, e);
                        errores++;
                    }
                }

                cargarDatos();

                LoggerUtils.logInfo("Categorías", "Importación finalizada. Éxitos: " + exitos + " | Errores: " + errores);
                Utilidades.mostrarAlerta2("Importación finalizada",
                        "Categorías importadas correctamente: " + exitos + "\nErrores: " + errores,
                        Alert.AlertType.INFORMATION);

            } catch (IOException e) {
                LoggerUtils.logError("Categorías", "Error al leer el archivo de importación: " + archivo.getAbsolutePath(), e);
                Utilidades.mostrarAlerta2("Error", "No se pudo leer el archivo seleccionado.", Alert.AlertType.ERROR);
            }
        }
    }

    // Exporta las categorías a un archivo CSV
    @FXML
    private void btnExportarAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar Categorías");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        File archivo = fileChooser.showSaveDialog(null);

        if (archivo != null) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivo), "ISO-8859-1"))) {
                writer.write("Nombre\n"); // Escribe la cabecera
                for (Categoria categoria : listaCategorias) {
                    String linea = categoria.getNombre() != null ? categoria.getNombre() : "";
                    writer.write(linea + "\n");
                }

                Utilidades.mostrarAlerta2("Éxito", "Exportación realizada correctamente.", Alert.AlertType.INFORMATION);

            } catch (IOException e) {
                LoggerUtils.logError("EXPORTACION", "Error al exportar categorías: " + archivo, e);
                Utilidades.mostrarAlerta2("Error", "No se pudo exportar el archivo.", Alert.AlertType.ERROR);
            }
        }
    }
}
