package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.CentroEducativo;
import dao.CentroEducativoDAO;
import utils.LoggerUtils;
import utils.Utilidades;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import main.Session;
import static utils.Utilidades.mostrarAlerta2;

public class CentroEducativoController implements Initializable {

    // Componentes del FXML
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
    private Button btnNuevoCentro;
    @FXML
    private Button btnEliminarCentro;
    @FXML
    private Button btnBuscarCentro;
    @FXML
    private Button btnImportar;
    @FXML
    private Button btnExportar;
    @FXML
    private Button btnEstablecer;

    // Lista observable para tabla
    private final ObservableList<CentroEducativo> listaCentros = FXCollections.observableArrayList();

    // Acceso a base de datos (DAO)
    private final CentroEducativoDAO centroEducativoDAO = new CentroEducativoDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LoggerUtils.logSection("CENTROS EDUCATIVOS");

        tablaCentroEducativos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        configurarColumnas();
        cargarDatos();

        // Doble clic para editar centro
        tablaCentroEducativos.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tablaCentroEducativos.getSelectionModel().isEmpty()) {
                abrirFormularioCentro(tablaCentroEducativos.getSelectionModel().getSelectedItem());
            }
        });

        // Filtro en vivo al borrar texto
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                tablaCentroEducativos.setItems(listaCentros);
            } else {
                buscarCentros(newVal);
            }
        });
    }

    // Asocia las columnas de la tabla con las propiedades del modelo
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

    // Carga los datos desde la base de datos
    private void cargarDatos() {
        List<CentroEducativo> centros = centroEducativoDAO.obtenerCentros();
        listaCentros.setAll(centros);
        tablaCentroEducativos.setItems(listaCentros);
        LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Total centros cargados: " + listaCentros.size());
    }

    // Acción: Nuevo centro
    @FXML
    private void btnActionNuevoCentro(ActionEvent event) {
        abrirFormularioCentro(null);
    }

    // Abre la ventana para nuevo o editar centro
    private void abrirFormularioCentro(CentroEducativo centro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CentroEducativoMantenim.fxml"));
            Parent root = loader.load();

            CentroEducativoMantenimController controller = loader.getController();
            controller.setCentro(centro);

            Stage modal = new Stage();
            modal.setScene(new Scene(root));
            modal.setTitle(centro == null ? "Nuevo Centro Educativo" : "Editar Centro Educativo");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setResizable(false);
            modal.showAndWait();

            cargarDatos();

        } catch (IOException e) {
            LoggerUtils.logError("CENTROS EDUCATIVOS", "Error al abrir formulario de centro", e);
        }
    }

    // Acción: Eliminar centro seleccionado
    @FXML
    private void btnActionEliminarCentro() {
        CentroEducativo seleccionado = tablaCentroEducativos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            return;
        }

        boolean eliminado = centroEducativoDAO.eliminarCentro(seleccionado.getCodigoCentro());
        if (eliminado) {
            LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Centro eliminado → Código: " + seleccionado.getCodigoCentro());
            mostrarAlerta2("Centro eliminado", "Se ha eliminado correctamente el centro.", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta2("Error", "No se pudo eliminar el centro.", Alert.AlertType.ERROR);
        }
        cargarDatos();
    }

    // Acción: Buscar centros
    @FXML
    private void btnBuscarCentroAction() {
        String filtro = txtBuscar.getText().trim();
        if (filtro.isEmpty()) {
            cargarDatos();
        } else {
            buscarCentros(filtro);
        }
    }

    // Aplica el filtro de búsqueda a la lista
    private void buscarCentros(String filtro) {
        List<CentroEducativo> filtrados = centroEducativoDAO.buscarCentros(filtro);
        listaCentros.setAll(filtrados);
        tablaCentroEducativos.setItems(listaCentros);
        LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Filtro aplicado: " + filtro + " → Resultados: " + filtrados.size());
    }

    // Utilidad para mostrar alertas simples
    /*
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    */
    // Acción: Importar desde CSV usando seleccFichero
    @FXML
    private void btnImportarAction(ActionEvent event) {
        File fichero = Utilidades.seleccFichero("Archivos CSV", "*.csv", "r");

        if (fichero != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fichero)))) {
                String linea;

                while ((linea = br.readLine()) != null) {
                    String[] items = linea.split(";");
                    if (items.length == 9) {
                        CentroEducativo centro = new CentroEducativo(
                                items[0], items[1], items[2], items[3], items[4],
                                items[5], items[6], items[7], items[8]
                        );
                        centroEducativoDAO.insertarCentro(centro);
                    }
                }

                cargarDatos();
                mostrarAlerta2("Importación", "Centros importados correctamente.", Alert.AlertType.INFORMATION);

            } catch (IOException e) {
                LoggerUtils.logError("CENTROS EDUCATIVOS", "Error al importar centros: " + fichero, e);
                mostrarAlerta2("Error", "No se pudo importar el archivo.", Alert.AlertType.ERROR);
            }
        }
    }

    // Acción: Exportar a CSV usando seleccFichero
    @FXML
    private void btnExportarAction(ActionEvent event) {
        File fichero = Utilidades.seleccFichero("Archivos CSV", "*.csv", "w");

        if (fichero != null) {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fichero), "ISO-8859-1"))) {
                // Escribir cabecera
                bw.write("CodigoCentro;Nombre;Calle;Localidad;CP;Municipio;Provincia;Telefono;Email\n");

                // Escribir datos de cada centro
                for (CentroEducativo c : listaCentros) {
                    String linea = String.join(";",
                            c.getCodigoCentro(),
                            c.getNombre(),
                            c.getCalle(),
                            c.getLocalidad(),
                            c.getCp(),
                            c.getMunicipio(),
                            c.getProvincia(),
                            c.getTelefono(),
                            c.getEmail()
                    );
                    bw.write(linea + "\n");
                }

                mostrarAlerta2("Exportación", "Centros exportados correctamente.", Alert.AlertType.INFORMATION);

            } catch (IOException e) {
                LoggerUtils.logError("CENTROS EDUCATIVOS", "Error al exportar centros: " + fichero, e);
                mostrarAlerta2("Error", "No se pudo exportar el archivo.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void btnEstablecerAction(ActionEvent event) {
        CentroEducativo seleccionado = tablaCentroEducativos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            // Establecer centro activo
            Session.getInstance().setCentroActivo(seleccionado);
            Session.getInstance().notificarCentro(seleccionado);
            
            mostrarAlerta2("", "Centros activo establecido.", Alert.AlertType.INFORMATION);
          
        } else {
            mostrarAlerta2("Sin selección", "Por favor, seleccione un centro.", Alert.AlertType.WARNING);
        }
    }
}
