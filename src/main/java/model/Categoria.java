package model;

// Clase que representa una categoría con código y nombre
public class Categoria {
    private int codigo;    // Código único identificador de la categoría
    private String nombre; // Nombre descriptivo de la categoría

    // Constructor que inicializa código y nombre
    public Categoria(int codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    // Constructor que inicializa solo el nombre (código puede ser asignado luego)
    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    // Método getter para obtener el código de la categoría
    public int getCodigo() {
        return codigo;
    }

    // Método getter para obtener el nombre de la categoría
    public String getNombre() {
        return nombre;
    }

    // Método setter para asignar/modificar el código de la categoría
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    // Método setter para asignar/modificar el nombre de la categoría
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
