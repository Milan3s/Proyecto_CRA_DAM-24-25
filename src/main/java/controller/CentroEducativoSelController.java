package controller;

import dao.CentroEducativoDAO;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import main.Session;
import model.CentroEducativo;
import utils.LoggerUtils;
import utils.Utilidades;
import static utils.Utilidades.mostrarAlerta2;

public class CentroEducativoSelController implements Initializable {

    @FXML
    private ComboBox<CentroEducativo> cboxCentro;
    @FXML
    private Button btnAceptar;
    
    private CentroEducativoDAO centroDAO = new CentroEducativoDAO();
    private ObservableList<CentroEducativo> listaCentros = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarCombo();
    }    

    @FXML
    private void btnAceptarAction(ActionEvent event) {
        CentroEducativo centro = cboxCentro.getValue();
        if (centro != null) {
            // Establecer centro activo
            Session.getInstance().setCentroActivo(centro);
            
            // Cerrar ventana
            Stage stage = (Stage) btnAceptar.getScene().getWindow();
            stage.close();
        } else {
            mostrarAlerta2("Sin selección", "Por favor, seleccione un centro.", Alert.AlertType.WARNING);
        }
    }
    
    private void cargarCombo() {
        try {
            listaCentros = FXCollections.observableArrayList(centroDAO.obtenerCentros());
            cboxCentro.setItems(listaCentros);
            Utilidades.cargarComboBox(cboxCentro, listaCentros, CentroEducativo::getNombre);
        } catch (Exception e) {
            LoggerUtils.logError("SELECCIÓN DE CENTRO", "Error al cargar comboBox: " + e.getMessage(), e);
        }
    }
}
