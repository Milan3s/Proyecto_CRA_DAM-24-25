package controller;

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

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CategoriaController implements Initializable {

    @FXML private Button btnNuevaCat;
    @FXML private Button btnEliminarCat;
    @FXML private Button btnImportar;
    @FXML private Button btnExportar;
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private TableView<Categoria> tablaCat;
    @FXML private TableColumn<Categoria, Integer> colCodigo;
    @FXML private TableColumn<Categoria, String> colNombre;

    private ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
    private final CategoriaDAO catDAO = new CategoriaDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();

        tablaCat.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tablaCat.getSelectionModel().isEmpty()) {
                Categoria seleccionada = tablaCat.getSelectionModel().getSelectedItem();
                abrirFormularioCategoria(seleccionada);
            }
        });
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
    }

    private void cargarDatos() {
        List<Categoria> categorias = catDAO.obtenerCategorias();
        listaCategorias.setAll(categorias);
        tablaCat.setItems(listaCategorias);
    }

    @FXML
    private void btnActionNuevaCat(ActionEvent event) {
        abrirFormularioCategoria(null);
    }

    @FXML
    private void btnActionEliminarCat(ActionEvent event) {
        Categoria seleccionada = tablaCat.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Ninguna selección", "Por favor, selecciona una categoría para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Estás seguro de que deseas eliminar esta categoría?");
        confirmacion.setContentText("Categoría: " + seleccionada.getNombre());

        ButtonType botonSi = new ButtonType("Sí", ButtonBar.ButtonData.YES);
        ButtonType botonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacion.getButtonTypes().setAll(botonSi, botonNo);

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == botonSi) {
                boolean eliminada = catDAO.eliminarCategoria(seleccionada.getCodigo());
                if (eliminada) {
                    cargarDatos();
                } else {
                    mostrarError("No se pudo eliminar la categoría.");
                }
            }
        });
    }

    private void mostrarError(String mensaje) {
        mostrarAlerta(Alert.AlertType.ERROR, "Error", mensaje);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

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

    @FXML
    private void btnImportarAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar Categorías");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        File archivo = fileChooser.showOpenDialog(null);

        if (archivo != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                String linea;
                int contador = 0;
                while ((linea = reader.readLine()) != null) {
                    String[] campos = linea.split(";");
                    if (campos.length == 2) {
                        try {
                            int codigo = Integer.parseInt(campos[0]);
                            String nombre = campos[1];
                            Categoria categoria = new Categoria(codigo, nombre);
                            catDAO.insertarCategoria(categoria);
                            contador++;
                        } catch (NumberFormatException ignored) {}
                    }
                }
                mostrarAlerta(Alert.AlertType.INFORMATION, "Importación completada", contador + " categorías importadas correctamente.");
                cargarDatos();
            } catch (IOException e) {
                mostrarError("Error al leer el archivo: " + e.getMessage());
            }
        }
    }

    @FXML
    private void btnExportarAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar Categorías");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        File archivo = fileChooser.showSaveDialog(null);

        if (archivo != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
                for (Categoria categoria : listaCategorias) {
                    writer.write(categoria.getCodigo() + ";" + categoria.getNombre());
                    writer.newLine();
                }
                mostrarAlerta(Alert.AlertType.INFORMATION, "Exportación completada", "Categorías exportadas correctamente.");
            } catch (IOException e) {
                mostrarError("Error al guardar el archivo: " + e.getMessage());
            }
        }
    }
}
