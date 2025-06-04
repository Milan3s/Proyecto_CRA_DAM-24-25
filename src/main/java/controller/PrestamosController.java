package controller;

import dao.AlumnosDAO;
import dao.CategoriaDAO;
import dao.DispositivoDAO;
import dao.MarcaDAO;
import dao.PrestamoDAO;
import dao.SedeDAO;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Alumno;
import model.Categoria;
import model.Dispositivo;
import model.Marca;
import model.Prestamo;
import model.Sede;
import utils.LoggerUtils;
import utils.Utilidades;
import static utils.Utilidades.mostrarAlerta2;

public class PrestamosController implements Initializable {

    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnImportar;
    @FXML
    private Button btnExportar;
    @FXML
    private TableView<Prestamo> tablaPrest;
    @FXML
    private TableColumn<Prestamo, String> colDispositivo;
    @FXML
    private TableColumn<Prestamo, String> colAlumno;
    @FXML
    private TableColumn<Prestamo, String> colFechIni;
    @FXML
    private TableColumn<Prestamo, String> colFechFin;
    @FXML
    private TableColumn<Prestamo, String> colNetiqueta;
    @FXML
    private TableColumn<Prestamo, String> colMarca;
    @FXML
    private TableColumn<Prestamo, String> colModelo;
    @FXML
    private TableColumn<Prestamo, String> colNserie;
    @FXML
    private TableColumn<Prestamo, String> colNimei;
    @FXML
    private TableColumn<Prestamo, String> colCategoria;
    @FXML
    private TableColumn<Prestamo, String> colNre;
    @FXML
    private TableColumn<Prestamo, String> colSede;
    @FXML
    private TableColumn<Prestamo, String> colCurso;
    @FXML
    private TextField txtCurso;
    @FXML
    private ComboBox<Sede> cboxSede;
    @FXML
    private ComboBox<Marca> cboxMarca;
    @FXML
    private ComboBox<Categoria> cboxCategoria;
    
    private PrestamoDAO prestDAO = new PrestamoDAO();
    private SedeDAO sedeDAO = new SedeDAO();
    private CategoriaDAO catDAO = new CategoriaDAO();
    private MarcaDAO marcaDAO = new MarcaDAO();
    private DispositivoDAO dispositivoDAO = new DispositivoDAO();
    
    private ObservableList<Prestamo> listaPrest = FXCollections.observableArrayList();
    private ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
    private ObservableList<Marca> listaMarcas = FXCollections.observableArrayList();
    private ObservableList<Sede> listaSedes = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
        cargarCombos();
    }

    private void configurarColumnas() {
        SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
        
        colFechIni.setCellValueFactory(cellData -> {
            Prestamo prest = cellData.getValue();
            if (prest.getFecha_inicio()!= null) {
                return new SimpleStringProperty(formatFecha.format(prest.getFecha_inicio()));
            } else {
                return new SimpleStringProperty("");
            }
        });
        
        colFechFin.setCellValueFactory(cellData -> {
            Prestamo prest = cellData.getValue();
            if (prest.getFecha_fin()!= null) {
                return new SimpleStringProperty(formatFecha.format(prest.getFecha_fin()));
            } else {
                return new SimpleStringProperty("");
            }
        });
        
        colDispositivo.setCellValueFactory(cellData -> {
            Prestamo prest = cellData.getValue();
            if (prest.getDispositivo() != null) {
                return new SimpleStringProperty(prest.getDispositivo().getNombre());
            } else {
                return new SimpleStringProperty("");
            }
        });
        
        colNetiqueta.setCellValueFactory(cellData -> {
            Prestamo prest = cellData.getValue();
            if (prest.getDispositivo() != null) {
                return new SimpleStringProperty(String.valueOf(prest.getDispositivo().getNum_etiqueta()));
            } else {
                return new SimpleStringProperty("");
            }
        });
        
        colMarca.setCellValueFactory(cellData -> {
            Prestamo prest = cellData.getValue();
            if (prest.getDispositivo() != null && prest.getDispositivo().getMarca() != null) {
                return new SimpleStringProperty(prest.getDispositivo().getMarca().getNombre());
            } else {
                return new SimpleStringProperty("");
            }
        });
        
        colModelo.setCellValueFactory(cellData -> {
            Prestamo prest = cellData.getValue();
            if (prest.getDispositivo() != null) {
                return new SimpleStringProperty(prest.getDispositivo().getModelo());
            } else {
                return new SimpleStringProperty("");
            }
        });
        
        colCategoria.setCellValueFactory(cellData -> {
            Prestamo prest = cellData.getValue();
            if (prest.getDispositivo() != null && prest.getDispositivo().getCategoria() != null) {
                return new SimpleStringProperty(prest.getDispositivo().getCategoria().getNombre());
            } else {
                return new SimpleStringProperty("");
            }
        });
        
        colNserie.setCellValueFactory(cellData -> {
            Prestamo prest = cellData.getValue();
            if (prest.getDispositivo() != null) {
                return new SimpleStringProperty(prest.getDispositivo().getNum_serie());
            } else {
                return new SimpleStringProperty("");
            }
        });
        
        colNimei.setCellValueFactory(cellData -> {
            Prestamo prest = cellData.getValue();
            if (prest.getDispositivo() != null) {
                return new SimpleStringProperty(prest.getDispositivo().getImei());
            } else {
                return new SimpleStringProperty("");
            }
        });
        
        colAlumno.setCellValueFactory(cellData -> {
            Prestamo prest = cellData.getValue();
            if (prest.getAlumno() != null) {
                return new SimpleStringProperty(prest.getAlumno().getNombre());
            } else {
                return new SimpleStringProperty("");
            }
        });
        
        colNre.setCellValueFactory(cellData -> {
            Prestamo prest = cellData.getValue();
            if (prest.getAlumno() != null) {
                return new SimpleStringProperty(prest.getAlumno().getNre());
            } else {
                return new SimpleStringProperty("");
            }
        });
        
        colCurso.setCellValueFactory(cellData -> {
            Prestamo prest = cellData.getValue();
            if (prest.getAlumno() != null) {
                return new SimpleStringProperty(prest.getAlumno().getCurso());
            } else {
                return new SimpleStringProperty("");
            }
        });
        
        colSede.setCellValueFactory(cellData -> {
            Prestamo prest = cellData.getValue();
            if (prest.getAlumno() != null) {
                return new SimpleStringProperty(prest.getAlumno().getNombreSede());
            } else {
                return new SimpleStringProperty("");
            }
        });
    }
    
    private void cargarDatos() {
        listaPrest = prestDAO.obtenerPrestamos(null, null);
        tablaPrest.setItems(listaPrest);
    }
    
    private void cargarCombos() {
        try {
            // Categorías
            listaCategorias = FXCollections.observableArrayList(catDAO.obtenerCategorias());
            cboxCategoria.setItems(listaCategorias);
            Utilidades.cargarComboBox(cboxCategoria, listaCategorias, Categoria::getNombre);

            // Marcas
            listaMarcas = FXCollections.observableArrayList(marcaDAO.obtenerMarcas());
            cboxMarca.setItems(listaMarcas);
            Utilidades.cargarComboBox(cboxMarca, listaMarcas, Marca::getNombre);

            // Sedes
            listaSedes = FXCollections.observableArrayList(sedeDAO.obtenerSede());
            cboxSede.setItems(listaSedes);
            Utilidades.cargarComboBox(cboxSede, listaSedes, Sede::getNombre);
            
        } catch (Exception e) {
            LoggerUtils.logError("PRESTAMOS", "Error al cargar comboBox: " + e.getMessage(), e);
        }
    }

    @FXML
    private void btnNuevoAction(ActionEvent event) {
    }

    @FXML
    private void btnEliminarAction(ActionEvent event) {
        Prestamo prestamoSelec = tablaPrest.getSelectionModel().getSelectedItem();
        
        if (prestamoSelec == null) {
            mostrarAlerta2("Sin selección", "Por favor, seleccione un préstamo a eliminar.", Alert.AlertType.WARNING);
            LoggerUtils.logInfo("PRESTAMOS", "Intento de eliminar sin seleccionar préstamo.");
            return;
        }
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Seguro que desea eliminar el siguiente préstamo?");
        confirmacion.setContentText(prestamoSelec.getDispositivo().getNombre());
        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                int filas = prestDAO.eliminarPrestamo(prestamoSelec);
                if (filas > 0) {
                    cargarDatos();
                    dispositivoDAO.actualizarPrestado(prestamoSelec.getDispositivo().getCodigo(), false);
                }
            }
        });
    }

    @FXML
    private void capturarClick(MouseEvent event) {
        if (event.getClickCount() == 2 && !tablaPrest.getSelectionModel().isEmpty()) {
            Prestamo prestamo = tablaPrest.getSelectionModel().getSelectedItem();
            abrirMantenimiento(prestamo, prestamo.getDispositivo());
        }
    }

    @FXML
    private void btnBuscarAction(ActionEvent event) {
        String cursoFilt = txtCurso.getText();
        Categoria categFilt = cboxCategoria.getValue();
        Marca marcaFilt = cboxMarca.getValue();
        Sede sedeFilt = cboxSede.getValue();
        
        FilteredList<Prestamo> filteredList = new FilteredList<>(listaPrest, p -> true);
        
        filteredList.setPredicate(prestamo -> {
            boolean coincCurso = true;
            boolean coincCateg = true;
            boolean coincMarca = true;
            boolean coincSede = true;
            
            // Filtro por curso
            if (cursoFilt != null && !cursoFilt.isEmpty()) {
                coincCurso = prestamo.getAlumno().getCurso() != null && prestamo.getAlumno().getCurso().equals(cursoFilt);
            }
            
            // Filtro por categoría            
            if (categFilt != null) {
                coincCateg = prestamo.getDispositivo().getCategoria() != null && prestamo.getDispositivo().getCategoria().getCodigo() == categFilt.getCodigo();
            }
            
            // Filtro por Marca
            if (marcaFilt != null) {
                coincMarca = prestamo.getDispositivo().getMarca() != null && prestamo.getDispositivo().getMarca().getCodigo() == marcaFilt.getCodigo();
            }
            
            // Filtro por sede
            if (sedeFilt != null) {
                coincSede = prestamo.getAlumno().getCodigo_sede() != 0 && prestamo.getAlumno().getCodigo_sede() == sedeFilt.getCodigoSede();
            }
            
            return coincCurso && coincCateg && coincMarca && coincSede;
        });
        
        tablaPrest.setItems(filteredList);
    }

    @FXML
    private void btnLimpiarAction(ActionEvent event) {
        txtCurso.setText("");
        cboxCategoria.setValue(null);
        cboxMarca.setValue(null);
        cboxSede.setValue(null);
    }
    
    private void abrirMantenimiento(Prestamo prestamo, Dispositivo dispositivo) {
        try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/PrestamosMantenim.fxml"));
            Parent root = loader.load();
            
            PrestamosMantenimController controller = loader.getController();
            controller.setPrestamo(prestamo, dispositivo);
            
            Stage modalStage = new Stage();
            modalStage.setTitle("Mantenimiento de préstamos");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.showAndWait();
            
            cargarDatos();
        } catch (IOException e) {
            LoggerUtils.logError("PRESTAMOS", "Error al abrir ventana PrestamosMantenim: " + e.getMessage(), e);
        }
    }

    @FXML
    private void btnImportarAction(ActionEvent event) {
        // Para seleccionar un fichero .csv
        File fichero = Utilidades.seleccFichero("Archivos CSV", "*.csv", "r");
        
        if (fichero != null) {
            String[] items;
            String numSerie;
            String nre;
            int codigoDisp;
            int codigoAlu;
            Date fechaIni;
            AlumnosDAO alumnoDAO = new AlumnosDAO();
            
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fichero)))) {
                String linea = "";

                while ((linea = br.readLine()) != null) {
                    // Vamos leyendo cada línea del fichero
                    items = linea.split(";");
                    numSerie = items[0];
                    codigoDisp = dispositivoDAO.buscarCodigoXSerie(numSerie);
                    nre = items[1];
                    codigoAlu = alumnoDAO.buscarCodigoXnre(nre);
                    fechaIni = Utilidades.convertirFecha(items[2]);
                    if (codigoDisp > 0 && codigoAlu > 0 && fechaIni != null) {
                        boolean resul = prestDAO.insertarPrestamo(codigoDisp, codigoAlu, fechaIni);
                        if (resul) {
                            // Se actualiza el campo prestado en el dispositivo
                            dispositivoDAO.actualizarPrestado(codigoDisp, true);
                        }
                    }
                }
                cargarDatos();
                mostrarAlerta2("Éxito", "Importación realizada.", Alert.AlertType.INFORMATION);
                
            } catch (FileNotFoundException e) {
                LoggerUtils.logError("IMPORTACION", "Error al acceder al fichero : " + "\n" + fichero + e.getMessage(), e);
            } catch (IOException e) {
                LoggerUtils.logError("IMPORTACION", "Error al leer el fichero : " + "\n" + fichero + e.getMessage(), e);
            }
        }
    }

    @FXML
    private void btnExportarAction(ActionEvent event) {
        // Seleccionar fichero destino
        File fichero = Utilidades.seleccFichero("Archivos CSV", "*.csv", "w");
        
        if (fichero != null) {
            // Hay que guardarlo con codificación ISO-8859-1 para que los acentos se muestren correctamente al abrirlo con Excel
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fichero), "ISO-8859-1"))) {
                // Línea de cabecera
                bw.write("Dispositivo;Modelo;Marca;Categoria;NumSerie;Imei;Num_etiqueta;Alumno;Curso;Sede;NRE;Fecha_inicio;Fecha_fin\n");
                
                String linea = "";
                
                // Se recorren los elementos del ObservableList y se van grabando las líneas en el fichero destino
                for (Prestamo prest : listaPrest) {
                    Dispositivo disp = prest.getDispositivo();
                    Alumno alu = prest.getAlumno();
                    
                    linea = disp.getNombre() != null ? disp.getNombre() + ";" : ";";
                    linea += disp.getModelo() != null ? disp.getModelo() + ";" : ";";
                    linea += disp.getMarca() != null ? disp.getMarca().getNombre() + ";" : ";";
                    linea += disp.getCategoria() != null ? disp.getCategoria().getNombre() + ";" : ";";
                    linea += disp.getNum_serie() != null ? disp.getNum_serie() + ";" : ";";
                    linea += disp.getImei() != null ? disp.getImei() + ";" : ";";
                    linea += String.valueOf(disp.getNum_etiqueta()) + ";";
                    linea += alu.getNombre() != null ? alu.getNombre() + ";" : ";";
                    linea += alu.getCurso() != null ? alu.getCurso() + ";" : ";";
                    linea += alu.getNombreSede() != null ? alu.getNombreSede() + ";" : ";";
                    linea += alu.getNre() != null ? alu.getNre() + ";" : ";";
                    linea += prest.getFecha_inicio() != null ? prest.getFecha_inicio() + ";" : ";";
                    linea += prest.getFecha_fin() != null ? prest.getFecha_fin() + ";" : ";";
                    bw.write(linea + "\n");
                }
                
                mostrarAlerta2("Éxito", "Exportación realizada.", Alert.AlertType.INFORMATION);
                
            } catch (IOException e) {
                LoggerUtils.logError("EXPORTACIÓN", "Error al leer el fichero : " + "\n" + fichero + e.getMessage(), e);
            }
        }
    }
}
