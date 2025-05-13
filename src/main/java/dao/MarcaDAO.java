package dao;

import utils.DataBaseConection; 
import utils.LoggerUtils;       
import java.sql.*;              
import java.util.ArrayList;
import java.util.List;
import model.Marca;

public class MarcaDAO {

    private Connection conn;

    public MarcaDAO() {
        conn = DataBaseConection.getConnection();
    }

    public List<Marca> obtenerMarcas() {
        List<Marca> lista = new ArrayList<>();
        String query = "SELECT codigo_marca, nombre FROM marcas";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                Marca m = new Marca(rs.getInt("codigo_marca"), rs.getString("nombre"));
                lista.add(m);
            }
        } catch (SQLException e) {
            LoggerUtils.logError("MARCAS", "Error al obtener marcas", e);
        } finally {
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    public boolean insertarMarca(String nombre) {
        String query = "INSERT INTO marcas (nombre) VALUES (?)";

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(query);
            stmt.setString(1, nombre);

            int filas = stmt.executeUpdate();

            return filas > 0;
        } catch (SQLException e) {
            LoggerUtils.logError("MARCAS", "Error al insertar marca", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return false;
    }

    public boolean actualizarMarca(int codigo, String nombre) {
        String query = "UPDATE marcas SET nombre = ? WHERE codigo_marca = ?";

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(query);
            stmt.setString(1, nombre);
            stmt.setInt(2, codigo);

            int filas = stmt.executeUpdate();

            return filas > 0;
        } catch (SQLException e) {
            LoggerUtils.logError("MARCAS", "Error al actualizar marca", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return false;
    }

    public boolean eliminarMarca(int codigo) {
        String query = "DELETE FROM marcas WHERE codigo_marca = ?";

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, codigo);

            int filas = stmt.executeUpdate();

            return filas > 0;
        } catch (SQLException e) {
            LoggerUtils.logError("MARCAS", "Error al eliminar marca", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return false;
    }

    public int eliminarTodasMarcas() {
        String query = "DELETE FROM marcas";
        PreparedStatement stmt = null;
        int filasEliminadas = 0;

        try {
            stmt = conn.prepareStatement(query);
            filasEliminadas = stmt.executeUpdate();
        } catch (SQLException e) {
            LoggerUtils.logError("MARCAS", "Error al eliminar todas las marcas", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return filasEliminadas;
    }

    public List<Marca> buscarMarcas(String filtro) {
        List<Marca> lista = new ArrayList<>();
        String query = "SELECT codigo_marca, nombre FROM marcas WHERE nombre LIKE ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + filtro + "%");

            rs = stmt.executeQuery();

            while (rs.next()) {
                Marca m = new Marca(rs.getInt("codigo_marca"), rs.getString("nombre"));
                lista.add(m);
            }
        } catch (SQLException e) {
            LoggerUtils.logError("MARCAS", "Error al buscar marcas", e);
        } finally {
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }
}