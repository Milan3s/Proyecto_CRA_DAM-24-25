package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Proveedor;
import model.ProveedorDAO;
import utils.LoggerUtils;
import static utils.Utilidades.mostrarAlerta2;

public class ProveedoresController implements Initializable {

    @FXML
    private TableView<Proveedor> tablaProv;
    @FXML
    private TableColumn<Proveedor, Integer> colCodigo;
    @FXML
    private TableColumn<Proveedor, String> colNombre;
    @FXML
    private TableColumn<Proveedor, String> colCalle;
    @FXML
    private TableColumn<Proveedor, String> colLocalidad;
    @FXML
    private TableColumn<Proveedor, String> colCp;
    @FXML
    private TableColumn<Proveedor, String> colMunicipio;
    @FXML
    private TableColumn<Proveedor, String> colProvincia;
    @FXML
    private TableColumn<Proveedor, String> colTelefono;
    @FXML
    private TableColumn<Proveedor, String> colEmail;

    private ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();
   
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEliminar;
    @FXML
    private TextField txtBuscar;
    @FXML
    private Button btnBuscar;
    
    private ProveedorDAO provDAO = new ProveedorDAO();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
    }    
    
    private void configurarColumnas() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCalle.setCellValueFactory(new PropertyValueFactory<>("calle"));
        colLocalidad.setCellValueFactory(new PropertyValueFactory<>("localidad"));
        colCp.setCellValueFactory(new PropertyValueFactory<>("cp"));
        colMunicipio.setCellValueFactory(new PropertyValueFactory<>("municipio"));
        colProvincia.setCellValueFactory(new PropertyValueFactory<>("provincia"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }
    
    private void cargarDatos() {
        listaProveedores = provDAO.obtenerProveedores();
        tablaProv.setItems(listaProveedores);
    }

    @FXML
    private void btnNuevoAction(ActionEvent event) {
        abrirMantenimiento(null);
    }
    
    @FXML
    private void capturarClick(MouseEvent event) {
        if (event.getClickCount() == 2 && !tablaProv.getSelectionModel().isEmpty()) {
            Proveedor proveedor = tablaProv.getSelectionModel().getSelectedItem();
            abrirMantenimiento(proveedor);
        }
    }
    
    private void abrirMantenimiento(Proveedor proveedor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ProveedoresMantenim.fxml"));
            Parent root = loader.load();
            
            ProveedoresMantenimController controller = loader.getController();
            controller.setProveedor(proveedor);

            Stage modalStage = new Stage();
            modalStage.setTitle("Mantenimiento de proveedores");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.showAndWait();

            cargarDatos();

        } catch (IOException e) {
            LoggerUtils.logError("PROVEEDORES", "Error al abrir ventana ProveedoresMantenim", e);
        }
    }

    @FXML
    private void btnEliminarAction(ActionEvent event) {
        Proveedor provSelec = tablaProv.getSelectionModel().getSelectedItem();
        
        if (provSelec == null) {
            mostrarAlerta2("Sin selección", "Por favor, seleccione un proveedor a eliminar.", Alert.AlertType.WARNING);
            LoggerUtils.logInfo("PROVEEDORES", "Intento de eliminar sin seleccionar proveedor.");
            return;
        }
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Seguro que desea eliminar el siguiente proveedor?");
        confirmacion.setContentText(provSelec.getNombre());
        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                int filas = provDAO.eliminarProveedor(provSelec.getCodigo());
                if (filas > 0) cargarDatos();
            }
        });
    }
    
    @FXML
    private void btnBuscarAction(ActionEvent event) {
        String filtro = txtBuscar.getText().toLowerCase();
        
        if (filtro.isEmpty()) {
            tablaProv.setItems(listaProveedores);
        } else {
            ObservableList<Proveedor> filtrados = FXCollections.observableArrayList();
            boolean coincNombre = false;
            boolean coincLocal = false;
            boolean coincMunic = false;
            boolean coincProvin = false;
            
            for (Proveedor p : listaProveedores) {
                coincNombre = p.getNombre() != null && p.getNombre().toLowerCase().contains(filtro);
                coincLocal = p.getLocalidad() != null && p.getLocalidad().toLowerCase().contains(filtro);
                coincMunic = p.getMunicipio() != null && p.getMunicipio().toLowerCase().contains(filtro);
                coincProvin = p.getProvincia() != null && p.getProvincia().toLowerCase().contains(filtro);
                
               if (coincNombre || coincLocal || coincMunic || coincProvin) filtrados.add(p);
            }
            tablaProv.setItems(filtrados);
        }
    }
}
