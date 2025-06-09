package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Alumno;
import dao.AlumnosDAO;
import utils.LoggerUtils;
import utils.Utilidades;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;

public class AlumnosController implements Initializable {

    // FXML - Componentes de la vista
    @FXML
    private TableView<Alumno> tablaUsuarios;
    @FXML
    private TableColumn<Alumno, Integer> colCodigo;
    @FXML
    private TableColumn<Alumno, String> colNombre;
    @FXML
    private TableColumn<Alumno, String> colCurso;
    @FXML
    private TableColumn<Alumno, String> colCodigoSede;
    @FXML
    private Button btnNuevoAlumno;
    @FXML
    private Button btnEliminarAlumno;
    @FXML
    private Button btnBuscar;
    @FXML
    private TextField txtBuscar;
    @FXML
    private Button btnImportar;
    @FXML
    private Button btnExportar;

    // Lista observable para la tabla
    private ObservableList<Alumno> listaAlumnos = FXCollections.observableArrayList();

    // Acceso a base de datos (DAO)
    private final AlumnosDAO alumnosDAO = new AlumnosDAO();

    @FXML
    private TableColumn<Alumno, String> colNRE;
    @FXML
    private TableColumn<Alumno, String> colTELTUTOR1;
    @FXML
    private TableColumn<Alumno, String> colTELTUTOR2;

    // Inicialización del controlador
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();

        // Evento doble clic en fila de tabla → abrir edición
        tablaUsuarios.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tablaUsuarios.getSelectionModel().isEmpty()) {
                Alumno alumnoSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
                abrirFormularioAlumno(alumnoSeleccionado);
            }
        });

        // Si se borra el filtro, se recargan todos los alumnos
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                tablaUsuarios.setItems(listaAlumnos);
            }
        });
    }

    // Configura las columnas de la tabla según el modelo Alumno
    private void configurarColumnas() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCurso.setCellValueFactory(new PropertyValueFactory<>("curso"));
        colCodigoSede.setCellValueFactory(new PropertyValueFactory<>("nombreSede"));
        colNRE.setCellValueFactory(new PropertyValueFactory<>("nre"));
        colTELTUTOR1.setCellValueFactory(new PropertyValueFactory<>("telTutor1"));
        colTELTUTOR2.setCellValueFactory(new PropertyValueFactory<>("telTutor2"));
    }

    // Carga todos los alumnos desde la base de datos
    private void cargarDatos() {
        List<Alumno> alumnos = alumnosDAO.obtenerAlumnos();
        listaAlumnos.setAll(alumnos);
        tablaUsuarios.setItems(listaAlumnos);
    }

    // Acción del botón "Nuevo Alumno"
    @FXML
    private void btnActionNuevoAlumno() {
        abrirFormularioAlumno(null); // null = nuevo alumno
    }

    // Abre el formulario para añadir o editar un alumno
    private void abrirFormularioAlumno(Alumno alumno) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AlumnosMantenim.fxml"));
            Parent root = loader.load();

            AlumnosMantenimController controller = loader.getController();
            controller.setAlumno(alumno); // Puede ser null (nuevo) o existente

            Stage modal = new Stage();
            modal.setScene(new Scene(root));
            modal.setTitle(alumno == null ? "Nuevo Alumno" : "Editar Alumno");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setResizable(false);
            modal.showAndWait();

            cargarDatos(); // Recargar la tabla tras cerrar el modal

        } catch (IOException e) {
            LoggerUtils.logError("ALUMNOS", "Error al abrir el formulario de alumno", e);
        }
    }

    // Elimina el alumno seleccionado
    @FXML
    private void btnActionEliminarAlumno() {
        Alumno alumno = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (alumno == null) {
            Utilidades.mostrarAlerta2("Sin selección", "Por favor, selecciona un alumno para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Estás seguro de que deseas eliminar este alumno?");
        confirmacion.setContentText("Nombre: " + alumno.getNombre() + "\nCódigo: " + alumno.getCodigo());

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    boolean eliminado = alumnosDAO.eliminarAlumno(alumno.getCodigo());

                    if (eliminado) {
                        cargarDatos();
                        Utilidades.mostrarAlerta2("Eliminado", "Alumno eliminado correctamente.", Alert.AlertType.INFORMATION);
                    } else {
                        Utilidades.mostrarAlerta2("Error", "No se pudo eliminar el alumno.", Alert.AlertType.ERROR);
                    }

                } catch (Exception e) {
                    String msg = e.getMessage().toLowerCase();
                    if (msg.contains("foreign key") || msg.contains("constraint")) {
                        Utilidades.mostrarAlerta2(
                                "Eliminación bloqueada",
                                "No se puede eliminar el alumno porque tiene sedes u otros elementos asociados.\nElimina primero las relaciones dependientes.",
                                Alert.AlertType.ERROR
                        );
                    } else {
                        Utilidades.mostrarAlerta2("Error inesperado", "Error al eliminar el alumno: " + e.getMessage(), Alert.AlertType.ERROR);
                    }
                    LoggerUtils.logError("ALUMNOS", "Error al intentar eliminar alumno con ID: " + alumno.getCodigo(), e);
                }
            }
        });
    }

    // Acción del botón "Buscar"
    @FXML
    private void btnBuscarAction() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarDatos();
        } else {
            List<Alumno> filtrados = alumnosDAO.buscarAlumnos(filtro);
            listaAlumnos.setAll(filtrados);
            tablaUsuarios.setItems(listaAlumnos);
        }
    }

    // Importar alumnos desde archivo CSV
    @FXML
    private void btnImportarAction(ActionEvent event) {
        File fichero = Utilidades.seleccFichero("Archivos CSV", "*.csv", "r");

        if (fichero != null) {
            int exitos = 0;
            int errores = 0;

            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fichero), "UTF-8"))) {
                String linea;
                int lineaActual = 0;

                LoggerUtils.logInfo("ALUMNOS", "Iniciando importación desde: " + fichero.getAbsolutePath());

                while ((linea = br.readLine()) != null) {
                    lineaActual++;

                    // Omitir cabecera si se detecta
                    if (lineaActual == 1 && linea.toLowerCase().contains("nombre;curso")) {
                        continue;
                    }

                    String[] items = linea.split(";");

                    if (items.length < 7) {
                        LoggerUtils.logWarning("ALUMNOS", "Línea incompleta ignorada (línea " + lineaActual + "): " + linea);
                        errores++;
                        continue;
                    }

                    try {
                        String nombre = items[0].trim();
                        String curso = items[1].trim();
                        String nombreSede = items[2].trim(); // Aunque no se usa en inserción
                        String codigoSedeStr = items[3].trim();
                        String nre = items[4].trim();
                        String telTutor1 = items[5].trim();
                        String telTutor2 = items[6].trim();

                        int codigoSede = Integer.parseInt(codigoSedeStr);

                        boolean insertado = alumnosDAO.insertarAlumno(nombre, curso, codigoSede, nre, telTutor1, telTutor2);

                        if (insertado) {
                            LoggerUtils.logInfo("ALUMNOS", "Alumno importado (línea " + lineaActual + "): " + nombre);
                            exitos++;
                        } else {
                            LoggerUtils.logWarning("ALUMNOS", "No se pudo insertar el alumno (línea " + lineaActual + "): " + nombre);
                            errores++;
                        }

                    } catch (NumberFormatException e) {
                        LoggerUtils.logWarning("ALUMNOS", "Código de sede inválido (línea " + lineaActual + "): " + items[3]);
                        errores++;
                    } catch (Exception e) {
                        LoggerUtils.logError("ALUMNOS", "Error al procesar línea " + lineaActual + ": " + linea, e);
                        errores++;
                    }
                }

                cargarDatos();

                LoggerUtils.logInfo("ALUMNOS", "Importación finalizada. Éxitos: " + exitos + " | Errores: " + errores);
                Utilidades.mostrarAlerta2("Importación finalizada",
                        "Alumnos importados correctamente: " + exitos + "\nErrores: " + errores,
                        Alert.AlertType.INFORMATION);

            } catch (IOException e) {
                LoggerUtils.logError("ALUMNOS", "Error al leer el archivo de importación: " + fichero.getAbsolutePath(), e);
                Utilidades.mostrarAlerta2("Error", "No se pudo leer el archivo seleccionado.", Alert.AlertType.ERROR);
            }
        }
    }

    // Exportar alumnos a un archivo CSV
    @FXML
    private void btnExportarAction(ActionEvent event) {
        File fichero = Utilidades.seleccFichero("Archivos CSV", "*.csv", "w");

        if (fichero != null) {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fichero), "ISO-8859-1"))) {
                // Cabecera CSV
                bw.write("Nombre;Curso;Nombre Sede;Código Sede;NRE;Tel Tutor 1;Tel Tutor 2\n");

                for (Alumno a : listaAlumnos) {
                    String linea = String.join(";",
                            a.getNombre() != null ? a.getNombre() : "",
                            a.getCurso() != null ? a.getCurso() : "",
                            a.getNombreSede() != null ? a.getNombreSede() : "",
                            String.valueOf(a.getCodigo_sede()),
                            a.getNre() != null ? a.getNre() : "",
                            a.getTelTutor1() != null ? a.getTelTutor1() : "",
                            a.getTelTutor2() != null ? a.getTelTutor2() : ""
                    );
                    bw.write(linea + "\n");
                }

                Utilidades.mostrarAlerta2("Éxito", "Exportación realizada correctamente.", Alert.AlertType.INFORMATION);

            } catch (IOException e) {
                LoggerUtils.logError("EXPORTACION", "Error al exportar alumnos: " + fichero, e);
                Utilidades.mostrarAlerta2("Error", "No se pudo exportar el archivo.", Alert.AlertType.ERROR);
            }
        }
    }

}
