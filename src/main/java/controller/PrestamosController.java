package controller;

import dao.CategoriaDAO;
import dao.MarcaDAO;
import dao.PrestamoDAO;
import dao.SedeDAO;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.Categoria;
import model.Dispositivo;
import model.Marca;
import model.Prestamo;
import model.Sede;
import utils.LoggerUtils;
import utils.Utilidades;

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
        listaPrest = prestDAO.obtenerPrestamos();
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
    }

    @FXML
    private void capturarClick(MouseEvent event) {
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
    
}
