package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

public class PrestamosController implements Initializable {

    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEliminar;
    @FXML
    private TableView<?> tablaPrest;
    @FXML
    private TableColumn<?, ?> colDispositivo;
    @FXML
    private TableColumn<?, ?> colAlumno;
    @FXML
    private TableColumn<?, ?> colFechIni;
    @FXML
    private TableColumn<?, ?> colFechFin;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }    

    @FXML
    private void btnNuevoAction(ActionEvent event) {
    }

    @FXML
    private void btnEliminarAction(ActionEvent event) {
    }

    @FXML
    private void capturarClick(MouseEvent event) {
    }
    
}
