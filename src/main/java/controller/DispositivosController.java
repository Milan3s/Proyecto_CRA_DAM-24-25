package controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import model.Dispositivo;
import utils.DataBaseConection;

public class DispositivosController implements Initializable {

    @FXML
    private Button btnNuevoDispositivo;
    @FXML
    private Button btnEliminarDispositivo;
    @FXML
    private Button btnEliminarTodos;
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
    private TableColumn<Dispositivo, String> colMarca;
    @FXML
    private TableColumn<Dispositivo, String> colModelo;
    @FXML
    private TableColumn<Dispositivo, String> colNumSerie;
    @FXML
    private TableColumn<Dispositivo, String> colCategoria;
    @FXML
    private TableColumn<Dispositivo, Integer> colNumEti;
    @FXML
    private TableColumn<Dispositivo, String> colMac;
    @FXML
    private TableColumn<Dispositivo, String> colImei;
    @FXML
    private TableColumn<Dispositivo, String> colSede;
    @FXML
    private TableColumn<Dispositivo, Integer> colEspacio;
    @FXML
    private TableColumn<Dispositivo, String> colAlumno;
    @FXML
    private TableColumn<Dispositivo, Integer> colCurso;
    @FXML
    private TableColumn<Dispositivo, String> colFechaAdqui;
    @FXML
    private TableColumn<Dispositivo, String> colPrograma;
    @FXML
    private TableColumn<Dispositivo, String> colProveedor;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    private void cargarDatos() {
        try (Connection connection = DataBaseConection.getConnection()) {
            String query = "";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            listaDisposit.clear();
            
            while (rs.next()) {
                
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnNuevoDispositivoAction(ActionEvent event) {
    }

    @FXML
    private void btnEliminarDispositivoAction(ActionEvent event) {
    }

    @FXML
    private void btnEliminarTodosAction(ActionEvent event) {
    }

    @FXML
    private void btnBuscarAction(ActionEvent event) {
    }
    
}
