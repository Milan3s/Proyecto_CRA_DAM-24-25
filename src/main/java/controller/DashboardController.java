package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

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
        }

        // Cargar la vista del menú lateral y asignarla al panel izquierdo del BorderPane
        try {
            FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/views/Sidebar.fxml"));
            Node sidebar = sidebarLoader.load();

            // Enviar el nombre del usuario al controlador del sidebar
            SidebarController sidebarController = sidebarLoader.getController();
            sidebarController.setNombreUsuario(userName);

            // Asignar el sidebar al panel izquierdo del layout principal
            rootPane.setLeft(sidebar);  // ⚠️ Asegúrate que el FXML del dashboard tiene fx:id="rootPane"

        } catch (Exception e) {
            e.printStackTrace();  // Muestra errores de carga si fallan
        }
    }
}
