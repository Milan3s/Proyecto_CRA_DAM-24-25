package controller;

import dao.AlumnosDAO;
import dao.CategoriaDAO;
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
import dao.EspacioDAO;
import dao.MarcaDAO;
import dao.PrestamoDAO;
import dao.ProgramasEduDAO;
import model.Proveedor;
import dao.ProveedorDAO;
import dao.SedeDAO;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import model.Categoria;
import model.Espacio;
import model.Marca;
import model.Prestamo;
import model.ProgramasEdu;
import model.Sede;
import utils.LoggerUtils;
import utils.Utilidades;
import static utils.Utilidades.mostrarAlerta2;

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
    private ComboBox<Marca> cboxMarca;
    @FXML
    private ComboBox<Categoria> cboxCategoria;
    @FXML
    private ComboBox<Proveedor> cboxProveedor;
    @FXML
    private ComboBox<Sede> cboxSede;
    @FXML
    private ComboBox<ProgramasEdu> cboxPrograma;
    @FXML
    private ComboBox<Espacio> cboxEspacio;
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
    @FXML
    private TextField txtAlumno;
    @FXML
    private TextArea txtObservaciones;
    
    private Dispositivo dispositivo;
    private DispositivoDAO dispDAO = new DispositivoDAO();
    
    private ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();
    private ProveedorDAO provDAO = new ProveedorDAO();
    
    private ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
    private CategoriaDAO catDAO = new CategoriaDAO();
    
    private ObservableList<Marca> listaMarcas = FXCollections.observableArrayList();
    private MarcaDAO marcaDAO = new MarcaDAO();
    
    private ObservableList<Sede> listaSedes = FXCollections.observableArrayList();
    private SedeDAO sedeDAO = new SedeDAO();
    
    private ObservableList<Espacio> listaEspacios = FXCollections.observableArrayList();
    private EspacioDAO espacioDAO = new EspacioDAO();
    
    private ObservableList<ProgramasEdu> listaProgramas = FXCollections.observableArrayList();
    private ProgramasEduDAO programaDAO = new ProgramasEduDAO();
    
    private ObservableList<Alumno> listaAlumnos = FXCollections.observableArrayList();
    private AlumnosDAO alumnoDAO = new AlumnosDAO();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setDispositivo(Dispositivo disp) {
        Utilidades.formatearFecha(dtpFecha);
        
        cargarCombos();
        
        if (null != disp) {
            this.dispositivo = disp;
            txtNombre.setText(disp.getNombre());
            cboxMarca.setValue(disp.getMarca());
            txtModelo.setText(disp.getModelo());
            txtNserie.setText(disp.getNum_serie());
            cboxCategoria.setValue(disp.getCategoria());
            if (null != disp.getFecha_adquisicion()) dtpFecha.setValue(disp.getFecha_adquisicion().toLocalDate());
            cboxProveedor.setValue(disp.getProveedor());
            cboxSede.setValue(disp.getSede());
            cboxEspacio.setValue(disp.getEspacio());
            txtMac.setText(disp.getMac());
            txtImei.setText(disp.getImei());
            txtNetiqueta.setText(String.valueOf(disp.getNum_etiqueta()));
            cboxPrograma.setValue(disp.getProgramae());
            if (null != disp.getAlumno()) {
                txtAlumno.setText(disp.getAlumno().getNombre());
                cboxSede.setDisable(true);
            }
            txtComent.setText(disp.getComentario());
            txtObservaciones.setText(disp.getObservaciones());
        }
    }

    @FXML
    private void btnGuardarAction(ActionEvent event) {
        guardarDispositivo();
    }

    @FXML
    private void btnCancelarAction(ActionEvent event) {
        cerrarVentana();
    }
    
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void cargarCombos() {
        try {
            // Categorías 
            listaCategorias = FXCollections.observableArrayList(catDAO.obtenerCategorias());
            cboxCategoria.setItems(listaCategorias);
            Utilidades.cargarComboBox(cboxCategoria, listaCategorias, Categoria::getNombre);
            
            // Marcas
            listaMarcas = FXCollections.observableArrayList(marcaDAO.obtenerMarcas());
            cboxMarca.setItems(listaMarcas);
            Utilidades.cargarComboBox(cboxMarca, listaMarcas, Marca::getNombre);
            
            // Sedes
            listaSedes = FXCollections.observableArrayList(sedeDAO.obtenerSede());
            cboxSede.setItems(listaSedes);
            Utilidades.cargarComboBox(cboxSede, listaSedes, Sede::getNombre);
            
            // Programas
            listaProgramas = FXCollections.observableArrayList(programaDAO.obtenerProgramas());
            cboxPrograma.setItems(listaProgramas);
            Utilidades.cargarComboBox(cboxPrograma, listaProgramas, ProgramasEdu::getNombre);
            
            // Espacios
            listaEspacios = FXCollections.observableArrayList(espacioDAO.obtenerEspacios());
            cboxEspacio.setItems(listaEspacios);
            Utilidades.cargarComboBox(cboxEspacio, listaEspacios, Espacio::getNombre);
            
            // Proveedores
            listaProveedores = provDAO.obtenerProveedores();
            cboxProveedor.setItems(listaProveedores);
            Utilidades.cargarComboBox(cboxProveedor, listaProveedores, Proveedor::getNombre);
            
        } catch (Exception e) {
            LoggerUtils.logError("DISPOSITIVOS", "Error al cargar comboBox: " + e.getMessage(), e);
        }
    }
    
    private void guardarDispositivo() {
        int codDisp;
        boolean prestado;
        
        if (null == this.dispositivo) {
            codDisp = 0;
            prestado = false;
        } else {
            codDisp = this.dispositivo.getCodigo();
            prestado = this.dispositivo.isPrestado();
        }
        
        String nombre = txtNombre.getText();
        String modelo = txtModelo.getText();
        String nSerie = txtNserie.getText();
        Date fecha_adq = null;
        if (null != dtpFecha.getValue()) fecha_adq = Date.valueOf(dtpFecha.getValue());
        String mac = txtMac.getText();
        String imei = txtImei.getText();
        int numEtiq = 0;
        if (!txtNetiqueta.getText().isEmpty()) numEtiq = Integer.parseInt(txtNetiqueta.getText());
        Proveedor proveedor = cboxProveedor.getValue();
        Alumno alumno = null;
        String comentario = txtComent.getText();
        String observaciones = txtObservaciones.getText();
        
        Categoria categoria = cboxCategoria.getValue();
        Marca marca = cboxMarca.getValue();
        Espacio espacio = cboxEspacio.getValue();
        ProgramasEdu programae = cboxPrograma.getValue();
        Sede sede = cboxSede.getValue();
        
        if (nombre.isEmpty()) {
            mostrarAlerta2("Campos incompletos", "Por favor, completa todos los campos obligatorios.", Alert.AlertType.WARNING);
            LoggerUtils.logWarning("DISPOSITIVOS", "Faltan campos obligatorios en el formulario.");
            return;
        }
        
        Dispositivo disp = new Dispositivo(codDisp, nombre, modelo, nSerie, fecha_adq, mac, imei, numEtiq, proveedor, alumno, comentario, 
            categoria, marca, espacio, programae, sede, prestado, observaciones);
        
        if (null == this.dispositivo) {
            dispDAO.insertarDispositivo(disp);
        } else {
            dispDAO.actualizarDispositivo(disp);
        }
        
        cerrarVentana();
    }

    @FXML
    private void btnPrestarAction(ActionEvent event) {
        try {
            // Comprobar si el dispositivo está prestado y en ese caso obtener el objeto Prestamo correspondiente
            Prestamo prestamo = null;
 
            if (dispositivo.isPrestado()) {
                PrestamoDAO prestamoDAO = new PrestamoDAO();
                prestamo = prestamoDAO.obtenerPrestamos(dispositivo, dispositivo.getAlumno()).get(0);
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/PrestamosMantenim.fxml"));
            Parent root = loader.load();
            
            PrestamosMantenimController controller = loader.getController();
            controller.setPrestamo(prestamo, dispositivo);
            
            Stage modalStage = new Stage();
            modalStage.setTitle("Mantenimiento de préstamos");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.showAndWait();
            
            if (null != dispositivo.getAlumno()) {
                txtAlumno.setText(dispositivo.getAlumno().getNombre());
                cboxSede.setValue(dispositivo.getSede());
                cboxSede.setDisable(true);
            } else {
                txtAlumno.setText("");
                cboxSede.setValue(null);
                cboxSede.setDisable(false);
            }
            
        } catch (IOException e) {
            LoggerUtils.logError("DISPOSITIVOS", "Error al abrir ventana PrestamosMantenim: " + e.getMessage(), e);
        }
    }
}
