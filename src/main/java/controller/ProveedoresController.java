package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
import dao.ProveedorDAO;
import utils.LoggerUtils;
import utils.Utilidades;
import static utils.Utilidades.mostrarAlerta2;

/**
 * Clase controller asociada a la vista Proveedores.fxml
 * Contiene la lógica correspondiente a dicha vista.
 * 
 */
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
    @FXML
    private Button btnImportar;
    @FXML
    private Button btnExportar;
    
    private ProveedorDAO provDAO = new ProveedorDAO();
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
    }    
    
    /**
     * Se establece para cada columna del TableView qué atributo del objeto debe mostrar.
     * 
     */
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
    
    /**
     * Carga los proveedores de la base de datos en el TableView
     */
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
    
    /**
     * Abre el formulario de mantenimiento de proveedores.
     * 
     * @param proveedor Proveedor
     */
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

    /**
     * Elimina de la base de datos el proveedor de la fila seleccionada en el TableView
     * 
     * @param event ActionEvent
     */
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
    
    /**
     * Filtra los registros que se muestran en el TableView en función de los campos
     * nombre, localidad, municipio y provincia.
     * 
     * @param event ActionEvent
     */
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

    /**
     * Importa los datos de proveedores de un fichero .csv en la tabla proveedores.
     * El fichero no debe tener fila de cabecera.
     * 
     * La estructura de cada fila del fichero debe ser:
     * nombre_proveedor;calle;localidad;codigo_postal;municipio;provincia;telefono;email
     * 
     * Si el fichero se obtiene a partir de un archivo Excel, debe guardarse como CSV UTF-8
     * 
     * @param event ActionEvent
     */
    @FXML
    private void btnImportarAction(ActionEvent event) {
        // Para seleccionar un fichero .csv
        File fichero = Utilidades.seleccFichero("Archivos CSV", "*.csv", "r");
        
        if (fichero != null) {
            String[] items;
            String nombre;
            String calle;
            String localidad;
            String cp;
            String municipio;
            String provincia;
            String telefono;
            String email;

            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fichero)))) {
                String linea = "";

                while ((linea = br.readLine()) != null) {
                    // Vamos leyendo cada línea del fichero
                    items = linea.split(";");
                    nombre = items[0];
                    calle = items[1];
                    localidad = items[2];
                    cp = items[3];
                    municipio = items[4];
                    provincia = items[5];
                    telefono = items[6];
                    email = items[7];

                    // Creamos un objeto Proveedor y lo insertamos en la BD
                    Proveedor p = new Proveedor(0, nombre, calle, localidad, cp, municipio, provincia, telefono, email);
                    provDAO.insertarProveedor(p);
                }
                cargarDatos();
                mostrarAlerta2("Éxito", "Importación realizada.", Alert.AlertType.INFORMATION);
                
            } catch (FileNotFoundException e) {
                LoggerUtils.logError("IMPORTACION", "Error al acceder al fichero : " + "\n" + fichero + e.getMessage(), e);
            } catch (IOException e) {
                LoggerUtils.logError("IMPORTACION", "Error al leer el fichero : " + "\n" + fichero + e.getMessage(), e);
            }
        }
    }

    /**
     * Exporta los datos de los proveedores mostrados en el TableView a un archivo csv.
     * 
     * @param event ActionEvent
     */
    @FXML
    private void btnExportarAction(ActionEvent event) {
        // Seleccionar fichero destino
        File fichero = Utilidades.seleccFichero("Archivos CSV", "*.csv", "w");
        
        if (fichero != null) {
            // Hay que guardarlo con codificación ISO-8859-1 para que los acentos se muestren correctamente al abrirlo con Excel
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fichero), "ISO-8859-1"))) {
                // Línea de cabecera
                bw.write("Nombre;Calle;Localidad;CP;Municipio;Provincia;Teléfono;Email\n");
                
                String linea = "";
                
                // Se recorren los elementos del TableView y se van grabando las líneas en el fichero destino
                for (Proveedor p : tablaProv.getItems()) {
                    linea = p.getNombre() != null ? p.getNombre() + ";" : ";";
                    linea += p.getCalle() != null ? p.getCalle() + ";" : ";";
                    linea += p.getLocalidad() != null ? p.getLocalidad() + ";" : ";";
                    linea += p.getCp() != null ? p.getCp() + ";" : ";";
                    linea += p.getMunicipio() != null ? p.getMunicipio() + ";" : ";";
                    linea += p.getProvincia() != null ? p.getProvincia() + ";" : ";";
                    linea += p.getTelefono() != null ? p.getTelefono() + ";" : ";";
                    linea += p.getEmail() != null ? p.getEmail() + ";" : ";";
                    bw.write(linea + "\n");
                }
                mostrarAlerta2("Éxito", "Exportación realizada.", Alert.AlertType.INFORMATION);
                
            } catch (IOException e) {
                LoggerUtils.logError("IMPORTACION", "Error al leer el fichero : " + "\n" + fichero + e.getMessage(), e);
            }
        }
    }
}
