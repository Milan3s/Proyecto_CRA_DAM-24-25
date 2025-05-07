package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

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

    // Método para establecer el nombre de usuario en la interfaz
    public void setNombreUsuario(String nombre) {
        if (labelUsuario != null) {
            labelUsuario.setText("Usuario: " + nombre);
        } else {
            System.out.println("labelUsuario es null");
        }
    }

    // Método genérico para cargar contenido en el panel
    private void cambiarContenido(String rutaFXML) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + rutaFXML));
        Node nodo = loader.load();

        // Obtener la instancia de DashboardController y cargar el contenido
        DashboardController dashboardController = DashboardController.getInstance();
        StackPane panelContenido = dashboardController.getPanelContenido();

        panelContenido.getChildren().setAll(nodo);
    }

    // Método para cargar la vista de "Usuarios"
    @FXML
    private void loadUsuarios() throws IOException {
        cambiarContenido("Alumnos.fxml");
    }
    
    // Método para cargar la vista de "Centro Educativo"
    @FXML
    private void loadCentroEducativo(ActionEvent event) throws IOException {
        cambiarContenido("CentroEducativo.fxml");
    }

    // Método para cargar la vista de "Dispositivos"
    @FXML
    private void loadDispositivos(ActionEvent event) throws IOException {
        cambiarContenido("Dispositivos.fxml");
    }

    // Método para cargar la vista de "Proveedores"
    @FXML
    private void loadProveedores(ActionEvent event) throws IOException {
        cambiarContenido("Proveedores.fxml");
    }

    @FXML
    private void loadSedes(ActionEvent event) {
    }

    @FXML
    private void loadEspacios(ActionEvent event) {
    }

    @FXML
    private void loadCategorias(ActionEvent event) {
    }

    @FXML
    private void loadMarcas(ActionEvent event) {
    }

    @FXML
    private void loadProgramas(ActionEvent event) {
    }
}
