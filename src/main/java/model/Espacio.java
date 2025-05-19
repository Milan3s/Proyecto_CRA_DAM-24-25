package model;

public class Espacio {

    private int codigoEspacio;
    private String nombre;
    private String pabellon;
    private int planta; // ✅ Cambiado a int
    private int codigoSede;
    private String nombreSede;
    private String numAbaco;

    public Espacio(int codigoEspacio, String nombre, String pabellon, int planta, int codigoSede, String nombreSede, String numAbaco) {
        this.codigoEspacio = codigoEspacio;
        this.nombre = nombre;
        this.pabellon = pabellon;
        this.planta = planta;
        this.codigoSede = codigoSede;
        this.nombreSede = nombreSede;
        this.numAbaco = numAbaco;
    }
    
    public Espacio(int codigoEspacio, String nombre) {
        this.codigoEspacio = codigoEspacio;
        this.nombre = nombre;
    }

    public int getCodigoEspacio() {
        return codigoEspacio;
    }

    public void setCodigoEspacio(int codigoEspacio) {
        this.codigoEspacio = codigoEspacio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPabellon() {
        return pabellon;
    }

    public void setPabellon(String pabellon) {
        this.pabellon = pabellon;
    }

    public int getPlanta() {
        return planta;
    }

    public void setPlanta(int planta) {
        this.planta = planta;
    }

    public int getCodigoSede() {
        return codigoSede;
    }

    public void setCodigoSede(int codigoSede) {
        this.codigoSede = codigoSede;
    }

    public String getNombreSede() {
        return nombreSede;
    }

    public void setNombreSede(String nombreSede) {
        this.nombreSede = nombreSede;
    }

    public String getNumAbaco() {
        return numAbaco;
    }

    public void setNumAbaco(String numAbaco) {
        this.numAbaco = numAbaco;
    }

    @Override
    public String toString() {
        return nombre + " - " + pabellon + " (Planta " + planta + ", Sede: " + nombreSede + " [" + codigoSede + "])";
    }
}
