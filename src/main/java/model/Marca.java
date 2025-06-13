package model;

// Clase que representa una Marca con código y nombre
public class Marca {
    // Atributos privados para encapsulación
    private int codigo;    // Código identificador único de la marca
    private String nombre; // Nombre de la marca

    // Constructor que inicializa el código y el nombre
    public Marca(int codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    // Método getter para obtener el código de la marca
    public int getCodigo() {
        return codigo;
    }

    // Método getter para obtener el nombre de la marca
    public String getNombre() {
        return nombre;
    }

    // Método setter para cambiar el nombre de la marca
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Método setter para cambiar el código de la marca
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }
}
