package model;

public class Proveedor {
    private int codigo;
    private String nombre;
    private String calle;
    private String localidad;
    private String cp;
    private String municipio;
    private String provincia;
    private String telefono;
    private String email;

    public Proveedor(int codigo, String nombre, String calle, String localidad, String cp, String municipio, String provincia, String telefono, String email) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.calle = calle;
        this.localidad = localidad;
        this.cp = cp;
        this.municipio = municipio;
        this.provincia = provincia;
        this.telefono = telefono;
        this.email = email;
    }

    public Proveedor(int codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }
    
    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /*
    @Override
    public String toString() {
        return nombre;
    }
    */
}
