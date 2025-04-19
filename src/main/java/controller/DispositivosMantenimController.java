package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Alumno;
import model.Dispositivo;
import model.Proveedor;

public class DispositivosMantenimController implements Initializable {

    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
    
    private Dispositivo dispositivo;
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
    private DatePicker txtFecha;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setDispositivo(Dispositivo disp) {
        if (null != disp) {
            this.dispositivo = disp;
            txtNombre.setText(disp.getNombre());
            txtModelo.setText(disp.getModelo());
            txtNserie.setText(disp.getNum_serie());
            txtComent.setText(disp.getComentario());
            txtMac.setText(disp.getMac());
            txtImei.setText(disp.getImei());
            txtNetiqueta.setText(String.valueOf(disp.getNum_etiqueta()));
            //txtFecha.setText(String.valueOf(disp.getFecha_adquisicion()));
        }
    }

    @FXML
    private void btnGuardarAction(ActionEvent event) {
    }

    @FXML
    private void btnCancelarAction(ActionEvent event) {
        cerrarVentana();
    }
    
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
