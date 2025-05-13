package model;

import utils.DataBaseConection;
import utils.LoggerUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgramasEduDAO {

    private Connection conn;

    public ProgramasEduDAO() {
        conn = DataBaseConection.getConnection();
    }

    public List<ProgramasEdu> obtenerProgramas() {
        List<ProgramasEdu> lista = new ArrayList<>();
        String query = "SELECT codigo_programa, nombre FROM programas_edu";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                ProgramasEdu p = new ProgramasEdu(rs.getInt("codigo_programa"), rs.getString("nombre"));
                lista.add(p);
            }
        } catch (SQLException e) {
            LoggerUtils.logError("PROGRAMAS_EDU", "Error al obtener programas", e);
        } finally {
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    public boolean insertarPrograma(String nombre) {
        String query = "INSERT INTO programas_edu (nombre) VALUES (?)";
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(query);
            stmt.setString(1, nombre);

            int filas = stmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            LoggerUtils.logError("PROGRAMAS_EDU", "Error al insertar programa", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return false;
    }

    public boolean actualizarPrograma(int codigo, String nombre) {
        String query = "UPDATE programas_edu SET nombre = ? WHERE codigo_programa = ?";
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(query);
            stmt.setString(1, nombre);
            stmt.setInt(2, codigo);

            int filas = stmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            LoggerUtils.logError("PROGRAMAS_EDU", "Error al actualizar programa", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return false;
    }

    public boolean eliminarPrograma(int codigo) {
        String query = "DELETE FROM programas_edu WHERE codigo_programa = ?";
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, codigo);

            int filas = stmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            LoggerUtils.logError("PROGRAMAS_EDU", "Error al eliminar programa", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return false;
    }

    public int eliminarTodosProgramas() {
        String query = "DELETE FROM programas_edu";
        PreparedStatement stmt = null;
        int filasEliminadas = 0;

        try {
            stmt = conn.prepareStatement(query);
            filasEliminadas = stmt.executeUpdate();
        } catch (SQLException e) {
            LoggerUtils.logError("PROGRAMAS_EDU", "Error al eliminar todos los programas", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return filasEliminadas;
    }

    public List<ProgramasEdu> buscarProgramas(String filtro) {
        List<ProgramasEdu> lista = new ArrayList<>();
        String query = "SELECT codigo_programa, nombre FROM programas_edu WHERE nombre LIKE ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + filtro + "%");

            rs = stmt.executeQuery();

            while (rs.next()) {
                ProgramasEdu p = new ProgramasEdu(rs.getInt("codigo_programa"), rs.getString("nombre"));
                lista.add(p);
            }
        } catch (SQLException e) {
            LoggerUtils.logError("PROGRAMAS_EDU", "Error al buscar programas", e);
        } finally {
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }
}