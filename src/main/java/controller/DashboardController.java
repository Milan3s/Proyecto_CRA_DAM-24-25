package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import main.Session;
import model.CentroEducativo;

/**
 * Controlador principal del panel de usuario (Dashboard).
 * Se encarga de mostrar el nombre del usuario y gestionar el panel lateral (sidebar) y el contenido principal.
 */
public class DashboardController {

    // Instancia estática para acceso global al controlador (singleton)
    private static DashboardController instance;

    // =====================
    // Referencias FXML
    // =====================

    @FXML
    private Label userNameLabel;          // Etiqueta para mostrar el nombre del usuario

    @FXML
    private StackPane panelContenido;     // Zona central donde se cargarán las vistas dinámicamente

    @FXML
    private BorderPane rootPane;          // Contenedor principal de la vista (estructura con zonas: top, left, center...)
    @FXML
    private Label labelUsuario;
    @FXML
    private Label labelCentro;
    @FXML
    private HBox top_bar;
    
    public void initialize() {
        Session.getInstance().setDashboardController(this);
    }

    // =====================
    // Constructor
    // =====================

    /**
     * Constructor: guarda una instancia estática del controlador para acceder desde otras clases.
     */
    public DashboardController() {
        instance = this;
    }

    /**
     * Devuelve la instancia actual del controlador (singleton).
     * @return instancia del DashboardController
     */
    public static DashboardController getInstance() {
        return instance;
    }

    /**
     * Devuelve el panel de contenido principal.
     * Este panel puede usarse para cargar otras vistas dinámicamente.
     * @return panelContenido (StackPane)
     */
    public StackPane getPanelContenido() {
        return panelContenido;
    }

    /**
     * Establece el nombre del usuario en la interfaz y carga el menú lateral (sidebar).
     * @param userName Nombre del usuario autenticado
     */
    public void setUserName(String userName) {
        // Mostrar saludo personalizado
        if (userNameLabel != null) {
            userNameLabel.setText("Bienvenido, " + userName + "!" + "\nPulsa en los nombres para cambiar las vistas");
            labelUsuario.setText("Usuario: " + userName);
        }
        
        mostrarCentroActivo(null);

        // Cargar la vista del menú lateral y asignarla al panel izquierdo del BorderPane
        try {
            FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/views/Sidebar.fxml"));
            Node sidebar = sidebarLoader.load();

            // Enviar el nombre del usuario al controlador del sidebar
            SidebarController sidebarController = sidebarLoader.getController();
            sidebarController.setNombreUsuario(userName);

            // Asignar el sidebar al panel izquierdo del layout principal
            rootPane.setLeft(sidebar);  // El FXML del dashboard debe tener fx:id="rootPane"

        } catch (Exception e) {
            e.printStackTrace();  // Muestra errores de carga si fallan
        }
    }
    
    /**
     * Muestra el Centro activo en la barra superior horizontal junto al usuario
     * @param centro 
     */
    public void mostrarCentroActivo (CentroEducativo centro) {
        String cadCentro = "Centro activo: ";
        String nombreCentro = "";
        
        if (null == centro) {
            nombreCentro = "No se ha establecido ningún Centro activo";
        } else {
            nombreCentro = centro.getNombre();
        }
        
        labelCentro.setText(cadCentro + nombreCentro);
    }
}
