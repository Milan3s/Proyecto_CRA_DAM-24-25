package model;

import java.sql.Date;

public class Dispositivo {
    private int codigo;
    private String nombre;
    private String modelo;
    private String num_serie;
    private Date fecha_adquisicion;
    private String mac;
    private String imei;
    private int num_etiqueta;
    private int codigo_categoria;
    private int codigo_marca;
    private int codigo_proveedor;
    private int codigo_alumno;
    private int codigo_programa;
    private int codigo_espacio;

    public Dispositivo(int codigo, String nombre, String modelo, String num_serie, Date fecha_adquisicion, String mac, String imei, int num_etiqueta, int codigo_categoria, int codigo_marca, int codigo_proveedor, int codigo_alumno, int codigo_programa, int codigo_espacio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.modelo = modelo;
        this.num_serie = num_serie;
        this.fecha_adquisicion = fecha_adquisicion;
        this.mac = mac;
        this.imei = imei;
        this.num_etiqueta = num_etiqueta;
        this.codigo_categoria = codigo_categoria;
        this.codigo_marca = codigo_marca;
        this.codigo_proveedor = codigo_proveedor;
        this.codigo_alumno = codigo_alumno;
        this.codigo_programa = codigo_programa;
        this.codigo_espacio = codigo_espacio;
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

    public int getCodigo_categoria() {
        return codigo_categoria;
    }

    public void setCodigo_categoria(int codigo_categoria) {
        this.codigo_categoria = codigo_categoria;
    }

    public int getCodigo_marca() {
        return codigo_marca;
    }

    public void setCodigo_marca(int codigo_marca) {
        this.codigo_marca = codigo_marca;
    }

    public int getCodigo_proveedor() {
        return codigo_proveedor;
    }

    public void setCodigo_proveedor(int codigo_proveedor) {
        this.codigo_proveedor = codigo_proveedor;
    }

    public int getCodigo_alumno() {
        return codigo_alumno;
    }

    public void setCodigo_alumno(int codigo_alumno) {
        this.codigo_alumno = codigo_alumno;
    }

    public int getCodigo_programa() {
        return codigo_programa;
    }

    public void setCodigo_programa(int codigo_programa) {
        this.codigo_programa = codigo_programa;
    }

    public int getCodigo_espacio() {
        return codigo_espacio;
    }

    public void setCodigo_espacio(int codigo_espacio) {
        this.codigo_espacio = codigo_espacio;
    }
}
