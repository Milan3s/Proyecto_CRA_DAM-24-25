package controller;

// Importamos clases necesarias para manejo de datos, interfaz y utilidades
import dao.ProgramasEduDAO; // Acceso a datos de Programas Educativos
import java.io.*; // Para leer y escribir archivos
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.ProgramasEdu; // Modelo de datos
import utils.LoggerUtils; // Para registrar logs
import utils.Utilidades; // Funciones de apoyo (alertas, seleccionar ficheros)

public class ProgramasEController implements Initializable {

    // Botones y campos de la interfaz
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnImportar;
    @FXML
    private Button btnExportar;
    @FXML
    private TextField txtBuscar;

    // Tabla y columnas para mostrar programas
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
        // Configura las columnas y carga los datos
        configurarColumnas();
        cargarDatos();

        // Doble clic en la tabla abre el formulario de edición
        tablaProgramas.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tablaProgramas.getSelectionModel().isEmpty()) {
                ProgramasEdu programaSeleccionado = tablaProgramas.getSelectionModel().getSelectedItem();
                abrirFormularioPrograma(programaSeleccionado);
            }
        });

        // Si se borra el texto del buscador, recarga la lista completa
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) {
                tablaProgramas.setItems(listaProgramas);
            }
        });
    }

    // Asocia las columnas de la tabla con las propiedades del modelo
    private void configurarColumnas() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
    }

    // Carga los datos desde la base de datos
    private void cargarDatos() {
        List<ProgramasEdu> programas = programaDAO.obtenerProgramas();
        listaProgramas.setAll(programas);
        tablaProgramas.setItems(listaProgramas);
    }

    // Acción al presionar el botón "Nuevo"
    @FXML
    private void btnNuevoAction(ActionEvent event) {
        abrirFormularioPrograma(null);
    }

    // Abre el formulario para crear o editar un programa
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

            cargarDatos(); // Recarga los datos al cerrar el formulario

        } catch (IOException e) {
            LoggerUtils.logError("Programas Educativos", "Error al abrir el formulario de programas " + e.getMessage(), e);
        }
    }

    // Acción al presionar el botón "Eliminar"
    @FXML
    private void btnEliminarAction(ActionEvent event) {
        ProgramasEdu programa = tablaProgramas.getSelectionModel().getSelectedItem();
        if (programa == null) {
            Utilidades.mostrarAlerta2("Sin selección", "Selecciona un programa para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        // Muestra confirmación antes de eliminar
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Seguro que quieres eliminar este programa?");
        confirmacion.setContentText("Nombre: " + programa.getNombre() + "\nCódigo: " + programa.getCodigo());

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                boolean eliminado = programaDAO.eliminarPrograma(programa.getCodigo());
                if (eliminado) {
                    cargarDatos();
                    Utilidades.mostrarAlerta2("Eliminado", "Programa eliminado correctamente.", Alert.AlertType.INFORMATION);
                } else {
                    Utilidades.mostrarAlerta2("Error", "No se pudo eliminar el programa.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    // Acción al presionar el botón "Buscar"
    @FXML
    private void btnBuscarAction(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarDatos();
        } else {
            List<ProgramasEdu> filtrados = programaDAO.buscarProgramas(filtro);
            listaProgramas.setAll(filtrados);
            tablaProgramas.setItems(listaProgramas);
        }
    }

    // Importa programas desde un archivo CSV
    @FXML
    private void btnImportarAction(ActionEvent event) {
        File fichero = Utilidades.seleccFichero("Archivos CSV", "*.csv", "r");

        if (fichero != null) {
            int exitos = 0;
            int errores = 0;

            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fichero), "UTF-8"))) {
                String linea;
                int lineaActual = 0;

                LoggerUtils.logInfo("ProgramasEducativos", "Iniciando importación desde: " + fichero.getAbsolutePath());

                while ((linea = br.readLine()) != null) {
                    lineaActual++;

                    // Salta cabecera si la hay
                    if (lineaActual == 1 && linea.toLowerCase().contains("nombre")) {
                        continue;
                    }

                    String[] items = linea.split(";");

                    if (items.length < 1) {
                        LoggerUtils.logWarning("ProgramasEducativos", "Línea incompleta ignorada (línea " + lineaActual + "): " + linea);
                        errores++;
                        continue;
                    }

                    try {
                        String nombre = items[0].trim();

                        boolean insertado = programaDAO.insertarPrograma(nombre);

                        if (insertado) {
                            LoggerUtils.logInfo("ProgramasEducativos", "Programa importado (línea " + lineaActual + "): " + nombre);
                            exitos++;
                        } else {
                            LoggerUtils.logWarning("ProgramasEducativos", "No se pudo insertar el programa (línea " + lineaActual + "): " + nombre);
                            errores++;
                        }

                    } catch (Exception e) {
                        LoggerUtils.logError("ProgramasEducativos", "Error al procesar línea " + lineaActual + ": " + linea, e);
                        errores++;
                    }
                }

                cargarDatos();

                LoggerUtils.logInfo("ProgramasEducativos", "Importación finalizada. Éxitos: " + exitos + " | Errores: " + errores);
                Utilidades.mostrarAlerta2("Importación finalizada",
                        "Programas importados correctamente: " + exitos + "\nErrores: " + errores,
                        Alert.AlertType.INFORMATION);

            } catch (IOException e) {
                LoggerUtils.logError("ProgramasEducativos", "Error al leer el archivo de importación: " + fichero.getAbsolutePath(), e);
                Utilidades.mostrarAlerta2("Error", "No se pudo leer el archivo seleccionado.", Alert.AlertType.ERROR);
            }
        }
    }

    // Exporta los programas a un archivo CSV
    @FXML
    private void btnExportarAction(ActionEvent event) {
        File fichero = Utilidades.seleccFichero("Archivos CSV", "*.csv", "w");

        if (fichero != null) {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fichero), "ISO-8859-1"))) {
                // Cabecera CSV
                bw.write("Nombre\n");

                for (ProgramasEdu p : listaProgramas) {
                    String linea = p.getNombre() != null ? p.getNombre() : "";
                    bw.write(linea + "\n");
                }

                Utilidades.mostrarAlerta2("Éxito", "Exportación realizada correctamente.", Alert.AlertType.INFORMATION);

            } catch (IOException e) {
                LoggerUtils.logError("EXPORTACION", "Error al exportar programas: " + fichero, e);
                Utilidades.mostrarAlerta2("Error", "No se pudo exportar el archivo.", Alert.AlertType.ERROR);
            }
        }
    }

}
