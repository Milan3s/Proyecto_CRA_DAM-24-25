package model;

/**
 * Modelo que representa una sede educativa. Se utiliza para asignar alumnos y
 * mostrar información asociada a centros.
 */
public class Sede {

    // ===================
    // Atributos privados
    // ===================
    private int codigoSede;  // Identificador único de la sede (clave primaria)
    private String nombre;   // Nombre descriptivo de la sede

    // ===================
    // Constructor
    // ===================
    /**
     * Constructor principal que inicializa una sede con su código y nombre.
     *
     * @param codigoSede Código único de la sede
     * @param nombre Nombre de la sede
     */
    public Sede(int codigoSede, String nombre) {
        this.codigoSede = codigoSede;
        this.nombre = nombre;
    }

    // ===================
    // Getters
    // ===================
    /**
     * Devuelve el código único de la sede.
     *
     * @return código de la sede
     */
    public int getCodigoSede() {
        return codigoSede;
    }

    /**
     * Devuelve el nombre de la sede.
     *
     * @return nombre de la sede
     */
    public String getNombre() {
        return nombre;
    }

    // ===================
    // toString sobrescrito
    // ===================
    /**
     * Devuelve una representación en texto de la sede. Se usa al mostrarla en
     * ComboBox, listas, etc.
     *
     * @return nombre y código de la sede en formato legible
     */
    @Override
    public String toString() {
        return nombre + " (ID: " + codigoSede + ")";
    }
}
