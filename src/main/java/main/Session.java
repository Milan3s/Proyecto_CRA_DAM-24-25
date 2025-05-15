package main;

import model.CentroEducativo;

public class Session {
    private static Session instance;
    private CentroEducativo centroActivo;
    
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
}
