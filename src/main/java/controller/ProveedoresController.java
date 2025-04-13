package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Proveedor;
import utils.DataBaseConection;
import utils.LoggerUtils;

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
        String query = "SELECT * FROM proveedores";
        
        try (Connection conn = DataBaseConection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            listaProveedores.clear();
            while (rs.next()) {
                Proveedor proveedor = new Proveedor(
                        rs.getInt("codigo_proveedor"),
                        rs.getString("nombre"),
                        rs.getString("calle"),
                        rs.getString("localidad"),
                        rs.getString("cp"),
                        rs.getString("municipio"),
                        rs.getString("provincia"),
                        rs.getString("telefono"),
                        rs.getString("email")
                );
                listaProveedores.add(proveedor);
            }
            tablaProv.setItems(listaProveedores);
        } catch (SQLException e) {
            LoggerUtils.logError("PROVEEDORES", "Error al cargar proveedores", e);
        }
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
}
