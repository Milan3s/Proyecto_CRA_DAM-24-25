package controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Dispositivo;
import utils.DataBaseConection;
import utils.LoggerUtils;

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
        colProveedor.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProveedor().getNombre()));
        colAlumno.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAlumno().getNombre()));
    }
    
    private void cargarDatos() {
        String query = "SELECT * FROM dispositivos";
        
        try (Connection conn = DataBaseConection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {        
            listaDisposit.clear();
            while (rs.next()) {
                /*
                Dispositivo disp = new Dispositivo(
                    rs.getInt("codigo_dispositivo"),
                    rs.getString("nombre"),
                    rs.getString("modelo"),     
                    rs.getString("num_serie"),
                    rs.getDate("fecha_adquisicion"),
                    rs.getString("mac"),
                    rs.getString("imei"),
                    rs.getInt("num_etiqueta"),
                    //proveedor
                    //alumno
                listaDisposit.add(disp);
                */
            }
            tablaDisp.setItems(listaDisposit);
            rs.close();
            stmt.close();
        } catch (Exception e) {
            LoggerUtils.logError("DISPOSITIVOS", "Error al cargar dispositivos", e);
        }
    }


    @FXML
    private void btnBuscarAction(ActionEvent event) {
    }

    @FXML
    private void btnNuevoAction(ActionEvent event) {
    }

    @FXML
    private void btnEliminarAction(ActionEvent event) {
    }
    
}
