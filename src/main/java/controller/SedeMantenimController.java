package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.CentroEducativo;
import dao.CentroEducativoDAO;
import model.Sede;
import dao.SedeDAO;
import utils.LoggerUtils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SedeMantenimController implements Initializable {

    // Campos de texto para ingresar o mostrar datos de la sede
    @FXML private TextField txtNombre;
    @FXML private TextField txtCalle;
    @FXML private TextField txtLocalidad;
    @FXML private TextField txtCP;
    @FXML private TextField txtMunicipio;
    @FXML private TextField txtProvincia;
    @FXML private TextField txtTelefono;
    
    // ComboBox para seleccionar el código del centro educativo asociado
    @FXML private ComboBox<Integer> comboCodigoCentro;
    
    // Botones para guardar o cancelar la operación
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    // Objeto sede actual para editar, puede ser null si es una nueva sede
    private Sede sede;
    
    // DAO para operaciones de base de datos sobre sedes
    private final SedeDAO sedeDAO = new SedeDAO();
    
    // DAO para obtener la lista de centros educativos disponibles
    private final CentroEducativoDAO centroDAO = new CentroEducativoDAO();

    // Método llamado automáticamente al inicializar el controlador
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Registrar inicio de la sección en log
        LoggerUtils.logSection("MANTENIMIENTO SEDE");

        // Cargar los códigos de centros educativos en el ComboBox
        cargarCodigosCentro();

        // Asociar acción al botón Guardar
        btnGuardar.setOnAction(e -> guardarSede());
    }

    // Carga los códigos de centros educativos en el comboBox
    private void cargarCodigosCentro() {
        // Obtener la lista de centros desde el DAO
        List<CentroEducativo> centros = centroDAO.obtenerCentros();
        
        // Crear lista observable para el ComboBox
        ObservableList<Integer> codigos = FXCollections.observableArrayList();
        
        // Recorrer cada centro y agregar su código (convertido a Integer)
        for (CentroEducativo c : centros) {
            codigos.add(Integer.parseInt(c.getCodigoCentro()));
        }
        
        // Asignar los códigos al ComboBox
        comboCodigoCentro.setItems(codigos);
    }

    // Método para establecer la sede a editar (o null si es nueva)
    public void setSede(Sede sede) {
        this.sede = sede;

        // Si la sede no es null, llenar los campos con sus datos
        if (sede != null) {
            txtNombre.setText(sede.getNombre());
            txtCalle.setText(sede.getCalle());
            txtLocalidad.setText(sede.getLocalidad());
            txtCP.setText(sede.getCp());
            txtMunicipio.setText(sede.getMunicipio());
            txtProvincia.setText(sede.getProvincia());
            txtTelefono.setText(sede.getTelefono());
            comboCodigoCentro.setValue(sede.getCodigoCentro());
        }
    }

    // Método para validar y guardar la sede (nueva o actualizada)
    private void guardarSede() {
        // Obtener valores de los campos
        String nombre = txtNombre.getText();
        String calle = txtCalle.getText();
        String localidad = txtLocalidad.getText();
        String cp = txtCP.getText();
        String municipio = txtMunicipio.getText();
        String provincia = txtProvincia.getText();
        String telefono = txtTelefono.getText();
        Integer codigoCentro = comboCodigoCentro.getValue();

        // Validar campos obligatorios (nombre y código centro)
        if (null == nombre || nombre.isEmpty() || codigoCentro == null) {
            mostrarAlerta("Campos incompletos", "Por favor, completa los campos obligatorios.", Alert.AlertType.WARNING);
            return;
        }

        // Validar longitud del código postal (máximo 5 caracteres)
        if (null != cp && !cp.isEmpty() && cp.length() > 5) {
            mostrarAlerta("Código Postal inválido", "El código postal debe tener máximo 5 caracteres.", Alert.AlertType.WARNING);
            return;
        }

        // Si la sede es null, significa que se crea una nueva
        if (sede == null) {
            // Crear objeto Sede con los datos ingresados
            Sede nueva = new Sede(nombre, calle, localidad, cp, municipio, provincia, telefono, codigoCentro);
            
            // Insertar la nueva sede usando DAO
            boolean inserted = sedeDAO.insertarSede(nueva);
            
            // Si se insertó correctamente, mostrar mensaje y cerrar ventana
            if (inserted) {
                mostrarAlerta("Éxito", "Sede agregada correctamente.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            }
        } else {
            // Si la sede existe, actualizar sus atributos con los valores nuevos
            sede.setNombre(nombre);
            sede.setCalle(calle);
            sede.setLocalidad(localidad);
            sede.setCp(cp);
            sede.setMunicipio(municipio);
            sede.setProvincia(provincia);
            sede.setTelefono(telefono);
            sede.setCodigoCentro(codigoCentro);

            // Actualizar la sede en la base de datos
            boolean updated = sedeDAO.actualizarSede(sede);
            
            // Si se actualizó correctamente, mostrar mensaje y cerrar ventana
            if (updated) {
                mostrarAlerta("Éxito", "Sede actualizada correctamente.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            }
        }
    }

    // Método para manejar el evento del botón cancelar
    @FXML
    private void btnActionCancelar() {
        cerrarVentana();
    }

    // Cierra la ventana actual
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    // Muestra una alerta con título, mensaje y tipo especificados
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
