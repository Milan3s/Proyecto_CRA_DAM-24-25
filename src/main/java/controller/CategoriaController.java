
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

// Esta clase controla la pantalla de categorías; Initializable-> para decir que haga eso al empezar
public class CategoriaController implements Initializable {

    // Conecta los botones y campos de texto del archivo FXML con el código
    @FXML private Button btnNuevaCat;
    @FXML private Button btnEliminarCat;
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private TableView<Categoria> tablaCat;
    @FXML private TableColumn<Categoria, Integer> colCodigo;
    @FXML private TableColumn<Categoria, String> colNombre;
    
    // lista de categorías que se mostrará en la tabla;  Sirve para mostrar las listas en la pantalla
    private ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();

    //  se encarga de hablar con la base de datos
    private CategoriaDAO catDAO = new CategoriaDAO();

    //  se ejecuta automáticamente al abrir la ventana; initialize-> para iniciar las columnas y datos
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas(); // configura qué va en cada columna
        cargarDatos(); // carga las categorías desde la base de datos

        // al hacer doble clic sobre una categoría, se abre la ventana para editarla
        tablaCat.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tablaCat.getSelectionModel().isEmpty()) { // al hacer doble click selecciona una linea en la tabla
                Categoria catSeleccionada = tablaCat.getSelectionModel().getSelectedItem();// guarda en una variable el objeto seleccionado 
                abrirFormularioCategoria(catSeleccionada); // Abre el formulario de esa categoría
            }
        });
    }

    // dice que  datos van en cada columna de la tabla
    private void configurarColumnas() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo")); // Muestra el código
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre")); // Muestra el nombre
    }
    
    // carga las categorías desde la base de datos y las muestra en la tabla
    private void cargarDatos() {
        List<Categoria> categorias = catDAO.obtenerCategorias(); // Trae las categorías
        listaCategorias.setAll(categorias); // Llena la lista
        tablaCat.setItems(listaCategorias); // Muestra la lista en la tabla
    }

    // acción al presionar el botón "Nueva Categoría"
    @FXML
    private void btnActionNuevaCat(ActionEvent event) {
        abrirFormularioCategoria(null); // abre el formulario  para agregar una nueva categoría
    }
    
    // abre el formulario para agregar o editar una categoría
    private void abrirFormularioCategoria(Categoria categoria) {
        try {
            // carga la interfaz del formulario
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CategoriaMantenim.fxml"));
            Parent root = loader.load();

            // pasa la categoría al controlador del formulario
            CategoriaMantenimController controller = loader.getController();
            controller.setCategoria(categoria); // si es null, es nueva; si no, se edita

           
            Stage modal = new Stage();//abre una nueva ventana
            modal.setScene(new Scene(root)); //le pone el contenido de la ventana 
            modal.setTitle(categoria == null ? "Nueva Categoria" : "Editar Categoria"); // le pone un titulo a la ventana
            modal.initModality(Modality.APPLICATION_MODAL); // hace que la ventana bloque a la anterior
            modal.setResizable(false); // no se puede cambiar el tamaño
            modal.showAndWait(); // espera a que se cierre

            cargarDatos(); // all cerrarla, recarga la tabla con los nuevos datos
        } catch (IOException e) { // atrapa errores
            // si algo sale mal, muestra el error 
            LoggerUtils.logError("Categorías", "Error al abrir el formulario de categoría" + e.getMessage(), e);
        }
    }

    // Acción al presionar el botón "Eliminar Categoría"
    @FXML
    private void btnActionEliminarCat(ActionEvent event) {
        Categoria categoria = tablaCat.getSelectionModel().getSelectedItem(); // Toma la categoría seleccionada
        if (categoria != null && catDAO.eliminarCategoria(categoria.getCodigo())) {
            cargarDatos(); // Si se eliminó, recarga la tabla
        }
    }

    // acción al presionar el botón "Buscar"
    @FXML
    private void btnBuscarAction(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase(); // toma el texto escrito
        if (filtro.isEmpty()) {
            cargarDatos(); // si no escribió nada, carga todo
        } else {
            // si escribió en algunas, busca solo esas categorías
            List<Categoria> filtradas = catDAO.buscarCategorias(filtro);
            listaCategorias.setAll(filtradas); // muestra las que coinciden
            tablaCat.setItems(listaCategorias);
        }
    }   
}
