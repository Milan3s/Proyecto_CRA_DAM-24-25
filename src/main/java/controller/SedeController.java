package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Sede;
import dao.SedeDAO;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import utils.LoggerUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SedeController {

    @FXML
    private TableView<Sede> tablaSedes;

    @FXML
    private TableColumn<Sede, Integer> colCodigoSede;

    @FXML
    private TableColumn<Sede, String> colNombre;

    @FXML
    private TableColumn<Sede, String> colCalle;

    @FXML
    private TableColumn<Sede, String> colLocalidad;

    @FXML
    private TableColumn<Sede, String> colCP;

    @FXML
    private TableColumn<Sede, String> colMunicipio;

    @FXML
    private TableColumn<Sede, String> colProvincia;

    @FXML
    private TableColumn<Sede, String> colTelefono;

    @FXML
    private TableColumn<Sede, Integer> colCodigoCentro;

    @FXML
    private TextField txtBuscar;

    private final SedeDAO sedeDAO = new SedeDAO();
    private final ObservableList<Sede> listaSedes = FXCollections.observableArrayList();

    @FXML
    private Button btnNuevaSede;

    @FXML
    private Button btnEliminarSede;

    @FXML
    private Button btnBuscarSede;

    @FXML
    private Button btnImportarSede;

    @FXML
    private Button btnExportarSede;

    @FXML
    public void initialize() {
        // Vincula las columnas con las propiedades del modelo Sede
        colCodigoSede.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCodigoSede()).asObject());
        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));
        colCalle.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCalle()));
        colLocalidad.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLocalidad()));
        colCP.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCp()));
        colMunicipio.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMunicipio()));
        colProvincia.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getProvincia()));
        colTelefono.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTelefono()));
        colCodigoCentro.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCodigoCentro()).asObject());

        cargarDatos();
    }

    private void cargarDatos() {
        try {
            List<Sede> sedes = sedeDAO.obtenerSede();
            listaSedes.setAll(sedes);
            tablaSedes.setItems(listaSedes);
        } catch (Exception e) {
            LoggerUtils.logError("SEDES", "Error cargando datos", e);
            mostrarError("Error", "No se pudo cargar la lista de sedes.");
        }
    }

    @FXML
    private void btnNuevaSedeAction() {
        abrirMantenimiento(null);
    }

    @FXML
    private void btnEliminarSedeAction() {
        Sede sede = tablaSedes.getSelectionModel().getSelectedItem();

        if (sede == null) {
            mostrarAdvertencia("Por favor, selecciona una sede para eliminar.");
            return;
        }

        if (sedeDAO.tieneDependencias(sede.getCodigoSede())) {
            mostrarError("Error al eliminar ", "Esta sede tiene elementos asociados. Elimínalos primero para poder eliminar la sede.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Estás seguro de que deseas eliminar esta sede?");
        confirmacion.setContentText("Nombre de la sede: " + sede.getNombre());

        ButtonType btnSi = new ButtonType("Sí");
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacion.getButtonTypes().setAll(btnSi, btnNo);

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == btnSi) {
                if (sedeDAO.eliminarSede(sede.getCodigoSede())) {
                    cargarDatos();
                } else {
                    mostrarError("Error al eliminar", "Esta sede tiene elementos asociados. Elimínalos primero para poder eliminar la sede.");
                }
            }
        });
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert aviso = new Alert(Alert.AlertType.WARNING);
        aviso.setTitle("Atención");
        aviso.setHeaderText(null);
        aviso.setContentText(mensaje);
        aviso.showAndWait();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle(titulo);
        error.setHeaderText(null);
        error.setContentText(mensaje);
        error.showAndWait();
    }

    @FXML
    private void btnBuscarSedeAction() {
        String filtro = txtBuscar.getText();
        if (filtro == null || filtro.trim().isEmpty()) {
            cargarDatos();
        } else {
            try {
                List<Sede> resultado = sedeDAO.buscarSedes(filtro.trim());
                listaSedes.setAll(resultado);
                tablaSedes.setItems(listaSedes);
            } catch (Exception e) {
                LoggerUtils.logError("SEDES", "Error buscando sedes", e);
                mostrarError("Error", "No se pudo realizar la búsqueda.");
            }
        }
    }

    @FXML
    private void capturarClick(MouseEvent event) {
        if (event.getClickCount() == 2 && !tablaSedes.getSelectionModel().isEmpty()) {
            Sede sede = tablaSedes.getSelectionModel().getSelectedItem();
            abrirMantenimiento(sede);
        }
    }

    private void abrirMantenimiento(Sede sede) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SedeMantenim.fxml"));
            Parent root = loader.load();

            SedeMantenimController controller = loader.getController();
            controller.setSede(sede);

            Stage modalStage = new Stage();
            modalStage.setTitle("Mantenimiento de sedes");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.showAndWait();

            cargarDatos();

        } catch (IOException e) {
            LoggerUtils.logError("SEDES", "Error al abrir ventana SedeMantenim", e);
            mostrarError("Error", "No se pudo abrir la ventana de mantenimiento.");
        }
    }

    // --------------------- IMPORTAR ----------------------------
    @FXML
    private void btnImportarSedeAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar archivo de sedes");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        File file = fileChooser.showOpenDialog(getStage());
        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                List<Sede> sedesImportadas = new ArrayList<>();
                while ((line = br.readLine()) != null) {
                    String[] campos = line.split(",");
                    if (campos.length >= 9) {
                        try {
                            // Ojo: El código puede ser autogenerado por BD, no insertarlo si así es el caso
                            int codigo = Integer.parseInt(campos[0].trim());
                            String nombre = campos[1].trim();
                            String calle = campos[2].trim();
                            String localidad = campos[3].trim();
                            String cp = campos[4].trim();
                            String municipio = campos[5].trim();
                            String provincia = campos[6].trim();
                            String telefono = campos[7].trim();
                            int codigoCentro = Integer.parseInt(campos[8].trim());

                            Sede sede = new Sede(codigo, nombre, calle, localidad, cp, municipio, provincia, telefono, codigoCentro);
                            sedesImportadas.add(sede);

                        } catch (NumberFormatException e) {
                            mostrarError("Error en formato de número", "Error en formato de número en línea: " + line);
                            return;
                        }
                    } else {
                        mostrarError("Error de formato", "Línea incompleta: " + line);
                        return;
                    }
                }

                for (Sede s : sedesImportadas) {
                    // Si la BD genera el ID automáticamente, usar solo insertarSede sin código
                    sedeDAO.insertarSede(s);
                }
                cargarDatos();
                mostrarAdvertencia("Importación completada. " + sedesImportadas.size() + " sedes importadas.");
            } catch (IOException e) {
                mostrarError("Error de archivo", "Error al leer el archivo: " + e.getMessage());
            }
        }
    }

    // --------------------- EXPORTAR ----------------------------
    @FXML
    private void btnExportarSedeAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar sedes a archivo CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        File file = fileChooser.showSaveDialog(getStage());
        if (file != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (Sede s : listaSedes) {
                    String linea = String.format("%d,%s,%s,%s,%s,%s,%s,%s,%d",
                            s.getCodigoSede(),
                            s.getNombre(),
                            s.getCalle(),
                            s.getLocalidad(),
                            s.getCp(),
                            s.getMunicipio(),
                            s.getProvincia(),
                            s.getTelefono(),
                            s.getCodigoCentro());
                    bw.write(linea);
                    bw.newLine();
                }
                mostrarAdvertencia("Exportación completada.");
            } catch (IOException e) {
                mostrarError("Error de archivo", "Error al guardar el archivo: " + e.getMessage());
            }
        }
    }

    private Stage getStage() {
        return (Stage) tablaSedes.getScene().getWindow();
    }
}
