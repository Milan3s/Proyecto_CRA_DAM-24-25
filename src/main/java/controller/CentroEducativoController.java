package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.CentroEducativo;
import utils.DataBaseConection;
import utils.LoggerUtils;

public class CentroEducativoController implements Initializable {

    @FXML private TableView<CentroEducativo> tablaCentroEducativos;
    @FXML private TableColumn<CentroEducativo, String> colCodigoCentro;
    @FXML private TableColumn<CentroEducativo, String> colNombre;
    @FXML private TableColumn<CentroEducativo, String> colCalle;
    @FXML private TableColumn<CentroEducativo, String> colLocalidad;
    @FXML private TableColumn<CentroEducativo, String> colCP;
    @FXML private TableColumn<CentroEducativo, String> colMunicipio;
    @FXML private TableColumn<CentroEducativo, String> colProvincia;
    @FXML private TableColumn<CentroEducativo, String> colTelefono;
    @FXML private TableColumn<CentroEducativo, String> colEmail;
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscarCentro;
    @FXML private Button btnNuevoCentro;
    @FXML private Button btnEliminarCentro;
    @FXML private Button btnEliminarTodosCentros;

    private final ObservableList<CentroEducativo> listaCentros = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LoggerUtils.logSection("CENTROS EDUCATIVOS");
        tablaCentroEducativos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        configurarColumnas();
        cargarDatos();

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                tablaCentroEducativos.setItems(listaCentros);
            }
        });

        tablaCentroEducativos.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tablaCentroEducativos.getSelectionModel().isEmpty()) {
                CentroEducativo centroSeleccionado = tablaCentroEducativos.getSelectionModel().getSelectedItem();
                abrirModalEditarCentro(centroSeleccionado);
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
            LoggerUtils.logQuery("CENTROS EDUCATIVOS", "Cargar todos los centros educativos", query);

            while (rs.next()) {
                CentroEducativo centro = new CentroEducativo(
                        rs.getString("codigo_centro"),
                        rs.getString("nombre"),
                        rs.getString("calle"),
                        rs.getString("localidad"),
                        rs.getString("cp"),
                        rs.getString("municipio"),
                        rs.getString("provincia"),
                        rs.getString("telefono"),
                        rs.getString("email")
                );
                listaCentros.add(centro);
                LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Centro cargado → Código: " + centro.getCodigoCentro() + ", Nombre: " + centro.getNombre());
            }

            tablaCentroEducativos.setItems(listaCentros);
            LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Total centros cargados: " + listaCentros.size());

        } catch (SQLException e) {
            LoggerUtils.logError("CENTROS EDUCATIVOS", "Error al cargar centros educativos", e);
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
        LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Filtro aplicado: \"" + filtro + "\". Resultados encontrados: " + filtrados.size());
    }

    @FXML
    private void btnActionNuevoCentro(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AgregarCentroEducativo.fxml"));
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle("Nuevo Centro Educativo");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.showAndWait();

            cargarDatos();

        } catch (IOException e) {
            LoggerUtils.logError("CENTROS EDUCATIVOS", "Error al abrir ventana AgregarCentroEducativo", e);
        }
    }

    @FXML
    private void btnActionEliminarCentro(ActionEvent event) {
        CentroEducativo seleccionado = tablaCentroEducativos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Sin selección", "Por favor, selecciona un centro educativo a eliminar.", Alert.AlertType.WARNING);
            LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Intento de eliminar sin seleccionar centro.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Seguro que deseas eliminar el siguiente centro?");
        confirmacion.setContentText(
                "Código: " + seleccionado.getCodigoCentro() + "\n"
                + "Nombre: " + seleccionado.getNombre() + "\n"
                + "Localidad: " + seleccionado.getLocalidad());

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                String sql = "DELETE FROM centroeducativo WHERE codigo_centro = ?";

                try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                    LoggerUtils.logQuery("CENTROS EDUCATIVOS", "Eliminar centro con código: " + seleccionado.getCodigoCentro(), sql);

                    stmt.setString(1, seleccionado.getCodigoCentro());
                    int filas = stmt.executeUpdate();

                    if (filas > 0) {
                        mostrarAlerta("Eliminado", "Centro eliminado exitosamente.", Alert.AlertType.INFORMATION);
                        LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Centro eliminado: " + seleccionado.getCodigoCentro());
                        cargarDatos();
                    } else {
                        mostrarAlerta("Error", "No se pudo eliminar el centro.", Alert.AlertType.ERROR);
                        LoggerUtils.logInfo("CENTROS EDUCATIVOS", "No se eliminó ningún centro (código: " + seleccionado.getCodigoCentro() + ")");
                    }

                } catch (SQLException e) {
                    mostrarAlerta("Error de BD", "No se pudo eliminar debido a un error de base de datos.", Alert.AlertType.ERROR);
                    LoggerUtils.logError("CENTROS EDUCATIVOS", "Error al eliminar centro", e);
                }
            }
        });
    }

    @FXML
    private void btnActionEliminarTodosCentros(ActionEvent event) {
        if (listaCentros.isEmpty()) {
            mostrarAlerta("Sin datos", "No hay centros para eliminar.", Alert.AlertType.INFORMATION);
            LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Intento de eliminar todos los centros, pero la lista está vacía.");
            return;
        }

        String sql = "DELETE FROM centroeducativo";

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar todos los centros");
        confirmacion.setHeaderText("¿Seguro que deseas eliminar TODOS los centros educativos?");
        confirmacion.setContentText("Cantidad total: " + listaCentros.size());

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                    LoggerUtils.logQuery("CENTROS EDUCATIVOS", "Eliminar todos los centros", sql);

                    int filas = stmt.executeUpdate();

                    if (filas > 0) {
                        mostrarAlerta("Eliminados", "Se eliminaron " + filas + " centros.", Alert.AlertType.INFORMATION);
                        LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Eliminación masiva → Total eliminados: " + filas);
                        cargarDatos();
                    } else {
                        mostrarAlerta("Aviso", "No se eliminó ningún centro.", Alert.AlertType.WARNING);
                        LoggerUtils.logInfo("CENTROS EDUCATIVOS", "No se eliminó ningún centro en la eliminación masiva.");
                    }

                } catch (SQLException e) {
                    mostrarAlerta("Error", "No se pudieron eliminar los centros.", Alert.AlertType.ERROR);
                    LoggerUtils.logError("CENTROS EDUCATIVOS", "Error al eliminar todos los centros", e);
                }
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void abrirModalEditarCentro(CentroEducativo centro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EditarCentroEducativo.fxml"));
            Parent root = loader.load();

            EditarCentroEducativoController controller = loader.getController();
            controller.setCentro(centro, true);

            Stage modalStage = new Stage();
            modalStage.setTitle("Editar Centro Educativo");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.showAndWait();

            cargarDatos();

        } catch (IOException e) {
            LoggerUtils.logError("CENTROS EDUCATIVOS", "Error al abrir ventana EditarCentroEducativo", e);
        }
    }
}
