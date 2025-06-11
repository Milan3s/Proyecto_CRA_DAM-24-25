package dao;

import utils.DataBaseConection;
import utils.LoggerUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Categoria;

public class CategoriaDAO {

    private Connection conn;

    public CategoriaDAO() {
        conn = DataBaseConection.getConnection();
    }

    public List<Categoria> obtenerCategorias() {
        List<Categoria> lista = new ArrayList<>();
        String query = "SELECT codigo_categoria, nombre FROM categorias";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Categoria c = new Categoria(rs.getInt("codigo_categoria"), rs.getString("nombre"));
                lista.add(c);
            }
        } catch (SQLException e) {
            LoggerUtils.logError("CATEGORIAS", "Error al obtener categorias", e);
        } finally {
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    public boolean insertarCategoria(String nombre) {
        String query = "INSERT INTO categorias (nombre) VALUES (?)";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(query);
            stmt.setString(1, nombre);
            int filas = stmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            LoggerUtils.logError("CATEGORIAS", "Error al insertar categoria: " + e.getMessage(), e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }
        return false;
    }

    // ✅ NUEVO MÉTODO insertando con objeto Categoria
    public boolean insertarCategoria(Categoria categoria) {
        if (categoria == null || categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            LoggerUtils.logError("CATEGORIAS", "Categoría inválida o nombre vacío", null);
            return false;
        }

        String query = "INSERT INTO categorias (nombre) VALUES (?)";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(query);
            stmt.setString(1, categoria.getNombre());
            int filas = stmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            LoggerUtils.logError("CATEGORIAS", "Error al insertar categoría: " + e.getMessage(), e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }
        return false;
    }

    public boolean actualizarCategoria(int codigo, String nombre) {
        String query = "UPDATE categorias SET nombre = ? WHERE codigo_categoria = ?";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(query);
            stmt.setString(1, nombre);
            stmt.setInt(2, codigo);
            int filas = stmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            LoggerUtils.logError("CATEGORIAS", "Error al actualizar categoria", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }
        return false;
    }

    public boolean eliminarCategoria(int codigo) {
        String query = "DELETE FROM categorias WHERE codigo_categoria = ?";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, codigo);
            int filas = stmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            LoggerUtils.logError("CATEGORIAS", "Error al eliminar categoria", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
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
        String query = "SELECT codigo_categoria, nombre FROM categorias WHERE nombre LIKE ?";
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
