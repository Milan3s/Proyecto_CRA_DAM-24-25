package model;

public class Marca {
// atrbutos privados
    private int codigo;
    private String nombre;

    // constructor
    public Marca(int codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public int getCodigo() { //devuelve el valor
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) { // cambia el valor
        this.nombre = nombre;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }
}