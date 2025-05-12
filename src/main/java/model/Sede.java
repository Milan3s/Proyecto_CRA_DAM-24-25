package model;

public class Sede {

    private int codigoSede;
    private String nombre;
    private String calle;
    private String localidad;
    private String cp;
    private String municipio;
    private String provincia;
    private String telefono;
    private int codigoCentro;

    // Constructor completo (con código de sede)
    public Sede(int codigoSede, String nombre, String calle, String localidad, String cp,
                String municipio, String provincia, String telefono, int codigoCentro) {
        this.codigoSede = codigoSede;
        this.nombre = nombre;
        this.calle = calle;
        this.localidad = localidad;
        this.cp = cp;
        this.municipio = municipio;
        this.provincia = provincia;
        this.telefono = telefono;
        this.codigoCentro = codigoCentro;
    }

    // Constructor sin código de sede (para insertar nuevas sedes)
    public Sede(String nombre, String calle, String localidad, String cp, String municipio,
                String provincia, String telefono, int codigoCentro) {
        this.nombre = nombre;
        this.calle = calle;
        this.localidad = localidad;
        this.cp = cp;
        this.municipio = municipio;
        this.provincia = provincia;
        this.telefono = telefono;
        this.codigoCentro = codigoCentro;
    }

    // Constructor reducido (código y nombre)
    public Sede(int codigoSede, String nombre) {
        this.codigoSede = codigoSede;
        this.nombre = nombre;
    }

    // Getters
    public int getCodigoSede() { return codigoSede; }
    public String getNombre() { return nombre; }
    public String getCalle() { return calle; }
    public String getLocalidad() { return localidad; }
    public String getCp() { return cp; }
    public String getMunicipio() { return municipio; }
    public String getProvincia() { return provincia; }
    public String getTelefono() { return telefono; }
    public int getCodigoCentro() { return codigoCentro; }

    // Setters
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCalle(String calle) { this.calle = calle; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }
    public void setCp(String cp) { this.cp = cp; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }
    public void setProvincia(String provincia) { this.provincia = provincia; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setCodigoCentro(int codigoCentro) { this.codigoCentro = codigoCentro; }

    @Override
    public String toString() {
        return nombre;
    }
}
