package controller;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Alumno;
import model.Dispositivo;
import dao.DispositivoDAO;
import model.Proveedor;
import dao.ProveedorDAO;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import model.Sede;
import utils.LoggerUtils;
import utils.Utilidades;

public class DispositivosMantenimController implements Initializable {

    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnPrestar;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtModelo;
    @FXML
    private TextField txtNserie;
    @FXML
    private ComboBox<?> cboxMarca;
    @FXML
    private ComboBox<?> cboxCategoria;
    @FXML
    private ComboBox<Proveedor> cboxProveedor;
    @FXML
    private ComboBox<Sede> cboxSede;
    //private ComboBox<Alumno> cboxAlumno;
    @FXML
    private ComboBox<?> cboxPrograma;
    @FXML
    private TextArea txtComent;
    @FXML
    private TextField txtMac;
    @FXML
    private TextField txtImei;
    @FXML
    private TextField txtNetiqueta;
    @FXML
    private DatePicker dtpFecha;
    
    private Dispositivo dispositivo;
    private ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();
    private ProveedorDAO provDAO = new ProveedorDAO();
    private DispositivoDAO dispDAO = new DispositivoDAO();
    
    /*
    private ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
    private CategoriaDAO catDAO = new CategoriaDAO();
    
    private ObservableList<Marca> listaMarcas = FXCollections.observableArrayList();
    private MarcaDAO marcaDAO = new MarcaDAO();
    
    private ObservableList<Sede> listaSedes = FXCollections.observableArrayList();
    private SedeDAO sedeDAO = new SedeDAO();
    
    private ObservableList<Espacio> listaEspacios = FXCollections.observableArrayList();
    private EspacioDAO espacioDAO = new EspacioDAO();
    
    private ObservableList<Programa> listaProgramas = FXCollections.observableArrayList();
    private ProgramaDAO programaDAO = new ProgramaDAO();
    
    private ObservableList<Alumno> listaAlumnos = FXCollections.observableArrayList();
    private AlumnoDAO alumnoDAO = new AlumnoDAO();
    */
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setDispositivo(Dispositivo disp) {
        formatearFecha();
        cargarCombos();
        
        if (null != disp) {
            this.dispositivo = disp;
            txtNombre.setText(disp.getNombre());
            txtModelo.setText(disp.getModelo());
            txtNserie.setText(disp.getNum_serie());
            txtComent.setText(disp.getComentario());
            txtMac.setText(disp.getMac());
            txtImei.setText(disp.getImei());
            txtNetiqueta.setText(String.valueOf(disp.getNum_etiqueta()));
            if (null != disp.getFecha_adquisicion()) dtpFecha.setValue(disp.getFecha_adquisicion().toLocalDate());
            cboxProveedor.setValue(disp.getProveedor());
            //cboxAlumno.setValue(disp.getAlumno());
        }
    }

    @FXML
    private void btnGuardarAction(ActionEvent event) {
        if (null == this.dispositivo) {
            insertarDisp();
        } else {
            actualizarDisp();
        }
    }

    @FXML
    private void btnCancelarAction(ActionEvent event) {
        cerrarVentana();
    }
    
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
    
    private void formatearFecha() {
        // Formatea cómo se muestra la fecha en el DatePicker
        DateTimeFormatter formatFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        dtpFecha.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? formatFecha.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, formatFecha) : null;
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
            
            // Espacios
            /*
            listaEspacios = espacioDAO.obtenerEspacios();
            cboxEspacio.setItems(listaEspacios);
            Utilidades.cargarComboBox(cboxEspacio, listaEspacios, Espacio::getNombre);
            */
            
            // Alumnos
            /*
            listaAlumnos = alumnoDAO.obtenerAlumnos();
            cboxAlumno.setItems(listaAlumnos);
            Utilidades.cargarComboBox(cboxAlumno, listaAlumnos, Alumno::getNombre);
            */
            
            // Proveedores
            listaProveedores = provDAO.obtenerProveedores();
            cboxProveedor.setItems(listaProveedores);
            Utilidades.cargarComboBox(cboxProveedor, listaProveedores, Proveedor::getNombre);
            
        } catch (Exception e) {
            LoggerUtils.logError("DISPOSITIVOS", "Error al cargar comboBox: " + e.getMessage(), e);
        }
    }
    
    private void actualizarDisp() {
        int codDisp = this.dispositivo.getCodigo();
        String nombre = txtNombre.getText();
        String modelo = txtModelo.getText();
        String nSerie = txtNserie.getText();
        Date fecha_adq = Date.valueOf(dtpFecha.getValue());
        String mac = txtMac.getText();
        String imei = txtImei.getText();
        int numEtiq = Integer.parseInt(txtNetiqueta.getText());
        Proveedor proveedor = cboxProveedor.getValue();
        Alumno alumno = null;
        String comentario = txtComent.getText();
        Dispositivo disp = new Dispositivo(codDisp, nombre, modelo, nSerie, fecha_adq, mac, imei, numEtiq, proveedor, alumno, comentario);
        dispDAO.actualizarDispositivo(disp);
        
        cerrarVentana();
    }
    
    private void insertarDisp() {
        String nombre = txtNombre.getText();
        String modelo = txtModelo.getText();
        String nSerie = txtNserie.getText();
        Date fecha_adq = Date.valueOf(dtpFecha.getValue());
        String mac = txtMac.getText();
        String imei = txtImei.getText();
        int numEtiq = Integer.parseInt(txtNetiqueta.getText());
        Proveedor proveedor = cboxProveedor.getValue();
        Alumno alumno = null;
        String comentario = txtComent.getText();
        Dispositivo disp = new Dispositivo(0, nombre, modelo, nSerie, fecha_adq, mac, imei, numEtiq, proveedor, alumno, comentario);
        dispDAO.insertarDispositivo(disp);
        
        cerrarVentana();
    }

    @FXML
    private void btnPrestarAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/PrestamosMantenim.fxml"));
            Parent root = loader.load();
            
            PrestamosMantenimController controller = loader.getController();
            controller.setPrestamo(null, dispositivo);
            
            Stage modalStage = new Stage();
            modalStage.setTitle("Mantenimiento de préstamos");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.showAndWait();
        } catch (IOException e) {
            LoggerUtils.logError("DISPOSITIVOS", "Error al abrir ventana PrestamosMantenim", e);
        }
    }
}
