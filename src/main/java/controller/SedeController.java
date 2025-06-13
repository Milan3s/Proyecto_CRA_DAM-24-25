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

/**
 * Controlador para la gestión de sedes.
 * Permite listar, buscar, agregar, eliminar, importar y exportar sedes.
 */
public class SedeController {

    @FXML
    private TableView<Sede> tablaSedes; // Tabla que muestra las sedes

    @FXML
    private TableColumn<Sede, Integer> colCodigoSede; // Columna Código de sede

    @FXML
    private TableColumn<Sede, String> colNombre; // Columna Nombre

    @FXML
    private TableColumn<Sede, String> colCalle; // Columna Calle

    @FXML
    private TableColumn<Sede, String> colLocalidad; // Columna Localidad

    @FXML
    private TableColumn<Sede, String> colCP; // Columna Código Postal

    @FXML
    private TableColumn<Sede, String> colMunicipio; // Columna Municipio

    @FXML
    private TableColumn<Sede, String> colProvincia; // Columna Provincia

    @FXML
    private TableColumn<Sede, String> colTelefono; // Columna Teléfono

    @FXML
    private TableColumn<Sede, Integer> colCodigoCentro; // Columna Código de Centro

    @FXML
    private TextField txtBuscar; // Campo para ingresar texto de búsqueda

    private final SedeDAO sedeDAO = new SedeDAO(); // DAO para operaciones con sedes

    private final ObservableList<Sede> listaSedes = FXCollections.observableArrayList(); // Lista observable para la tabla

    @FXML
    private Button btnNuevaSede; // Botón para agregar nueva sede

    @FXML
    private Button btnEliminarSede; // Botón para eliminar sede seleccionada

    @FXML
    private Button btnBuscarSede; // Botón para buscar sedes

    @FXML
    private Button btnImportarSede; // Botón para importar sedes desde archivo CSV

    @FXML
    private Button btnExportarSede; // Botón para exportar sedes a archivo CSV

    /**
     * Inicializa la tabla y carga los datos.
     * Asocia las columnas con las propiedades del modelo Sede.
     */
    @FXML
    public void initialize() {
        // Configura cada columna para obtener el dato correcto del objeto Sede
        colCodigoSede.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCodigoSede()).asObject());
        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));
        colCalle.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCalle()));
        colLocalidad.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLocalidad()));
        colCP.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCp()));
        colMunicipio.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMunicipio()));
        colProvincia.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getProvincia()));
        colTelefono.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTelefono()));
        colCodigoCentro.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCodigoCentro()).asObject());

        cargarDatos(); // Carga datos iniciales en la tabla
    }

    /**
     * Obtiene la lista de sedes desde la base de datos y actualiza la tabla.
     * Maneja errores y los registra en el log.
     */
    private void cargarDatos() {
        try {
            List<Sede> sedes = sedeDAO.obtenerSede(); // Obtener sedes del DAO
            listaSedes.setAll(sedes);                 // Actualizar lista observable
            tablaSedes.setItems(listaSedes);          // Setear items en la tabla
        } catch (Exception e) {
            LoggerUtils.logError("SEDES", "Error cargando datos", e);
            mostrarError("Error", "No se pudo cargar la lista de sedes.");
        }
    }

    /**
     * Acción para botón nueva sede.
     * Abre ventana de mantenimiento con sede null para crear nueva.
     */
    @FXML
    private void btnNuevaSedeAction() {
        abrirMantenimiento(null);
    }

    /**
     * Acción para botón eliminar sede.
     * Verifica selección, dependencias y confirma eliminación.
     */
    @FXML
    private void btnEliminarSedeAction() {
        Sede sede = tablaSedes.getSelectionModel().getSelectedItem();

        if (sede == null) {
            mostrarAdvertencia("Por favor, selecciona una sede para eliminar.");
            return;
        }

        // Verifica si la sede tiene elementos relacionados y no permite eliminar si existen
        if (sedeDAO.tieneDependencias(sede.getCodigoSede())) {
            mostrarError("Error al eliminar ", "Esta sede tiene elementos asociados. Elimínalos primero para poder eliminar la sede.");
            return;
        }

        // Solicita confirmación al usuario
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Estás seguro de que deseas eliminar esta sede?");
        confirmacion.setContentText("Nombre de la sede: " + sede.getNombre());

        ButtonType btnSi = new ButtonType("Sí");
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacion.getButtonTypes().setAll(btnSi, btnNo);

        // Maneja respuesta del usuario
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == btnSi) {
                if (sedeDAO.eliminarSede(sede.getCodigoSede())) {
                    cargarDatos(); // Recarga datos para actualizar tabla
                } else {
                    mostrarError("Error al eliminar", "Esta sede tiene elementos asociados. Elimínalos primero para poder eliminar la sede.");
                }
            }
        });
    }

    /**
     * Muestra una alerta de advertencia con el mensaje recibido.
     * @param mensaje Mensaje a mostrar.
     */
    private void mostrarAdvertencia(String mensaje) {
        Alert aviso = new Alert(Alert.AlertType.WARNING);
        aviso.setTitle("Atención");
        aviso.setHeaderText(null);
        aviso.setContentText(mensaje);
        aviso.showAndWait();
    }

    /**
     * Muestra una alerta de error con título y mensaje recibidos.
     * @param titulo Título del diálogo.
     * @param mensaje Mensaje a mostrar.
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle(titulo);
        error.setHeaderText(null);
        error.setContentText(mensaje);
        error.showAndWait();
    }

    /**
     * Acción para botón buscar sede.
     * Filtra la lista por el texto ingresado o recarga todo si está vacío.
     */
    @FXML
    private void btnBuscarSedeAction() {
        String filtro = txtBuscar.getText();
        if (filtro == null || filtro.trim().isEmpty()) {
            cargarDatos(); // Sin filtro, carga todo
        } else {
            try {
                List<Sede> resultado = sedeDAO.buscarSedes(filtro.trim()); // Busca con filtro
                listaSedes.setAll(resultado);                             // Actualiza lista observable
                tablaSedes.setItems(listaSedes);
            } catch (Exception e) {
                LoggerUtils.logError("SEDES", "Error buscando sedes", e);
                mostrarError("Error", "No se pudo realizar la búsqueda.");
            }
        }
    }

    /**
     * Detecta doble clic sobre una fila para abrir ventana de mantenimiento con la sede seleccionada.
     * @param event Evento de clic del mouse.
     */
    @FXML
    private void capturarClick(MouseEvent event) {
        if (event.getClickCount() == 2 && !tablaSedes.getSelectionModel().isEmpty()) {
            Sede sede = tablaSedes.getSelectionModel().getSelectedItem();
            abrirMantenimiento(sede);
        }
    }

    /**
     * Abre la ventana modal para mantenimiento de sedes.
     * Pasa la sede para editar o null para nueva.
     * Recarga datos al cerrar la ventana.
     * @param sede Sede a editar o null para nueva.
     */
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

            cargarDatos(); // Recarga la tabla tras cerrar modal

        } catch (IOException e) {
            LoggerUtils.logError("SEDES", "Error al abrir ventana SedeMantenim", e);
            mostrarError("Error", "No se pudo abrir la ventana de mantenimiento.");
        }
    }

    // --------------------- IMPORTAR ----------------------------

    /**
     * Acción para botón importar sedes desde archivo CSV.
     * Lee el archivo, crea objetos Sede e inserta en BD.
     */
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
                            // Parseo de datos de la línea CSV
                            int codigo = Integer.parseInt(campos[0].trim());
                            String nombre = campos[1].trim();
                            String calle = campos[2].trim();
                            String localidad = campos[3].trim();
                            String cp = campos[4].trim();
                            String municipio = campos[5].trim();
                            String provincia = campos[6].trim();
                            String telefono = campos[7].trim();
                            int codigoCentro = Integer.parseInt(campos[8].trim());

                            // Crear objeto Sede con datos
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

                // Insertar cada sede importada en la BD
                for (Sede s : sedesImportadas) {
                    // Si la BD genera el ID automáticamente, usar solo insertarSede sin código
                    sedeDAO.insertarSede(s);
                }
                cargarDatos(); // Refrescar tabla con datos importados
                mostrarAdvertencia("Importación completada. " + sedesImportadas.size() + " sedes importadas.");
            } catch (IOException e) {
                mostrarError("Error de archivo", "Error al leer el archivo: " + e.getMessage());
            }
        }
    }

    // --------------------- EXPORTAR ----------------------------

    /**
     * Acción para botón exportar sedes a archivo CSV.
     * Guarda la lista actual en un archivo CSV seleccionado.
     */
    @FXML
    private void btnExportarSedeAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar sedes a archivo CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        File file = fileChooser.showSaveDialog(getStage());
        if (file != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                // Escribir cada sede en una línea CSV
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

    /**
     * Método auxiliar para obtener la ventana actual (Stage).
     * @return Stage de la ventana donde está la tabla.
     */
    private Stage getStage() {
        return (Stage) tablaSedes.getScene().getWindow();
    }
}
