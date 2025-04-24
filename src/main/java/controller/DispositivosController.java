package controller;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
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
import model.Dispositivo;
import model.DispositivoDAO;
import utils.LoggerUtils;
import static utils.Utilidades.mostrarAlerta2;

public class DispositivosController implements Initializable {

    @FXML
    private Button btnBuscar;

    private ObservableList<Dispositivo> listaDisposit = FXCollections.observableArrayList();
    
    @FXML
    private TextField txtBuscar;
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
    
    private DispositivoDAO dispDAO = new DispositivoDAO();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
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
}
