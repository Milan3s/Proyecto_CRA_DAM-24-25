package model;

public class Alumno {
    private int codigo;
    private String nombre;
    private String curso;
    private String nombreSede;
    private int codigo_sede;

    public Alumno(int codigo, String nombre, String curso, String nombreSede, int codigo_sede) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.curso = curso;
        this.nombreSede = nombreSede;
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

    public String getNombreSede() {
        return nombreSede;
    }

    public int getCodigo_sede() {
        return codigo_sede;
    }
}
