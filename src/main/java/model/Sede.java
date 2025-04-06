package model;

public class Sede {
    private int codigoSede;
    private String nombre;

    public Sede(int codigoSede, String nombre) {
        this.codigoSede = codigoSede;
        this.nombre = nombre;
    }

    public int getCodigoSede() {
        return codigoSede;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre + " (ID: " + codigoSede + ")";
    }
}
