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
    private Button btnNuevoAlumno;
    @FXML
    private Button btnEliminarAlumno;
    @FXML
    private Button btnEliminarTodos;
    @FXML
    private TextField txtBuscar;
    @FXML
    private Button btnBuscar;
    @FXML
    private TableView<?> tablaCentroEducativos;
    @FXML
    private TableColumn<?, ?> colCodigo;
    @FXML
    private TableColumn<?, ?> colNombre;
    @FXML
    private TableColumn<?, ?> colCurso;
    @FXML
    private TableColumn<?, ?> colCodigoSede;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void btnActionCentroEducativo(ActionEvent event) {
    }

    @FXML
    private void btnActionEliminarCentrosEducativos(ActionEvent event) {
    }

    @FXML
    private void btnActionEliminarTodosCentrosEducativos(ActionEvent event) {
    }

    @FXML
    private void btnBuscarAction(ActionEvent event) {
    }
    
}
