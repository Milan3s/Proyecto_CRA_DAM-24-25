package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Milanes
 */
public class CentroEducativoController implements Initializable {

    @FXML
    private TextField txtBuscar;
    @FXML
    private TableView<?> tablaCentroEducativos;
    @FXML
    private TableColumn<?, ?> colNombre;
    @FXML
    private Button btnNuevoCentro;
    @FXML
    private Button btnEliminarCentro;
    @FXML
    private Button btnEliminarTodosCentros;
    @FXML
    private TableColumn<?, ?> colCodigoCentro;
    @FXML
    private TableColumn<?, ?> colCalle;
    @FXML
    private TableColumn<?, ?> colLocalidad;
    @FXML
    private TableColumn<?, ?> colCP;
    @FXML
    private TableColumn<?, ?> colMunicipio;
    @FXML
    private TableColumn<?, ?> colProvincia;
    @FXML
    private TableColumn<?, ?> colTelefono;
    @FXML
    private TableColumn<?, ?> colEmail;
    @FXML
    private Button btnBuscarCentro;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tablaCentroEducativos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }    


    @FXML
    private void btnActionEliminarCentro(ActionEvent event) {
    }

    @FXML
    private void btnActionEliminarTodosCentros(ActionEvent event) {
    }

    @FXML
    private void btnBuscarCentroAction(ActionEvent event) {
    }

    @FXML
    private void btnActionNuevoCentro(ActionEvent event) {
    }
    
}
