package controller;

import dao.CategoriaDAO;
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
import javafx.util.StringConverter;
import model.Alumno;
import model.Dispositivo;
import model.Proveedor;
import model.Categoria;
import model.Marca;
import model.ProgramasEdu;
import model.Sede;
import dao.DispositivoDAO;
import dao.MarcaDAO;
import dao.ProgramasEduDAO;
import dao.ProveedorDAO;
import dao.SedeDAO;
import utils.LoggerUtils;
import utils.Utilidades;
import static utils.Utilidades.mostrarAlerta2;

public class DispositivosController implements Initializable {

    private ObservableList<Dispositivo> listaDisposit = FXCollections.observableArrayList();
    
    @FXML
    private TableView<Dispositivo> tablaDisp;
    @FXML
    private TableColumn<Dispositivo, Integer> colCodigo;
    @FXML
    private TableColumn<Dispositivo, String> colNombre;
    @FXML
    private TableColumn<Dispositivo, String> colModelo;
    @FXML
    private TableColumn<Dispositivo, String> colNumSerie;
    @FXML
    private TableColumn<Dispositivo, Integer> colNumEti;
    @FXML
    private TableColumn<Dispositivo, String> colMac;
    @FXML
    private TableColumn<Dispositivo, String> colImei;
    @FXML
    private TableColumn<Dispositivo, String> colAlumno;
    @FXML
    private TableColumn<Dispositivo, String> colCurso;
    @FXML
    private TableColumn<Dispositivo, String> colFechaAdqui;
    @FXML
    private TableColumn<Dispositivo, String> colProveedor;
    @FXML
    private TableColumn<Dispositivo, String> colCategoria;
    @FXML
    private TableColumn<Dispositivo, String> colMarca;
    @FXML
    private TableColumn<Dispositivo, String> colSede;
    @FXML
    private TableColumn<Dispositivo, String> colEspacio;
    @FXML
    private TableColumn<Dispositivo, String> colAbaco;
    @FXML
    private TableColumn<Dispositivo, String> colPrograma;
    @FXML
    private TableColumn<Dispositivo, String> colPrestado;
    
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
    private TextField txtNombre;
    @FXML
    private ComboBox<Categoria> cboxCategoria;
    @FXML
    private ComboBox<Marca> cboxMarca;
    @FXML
    private ComboBox<Sede> cboxSede;
    @FXML
    private ComboBox<ProgramasEdu> cboxPrograma;
    @FXML
    private ComboBox<Proveedor> cboxProveedor;
    @FXML
    private ComboBox<String> cboxPrestado;
    
    private DispositivoDAO dispDAO = new DispositivoDAO();
    private ProveedorDAO provDAO = new ProveedorDAO();
    private SedeDAO sedeDAO = new SedeDAO();
    private CategoriaDAO catDAO = new CategoriaDAO();
    private MarcaDAO marcaDAO = new MarcaDAO();
    private ProgramasEduDAO programaDAO = new ProgramasEduDAO();
    
    private ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
    private ObservableList<Marca> listaMarcas = FXCollections.observableArrayList();
    private ObservableList<Sede> listaSedes = FXCollections.observableArrayList();
    private ObservableList<ProgramasEdu> listaProgramas = FXCollections.observableArrayList();
    private ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
        cargarCombos();
    }

    private void configurarColumnas() {
        SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
        
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colNumSerie.setCellValueFactory(new PropertyValueFactory<>("num_serie"));
        colMac.setCellValueFactory(new PropertyValueFactory<>("mac"));
        colImei.setCellValueFactory(new PropertyValueFactory<>("imei"));
        colNumEti.setCellValueFactory(new PropertyValueFactory<>("num_etiqueta"));
        colFechaAdqui.setCellValueFactory(cellData -> {
            Dispositivo disp = cellData.getValue();
            if (disp.getFecha_adquisicion()!= null) {
                return new SimpleStringProperty(formatFecha.format(disp.getFecha_adquisicion()));
            } else {
                return new SimpleStringProperty("");
            }
        });
        colProveedor.setCellValueFactory(cellData -> {
            Dispositivo disp = cellData.getValue();
            if (disp.getProveedor() != null) {
                return new SimpleStringProperty(disp.getProveedor().getNombre());
            } else {
                return new SimpleStringProperty("");
            }
        });
        colAlumno.setCellValueFactory(cellData -> {
            Dispositivo disp = cellData.getValue();
            if (disp.getAlumno() != null) {
                return new SimpleStringProperty(disp.getAlumno().getNombre());
            } else {
                return new SimpleStringProperty("");
            }
        });
        colCategoria.setCellValueFactory(cellData -> {
            Dispositivo disp = cellData.getValue();
            if (disp.getCategoria() != null) {
                return new SimpleStringProperty(disp.getCategoria().getNombre());
            } else {
                return new SimpleStringProperty("");
            }
        });
        colMarca.setCellValueFactory(cellData -> {
            Dispositivo disp = cellData.getValue();
            if (disp.getMarca() != null) {
                return new SimpleStringProperty(disp.getMarca().getNombre());
            } else {
                return new SimpleStringProperty("");
            }
        });
        colSede.setCellValueFactory(cellData -> {
            Dispositivo disp = cellData.getValue();
            if (disp.getSede() != null) {
                return new SimpleStringProperty(disp.getSede().getNombre());
            } else {
                return new SimpleStringProperty("");
            }
        });
        colEspacio.setCellValueFactory(cellData -> {
            Dispositivo disp = cellData.getValue();
            if (disp.getEspacio() != null) {
                return new SimpleStringProperty(disp.getEspacio().getNombre());
            } else {
                return new SimpleStringProperty("");
            }
        });
        colAbaco.setCellValueFactory(cellData -> {
            Dispositivo disp = cellData.getValue();
            if (disp.getEspacio() != null) {
                return new SimpleStringProperty(disp.getEspacio().getNumAbaco());
            } else {
                return new SimpleStringProperty("");
            }
        });
        colPrograma.setCellValueFactory(cellData -> {
            Dispositivo disp = cellData.getValue();
            if (disp.getProgramae() != null) {
                return new SimpleStringProperty(disp.getProgramae().getNombre());
            } else {
                return new SimpleStringProperty("");
            }
        });
        colPrestado.setCellValueFactory(cellData -> {
            Dispositivo disp = cellData.getValue();
            if (disp.isPrestado()) {
                return new SimpleStringProperty("Sí");
            } else {
                return new SimpleStringProperty("");
            }
        });
        colCurso.setCellValueFactory(cellData -> {
            Dispositivo disp = cellData.getValue();
            if (disp.getAlumno() != null) {
                return new SimpleStringProperty(disp.getAlumno().getCurso());
            } else {
                return new SimpleStringProperty("");
            }
        });
    }
    
    private void cargarDatos() {
        listaDisposit = dispDAO.obtenerDispositivos();
        tablaDisp.setItems(listaDisposit);
    }

    @FXML
    private void btnBuscarAction(ActionEvent event) {
        String nombreFilt = txtNombre.getText();
        Categoria categFilt = cboxCategoria.getValue();
        Marca marcaFilt = cboxMarca.getValue();
        Sede sedeFilt = cboxSede.getValue();
        ProgramasEdu programaFilt = cboxPrograma.getValue();
        Proveedor provFilt = cboxProveedor.getValue();
        String prestFilt = cboxPrestado.getValue();
        
        FilteredList<Dispositivo> filteredList = new FilteredList<>(listaDisposit, p -> true);
        
        filteredList.setPredicate(dispositivo -> {
            boolean coincNombre = true;
            boolean coincCateg = true;
            boolean coincMarca = true;
            boolean coincSede = true;
            boolean coincProg = true;
            boolean coincProv = true;
            boolean coincPrest = true;
            
            // Filtro por nombre
            if (nombreFilt != null && !nombreFilt.isEmpty()) {
                coincNombre = dispositivo.getNombre() != null && dispositivo.getNombre().toLowerCase().contains(nombreFilt.toLowerCase());
            }
            
            // Filtro por categoría            
            if (categFilt != null) {
                coincCateg = dispositivo.getCategoria() != null && dispositivo.getCategoria().getCodigo() == categFilt.getCodigo();
            }
            
            // Filtro por Marca
            if (marcaFilt != null) {
                coincMarca = dispositivo.getMarca() != null && dispositivo.getMarca().getCodigo() == marcaFilt.getCodigo();
            }
            
            // Filtro por sede
            if (sedeFilt != null) {
                coincSede = dispositivo.getEspacio() != null && dispositivo.getEspacio().getCodigoSede() == sedeFilt.getCodigoSede();
            }
            
            // Filtro por Programa
            if (programaFilt != null) {
                coincProg = dispositivo.getProgramae() != null && dispositivo.getProgramae().getCodigo() == programaFilt.getCodigo();
            }
            
            // Filtro por proveedor
            if (provFilt != null) {
                coincProv = dispositivo.getProveedor() != null && dispositivo.getProveedor().getCodigo() == provFilt.getCodigo();
            }
            
            // Filtro por prestado
            if (prestFilt != null) {
                coincPrest = dispositivo.isPrestado() == prestFilt.equals("Sí");
            }
            
            return coincNombre && coincCateg && coincMarca && coincSede && coincProg && coincProv && coincPrest;
        });
        
        tablaDisp.setItems(filteredList);
    }

    @FXML
    private void btnNuevoAction(ActionEvent event) {
        abrirMantenimiento(null);
    }

    @FXML
    private void capturarClick(MouseEvent event) {
        if (event.getClickCount() == 2 && !tablaDisp.getSelectionModel().isEmpty()) {
            Dispositivo disp = tablaDisp.getSelectionModel().getSelectedItem();
            abrirMantenimiento(disp);
        }
    }
    
    private void abrirMantenimiento(Dispositivo disp) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/DispositivosMantenim.fxml"));
            Parent root = loader.load();
            
            DispositivosMantenimController controller = loader.getController();
            controller.setDispositivo(disp);
            
            Stage modalStage = new Stage();
            modalStage.setTitle("Mantenimiento de dispostivos");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.showAndWait();

            cargarDatos();
            
        } catch (IOException e) {
            LoggerUtils.logError("DISPOSITIVOS", "Error al abrir ventana DispositivosMantenim", e);
        }
    }
    
    @FXML
    private void btnEliminarAction(ActionEvent event) {
        Dispositivo dispSelec = tablaDisp.getSelectionModel().getSelectedItem();
        
        if (dispSelec == null) {
            mostrarAlerta2("Sin selección", "Por favor, seleccione un dispositivo a eliminar.", Alert.AlertType.WARNING);
            LoggerUtils.logInfo("DISPOSITIVOS", "Intento de eliminar sin seleccionar dispositivo.");
            return;
        }
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Seguro que desea eliminar el siguiente dispositivo?");
        confirmacion.setContentText(dispSelec.getNombre());
        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                int filas = dispDAO.eliminarDispositivo(dispSelec.getCodigo());
                if (filas > 0) cargarDatos();
            }
        });
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
            
            // Programas
            listaProgramas = FXCollections.observableArrayList(programaDAO.obtenerProgramas());
            cboxPrograma.setItems(listaProgramas);
            Utilidades.cargarComboBox(cboxPrograma, listaProgramas, ProgramasEdu::getNombre);
            
            // Proveedores
            listaProveedores = provDAO.obtenerProveedores();
            cboxProveedor.setItems(listaProveedores);
            Utilidades.cargarComboBox(cboxProveedor, listaProveedores, Proveedor::getNombre);
            
            // Prestado
            ObservableList<String> opcPrest = FXCollections.observableArrayList();
            opcPrest.add("Sí");
            opcPrest.add("No");       
            cboxPrestado.setItems(opcPrest);
            
        } catch (Exception e) {
            LoggerUtils.logError("DISPOSITIVOS", "Error al cargar comboBox: " + e.getMessage(), e);
        }
    }
    
    @FXML
    private void btnLimpiarAction(ActionEvent event) {
        txtNombre.setText("");
        cboxCategoria.setValue(null);
        cboxMarca.setValue(null);
        cboxSede.setValue(null);
        cboxPrograma.setValue(null);
        cboxProveedor.setValue(null);
        cboxPrestado.setValue(null);
        
        tablaDisp.setItems(listaDisposit);
    }

    @FXML
    private void btnImportarAction(ActionEvent event) {
        // Para seleccionar un fichero .csv
        File fichero = Utilidades.seleccFichero("Archivos CSV", "*.csv", "r");
        
        if (fichero != null) {
            String[] items;
            String nombre;
            String modelo;
            String nSerie;
            Date fecha_adq;
            String mac;
            String imei;
            int numEtiq;
            String comentReg;
            String observaciones;
            boolean prestado = false;
            
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fichero)))) {
                String linea = "";

                while ((linea = br.readLine()) != null) {
                    // Vamos leyendo cada línea del fichero
                    items = linea.split(";");
                    nombre = items[0];
                    modelo = items[1];
                    nSerie = items[2];
                    fecha_adq = Date.valueOf(items[3]);
                    mac = items[4];
                    imei = items[5];
                    numEtiq = Integer.parseInt(items[6]);
                    comentReg = items[7];
                    observaciones = items[8];
                    
                    Dispositivo disp = new Dispositivo(0, nombre, modelo, nSerie, fecha_adq, mac, imei, numEtiq, null, null, comentReg
                        , null, null, null, null, null, prestado, observaciones);
                    dispDAO.insertarDispositivo(disp);
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
                bw.write("Nombre;Modelo;Marca;Categoria;NumSerie;Fecha_adquisicion;Mac;Imei;Num_etiqueta;Alumno;Curso;Sede;Espacio;Num_abaco;Proveedor;ProgramaE;Comentario\n");
                
                String linea = "";
                String numEtiq = "";
                
                // Se recorren los elementos del ObservableList y se van grabando las líneas en el fichero destino
                for (Dispositivo disp : listaDisposit) {
                    linea = disp.getNombre() != null ? disp.getNombre() + ";" : ";";
                    linea += disp.getModelo() != null ? disp.getModelo() + ";" : ";";
                    linea += disp.getMarca() != null ? disp.getMarca().getNombre() + ";" : ";";
                    linea += disp.getCategoria() != null ? disp.getCategoria().getNombre() + ";" : ";";
                    linea += disp.getNum_serie() != null ? disp.getNum_serie() + ";" : ";";
                    linea += disp.getFecha_adquisicion() != null ? disp.getFecha_adquisicion() + ";" : ";";
                    linea += disp.getMac() != null ? disp.getMac() + ";" : ";";
                    linea += disp.getImei() != null ? disp.getImei() + ";" : ";";
                    linea += String.valueOf(disp.getNum_etiqueta()) + ";";
                    linea += disp.getAlumno() != null ? disp.getAlumno().getNombre() + ";" : ";";
                    linea += disp.getAlumno() != null ? disp.getAlumno().getCurso() + ";" : ";";
                    linea += disp.getSede() != null ? disp.getSede().getNombre() + ";" : ";";
                    linea += disp.getEspacio() != null ? disp.getEspacio().getNombre() + ";" : ";";
                    linea += disp.getEspacio() != null ? disp.getEspacio().getNumAbaco() + ";" : ";";
                    linea += disp.getProveedor() != null ? disp.getProveedor().getNombre() + ";" : ";";
                    linea += disp.getProgramae() != null ? disp.getProgramae().getNombre() + ";" : ";";
                    linea += disp.getComentario() != null ? disp.getComentario() + ";" : ";";
                    bw.write(linea + "\n");
                }
                mostrarAlerta2("Éxito", "Exportación realizada.", Alert.AlertType.INFORMATION);
                
            } catch (IOException e) {
                LoggerUtils.logError("IMPORTACION", "Error al leer el fichero : " + "\n" + fichero + e.getMessage(), e);
            }
        }
    }
}
