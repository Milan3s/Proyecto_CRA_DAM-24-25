package dao;

import utils.DataBaseConection;
import utils.LoggerUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import main.Session;
import model.CentroEducativo;
import model.Espacio;
import model.Sede;

public class EspacioDAO {

    private Connection conn;
    private CentroEducativo centro;

    public EspacioDAO() {
        conn = DataBaseConection.getConnection();
        centro = Session.getInstance().getCentroActivo();
    }

    public List<Sede> obtenerSedes() {
        List<Sede> listaSedes = new ArrayList<>();
        String query = "SELECT codigo_sede, nombre FROM sedes";
        
        if (centro != null) {
            query += " WHERE codigo_centro = " + centro.getCodigoCentro();
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            LoggerUtils.logQuery("ESPACIOS", "Cargar sedes para ComboBox", query);

            while (rs.next()) {
                listaSedes.add(new Sede(rs.getInt("codigo_sede"), rs.getString("nombre")));
            }
        } catch (SQLException e) {
            LoggerUtils.logError("ESPACIOS", "Error al cargar sedes", e);
        }
        return listaSedes;
    }

    public List<Espacio> obtenerEspacios() {
        List<Espacio> listaEspacios = new ArrayList<>();
        String query = "SELECT e.codigo_espacio, e.nombre, e.pabellon, e.planta, e.codigo_sede, s.nombre AS nombre_sede, e.numero_abaco "
                     + "FROM espacios e JOIN sedes s ON e.codigo_sede = s.codigo_sede";
        
        if (centro != null) {
            query += " WHERE s.codigo_centro = " + centro.getCodigoCentro();
        }
        
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Espacio espacio = new Espacio(
                        rs.getInt("codigo_espacio"),
                        rs.getString("nombre"),
                        rs.getString("pabellon"),
                        rs.getInt("planta"),
                        rs.getInt("codigo_sede"),
                        rs.getString("nombre_sede"),
                        rs.getString("numero_abaco")
                );
                listaEspacios.add(espacio);
            }
        } catch (SQLException e) {
            LoggerUtils.logError("ESPACIOS", "Error al obtener espacios: " + e.getMessage(), e);
        }

        return listaEspacios;
    }

    public boolean insertarEspacio(String nombre, String pabellon, int planta, int codigoSede, String numeroAbaco) {
        String sql = "INSERT INTO espacios (nombre, pabellon, planta, codigo_sede, numero_abaco) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            LoggerUtils.logQuery("ESPACIOS", "Insertando espacio",
                    String.format("QUERY: %s | VALUES: nombre=%s, pabellon=%s, planta=%d, codigo_sede=%d, numero_abaco=%s",
                            sql, nombre, pabellon, planta, codigoSede, numeroAbaco));

            stmt.setString(1, nombre);
            stmt.setString(2, pabellon);
            stmt.setInt(3, planta);
            stmt.setInt(4, codigoSede);
            stmt.setString(5, numeroAbaco);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                LoggerUtils.logInfo("ESPACIOS", "Espacio insertado correctamente → " + nombre);
                return true;
            }
        } catch (SQLException e) {
            LoggerUtils.logError("ESPACIOS", "Error al insertar espacio: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean actualizarEspacio(int codigoEspacio, String nombre, String pabellon, int planta, int codigoSede, String numeroAbaco) {
        String sql = "UPDATE espacios SET nombre = ?, pabellon = ?, planta = ?, codigo_sede = ?, numero_abaco = ? WHERE codigo_espacio = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, pabellon);
            stmt.setInt(3, planta);
            stmt.setInt(4, codigoSede);
            stmt.setString(5, numeroAbaco);
            stmt.setInt(6, codigoEspacio);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                LoggerUtils.logInfo("ESPACIOS", "Espacio actualizado correctamente → Código: " + codigoEspacio);
                return true;
            }
        } catch (SQLException e) {
            LoggerUtils.logError("ESPACIOS", "Error al actualizar espacio: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean eliminarEspacio(int codigoEspacio) {
        String sql = "DELETE FROM espacios WHERE codigo_espacio = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, codigoEspacio);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                LoggerUtils.logInfo("ESPACIOS", "Espacio eliminado → Código: " + codigoEspacio);
                return true;
            }
        } catch (SQLException e) {
            LoggerUtils.logError("ESPACIOS", "Error al eliminar espacio: " + e.getMessage(), e);
        }
        return false;
    }

    public List<Espacio> buscarEspacios(String filtro) {
        List<Espacio> listaFiltrada = new ArrayList<>();
        String sql = "SELECT e.codigo_espacio, e.nombre, e.pabellon, e.planta, e.codigo_sede, s.nombre AS nombre_sede, e.numero_abaco "
                   + "FROM espacios e JOIN sedes s ON e.codigo_sede = s.codigo_sede "
                   + "WHERE e.nombre LIKE ? OR e.pabellon LIKE ? OR CAST(e.planta AS CHAR) LIKE ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String likeFiltro = "%" + filtro + "%";
            stmt.setString(1, likeFiltro);
            stmt.setString(2, likeFiltro);
            stmt.setString(3, likeFiltro);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Espacio espacio = new Espacio(
                            rs.getInt("codigo_espacio"),
                            rs.getString("nombre"),
                            rs.getString("pabellon"),
                            rs.getInt("planta"),
                            rs.getInt("codigo_sede"),
                            rs.getString("nombre_sede"),
                            rs.getString("numero_abaco")
                    );
                    listaFiltrada.add(espacio);
                }
            }
        } catch (SQLException e) {
            LoggerUtils.logError("ESPACIOS", "Error al buscar espacios: " + e.getMessage(), e);
        }

        return listaFiltrada;
    }
}
