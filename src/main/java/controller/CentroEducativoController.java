package controller;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.CentroEducativo;
import utils.DataBaseConection;

public class CentroEducativoController implements Initializable {

    @FXML
    private TableView<CentroEducativo> tablaCentroEducativos;

    @FXML
    private TableColumn<CentroEducativo, String> colCodigoCentro;
    @FXML
    private TableColumn<CentroEducativo, String> colNombre;
    @FXML
    private TableColumn<CentroEducativo, String> colCalle;
    @FXML
    private TableColumn<CentroEducativo, String> colLocalidad;
    @FXML
    private TableColumn<CentroEducativo, String> colCP;
    @FXML
    private TableColumn<CentroEducativo, String> colMunicipio;
    @FXML
    private TableColumn<CentroEducativo, String> colProvincia;
    @FXML
    private TableColumn<CentroEducativo, String> colTelefono;
    @FXML
    private TableColumn<CentroEducativo, String> colEmail;

    @FXML
    private TextField txtBuscar;

    @FXML
    private Button btnBuscarCentro;

    private ObservableList<CentroEducativo> listaCentros = FXCollections.observableArrayList();
    @FXML
    private Button btnNuevoCentro;
    @FXML
    private Button btnEliminarCentro;
    @FXML
    private Button btnEliminarTodosCentros;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tablaCentroEducativos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        configurarColumnas();
        cargarDatos();

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                tablaCentroEducativos.setItems(listaCentros);
            }
        });
    }

    private void configurarColumnas() {
        colCodigoCentro.setCellValueFactory(new PropertyValueFactory<>("codigoCentro"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCalle.setCellValueFactory(new PropertyValueFactory<>("calle"));
        colLocalidad.setCellValueFactory(new PropertyValueFactory<>("localidad"));
        colCP.setCellValueFactory(new PropertyValueFactory<>("cp"));
        colMunicipio.setCellValueFactory(new PropertyValueFactory<>("municipio"));
        colProvincia.setCellValueFactory(new PropertyValueFactory<>("provincia"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private void cargarDatos() {
        listaCentros.clear();
        String query = "SELECT * FROM centroeducativo";

        try (Connection conn = DataBaseConection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                listaCentros.add(new CentroEducativo(
                        rs.getString("codigo_centro"),
                        rs.getString("nombre"),
                        rs.getString("calle"),
                        rs.getString("localidad"),
                        rs.getString("cp"),
                        rs.getString("municipio"),
                        rs.getString("provincia"),
                        rs.getString("telefono"),
                        rs.getString("email")
                ));
            }

            tablaCentroEducativos.setItems(listaCentros);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnBuscarCentroAction(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();

        if (filtro.isEmpty()) {
            tablaCentroEducativos.setItems(listaCentros);
            return;
        }

        ObservableList<CentroEducativo> filtrados = FXCollections.observableArrayList();

        for (CentroEducativo c : listaCentros) {
            if (c.getNombre().toLowerCase().contains(filtro)
                    || c.getLocalidad().toLowerCase().contains(filtro)
                    || c.getMunicipio().toLowerCase().contains(filtro)
                    || c.getProvincia().toLowerCase().contains(filtro)
                    || c.getCodigoCentro().toLowerCase().contains(filtro)) {
                filtrados.add(c);
            }
        }

        tablaCentroEducativos.setItems(filtrados);
    }

    @FXML
    private void btnActionNuevoCentro(ActionEvent event) {
    }

    @FXML
    private void btnActionEliminarCentro(ActionEvent event) {
    }

    @FXML
    private void btnActionEliminarTodosCentros(ActionEvent event) {
    }
}
