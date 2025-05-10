package controller;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Dispositivo;
import model.DispositivoDAO;
import model.Proveedor;
import model.ProveedorDAO;
import model.Sede;
import utils.LoggerUtils;
import utils.Utilidades;
import static utils.Utilidades.mostrarAlerta2;

public class DispositivosController implements Initializable {

    private ObservableList<Dispositivo> listaDisposit = FXCollections.observableArrayList();
    
    @FXML
    private TableView<Dispositivo> tablaDisp;
    @FXML
    private TableColumn<Dispositivo, Integer> colCodigo;
    @FXML
    private TableColumn<Dispositivo, String> colNombre;
    @FXML
    private TableColumn<Dispositivo, String> colModelo;
    @FXML
    private TableColumn<Dispositivo, String> colNumSerie;
    @FXML
    private TableColumn<Dispositivo, Integer> colNumEti;
    @FXML
    private TableColumn<Dispositivo, String> colMac;
    @FXML
    private TableColumn<Dispositivo, String> colImei;
    @FXML
    private TableColumn<Dispositivo, String> colAlumno;
    @FXML
    private TableColumn<Dispositivo, Integer> colCurso;
    @FXML
    private TableColumn<Dispositivo, String> colFechaAdqui;
    @FXML
    private TableColumn<Dispositivo, String> colProveedor;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnLimpiar;
    
    @FXML
    private TextField txtNombre;
    @FXML
    private ComboBox<?> cboxCategoria;
    @FXML
    private ComboBox<?> cboxMarca;
    @FXML
    private ComboBox<Sede> cboxSede;
    @FXML
    private ComboBox<?> cboxPrograma;
    @FXML
    private ComboBox<Proveedor> cboxProveedor;
    
    private DispositivoDAO dispDAO = new DispositivoDAO();
    private ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();
    private ProveedorDAO provDAO = new ProveedorDAO();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
        cargarCombos();
    }

    private void configurarColumnas() {
        SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
        
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colNumSerie.setCellValueFactory(new PropertyValueFactory<>("num_serie"));
        colMac.setCellValueFactory(new PropertyValueFactory<>("mac"));
        colImei.setCellValueFactory(new PropertyValueFactory<>("imei"));
        colNumEti.setCellValueFactory(new PropertyValueFactory<>("num_etiqueta"));
        colFechaAdqui.setCellValueFactory(cellData -> {
            Dispositivo disp = cellData.getValue();
            if (disp.getFecha_adquisicion()!= null) {
                return new SimpleStringProperty(formatFecha.format(disp.getFecha_adquisicion()));
            } else {
                return new SimpleStringProperty("");
            }
        });
        colProveedor.setCellValueFactory(cellData -> {
            Dispositivo disp = cellData.getValue();
            if (disp.getProveedor() != null) {
                return new SimpleStringProperty(disp.getProveedor().getNombre());
            } else {
                return new SimpleStringProperty("");
            }
        });
        colAlumno.setCellValueFactory(cellData -> {
            Dispositivo disp = cellData.getValue();
            if (disp.getAlumno() != null) {
                return new SimpleStringProperty(disp.getAlumno().getNombre());
            } else {
                return new SimpleStringProperty("");
            }
        });
    }
    
    private void cargarDatos() {
        listaDisposit = dispDAO.obtenerDispositivos();
        tablaDisp.setItems(listaDisposit);
    }

    @FXML
    private void btnBuscarAction(ActionEvent event) {
        String nombreFilt = txtNombre.getText();
        //Categoria categFilt = cboxCategoria.getValue();
        //Marca marcaFilt = cboxMarca.getValue();
        Sede sedeFilt = cboxSede.getValue();
        //Programa programaFilt = cboxPrograma.getValue();
        Proveedor provFilt = cboxProveedor.getValue();
        
        FilteredList<Dispositivo> filteredList = new FilteredList<>(listaDisposit, p -> true);
        
        filteredList.setPredicate(dispositivo -> {
            boolean coincNombre = true;
            boolean coincCateg = true;
            boolean coincMarca = true;
            boolean coincSede = true;
            boolean coincProg = true;
            boolean coincProv = true;
            
            // Filtro por nombre
            if (nombreFilt != null && !nombreFilt.isEmpty()) {
                coincNombre = dispositivo.getNombre() != null && dispositivo.getNombre().toLowerCase().contains(nombreFilt.toLowerCase());
            }
            
            // Filtro por categoría
            /*
            if (categFilt != null) {
                coincCateg = dispositivo.getCategoria() != null && dispositivo.getCategoria().getCodigo() == categFilt.getCodigo();
            }
            */
            
            // Filtro por Marca
            /*
            if (marcaFilt != null) {
                coincMarca = dispositivo.getMarca() != null && dispositivo.getMarca().getCodigo() == marcaFilt.getCodigo();
            }
            */
            
            // Filtro por sede
            /*
            if (sedeFilt != null) {
                coincSede = dispositivo.getEspacio() != null && dispositivo.getEspacio().getSede().getCodigoSede() == sedeFilt.getCodigoSede();
            }
            */
            
            // Filtro por Programa
            /*
            if (programaFilt != null) {
                coincProg = dispositivo.getPrograma() != null && dispositivo.getPrograma().getCodigo() == programaFilt.getCodigo();
            }
            */
            
            // Filtro por proveedor
            if (provFilt != null) {
                coincProv = dispositivo.getProveedor() != null && dispositivo.getProveedor().getCodigo() == provFilt.getCodigo();
            }
            
            return coincNombre && coincCateg && coincMarca && coincSede && coincProg && coincProv;
        });
        
        tablaDisp.setItems(filteredList);
    }

    @FXML
    private void btnNuevoAction(ActionEvent event) {
        abrirMantenimiento(null);
    }

    @FXML
    private void capturarClick(MouseEvent event) {
        if (event.getClickCount() == 2 && !tablaDisp.getSelectionModel().isEmpty()) {
            Dispositivo disp = tablaDisp.getSelectionModel().getSelectedItem();
            abrirMantenimiento(disp);
        }
    }
    
    private void abrirMantenimiento(Dispositivo disp) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/DispositivosMantenim.fxml"));
            Parent root = loader.load();
            
            DispositivosMantenimController controller = loader.getController();
            controller.setDispositivo(disp);
            
            Stage modalStage = new Stage();
            modalStage.setTitle("Mantenimiento de dispostivos");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.showAndWait();

            cargarDatos();
            
        } catch (IOException e) {
            LoggerUtils.logError("DISPOSITIVOS", "Error al abrir ventana DispositivosMantenim", e);
        }
    }
    
    @FXML
    private void btnEliminarAction(ActionEvent event) {
        Dispositivo dispSelec = tablaDisp.getSelectionModel().getSelectedItem();
        
        if (dispSelec == null) {
            mostrarAlerta2("Sin selección", "Por favor, seleccione un dispositivo a eliminar.", Alert.AlertType.WARNING);
            LoggerUtils.logInfo("DISPOSITIVOS", "Intento de eliminar sin seleccionar dispositivo.");
            return;
        }
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Seguro que desea eliminar el siguiente dispositivo?");
        confirmacion.setContentText(dispSelec.getNombre());
        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                int filas = dispDAO.eliminarDispositivo(dispSelec.getCodigo());
                if (filas > 0) cargarDatos();
            }
        });
    }
    
    private void cargarCombos() {
        try {
            // Categorías
            /*
            listaCategorías = catDAO.obtenerCategorias();
            cboxCategoria.setItems(listaCategorias);
            Utilidades.cargarComboBox(cboxCategoria, listaCategorias, Categoria::getNombre);
            */
            // Marcas
            /*
            listaMarcas = marcaDAO.obtenerMarcas();
            cboxMarca.setItems(listaMarcas);
            Utilidades.cargarComboBox(cboxMarca, listaMarcas, Marca::getNombre);
            */
            // Sedes
            /*
            listaSedes = sedeDAO.obtenerSedes();
            cboxSede.setItems(listaSedes);
            Utilidades.cargarComboBox(cboxSede, listaSedes, Sede::getNombre);
            */
            // Programas
            /*
            listaProgramas = programaDAO.obtenerProgramas();
            cboxPrograma.setItems(listaProgramas);
            Utilidades.cargarComboBox(cboxPrograma, listaProgramas, Programa::getNombre);
            */
            // Proveedores
            listaProveedores = provDAO.obtenerProveedores();
            cboxProveedor.setItems(listaProveedores);
            Utilidades.cargarComboBox(cboxProveedor, listaProveedores, Proveedor::getNombre);
            
        } catch (Exception e) {
            LoggerUtils.logError("DISPOSITIVOS", "Error al cargar comboBox: " + e.getMessage(), e);
        }
    }
    
    @FXML
    private void btnLimpiarAction(ActionEvent event) {
        txtNombre.setText("");
        cboxCategoria.setValue(null);
        cboxMarca.setValue(null);
        cboxSede.setValue(null);
        cboxPrograma.setValue(null);
        cboxProveedor.setValue(null);
        
        tablaDisp.setItems(listaDisposit);
    }
}
