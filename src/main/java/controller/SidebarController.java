package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import javafx.event.ActionEvent;

public class SidebarController {

     @FXML
    private Label labelUsuario;

    public void setNombreUsuario(String nombre) {
        if (labelUsuario != null) {
            labelUsuario.setText("Usuario: " + nombre);
        } else {
            System.out.println("labelUsuario es null");
        }
    }

    @FXML
    private void loadUsuarios() throws IOException {
        cambiarContenido("Alumnos.fxml");
    }

    private void cambiarContenido(String rutaFXML) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + rutaFXML));
        Node nodo = loader.load();

        DashboardController dashboardController = DashboardController.getInstance();
        StackPane panelContenido = dashboardController.getPanelContenido();

        panelContenido.getChildren().setAll(nodo);
    }

    @FXML
    private void loadCentroEducativo(ActionEvent event) throws IOException {
        cambiarContenido("CentroEducativo.fxml");
    }
}
