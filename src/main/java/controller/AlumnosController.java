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
        if (alumno != null && alumnosDAO.eliminarAlumno(alumno.getCodigo())) {
            cargarDatos();
            Utilidades.mostrarAlerta2("Eliminado", "Alumno eliminado correctamente.", Alert.AlertType.INFORMATION);
        } else {
            Utilidades.mostrarAlerta2("Error", "No se pudo eliminar el alumno.", Alert.AlertType.ERROR);
        }
    }

    // (Opcional) Eliminar todos los alumnos — si se usa
    private void btnActionEliminarTodos() {
        int filasEliminadas = alumnosDAO.eliminarTodosAlumnos();
        LoggerUtils.logInfo("ALUMNOS", "Total de alumnos eliminados: " + filasEliminadas);
        cargarDatos();
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
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fichero)))) {
                String linea;

                while ((linea = br.readLine()) != null) {
                    String[] items = linea.split(";");

                    if (items.length >= 7) {
                        String nombre = items[0];
                        String curso = items[1];
                        String nombreSede = items[2];
                        int codigoSede = Integer.parseInt(items[3]);
                        String nre = items[4];
                        String telTutor1 = items[5];
                        String telTutor2 = items[6];

                        Alumno nuevoAlumno = new Alumno(0, nombre, curso, nombreSede, codigoSede);
                        nuevoAlumno.setNre(nre);
                        nuevoAlumno.setTelTutor1(telTutor1);
                        nuevoAlumno.setTelTutor2(telTutor2);

                        alumnosDAO.insertarAlumno(nombre, curso, codigoSede, nre, telTutor1, telTutor2);
                    }
                }

                cargarDatos();
                Utilidades.mostrarAlerta2("Éxito", "Importación realizada correctamente.", Alert.AlertType.INFORMATION);

            } catch (IOException | NumberFormatException e) {
                LoggerUtils.logError("IMPORTACION", "Error al importar alumnos: " + fichero, e);
                Utilidades.mostrarAlerta2("Error", "No se pudo importar el archivo.", Alert.AlertType.ERROR);
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
