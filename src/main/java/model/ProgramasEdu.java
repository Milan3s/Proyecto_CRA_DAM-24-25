package model;

public class ProgramasEdu {
 //atributos priv.
    private int codigo;
    private String nombre;

    public ProgramasEdu(int codigo, String nombre) { // constructor
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public int getCodigo() {
        return codigo; //devuelve el valor
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre; // cambia el valor
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }
}
