package model;

/**
 * Clase que representa un centro educativo con toda su información básica.
 * Se utiliza como modelo para las vistas y para operaciones en base de datos.
 */
public class CentroEducativo {

    // Atributos privados del centro educativo
    private String codigoCentro;  // Código identificador del centro (clave primaria)
    private String nombre;        // Nombre del centro educativo
    private String calle;         // Dirección: nombre de la calle
    private String localidad;     // Localidad del centro
    private String cp;            // Código postal
    private String municipio;     // Municipio del centro
    private String provincia;     // Provincia donde se ubica el centro
    private String telefono;      // Teléfono de contacto
    private String email;         // Correo electrónico del centro

    /**
     * Constructor principal para crear un centro educativo.
     *
     * @param codigoCentro Código identificador del centro
     * @param nombre Nombre del centro educativo
     * @param calle Calle donde está ubicado
     * @param localidad Localidad donde se encuentra
     * @param cp Código postal
     * @param municipio Municipio al que pertenece
     * @param provincia Provincia del centro
     * @param telefono Número de contacto
     * @param email Correo electrónico de contacto
     */
    public CentroEducativo(String codigoCentro, String nombre, String calle, String localidad, String cp,
                           String municipio, String provincia, String telefono, String email) {
        this.codigoCentro = codigoCentro;
        this.nombre = nombre;
        this.calle = calle;
        this.localidad = localidad;
        this.cp = cp;
        this.municipio = municipio;
        this.provincia = provincia;
        this.telefono = telefono;
        this.email = email;
    }

    // ===================
    // Getters: permiten leer los valores de los atributos
    // ===================

    public String getCodigoCentro() {
        return codigoCentro;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCalle() {
        return calle;
    }

    public String getLocalidad() {
        return localidad;
    }

    public String getCp() {
        return cp;
    }

    public String getMunicipio() {
        return municipio;
    }

    public String getProvincia() {
        return provincia;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    // ===================
    // Setters: permiten modificar los valores de los atributos
    // ===================

    public void setCodigoCentro(String codigoCentro) {
        this.codigoCentro = codigoCentro;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
