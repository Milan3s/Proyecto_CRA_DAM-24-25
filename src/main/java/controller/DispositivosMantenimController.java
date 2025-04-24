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
import model.DispositivoDAO;
import model.Proveedor;
import model.ProveedorDAO;
import utils.LoggerUtils;

public class DispositivosMantenimController implements Initializable {

    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
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
    private ComboBox<?> cboxSede;
    @FXML
    private ComboBox<Alumno> cboxAlumno;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setDispositivo(Dispositivo disp) {
        formatearFecha();
        cargarCbProveedores();
        
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
    
    private void cargarCbProveedores() {      
        try {
            listaProveedores = provDAO.obtenerProveedores();
            cboxProveedor.setItems(listaProveedores);
            
            // Mostrar solamente el nombre del proveedor
            cboxProveedor.setConverter(new StringConverter<Proveedor>() {
                @Override
                public String toString(Proveedor proveedor) {
                    return proveedor != null ? proveedor.getNombre() : "";
                }

                @Override
                public Proveedor fromString(String string) {
                    return null;
                }
            });
        } catch (Exception e) {
            LoggerUtils.logError("PROVEEDORES", "Error al cargar comboBox Proveedor: " + e.getMessage(), e);
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
        int numEtiq = 0;
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
        int numEtiq = 0;
        Proveedor proveedor = cboxProveedor.getValue();
        Alumno alumno = null;
        String comentario = txtComent.getText();
        Dispositivo disp = new Dispositivo(0, nombre, modelo, nSerie, fecha_adq, mac, imei, numEtiq, proveedor, alumno, comentario);
        dispDAO.insertarDispositivo(disp);
        
        cerrarVentana();
    }
}
