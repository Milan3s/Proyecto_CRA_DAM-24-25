package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import model.Alumno;
import model.Dispositivo;
import model.Proveedor;
import utils.DataBaseConection;
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
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colNumSerie.setCellValueFactory(new PropertyValueFactory<>("num_serie"));
        colFechaAdqui.setCellValueFactory(new PropertyValueFactory<>("fecha_adquisicion"));
        colMac.setCellValueFactory(new PropertyValueFactory<>("mac"));
        colImei.setCellValueFactory(new PropertyValueFactory<>("imei"));
        colNumEti.setCellValueFactory(new PropertyValueFactory<>("num_etiqueta"));
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
        String query = "SELECT d.codigo_dispositivo, d.nombre, d.modelo, d.num_serie, d.fecha_adquisicion, d.mac, d.imei, d.num_etiqueta, d.coment_reg";
        query += " , p.codigo_proveedor, p.nombre AS nombre_prov, a.codigo_alumno, a.nombre AS nombre_alu";
        query += " FROM dispositivos d";
        query += " LEFT OUTER JOIN proveedores p ON d.codigo_proveedor = p.codigo_proveedor";
        query += " LEFT OUTER JOIN alumnos a ON d.codigo_alumno = a.codigo_alumno";
        
        try (Connection conn = DataBaseConection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {        
            listaDisposit.clear();
            while (rs.next()) {
                
                Proveedor prov = new Proveedor(rs.getInt("codigo_proveedor"), rs.getString("nombre_prov"));
                Alumno alu = null;
                Dispositivo disp = new Dispositivo(
                    rs.getInt("codigo_dispositivo"),
                    rs.getString("nombre"),
                    rs.getString("modelo"),     
                    rs.getString("num_serie"),
                    rs.getDate("fecha_adquisicion"),
                    rs.getString("mac"),
                    rs.getString("imei"),
                    rs.getInt("num_etiqueta"),
                    prov, alu, 
                    rs.getString("coment_reg"));
                listaDisposit.add(disp);
                
            }
            tablaDisp.setItems(listaDisposit);
            rs.close();
            stmt.close();
        } catch (Exception e) {
            LoggerUtils.logError("DISPOSITIVOS", "Error al cargar dispositivos: " + e.getMessage(), e);
        }
    }

    @FXML
    private void btnBuscarAction(ActionEvent event) {
    }

    @FXML
    private void btnNuevoAction(ActionEvent event) {
        abrirMantenimiento(null);
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
                eliminarDispositivo(dispSelec.getCodigo());
            }
        });
    }
    
    private void eliminarDispositivo(int codDisp) {
        String sql = "DELETE FROM dispositivos WHERE codigo_dispositivo = ?";
        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            LoggerUtils.logQuery("DISPOSITIVOS", "Eliminar dispositivo con código: " + codDisp, sql);

            stmt.setInt(1, codDisp);
            int filas = stmt.executeUpdate();

            if (filas > 0) {
                mostrarAlerta2("Eliminado", "Dispositivo eliminado.", Alert.AlertType.INFORMATION);
                LoggerUtils.logInfo("DISPOSITIVOS", "dispositivo eliminado: " + codDisp);
                cargarDatos();
            } else {
                mostrarAlerta2("Error", "No se pudo eliminar el dispositivo.", Alert.AlertType.ERROR);
                LoggerUtils.logInfo("DISPOSITIVOS", "No se eliminó ningún dispositivo (código: " + codDisp + ")");
            }

        } catch (SQLException e) {
            mostrarAlerta2("Error de BD", "No se pudo eliminar debido a un error de base de datos.", Alert.AlertType.ERROR);
            LoggerUtils.logError("DISPOSITIVOS", "Error al eliminar dispositivo", e);
        }
    }

    @FXML
    private void capturarClick(MouseEvent event) {
        if (event.getClickCount() == 2 && !tablaDisp.getSelectionModel().isEmpty()) {
            Dispositivo disp = tablaDisp.getSelectionModel().getSelectedItem();
            abrirMantenimiento(disp);
        }
    }
}
