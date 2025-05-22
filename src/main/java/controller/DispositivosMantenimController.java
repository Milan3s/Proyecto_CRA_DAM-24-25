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
import dao.ProgramasEduDAO;
import model.Proveedor;
import dao.ProveedorDAO;
import dao.SedeDAO;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import model.Categoria;
import model.Espacio;
import model.Marca;
import model.ProgramasEdu;
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
    private ComboBox<Marca> cboxMarca;
    @FXML
    private ComboBox<Categoria> cboxCategoria;
    @FXML
    private ComboBox<Proveedor> cboxProveedor;
    @FXML
    private ComboBox<Sede> cboxSede;
    @FXML
    private ComboBox<ProgramasEdu> cboxPrograma;
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
        formatearFecha();
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
            if (null != disp.getAlumno()) txtAlumno.setText(disp.getAlumno().getNombre());
            txtComent.setText(disp.getComentario());
        }
    }

    @FXML
    private void btnGuardarAction(ActionEvent event) {
        guardarDispositivo();
        cerrarVentana();
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
        Date fecha_adq = Date.valueOf(dtpFecha.getValue());
        String mac = txtMac.getText();
        String imei = txtImei.getText();
        int numEtiq = Integer.parseInt(txtNetiqueta.getText());
        Proveedor proveedor = cboxProveedor.getValue();
        Alumno alumno = null;
        String comentario = txtComent.getText();
        String observaciones = txtObservaciones.getText();
        
        Categoria categoria = cboxCategoria.getValue();
        Marca marca = cboxMarca.getValue();
        Espacio espacio = cboxEspacio.getValue();
        ProgramasEdu programae = cboxPrograma.getValue();
        Sede sede = cboxSede.getValue();
        
        Dispositivo disp = new Dispositivo(codDisp, nombre, modelo, nSerie, fecha_adq, mac, imei, numEtiq, proveedor, alumno, comentario, 
            categoria, marca, espacio, programae, sede, prestado, observaciones);
        
        if (null == this.dispositivo) {
            dispDAO.insertarDispositivo(disp);
        } else {
            dispDAO.actualizarDispositivo(disp);
        }
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
