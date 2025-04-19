package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import utils.DataBaseConection;
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
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            LoggerUtils.logError("PROVEEDORES", "Error al cargar proveedores: " + e.getMessage(), e);
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
                eliminarProveedor(provSelec.getCodigo());
            }
        });
    }
    
    private void eliminarProveedor(int codProv) {
        String sql = "DELETE FROM proveedores WHERE codigo_proveedor = ?";
        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            LoggerUtils.logQuery("PROVEEDORES", "Eliminar proveedor con código: " + codProv, sql);

            stmt.setInt(1, codProv);
            int filas = stmt.executeUpdate();

            if (filas > 0) {
                mostrarAlerta2("Eliminado", "Proveedor eliminado.", Alert.AlertType.INFORMATION);
                LoggerUtils.logInfo("PROVEEDORES", "Proveedor eliminado: " + codProv);
                cargarDatos();
            } else {
                mostrarAlerta2("Error", "No se pudo eliminar el proveedor.", Alert.AlertType.ERROR);
                LoggerUtils.logInfo("PROVEEDORES", "No se eliminó ningún proveedor (código: " + codProv + ")");
            }

        } catch (SQLException e) {
            mostrarAlerta2("Error de BD", "No se pudo eliminar debido a un error de base de datos.", Alert.AlertType.ERROR);
            LoggerUtils.logError("PROVEEDORES", "Error al eliminar proveedor", e);
        }
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
