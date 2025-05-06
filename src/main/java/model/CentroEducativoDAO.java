package model;

import utils.DataBaseConection;
import utils.LoggerUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CentroEducativoDAO {

    // Método para cargar todos los centros educativos desde la base de datos
    public List<CentroEducativo> obtenerCentros() {
        List<CentroEducativo> listaCentros = new ArrayList<>();
        String query = "SELECT * FROM centros_edu";

        try (Connection conn = DataBaseConection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
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
        }
        return listaCentros;
    }

    // Método para insertar un nuevo centro educativo en la base de datos
    public boolean insertarCentro(CentroEducativo centro) {
        String sql = "INSERT INTO centros_edu (codigo_centro, nombre, calle, localidad, cp, municipio, provincia, telefono, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        }
        return false;
    }

    // Método para actualizar un centro educativo en la base de datos
    public boolean actualizarCentro(CentroEducativo centro) {
        String sql = "UPDATE centros_edu SET nombre=?, calle=?, localidad=?, cp=?, municipio=?, provincia=?, telefono=?, email=? WHERE codigo_centro=?";
        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        }
        return false;
    }

    // Método para eliminar un centro educativo de la base de datos por su código
    public boolean eliminarCentro(String codigoCentro) {
        String sql = "DELETE FROM centros_edu WHERE codigo_centro=?";
        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigoCentro);
            int filas = stmt.executeUpdate();
            if (filas > 0) {
                LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Centro eliminado → Código: " + codigoCentro);
                return true;
            }
        } catch (SQLException e) {
            LoggerUtils.logError("CENTROS EDUCATIVOS", "Error al eliminar centro", e);
        }
        return false;
    }

    // Método para eliminar todos los centros educativos de la base de datos
    public int eliminarTodosCentros() {
        String sql = "DELETE FROM centros_edu";
        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            int filas = stmt.executeUpdate();
            LoggerUtils.logInfo("CENTROS EDUCATIVOS", "Centros eliminados: " + filas);
            return filas;
        } catch (SQLException e) {
            LoggerUtils.logError("CENTROS EDUCATIVOS", "Error al eliminar todos los centros", e);
        }
        return 0;
    }

    // Método para buscar centros educativos por un filtro
    public List<CentroEducativo> buscarCentros(String filtro) {
        List<CentroEducativo> listaCentros = new ArrayList<>();
        String query = "SELECT * FROM centros_edu WHERE nombre LIKE ? OR localidad LIKE ? OR municipio LIKE ? OR provincia LIKE ? OR codigo_centro LIKE ?";

        try (Connection conn = DataBaseConection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            String searchTerm = "%" + filtro + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            stmt.setString(3, searchTerm);
            stmt.setString(4, searchTerm);
            stmt.setString(5, searchTerm);

            try (ResultSet rs = stmt.executeQuery()) {
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
            }
        } catch (SQLException e) {
            LoggerUtils.logError("CENTROS EDUCATIVOS", "Error al buscar centros", e);
        }
        return listaCentros;
    }
}
