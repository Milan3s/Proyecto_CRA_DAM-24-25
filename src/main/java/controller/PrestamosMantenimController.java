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

/**
 * Clase controller asociada a la vista PrestamosMantenim.fxml
 * Contiene la lógica correspondiente a dicha vista.
 * 
 */
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
    private FilteredList<Alumno> listaAluFilt;
    private AlumnosDAO alumnoDAO = new AlumnosDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Listeners para filtrar los alumnos en función de la sede y el curso
        cboxSede.valueProperty().addListener((obs, oldVal, newVal) -> filtrarAlumnos());
        txtCurso.textProperty().addListener((obs, oldVal, newVal) -> filtrarAlumnos());
        
        // Listener para mostrar el nre del alumno seleccionado
        cboxAlumno.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (null != newVal) {
                txtNRE.setText(newVal.getNre());
            } else {
                txtNRE.clear();
            }
        });
    }    

    /**
     * Informa los componentes gráficos con los datos del dispositivo pasado como parámetro,
     * si éste no es nulo.
     * 
     * @param prest Prestamo
     * @param disp Dispositivo
     */
    public void setPrestamo(Prestamo prest, Dispositivo disp) {
        Utilidades.formatearFecha(dtpFechaIni);
        Utilidades.formatearFecha(dtpFechaFin);
        
        cargarCombos();
        
        if (null != prest) {
            this.prestamo = prest;          
            this.dispositivo = disp;
 
            cargarDatosDispositivo();
            
            Alumno alumno = prest.getAlumno();
            cboxSede.setValue(new Sede(alumno.getCodigo_sede(), alumno.getNombreSede()));
            cboxAlumno.setValue(alumno);
            txtCurso.setText(alumno.getCurso());
            txtNRE.setText(alumno.getNre());
            dtpFechaIni.setValue(prest.getFecha_inicio().toLocalDate());
            
            if (null != prest.getFecha_fin()) {
                dtpFechaFin.setValue(prest.getFecha_fin().toLocalDate());
                dtpFechaFin.setDisable(true);
                btnDevolver.setDisable(true);
            }
            
            // Deshabilitar componentes
            cboxSede.setDisable(true);
            cboxAlumno.setDisable(true);
            txtCurso.setDisable(true);
            txtNRE.setDisable(true);
            dtpFechaIni.setDisable(true);
            btnPrestar.setDisable(true);
            
        } else if (null != disp) {
            // Si el préstamo es nulo pero el dispositivo no es porque se ha llamado desde 
            // el formulario de mantenimiento de dispositivos.
            this.dispositivo = disp;

            cargarDatosDispositivo();
        }
    }
    
    /**
     * Informa los datos del dispositivo en los controles correspondientes.
     */
    private void cargarDatosDispositivo() {
        txtNombreDisp.setText(dispositivo.getNombre());
        txtNetiqueta.setText(String.valueOf(dispositivo.getNum_etiqueta()));
        if (null != dispositivo.getMarca()) txtMarca.setText(dispositivo.getMarca().getNombre());
        txtModelo.setText(dispositivo.getModelo());
        txtNserie.setText(dispositivo.getNum_serie());
        txtImei.setText(dispositivo.getImei());
    }
    
    @FXML
    private void btnCancelarAction(ActionEvent event) {
        cerrarVentana();
    }
    
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    /**
     * Crea un nuevo préstamo en la base de datos con los datos correspondientes.
     * 
     * @param event 
     */
    @FXML
    private void btnPrestarAction(ActionEvent event) {
        Alumno alumno = cboxAlumno.getValue();
        
        if (null == alumno) {
            mostrarAlerta2("", "Deber informar un alumno.", Alert.AlertType.WARNING);
            return;
        }
        
        if (null == dtpFechaIni.getValue()) {
            mostrarAlerta2("", "Deber informar la fecha de inicio.", Alert.AlertType.WARNING);
            return;
        }
        
        int codigoDisp = dispositivo.getCodigo();
        Date fechaIni = Date.valueOf(dtpFechaIni.getValue());
        boolean resul = prestamoDAO.insertarPrestamo(codigoDisp, alumno.getCodigo(), fechaIni);
        
        if (resul) {
            // Se actualiza el campo prestado en el dispositivo
            dispositivoDAO.actualizarPrestado(codigoDisp, true);
            dispositivo.setPrestado(true);
            
            // Se asigna el alumno y la sede correspondiente 
            // para que se muestren en el formulario de mantenimiento de dispositivos
            dispositivo.setAlumno(alumno);
            dispositivo.setSede(new Sede(alumno.getCodigo_sede(), alumno.getNombreSede()));
            
            mostrarAlerta2("Éxito", "Préstamo realizado.", Alert.AlertType.INFORMATION);
        }
        
        cerrarVentana();
    }

    /**
     * Actualiza en la base de datos el préstamo correspondiente informando la fecha de fin
     * de dicho préstamo.
     * 
     * @param event 
     */
    @FXML
    private void btnDevolverAction(ActionEvent event) {
        if (null == this.prestamo) {
            mostrarAlerta2("", "No se puede devolver sin haber realizado el préstamo.", Alert.AlertType.WARNING);
            return;
        }
        
        if (null == dtpFechaFin.getValue()) {
            mostrarAlerta2("", "Deber informar la fecha de fin.", Alert.AlertType.WARNING);
            return;
        }
        
        int codigoDisp = dispositivo.getCodigo();
        Date fechaFin = Date.valueOf(dtpFechaFin.getValue());
        
        // Se actualiza en el prestamo la fecha de fin
        boolean resul = prestamoDAO.actualizarPrestamo(prestamo, fechaFin);
        
        if (resul) {
            // Se actualiza el campo prestado en el dispositivo
            dispositivoDAO.actualizarPrestado(codigoDisp, false);
            
            // Se desasigna el alumno y la sede
            this.dispositivo.setAlumno(null);
            //this.dispositivo.setSede(null);
            
            mostrarAlerta2("Éxito", "Devolución realizada.", Alert.AlertType.INFORMATION);
        }
        
        cerrarVentana();
    }
    
    /**
     * Carga los registros correspondientes en los distintos ComboBox del formulario
     */
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
    
    /**
     * Filtra los registros que se muestran en el ComboBox de alumnos en función de si
     * se ha seleccionado alguna sede en el ComboBox de sedes o algún curso.
     */
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
