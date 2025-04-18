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
}
