package dao;


import utils.DataBaseConection; 
import utils.LoggerUtils;       
import java.sql.*;              
import java.util.ArrayList;
import java.util.List;
import model.Categoria;


public class CategoriaDAO {

    // Variable que guarda la conexión con la base de datos
    private Connection conn;

    // Constructor 
    public CategoriaDAO() {
        conn = DataBaseConection.getConnection(); // Obtenemos la conexión a la base de datos
    }

    // Método para obtener todas las categorías guardadas en la tabla
    public List<Categoria> obtenerCategorias() {
        List<Categoria> lista = new ArrayList<>(); // Creamos una lista vacía para guardar las categorías
        String query = "SELECT codigo_categoria, nombre FROM categorias"; // Consulta SQL que selecciona todos los registros
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement(); // Creamos un objeto para ejecutar la consulta
            rs = stmt.executeQuery(query); // Ejecutamos la consulta y guardamos el resultado

            // Recorremos los resultados obtenidos
            while (rs.next()) {
                // Creamos un nuevo objeto Categoria con los datos de la base
                Categoria c = new Categoria(rs.getInt("codigo_categoria"), rs.getString("nombre"));
                lista.add(c); // Lo agregamos a la lista
            }
        } catch (SQLException e) {
            // Si ocurre un error, lo registramos
            LoggerUtils.logError("CATEGORIAS", "Error al obtener categorias", e);
        } finally {
            // Cerramos los objetos que usamos para liberar memoria
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return lista; // Devolvemos la lista con las categorías
    }

    // Método para insertar una nueva categoría en la base de datos
    public boolean insertarCategoria(String nombre) {
        String query = "INSERT INTO categorias (nombre) VALUES (?)"; // Consulta con un parámetro (?)

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(query); // Preparamos la consulta
            stmt.setString(1, nombre);           // Reempazamos el ? con el valor que recibimos

            int filas = stmt.executeUpdate();    // Ejecutamos la consulta y guardamos cuántas filas fueron afectadas

            return filas > 0; // Si se insertó al menos una fila, devolvemos true
        } catch (SQLException e) {
            // Si hay un error al insertar, lo registramos
            LoggerUtils.logError("CATEGORIAS", "Error al insertar categoria" +e.getMessage(), e);
        } finally {
            // Cerramos el PreparedStatement
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return false; // Si llegamos aquí, hubo un problema y no se insertó
    }

    // Método para actualizar una categoría existente en la base de datos
    public boolean actualizarCategoria(int codigo, String nombre) {
        String query = "UPDATE categorias SET nombre = ? WHERE codigo_categoria = ?"; // Consulta para actualizar

        PreparedStatement stmt = null; // variable para hacer una consulta a la BD

        try {
            stmt = conn.prepareStatement(query); // Preparamos la consulta
            stmt.setString(1, nombre);           // Nuevo nombre
            stmt.setInt(2, codigo);              // Código de la categoría a modificar

            int filas = stmt.executeUpdate();    // Ejecutamos la actualización

            return filas > 0; // dice si ha habido alguna fila afectada 
        } catch (SQLException e) {
            LoggerUtils.logError("CATEGORIAS", "Error al actualizar categoria", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {} // intenta cerrarlo y si da error sigue adelante
        }

        return false; 
    }

   //// Método para eliminar una categoría existente en la base de datos
    public boolean eliminarCategoria(int codigo) {
        String query = "DELETE FROM categorias WHERE codigo_categoria = ?"; 

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(query);  // Preparamos la consulta
            stmt.setInt(1, codigo);// pone un numero en la primera posicion de la consulta              

            int filas = stmt.executeUpdate();  // ejecuta la acción y te dice cuantas filas se actualizaron  

            return filas > 0; // tiene que ser mayor que 0 
        } catch (SQLException e) { // lo agarra para que no se caiga el programa
            LoggerUtils.logError("CATEGORIAS", "Error al eliminar categoria", e);
        } finally { // se ejecuta esto de error o no
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {} // intenta cerrar el recurso y si da error se ignora
        }

        return false;
    }

    
    public int eliminarTodasCategorias() {
        String query = "DELETE FROM categorias"; 
        PreparedStatement stmt = null;
        int filasEliminadas = 0;

        try {
            stmt = conn.prepareStatement(query); 
            filasEliminadas = stmt.executeUpdate(); 
        } catch (SQLException e) {
            LoggerUtils.logError("CATEGORIAS", "Error al eliminar todas las categorias", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return filasEliminadas; 
    }

    
    public List<Categoria> buscarCategorias(String filtro) {
        List<Categoria> lista = new ArrayList<>();
        String query = "SELECT codigo_categoria, nombre FROM categorias WHERE nombre LIKE ?"; // Consulta con filtro

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement(query); 
            stmt.setString(1, "%" + filtro + "%"); 

            rs = stmt.executeQuery(); 

            while (rs.next()) {
                Categoria c = new Categoria(rs.getInt("codigo_categoria"), rs.getString("nombre")); 
                lista.add(c); 
            }
        } catch (SQLException e) {
            LoggerUtils.logError("CATEGORIAS", "Error al buscar categorias", e);
        } finally {
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return lista; 
    }
}