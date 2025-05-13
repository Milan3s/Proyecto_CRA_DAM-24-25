package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Dispositivo;
import model.Prestamo;

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
    private TextField txtNombreAlu;
    @FXML
    private TextField txtNRE;
    @FXML
    private TextField txtSede;
    @FXML
    private TextField txtCurso;
    
    private Prestamo prestamo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }    

    public void setPrestamo(Prestamo prest, Dispositivo disp) {
        if (null != prest) {
            this.prestamo = prest;
        } else if (null != disp) {
            txtNombreDisp.setText(disp.getNombre());
            txtNetiqueta.setText(String.valueOf(disp.getNum_etiqueta()));
            //txtMarca.setText(disp.getMarca);
            txtModelo.setText(disp.getModelo());
            txtNserie.setText(disp.getNum_serie());
            txtImei.setText(disp.getImei());
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

    @FXML
    private void btnPrestarAction(ActionEvent event) {
    }

    @FXML
    private void btnDevolverAction(ActionEvent event) {
    }
    
}
