package controller;

import dao.ProgramasEduDAO;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ProgramasEdu;
import utils.LoggerUtils;

public class ProgramasEController implements Initializable {

    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnBuscar;
    @FXML
    private TextField txtBuscar;
    @FXML
    private TableView<ProgramasEdu> tablaProgramas;
    @FXML
    private TableColumn<ProgramasEdu, Integer> colCodigo;
    @FXML
    private TableColumn<ProgramasEdu, String> colNombre;
    
    private final ObservableList<ProgramasEdu> listaProgramas = FXCollections.observableArrayList();
    private final ProgramasEduDAO programaDAO = new ProgramasEduDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
    } 
    
        private void configurarColumnas() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
    }

    private void cargarDatos() {
        List<ProgramasEdu> programas = programaDAO.obtenerProgramas();
        listaProgramas.setAll(programas);
        tablaProgramas.setItems(listaProgramas);
    }

    @FXML
    private void btnNuevoAction(ActionEvent event) {
        abrirFormularioPrograma(null);
    }
    
    private void abrirFormularioPrograma(ProgramasEdu programa) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ProgramasEduMantenim.fxml"));
            Parent root = loader.load();

            ProgramasEduMantenim controller = loader.getController();
            controller.setPrograma(programa);

            Stage modal = new Stage();
            modal.setScene(new Scene(root));
            modal.setTitle(programa == null ? "Nuevo Programa" : "Editar Programa");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setResizable(false);
            modal.showAndWait();

            cargarDatos();
        } catch (IOException e) {
            LoggerUtils.logError("Programas Educativos", "Error al abrir el formulario de programas " + e.getMessage(), e);
        }
    }

    @FXML
    private void btnEliminarAction(ActionEvent event) {
        ProgramasEdu programa = tablaProgramas.getSelectionModel().getSelectedItem();
        if (programa != null && programaDAO.eliminarPrograma(programa.getCodigo())) {
            cargarDatos();
        }
    }

    @FXML
    private void btnBuscarAction(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarDatos();
        } else {
            List<ProgramasEdu> filtradas = programaDAO.buscarProgramas(filtro);
            listaProgramas.setAll(filtradas);
            tablaProgramas.setItems(listaProgramas);
        }
    }

    @FXML
    private void capturarClick(MouseEvent event) {
        if (event.getClickCount() == 2 && !tablaProgramas.getSelectionModel().isEmpty()) {
                ProgramasEdu programaSeleccionado = tablaProgramas.getSelectionModel().getSelectedItem();
                abrirFormularioPrograma(programaSeleccionado);
        }
    }    
}
