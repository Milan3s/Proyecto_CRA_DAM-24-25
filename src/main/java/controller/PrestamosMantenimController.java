package controller;

import dao.AlumnosDAO;
import dao.DispositivoDAO;
import dao.PrestamoDAO;
import dao.SedeDAO;
import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Alumno;
import model.Dispositivo;
import model.Prestamo;
import model.Sede;
import utils.LoggerUtils;
import utils.Utilidades;
import static utils.Utilidades.mostrarAlerta2;

public class PrestamosMantenimController implements Initializable {

    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnPrestar;
    @FXML
    private Button btnDevolver;
    @FXML
    private DatePicker dtpFechaIni;
    @FXML
    private DatePicker dtpFechaFin;
    @FXML
    private TextField txtNombreDisp;
    @FXML
    private TextField txtNetiqueta;
    @FXML
    private TextField txtMarca;
    @FXML
    private TextField txtModelo;
    @FXML
    private TextField txtNserie;
    @FXML
    private TextField txtImei;
    @FXML
    private TextField txtNRE;
    @FXML
    private TextField txtCurso;
    @FXML
    private ComboBox<Sede> cboxSede;
    @FXML
    private ComboBox<Alumno> cboxAlumno;
    
    private Prestamo prestamo;
    private PrestamoDAO prestamoDAO = new PrestamoDAO();
    
    private Dispositivo dispositivo;
    private DispositivoDAO dispositivoDAO = new DispositivoDAO();
    
    private ObservableList<Sede> listaSedes = FXCollections.observableArrayList();
    private SedeDAO sedeDAO = new SedeDAO();
    
    private ObservableList<Alumno> listaAlumnos = FXCollections.observableArrayList();
    FilteredList<Alumno> listaAluFilt;
    private AlumnosDAO alumnoDAO = new AlumnosDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Listeners para filtrar
        cboxSede.valueProperty().addListener((obs, oldVal, newVal) -> filtrarAlumnos());
        txtCurso.textProperty().addListener((obs, oldVal, newVal) -> filtrarAlumnos());
        cboxAlumno.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (null != newVal) {
                txtNRE.setText(newVal.getNre());
            } else {
                txtNRE.clear();
            }
        });
    }    

    public void setPrestamo(Prestamo prest, Dispositivo disp) {
        if (null != prest) {
            this.prestamo = prest;
        } else if (null != disp) {
            this.dispositivo = disp;
            txtNombreDisp.setText(disp.getNombre());
            txtNetiqueta.setText(String.valueOf(disp.getNum_etiqueta()));
            txtMarca.setText(disp.getMarca().getNombre());
            txtModelo.setText(disp.getModelo());
            txtNserie.setText(disp.getNum_serie());
            txtImei.setText(disp.getImei());
        }
        
        cargarCombos();
    }
    
    @FXML
    private void btnCancelarAction(ActionEvent event) {
        cerrarVentana();
    }
    
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void btnPrestarAction(ActionEvent event) {
        if (null == cboxAlumno.getValue()) {
            mostrarAlerta2("", "Deber informar un alumno.", Alert.AlertType.WARNING);
            return;
        }
        
        if (null == dtpFechaIni.getValue()) {
            mostrarAlerta2("", "Deber informar la fecha de inicio.", Alert.AlertType.WARNING);
            return;
        }
        
        int codigoDisp = dispositivo.getCodigo();
        Date fechaIni = Date.valueOf(dtpFechaIni.getValue());
        prestamoDAO.insertarPrestamo(codigoDisp, cboxAlumno.getValue().getCodigo(), fechaIni);
        
        // Se actualiza el campo prestado en el dispositivo
        dispositivoDAO.actualizarPrestado(codigoDisp, true);
    }

    @FXML
    private void btnDevolverAction(ActionEvent event) {
    }
    
    private void cargarCombos() {
        try {
        // Sedes
            listaSedes = FXCollections.observableArrayList(sedeDAO.obtenerSede());
            cboxSede.setItems(listaSedes);
            Utilidades.cargarComboBox(cboxSede, listaSedes, Sede::getNombre);
        
            // Alumnos
            listaAlumnos = FXCollections.observableArrayList(alumnoDAO.obtenerAlumnos());
            listaAluFilt = new FilteredList<>(listaAlumnos, p -> true);
            cboxAlumno.setItems(listaAluFilt);
            Utilidades.cargarComboBox(cboxAlumno, listaAluFilt, Alumno::getNombre);
            
        } catch (Exception e) {
            LoggerUtils.logError("MANTENIMIENTO PRESTAMOS", "Error al cargar comboBox: " + e.getMessage(), e);
        } 
    }
    
    private void filtrarAlumnos() {
        Sede sedeSel = cboxSede.getValue();
        String curso = txtCurso.getText().trim().toLowerCase();

        listaAluFilt.setPredicate(alumno -> {
            boolean coincSede = sedeSel == null || alumno.getCodigo_sede() == sedeSel.getCodigoSede();
            boolean coincCurso = curso.isEmpty() || alumno.getCurso().equals(curso);
            return coincSede && coincCurso;
        });
    }
}
