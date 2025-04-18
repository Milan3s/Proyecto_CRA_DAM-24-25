package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class DashboardController {

    private static DashboardController instance;

    @FXML
    private Label userNameLabel;

    @FXML
    private StackPane panelContenido;

    @FXML
    private BorderPane rootPane; // Asegúrate de que el root de tu FXML tenga fx:id="rootPane"

    public DashboardController() {
        instance = this;
    }

    public static DashboardController getInstance() {
        return instance;
    }

    public StackPane getPanelContenido() {
        return panelContenido;
    }

    public void setUserName(String userName) {
        if (userNameLabel != null) {
            userNameLabel.setText("Bienvenido, " + userName + "!" + "\nPulsa en los nombres para cambiar las vistas"
            );
            
        }

        // Cargar el sidebar manualmente y pasarlo al panel izquierdo
        try {
            FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/views/Sidebar.fxml"));
            Node sidebar = sidebarLoader.load();

            SidebarController sidebarController = sidebarLoader.getController();
            sidebarController.setNombreUsuario(userName);

            rootPane.setLeft(sidebar); // ⚠️ Asegúrate que el BorderPane tenga fx:id="rootPane"

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
