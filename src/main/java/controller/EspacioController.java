package controller;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.Espacio;
import dao.EspacioDAO;
import utils.LoggerUtils;

public class EspacioController implements Initializable {

    @FXML
    private Button btnNuevoEspacio;
    @FXML
    private Button btnEliminarEspacio;
    @FXML
    private TextField txtBuscarEspacio;
    @FXML
    private Button btnBuscarEspacio;
    @FXML
    private TableView<Espacio> tablaEspacios;
    @FXML
    private TableColumn<Espacio, Integer> colCodigoEspacio;
    @FXML
    private TableColumn<Espacio, String> colNombre;
    @FXML
    private TableColumn<Espacio, String> colPabellon;
    @FXML
    private TableColumn<Espacio, Integer> colPlanta;
    @FXML
    private TableColumn<Espacio, String> colNombreSede;
    @FXML
    private Button btnImportar;
    @FXML
    private Button btnExportar;

    private EspacioDAO espacioDAO;

    private ObservableList<Espacio> listaEspacios;

    @FXML
    private TableColumn<Espacio, String> colNumAbaco;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LoggerUtils.logSection("ESPACIOS - INICIALIZACIÓN");
        espacioDAO = new EspacioDAO();
        configurarColumnasTabla();
        configurarDobleClick();
        cargarEspacios();
    }

    private void configurarColumnasTabla() {
        colCodigoEspacio.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCodigoEspacio()).asObject());
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colPabellon.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPabellon()));
        colPlanta.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPlanta()).asObject());
        colNombreSede.setCellValueFactory(data -> {
            Espacio e = data.getValue();
            return new SimpleStringProperty(e.getCodigoSede() + " - " + e.getNombreSede());
        });
        colNumAbaco.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNumAbaco())); // ✅ Añadido
    }

    private void configurarDobleClick() {
        tablaEspacios.setRowFactory(tv -> {
            TableRow<Espacio> fila = new TableRow<>();
            fila.setOnMouseClicked((MouseEvent event) -> {
                if (event.getClickCount() == 2 && !fila.isEmpty()) {
                    LoggerUtils.logInfo("ESPACIOS", "Doble clic sobre espacio: " + fila.getItem().getNombre());
                    abrirFormularioEspacio(fila.getItem());
                }
            });
            return fila;
        });
    }

    private void cargarEspacios() {
        LoggerUtils.logInfo("ESPACIOS", "Cargando lista de espacios...");
        listaEspacios = FXCollections.observableArrayList(espacioDAO.obtenerEspacios());
        tablaEspacios.setItems(listaEspacios);
    }

    @FXML
    private void btnActionNuevoEspacio(ActionEvent event) {
        LoggerUtils.logInfo("ESPACIOS", "Abriendo formulario para nuevo espacio");
        abrirFormularioEspacio(null);
    }

    @FXML
    private void btnActionEliminarEspacio(ActionEvent event) {
        Espacio seleccionado = tablaEspacios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un espacio para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmación");
        confirmacion.setHeaderText("¿Eliminar espacio?");
        confirmacion.setContentText("¿Estás seguro de que deseas eliminar el espacio \"" + seleccionado.getNombre() + "\"?");
        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            boolean eliminado = espacioDAO.eliminarEspacio(seleccionado.getCodigoEspacio());
            if (eliminado) {
                listaEspacios.remove(seleccionado);
                LoggerUtils.logInfo("ESPACIOS", "Espacio eliminado: " + seleccionado.getNombre());
            } else {
                LoggerUtils.logWarning("ESPACIOS", "No se pudo eliminar el espacio: " + seleccionado.getNombre());
                mostrarAlerta("Error al eliminar el espacio.");
            }
        }
    }

    @FXML
    private void btnActionBuscarEspacio(ActionEvent event) {
        String filtro = txtBuscarEspacio.getText().trim();
        LoggerUtils.logInfo("ESPACIOS", "Búsqueda con filtro: " + filtro);
        if (!filtro.isEmpty()) {
            List<Espacio> filtrados = espacioDAO.buscarEspacios(filtro);
            tablaEspacios.setItems(FXCollections.observableArrayList(filtrados));
        } else {
            cargarEspacios();
        }
    }

    private void abrirFormularioEspacio(Espacio espacio) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EspacioMantenim.fxml"));
            Parent root = loader.load();

            EspacioMantenimController controller = loader.getController();
            if (espacio != null) {
                controller.setEspacio(espacio);
            }

            Stage stage = new Stage();
            stage.setTitle(espacio == null ? "Nuevo Espacio" : "Editar Espacio");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            cargarEspacios();
        } catch (IOException e) {
            LoggerUtils.logError("ESPACIOS", "Error al abrir formulario", e);
            mostrarAlerta("No se pudo abrir el formulario.");
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void btnActionImportar(ActionEvent event) {
        File fichero = utils.Utilidades.seleccFichero("Archivos CSV", "*.csv", "r");

        if (fichero != null) {
            LoggerUtils.logInfo("ESPACIOS", "Iniciando importación desde CSV: " + fichero.getName());
            int importados = 0;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fichero)))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] items = linea.split(";");
                    if (items.length >= 6) {
                        try {
                            String nombre = items[0].trim();
                            String pabellon = items[1].trim();
                            int planta = Integer.parseInt(items[2].trim());
                            String nombreSede = items[3].trim(); // solo informativo
                            int codigoSede = Integer.parseInt(items[4].trim());
                            String numAbaco = items[5].trim();

                            if (!nombre.isEmpty() && !pabellon.isEmpty()) {
                                if (espacioDAO.insertarEspacio(nombre, pabellon, planta, codigoSede, numAbaco)) {
                                    importados++;
                                }
                            }
                        } catch (NumberFormatException ex) {
                            LoggerUtils.logWarning("ESPACIOS", "Línea inválida en importación: " + linea);
                        }
                    } else {
                        LoggerUtils.logWarning("ESPACIOS", "Línea incompleta en archivo CSV: " + linea);
                    }
                }
                cargarEspacios();
                LoggerUtils.logInfo("ESPACIOS", "Importación completada. Total importados: " + importados);
                utils.Utilidades.mostrarAlerta2("Importación completada", "Espacios importados: " + importados, Alert.AlertType.INFORMATION);
            } catch (IOException e) {
                LoggerUtils.logError("ESPACIOS", "Error al importar archivo", e);
                utils.Utilidades.mostrarAlerta2("Error", "No se pudo importar el archivo.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void btnActionExportar(ActionEvent event) {
        File fichero = utils.Utilidades.seleccFichero("Archivos CSV", "*.csv", "w");

        if (fichero != null) {
            LoggerUtils.logInfo("ESPACIOS", "Exportando espacios a archivo: " + fichero.getAbsolutePath());
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fichero), "ISO-8859-1"))) {
                // Cabecera CSV con num_abaco
                bw.write("Nombre;Pabellón;Planta;Nombre Sede;Código Sede;Nº Ábaco\n");

                for (Espacio e : listaEspacios) {
                    String linea = String.join(";",
                            e.getNombre() != null ? e.getNombre() : "",
                            e.getPabellon() != null ? e.getPabellon() : "",
                            String.valueOf(e.getPlanta()),
                            e.getNombreSede() != null ? e.getNombreSede() : "",
                            String.valueOf(e.getCodigoSede()),
                            e.getNumAbaco() != null ? e.getNumAbaco() : ""
                    );
                    bw.write(linea + "\n");
                }

                LoggerUtils.logInfo("ESPACIOS", "Exportación finalizada con éxito.");
                utils.Utilidades.mostrarAlerta2("Éxito", "Exportación realizada correctamente.", Alert.AlertType.INFORMATION);

            } catch (IOException e) {
                LoggerUtils.logError("ESPACIOS", "Error al exportar archivo", e);
                utils.Utilidades.mostrarAlerta2("Error", "No se pudo exportar el archivo.", Alert.AlertType.ERROR);
            }
        }
    }

}
