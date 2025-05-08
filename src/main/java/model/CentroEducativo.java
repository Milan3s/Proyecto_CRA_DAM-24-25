package model;

public class CentroEducativo {

    private String codigoCentro;
    private String nombre;
    private String calle;
    private String localidad;
    private String cp;
    private String municipio;
    private String provincia;
    private String telefono;
    private String email;

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

    // Getters
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

    // Setters
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
