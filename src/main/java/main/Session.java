package main;

import controller.DashboardController;
import model.CentroEducativo;

public class Session {
    private static Session instance;
    private CentroEducativo centroActivo;
    private DashboardController dashboardController;
    
    private Session() {}
    
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }
    
    public CentroEducativo getCentroActivo() {
        return centroActivo;
    }

    public void setCentroActivo(CentroEducativo centroActivo) {
        this.centroActivo = centroActivo;
    }
    
    public void setDashboardController(DashboardController controller) {
        dashboardController = controller;
    }

    public void notificarCentro(CentroEducativo centro) {
        if (dashboardController != null) {
            dashboardController.mostrarCentroActivo(centro);
        }
    }
}
