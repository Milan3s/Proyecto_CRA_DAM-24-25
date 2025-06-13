package dao;

import utils.DataBaseConection;
import utils.LoggerUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Categoria;

public class CategoriaDAO {

    // Conexión a la base de datos
    private Connection conn;

    // Constructor que inicializa la conexión usando la clase utilitaria DataBaseConection
    public CategoriaDAO() {
        conn = DataBaseConection.getConnection();
    }

    // Método para obtener todas las categorías desde la base de datos
    public List<Categoria> obtenerCategorias() {
        List<Categoria> lista = new ArrayList<>(); // Lista para almacenar resultados
        String query = "SELECT codigo_categoria, nombre FROM categorias ORDER BY codigo_categoria ASC"; // Consulta SQL
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement(); // Crear statement
            rs = stmt.executeQuery(query); // Ejecutar consulta
            while (rs.next()) { // Iterar resultado
                // Crear objeto Categoria con datos de cada fila
                Categoria c = new Categoria(rs.getInt("codigo_categoria"), rs.getString("nombre"));
                lista.add(c); // Agregar a la lista
            }
        } catch (SQLException e) {
            // Loguear error si ocurre una excepción SQL
            LoggerUtils.logError("CATEGORIAS", "Error al obtener categorias", e);
        } finally {
            // Cerrar ResultSet y Statement para liberar recursos
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return lista; // Devolver lista con todas las categorías
    }

    // Método para insertar una categoría nueva dado solo el nombre
    public boolean insertarCategoria(String nombre) {
        String query = "INSERT INTO categorias (nombre) VALUES (?)"; // Consulta parametrizada para inserción
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(query); // Preparar statement
            stmt.setString(1, nombre); // Setear el parámetro nombre
            int filas = stmt.executeUpdate(); // Ejecutar y obtener número de filas afectadas
            return filas > 0; // Retorna true si insertó al menos una fila
        } catch (SQLException e) {
            // Loguear error si falla inserción
            LoggerUtils.logError("CATEGORIAS", "Error al insertar categoria: " + e.getMessage(), e);
        } finally {
            // Cerrar statement
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }
        return false; // Retorna false si hubo error
    }

    // ✅ NUEVO MÉTODO: inserta categoría usando un objeto Categoria
    public boolean insertarCategoria(Categoria categoria) {
        // Validar que el objeto no sea null y el nombre no esté vacío
        if (categoria == null || categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            LoggerUtils.logError("CATEGORIAS", "Categoría inválida o nombre vacío", null);
            return false;
        }

        String query = "INSERT INTO categorias (nombre) VALUES (?)";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(query);
            stmt.setString(1, categoria.getNombre()); // Usar nombre desde el objeto
            int filas = stmt.executeUpdate();
            return filas > 0; // True si se insertó
        } catch (SQLException e) {
            LoggerUtils.logError("CATEGORIAS", "Error al insertar categoría: " + e.getMessage(), e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }
        return false; // False si hubo error
    }

    // Método para actualizar el nombre de una categoría dado su código
    public boolean actualizarCategoria(int codigo, String nombre) {
        String query = "UPDATE categorias SET nombre = ? WHERE codigo_categoria = ?";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(query);
            stmt.setString(1, nombre); // Setear nuevo nombre
            stmt.setInt(2, codigo);     // Setear código de categoría a actualizar
            int filas = stmt.executeUpdate();
            return filas > 0; // True si actualizó al menos una fila
        } catch (SQLException e) {
            LoggerUtils.logError("CATEGORIAS", "Error al actualizar categoria", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }
        return false; // False si hubo error
    }

    // Método para eliminar una categoría según su código
    public boolean eliminarCategoria(int codigo) {
        String query = "DELETE FROM categorias WHERE codigo_categoria = ?";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, codigo); // Setear código a eliminar
            int filas = stmt.executeUpdate();
            return filas > 0; // True si eliminó al menos una fila
        } catch (SQLException e) {
            LoggerUtils.logError("CATEGORIAS", "Error al eliminar categoria", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }
        return false; // False si hubo error
    }

    // Método para eliminar todas las categorías de la tabla
    public int eliminarTodasCategorias() {
        String query = "DELETE FROM categorias";
        PreparedStatement stmt = null;
        int filasEliminadas = 0;
        try {
            stmt = conn.prepareStatement(query);
            filasEliminadas = stmt.executeUpdate(); // Ejecuta eliminación masiva y guarda número de filas afectadas
        } catch (SQLException e) {
            LoggerUtils.logError("CATEGORIAS", "Error al eliminar todas las categorias", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }
        return filasEliminadas; // Devuelve la cantidad de filas eliminadas
    }

    // Método para buscar categorías cuyo nombre contenga un filtro dado
    public List<Categoria> buscarCategorias(String filtro) {
        List<Categoria> lista = new ArrayList<>();
        String query = "SELECT codigo_categoria, nombre FROM categorias WHERE nombre LIKE ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + filtro + "%"); // Usar filtro con comodines para LIKE
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
        return lista; // Devuelve la lista con resultados que coinciden con el filtro
    }
}
