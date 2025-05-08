package model;

import utils.DataBaseConection;
import utils.LoggerUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CentroEducativoDAO {

    private Connection conn;

    public CentroEducativoDAO() {
        conn = DataBaseConection.getConnection();
    }

    public List<CentroEducativo> obtenerCentros() {
        List<CentroEducativo> listaCentros = new ArrayList<>();
        String query = "SELECT * FROM centros_edu";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                CentroEducativo centro = new CentroEducativo(
                        rs.getString("codigo_centro"),
                        rs.getString("nombre"),
                        rs.getString("calle"),
                        rs.getString("localidad"),
                        rs.getString("cp"),
                        rs.getString("municipio"),
                        rs.getString("provincia"),
                        rs.getString("telefono"),
                        rs.getString("email")
                );
                listaCentros.add(centro);
                LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Centro cargado → Código: " + centro.getCodigoCentro());
            }
        } catch (SQLException e) {
            LoggerUtils.logError("CENTROS EDUCATIVOS", "Error al cargar centros", e);
        } finally {
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return listaCentros;
    }

    public boolean insertarCentro(CentroEducativo centro) {
        String sql = "INSERT INTO centros_edu (codigo_centro, nombre, calle, localidad, cp, municipio, provincia, telefono, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, centro.getCodigoCentro());
            stmt.setString(2, centro.getNombre());
            stmt.setString(3, centro.getCalle());
            stmt.setString(4, centro.getLocalidad());
            stmt.setString(5, centro.getCp());
            stmt.setString(6, centro.getMunicipio());
            stmt.setString(7, centro.getProvincia());
            stmt.setString(8, centro.getTelefono());
            stmt.setString(9, centro.getEmail());

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Centro insertado correctamente → Código: " + centro.getCodigoCentro());
                return true;
            }
        } catch (SQLException e) {
            LoggerUtils.logError("CENTROS EDUCATIVOS", "Error al insertar centro", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }
        return false;
    }

    public boolean actualizarCentro(CentroEducativo centro) {
        String sql = "UPDATE centros_edu SET nombre=?, calle=?, localidad=?, cp=?, municipio=?, provincia=?, telefono=?, email=? WHERE codigo_centro=?";
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, centro.getNombre());
            stmt.setString(2, centro.getCalle());
            stmt.setString(3, centro.getLocalidad());
            stmt.setString(4, centro.getCp());
            stmt.setString(5, centro.getMunicipio());
            stmt.setString(6, centro.getProvincia());
            stmt.setString(7, centro.getTelefono());
            stmt.setString(8, centro.getEmail());
            stmt.setString(9, centro.getCodigoCentro());

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Centro actualizado correctamente → Código: " + centro.getCodigoCentro());
                return true;
            }
        } catch (SQLException e) {
            LoggerUtils.logError("CENTROS EDUCATIVOS", "Error al actualizar centro", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return false;
    }

    public boolean eliminarCentro(String codigoCentro) {
        String sql = "DELETE FROM centros_edu WHERE codigo_centro=?";
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, codigoCentro);
            int filas = stmt.executeUpdate();

            if (filas > 0) {
                LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Centro eliminado → Código: " + codigoCentro);
                return true;
            }
        } catch (SQLException e) {
            LoggerUtils.logError("CENTROS EDUCATIVOS", "Error al eliminar centro", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return false;
    }

    public int eliminarTodosCentros() {
        String sql = "DELETE FROM centros_edu";
        PreparedStatement stmt = null;
        int filas = 0;

        try {
            stmt = conn.prepareStatement(sql);
            filas = stmt.executeUpdate();
            LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Centros eliminados: " + filas);
        } catch (SQLException e) {
            LoggerUtils.logError("CENTROS EDUCATIVOS", "Error al eliminar todos los centros", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return filas;
    }

    public List<CentroEducativo> buscarCentros(String filtro) {
        List<CentroEducativo> listaCentros = new ArrayList<>();
        String query = "SELECT * FROM centros_edu WHERE nombre LIKE ? OR localidad LIKE ? OR municipio LIKE ? OR provincia LIKE ? OR codigo_centro LIKE ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement(query);
            String searchTerm = "%" + filtro + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            stmt.setString(3, searchTerm);
            stmt.setString(4, searchTerm);
            stmt.setString(5, searchTerm);

            rs = stmt.executeQuery();
            while (rs.next()) {
                CentroEducativo centro = new CentroEducativo(
                        rs.getString("codigo_centro"),
                        rs.getString("nombre"),
                        rs.getString("calle"),
                        rs.getString("localidad"),
                        rs.getString("cp"),
                        rs.getString("municipio"),
                        rs.getString("provincia"),
                        rs.getString("telefono"),
                        rs.getString("email")
                );
                listaCentros.add(centro);
                LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Centro filtrado → Código: " + centro.getCodigoCentro());
            }
        } catch (SQLException e) {
            LoggerUtils.logError("CENTROS EDUCATIVOS", "Error al buscar centros", e);
        } finally {
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return listaCentros;
    }
}
