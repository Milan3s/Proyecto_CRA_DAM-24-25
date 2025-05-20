package model;

/**
 * Modelo que representa a un alumno en el sistema. Contiene datos como nombre,
 * curso y la sede a la que pertenece.
 */
public class Alumno {

    // ===================
    // Atributos privados
    // ===================
    private int codigo;            // Identificador único del alumno (clave primaria)
    private String nombre;         // Nombre completo del alumno
    private String curso;          // Curso actual del alumno
    private String nombreSede;     // Nombre de la sede (usado para mostrar en tabla)
    private int codigo_sede;       // Código de la sede (clave foránea)
    private String nre;            // nre del alumno

    // ===================
    // Constructor
    // ===================
    /**
     * Constructor principal para crear un alumno con todos los datos.
     *
     * @param codigo Código único del alumno
     * @param nombre Nombre del alumno
     * @param curso Curso actual
     * @param nombreSede Nombre de la sede (texto visible)
     * @param codigo_sede Código numérico de la sede (FK)
     */
    public Alumno(int codigo, String nombre, String curso, String nombreSede, int codigo_sede) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.curso = curso;
        this.nombreSede = nombreSede;
        this.codigo_sede = codigo_sede;
    }

    // ===================
    // Getters: obtienen los valores de los atributos
    // ===================
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
    
    public String getNre() {
        return nre;
    }

    // ===================
    // Setters: modifican los valores de los atributos
    // ===================
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public void setCodigo_sede(int codigo_sede) {
        this.codigo_sede = codigo_sede;
    }

    public void setNombreSede(String nombreSede) {
        this.nombreSede = nombreSede;
    }
    
    public void setNre(String nre) {
        this.nre = nre;
    }
}
