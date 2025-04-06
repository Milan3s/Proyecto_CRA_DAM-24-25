package model;

public class AgregarAlumnos {

    private int codigo;
    private String nombre;
    private String curso;
    private int codigo_sede;

    public AgregarAlumnos(int codigo, String nombre, String curso, int codigo_sede) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.curso = curso;
        this.codigo_sede = codigo_sede;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCurso() {
        return curso;
    }

    public int getCodigo_sede() {
        return codigo_sede;
    }
}
