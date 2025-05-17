package controller;

import dao.CategoriaDAO;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Categoria;
import utils.LoggerUtils;

public class CategoriaController implements Initializable {

    @FXML
    private Button btnNuevaCat;
    @FXML
    private Button btnEliminarCat;
    @FXML
    private Button btnEliminarTodos;
    @FXML
    private TextField txtBuscar;
    @FXML
    private Button btnBuscar;
    @FXML
    private TableView<Categoria> tablaCat;
    @FXML
    private TableColumn<Categoria, Integer> colCodigo;
    @FXML
    private TableColumn<Categoria, String> colNombre;
    
    private ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
    private CategoriaDAO catDAO = new CategoriaDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
        
        tablaCat.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tablaCat.getSelectionModel().isEmpty()) {
                Categoria catSeleccionada = tablaCat.getSelectionModel().getSelectedItem();
                abrirFormularioCategoria(catSeleccionada);
            }
        });
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
    }
    
    private void cargarDatos() {
        List<Categoria> marcas = catDAO.obtenerCategorias();
        listaCategorias.setAll(marcas);
        tablaCat.setItems(listaCategorias);
    }

    @FXML
    private void btnActionNuevaCat(ActionEvent event) {
        abrirFormularioCategoria(null);
    }
    
    private void abrirFormularioCategoria(Categoria categoria) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CategoriaMantenim.fxml"));
            Parent root = loader.load();

            CategoriaMantenimController controller = loader.getController();
            controller.setCategoria(categoria);

            Stage modal = new Stage();
            modal.setScene(new Scene(root));
            modal.setTitle(categoria == null ? "Nueva Categoria" : "Editar Categoria");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setResizable(false);
            modal.showAndWait();

            cargarDatos();
        } catch (IOException e) {
            LoggerUtils.logError("Categorías", "Error al abrir el formulario de categoría" + e.getMessage(), e);
        }
    }

    @FXML
    private void btnActionEliminarCat(ActionEvent event) {
        Categoria categoria = tablaCat.getSelectionModel().getSelectedItem();
        if (categoria != null && catDAO.eliminarCategoria(categoria.getCodigo())) {
            cargarDatos();
        }
    }

    @FXML
    private void btnActionEliminarTodos(ActionEvent event) {
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
}
