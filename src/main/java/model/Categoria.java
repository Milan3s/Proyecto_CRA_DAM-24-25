package model;

public class Categoria {
// atributos privados
    private int codigo; 
    private String nombre;

    public Categoria(int codigo, String nombre) {// constructor
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public int getCodigo() {
        return codigo; // devuelve el valor del codigo
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCodigo(int codigo) { // cambia el valor del codigo
        this.codigo = codigo;
    }
}
