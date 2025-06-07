package model;

import java.sql.Date;

/**
 * Clase que representa un dispositivo.
 * 
 */
public class Dispositivo {
    private int codigo;
    private String nombre;
    private String modelo;
    private String num_serie;
    private Date fecha_adquisicion;
    private String mac;
    private String imei;
    private String comentario;
    private int num_etiqueta;
    private Proveedor proveedor;
    private Categoria categoria;
    private Marca marca;
    private Espacio espacio;
    private ProgramasEdu programae;
    private boolean prestado;
    private String observaciones;
    
    // Alumno actual que tiene el dispositivo prestado (solo para mostrar en el TableView)
    private Alumno alumno;
    
    // Sede, solo para mostrar en el tableView
    private Sede sede;

    /**
     * Constructor para crear un dispositivo con todos sus atributos.
     * 
     * @param codigo                Identificador único
     * @param nombre
     * @param modelo
     * @param num_serie
     * @param fecha_adquisicion
     * @param mac
     * @param imei
     * @param num_etiqueta          Etiqueta identificativa física que el usuario coloca en ciertos dispositivos
     * @param proveedor
     * @param alumno
     * @param comentario            Asociado habitualmente al proceso de recepción física del dispositivo
     * @param categoria
     * @param marca
     * @param espacio               Aula o sala donde puede estar ubicado el dispositivo
     * @param programae             Programa educativo en el que puede estar enmarcado, como p. ej. Escuela 4.0, Aula Edit, etc.
     * @param sede
     * @param prestado              Indica si el dispositivo está prestado o no.
     * @param observaciones         Observaciones varias
     */
    public Dispositivo(int codigo, String nombre, String modelo, String num_serie, Date fecha_adquisicion, String mac, String imei, int num_etiqueta
        , Proveedor proveedor, Alumno alumno, String comentario, Categoria categoria, Marca marca, Espacio espacio, ProgramasEdu programae, Sede sede
        , boolean prestado, String observaciones) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.modelo = modelo;
        this.num_serie = num_serie;
        this.fecha_adquisicion = fecha_adquisicion;
        this.mac = mac;
        this.imei = imei;
        this.num_etiqueta = num_etiqueta;
        this.proveedor = proveedor;
        this.alumno = alumno;
        this.comentario = comentario;
        this.categoria = categoria;
        this.marca = marca;
        this.espacio = espacio;
        this.programae = programae;
        this.sede = sede;
        this.prestado = prestado;
        this.observaciones = observaciones;
    }
    
    /**
     * Constructor para crear un dispositivo con sus atributos básicos.
     * 
     * @param codigo
     * @param nombre
     * @param modelo
     * @param num_serie
     * @param imei
     * @param num_etiqueta
     * @param categoria
     * @param marca 
     */
    public Dispositivo(int codigo, String nombre, String modelo, String num_serie, String imei, int num_etiqueta, Categoria categoria, Marca marca) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.modelo = modelo;
        this.num_serie = num_serie;
        this.imei = imei;
        this.num_etiqueta = num_etiqueta;
        this.categoria = categoria;
        this.marca = marca;
    }
    
    /**
     * Constructor para crear un dispositivo solo con su nombre.
     * 
     * @param codigo
     * @param nombre 
     */
    public Dispositivo(int codigo, String nombre) {
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

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getNum_serie() {
        return num_serie;
    }

    public void setNum_serie(String num_serie) {
        this.num_serie = num_serie;
    }

    public Date getFecha_adquisicion() {
        return fecha_adquisicion;
    }

    public void setFecha_adquisicion(Date fecha_adquisicion) {
        this.fecha_adquisicion = fecha_adquisicion;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public int getNum_etiqueta() {
        return num_etiqueta;
    }

    public void setNum_etiqueta(int num_etiqueta) {
        this.num_etiqueta = num_etiqueta;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public Espacio getEspacio() {
        return espacio;
    }

    public void setEspacio(Espacio espacio) {
        this.espacio = espacio;
    }

    public ProgramasEdu getProgramae() {
        return programae;
    }

    public void setProgramae(ProgramasEdu programae) {
        this.programae = programae;
    }

    public Sede getSede() {
        return sede;
    }

    public void setSede(Sede sede) {
        this.sede = sede;
    }

    public boolean isPrestado() {
        return prestado;
    }

    public void setPrestado(boolean prestado) {
        this.prestado = prestado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
