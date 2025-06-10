package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class SidebarController {

    @FXML
    private Label labelUsuario;
    @FXML
    private Button btnDispositivos;
    @FXML
    private Button btnProveedores;
    @FXML
    private Button btnSedes;
    @FXML
    private Button btnEspacios;
    @FXML
    private Button btnCategorias;
    @FXML
    private Button btnMarcas;
    @FXML
    private Button btnProgramas;
    @FXML
    private Button btnPrestamos;
    @FXML
    private VBox sidebar;

    private Button botonActivo = null;
    
    // Método para establecer el nombre de usuario en la interfaz
    public void setNombreUsuario(String nombre) {
        labelUsuario.setText("");
    }

    // Método genérico para cargar contenido en el panel
    private void cambiarContenido(String rutaFXML, ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + rutaFXML));
            Node nodo = loader.load();

            // Obtener la instancia de DashboardController y cargar el contenido
            DashboardController dashboardController = DashboardController.getInstance();
            StackPane panelContenido = dashboardController.getPanelContenido();

            panelContenido.getChildren().setAll(nodo);
            
            // Se obtiene el botón pulsado desde el evento para establecer
            // su apariencia mediante la clase css
            Button botonPulsado = (Button) event.getSource();

            if (botonActivo != null) {
                botonActivo.getStyleClass().remove("sidebar-button-active");
            }
            
            botonPulsado.getStyleClass().add("sidebar-button-active");
            botonActivo = botonPulsado;
            
        } catch (IOException e) {
            // Si ocurre un error al cargar el contenido, registrarlo
            System.err.println("Error al cargar el archivo FXML: " + rutaFXML);
            e.printStackTrace();
        }
    }

    // Método para cargar la vista de "Usuarios"
    @FXML
    private void loadUsuarios(ActionEvent event) throws IOException {
        cambiarContenido("Alumnos.fxml", event);
    }

    // Método para cargar la vista de "Centro Educativo"
    @FXML
    private void loadCentroEducativo(ActionEvent event) throws IOException {
        cambiarContenido("CentroEducativo.fxml", event);
    }

    // Método para cargar la vista de "Dispositivos"
    @FXML
    private void loadDispositivos(ActionEvent event) throws IOException {
        cambiarContenido("Dispositivos.fxml", event);
    }

    // Método para cargar la vista de "Proveedores"
    @FXML
    private void loadProveedores(ActionEvent event) throws IOException {
        cambiarContenido("Proveedores.fxml", event);
    }

    @FXML
    private void loadSedes(ActionEvent event) throws IOException {
        cambiarContenido("Sede.fxml", event);
    }

    @FXML
    private void loadEspacios(ActionEvent event) throws IOException {
        cambiarContenido("Espacio.fxml", event);
    }

    @FXML
    private void loadCategorias(ActionEvent event) throws IOException {
        cambiarContenido("Categoria.fxml", event);
    }

    @FXML
    private void loadMarcas(ActionEvent event) throws IOException {
        cambiarContenido("Marca.fxml", event);
    }

    @FXML
    private void loadProgramas(ActionEvent event) throws IOException {
        cambiarContenido("ProgramasE.fxml", event);
    }

    @FXML
    private void loadPrestamos(ActionEvent event) throws IOException {
        cambiarContenido("Prestamos.fxml", event);
    }
}
