package model;

/**
 * Modelo que representa a un alumno en el sistema. Contiene datos como nombre,
 * curso, NRE, teléfonos de tutores y la sede a la que pertenece.
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
    private String nre;            // NRE del alumno
    private String telTutor1;      // Teléfono del tutor 1
    private String telTutor2;      // Teléfono del tutor 2

    // ===================
    // Constructor
    // ===================
    /**
     * Constructor principal para crear un alumno con todos los datos excepto tutores.
     *
     * @param codigo Código único del alumno
     * @param nombre Nombre del alumno
     * @param curso Curso actual
     * @param nombreSede Nombre de la sede (texto visible)
     * @param codigo_sede Código numérico de la sede (FK)
     */
    public Alumno(int codigo, String nombre, String curso, String nombreSede, int codigo_sede, String nre) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.curso = curso;
        this.nombreSede = nombreSede;
        this.codigo_sede = codigo_sede;
        this.nre = nre;
    }
    
    public Alumno(int codigo, String nombre, String curso, String nombreSede, int codigo_sede) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.curso = curso;
        this.nombreSede = nombreSede;
        this.codigo_sede = codigo_sede;
    }
    
    /**
     * Constructor solo con código y nombre
     * 
     * @param codigo
     * @param nombre 
     */
    public Alumno(int codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    // ===================
    // Getters
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

    public String getTelTutor1() {
        return telTutor1;
    }

    public String getTelTutor2() {
        return telTutor2;
    }

    // ===================
    // Setters
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

    public void setTelTutor1(String telTutor1) {
        this.telTutor1 = telTutor1;
    }

    public void setTelTutor2(String telTutor2) {
        this.telTutor2 = telTutor2;
    }
}
