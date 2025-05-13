package model;

import utils.DataBaseConection;
import utils.LoggerUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SedeDAO {

    private Connection conn;

    public SedeDAO() {
        conn = DataBaseConection.getConnection();
    }

    
    public List<Sede> obtenerSede() {
        List<Sede> lista = new ArrayList<>();
        String query = "SELECT codigo_sede, nombre, calle, localidad, cp, municipio, provincia, telefono, codigo_centro FROM sedes";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                Sede sede = new Sede(
                        rs.getInt("codigo_sede"),
                        rs.getString("nombre"),
                        rs.getString("calle"),
                        rs.getString("localidad"),
                        rs.getString("cp"),
                        rs.getString("municipio"),
                        rs.getString("provincia"),
                        rs.getString("telefono"),
                        rs.getInt("codigo_centro")
                );
                lista.add(sede);
            }
        } catch (SQLException e) {
            LoggerUtils.logError("SEDES", "Error al obtener sedes", e);
        } finally {
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    public boolean insertarSede(String nombre, String calle, String localidad, String cp,
                                String municipio, String provincia, String telefono, int codigoCentro) {
        String sql = "INSERT INTO sedes (nombre, calle, localidad, cp, municipio, provincia, telefono, codigo_centro) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombre);
            stmt.setString(2, calle);
            stmt.setString(3, localidad);
            stmt.setString(4, cp);
            stmt.setString(5, municipio);
            stmt.setString(6, provincia);
            stmt.setString(7, telefono);
            stmt.setInt(8, codigoCentro);

            int filas = stmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            LoggerUtils.logError("SEDES", "Error al insertar sede", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return false;
    }

    // Nuevo método para insertar sede con un objeto Sede
    public boolean insertarSede(Sede sede) {
        return insertarSede(
            sede.getNombre(),
            sede.getCalle(),
            sede.getLocalidad(),
            sede.getCp(),
            sede.getMunicipio(),
            sede.getProvincia(),
            sede.getTelefono(),
            sede.getCodigoCentro()
        );
    }

    public boolean actualizarSede(int codigoSede, String nombre, String calle, String localidad, String cp,
                                  String municipio, String provincia, String telefono, int codigoCentro) {
        String sql = "UPDATE sedes SET nombre = ?, calle = ?, localidad = ?, cp = ?, municipio = ?, " +
                     "provincia = ?, telefono = ?, codigo_centro = ? WHERE codigo_sede = ?";
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombre);
            stmt.setString(2, calle);
            stmt.setString(3, localidad);
            stmt.setString(4, cp);
            stmt.setString(5, municipio);
            stmt.setString(6, provincia);
            stmt.setString(7, telefono);
            stmt.setInt(8, codigoCentro);
            stmt.setInt(9, codigoSede);

            int filas = stmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            LoggerUtils.logError("SEDES", "Error al actualizar sede", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return false;
    }

    // Nuevo método para actualizar sede con un objeto Sede
    public boolean actualizarSede(Sede sede) {
        return actualizarSede(
            sede.getCodigoSede(),
            sede.getNombre(),
            sede.getCalle(),
            sede.getLocalidad(),
            sede.getCp(),
            sede.getMunicipio(),
            sede.getProvincia(),
            sede.getTelefono(),
            sede.getCodigoCentro()
        );
    }

    public boolean eliminarSede(int codigo) {
        String sql = "DELETE FROM sedes WHERE codigo_sede = ?";
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, codigo);

            int filas = stmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            LoggerUtils.logError("SEDES", "Error al eliminar sede", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return false;
    }

    public int eliminarTodasSedes() {
        String sql = "DELETE FROM sedes";
        PreparedStatement stmt = null;
        int filas = 0;

        try {
            stmt = conn.prepareStatement(sql);
            filas = stmt.executeUpdate();
        } catch (SQLException e) {
            LoggerUtils.logError("SEDES", "Error al eliminar todas las sedes", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return filas;
    }

    public List<Sede> buscarSedes(String filtro) {
        List<Sede> lista = new ArrayList<>();
        String sql = "SELECT codigo_sede, nombre, calle, localidad, cp, municipio, provincia, telefono, codigo_centro " +
                     "FROM sedes WHERE nombre LIKE ? OR calle LIKE ? OR localidad LIKE ? OR municipio LIKE ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement(sql);
            String filtroLike = "%" + filtro + "%";
            stmt.setString(1, filtroLike);
            stmt.setString(2, filtroLike);
            stmt.setString(3, filtroLike);
            stmt.setString(4, filtroLike);

            rs = stmt.executeQuery();

            while (rs.next()) {
                Sede sede = new Sede(
                        rs.getInt("codigo_sede"),
                        rs.getString("nombre"),
                        rs.getString("calle"),
                        rs.getString("localidad"),
                        rs.getString("cp"),
                        rs.getString("municipio"),
                        rs.getString("provincia"),
                        rs.getString("telefono"),
                        rs.getInt("codigo_centro")
                );
                lista.add(sede);
            }
        } catch (SQLException e) {
            LoggerUtils.logError("SEDES", "Error al buscar sedes", e);
        } finally {
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    public List<Sede> obtenerSedes() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
