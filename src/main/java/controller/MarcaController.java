
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
import dao.MarcaDAO;
import utils.LoggerUtils;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

// esta es la clase
public class MarcaController implements Initializable {

    // elementos visuales de la pantalla 
    @FXML private TableView<Marca> tablaMarcas;
    @FXML private TableColumn<Marca, Integer> colCodigo;
    @FXML private TableColumn<Marca, String> colNombre;
    @FXML private Button btnNuevoMarca;
    @FXML private Button btnEliminarMarca;
    @FXML private Button btnBuscar;
    @FXML private TextField txtBuscar;

    // lista que se mostrará en la tabla
    private final ObservableList<Marca> listaMarcas = FXCollections.observableArrayList();

    //  se comunica con la base de datos
    private final MarcaDAO marcaDAO = new MarcaDAO();

    // Este método se ejecuta cuando se abre la ventana
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas(); // Prepara las columnas de la tabla
        cargarDatos();        // Carga los datos desde la base de datos

        // Cuando se hace doble clic en una marca, se abre la ventana para editarla
        tablaMarcas.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tablaMarcas.getSelectionModel().isEmpty()) {
                Marca marcaSeleccionada = tablaMarcas.getSelectionModel().getSelectedItem();
                abrirFormularioMarca(marcaSeleccionada); // Editar
            }
        });

        // Si el campo de buscar se borra, se vuelve a mostrar todo
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                tablaMarcas.setItems(listaMarcas); // Mostrar toda la lista
            }
        });
    }

    // Indica qué datos van en cada columna de la tabla
    private void configurarColumnas() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo")); // Columna código
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre")); // Columna nombre
    }

    // Carga todas las marcas desde la base de datos
    private void cargarDatos() {
        List<Marca> marcas = marcaDAO.obtenerMarcas(); // Obtener lista
        listaMarcas.setAll(marcas);                     // Poner en lista observable
        tablaMarcas.setItems(listaMarcas);              // Mostrar en tabla
    }

    // Cuando se hace clic en "Nueva Marca"
    @FXML
    private void btnActionNuevoMarca() {
        abrirFormularioMarca(null); // Abre el formulario vacío (para nueva)
    }

    // Abre la ventana de formulario para agregar o editar una marca
    private void abrirFormularioMarca(Marca marca) {
        try {
            // Carga la vista del formulario
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MarcaMantenim.fxml"));
            Parent root = loader.load();

            // Obtiene el controlador del formulario y le pasa la marca (si hay)
            MarcaMantenimController controller = loader.getController();
            controller.setMarca(marca);

            // Configura la nueva ventana como modal
            Stage modal = new Stage();
            modal.setScene(new Scene(root));
            modal.setTitle(marca == null ? "Nueva Marca" : "Editar Marca");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setResizable(false);
            modal.showAndWait(); // Espera a que el usuario cierre la ventana

            cargarDatos(); // Después de cerrar, vuelve a cargar los datos
        } catch (IOException e) {
            // Si ocurre un error, se guarda en el log
            LoggerUtils.logError("Marcas", "Error al abrir el formulario de marca", e);
        }
    }

    // Cuando se hace clic en "Eliminar Marca"
    @FXML
    private void btnActionEliminarMarca() {
        Marca marca = tablaMarcas.getSelectionModel().getSelectedItem(); // Marca seleccionada
        if (marca != null && marcaDAO.eliminarMarca(marca.getCodigo())) {
            cargarDatos(); // Si se eliminó, actualiza la tabla
        }
    }

    // (Este método no está conectado con un botón, pero elimina todas las marcas si se llama)
    private void btnActionEliminarTodos() {
        int filasEliminadas = marcaDAO.eliminarTodasMarcas(); // Elimina todas las marcas
        LoggerUtils.logInfo("Marcas", "Total de marcas eliminadas: " + filasEliminadas);
        cargarDatos(); // Recarga la tabla
    }

    // Cuando se hace clic en "Buscar"
    @FXML
    private void btnBuscarAction() {
        String filtro = txtBuscar.getText().trim().toLowerCase(); // Texto de búsqueda
        if (filtro.isEmpty()) {
            cargarDatos(); // Si está vacío, muestra todo
        } else {
            // Si hay texto, muestra solo las marcas que coincidan
            List<Marca> filtradas = marcaDAO.buscarMarcas(filtro);
            listaMarcas.setAll(filtradas);
            tablaMarcas.setItems(listaMarcas);
        }
    }
}