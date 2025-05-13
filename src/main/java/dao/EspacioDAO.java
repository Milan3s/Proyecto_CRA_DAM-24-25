package dao;

import utils.DataBaseConection;
import utils.LoggerUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Espacio;
import model.Sede;

public class EspacioDAO {

    private Connection conn;

    public EspacioDAO() {
        conn = DataBaseConection.getConnection();
    }

    // Obtiene la lista de sedes para el ComboBox
    public List<Sede> obtenerSedes() {
        List<Sede> listaSedes = new ArrayList<>();
        String query = "SELECT codigo_sede, nombre FROM sedes";
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

    // Lista todos los espacios (incluyendo nombre de sede)
    public List<Espacio> obtenerEspacios() {
        List<Espacio> listaEspacios = new ArrayList<>();
        String query = "SELECT e.codigo_espacio, e.nombre, e.pabellon, e.planta, e.codigo_sede, s.nombre AS nombre_sede "
                + "FROM espacios e JOIN sedes s ON e.codigo_sede = s.codigo_sede";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Espacio espacio = new Espacio(
                        rs.getInt("codigo_espacio"),
                        rs.getString("nombre"),
                        rs.getString("pabellon"),
                        rs.getInt("planta"),
                        rs.getInt("codigo_sede"),
                        rs.getString("nombre_sede")
                );
                listaEspacios.add(espacio);
            }
        } catch (SQLException e) {
            LoggerUtils.logError("ESPACIOS", "Error al obtener espacios", e);
        }

        return listaEspacios;
    }

    // Inserta un nuevo espacio
    public boolean insertarEspacio(String nombre, String pabellon, int planta, int codigoSede) {
        String sql = "INSERT INTO espacios (nombre, pabellon, planta, codigo_sede) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            LoggerUtils.logQuery("ESPACIOS", "Insertando espacio",
                    String.format("QUERY: %s | VALUES: nombre=%s, pabellon=%s, planta=%d, codigo_sede=%d",
                            sql, nombre, pabellon, planta, codigoSede));

            stmt.setString(1, nombre);
            stmt.setString(2, pabellon);
            stmt.setInt(3, planta);
            stmt.setInt(4, codigoSede);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                LoggerUtils.logInfo("ESPACIOS", "Espacio insertado correctamente → " + nombre);
                return true;
            }
        } catch (SQLException e) {
            LoggerUtils.logError("ESPACIOS", "Error al insertar espacio", e);
        }
        return false;
    }

    // Actualiza un espacio existente
    public boolean actualizarEspacio(int codigoEspacio, String nombre, String pabellon, int planta, int codigoSede) {
        String sql = "UPDATE espacios SET nombre = ?, pabellon = ?, planta = ?, codigo_sede = ? WHERE codigo_espacio = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, pabellon);
            stmt.setInt(3, planta);
            stmt.setInt(4, codigoSede);
            stmt.setInt(5, codigoEspacio);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                LoggerUtils.logInfo("ESPACIOS", "Espacio actualizado correctamente → Código: " + codigoEspacio);
                return true;
            }
        } catch (SQLException e) {
            LoggerUtils.logError("ESPACIOS", "Error al actualizar espacio", e);
        }
        return false;
    }

    // Elimina un espacio por su código
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
            LoggerUtils.logError("ESPACIOS", "Error al eliminar espacio", e);
        }
        return false;
    }

    // Busca espacios por nombre, pabellón o planta (incluye nombre de sede)
    public List<Espacio> buscarEspacios(String filtro) {
        List<Espacio> listaFiltrada = new ArrayList<>();
        String sql = "SELECT e.codigo_espacio, e.nombre, e.pabellon, e.planta, e.codigo_sede, s.nombre AS nombre_sede "
                + "FROM espacios e JOIN sedes s ON e.codigo_sede = s.codigo_sede "
                + "WHERE e.nombre LIKE ? OR e.pabellon LIKE ? OR e.planta LIKE ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String likeFiltro = "%" + filtro + "%";
            stmt.setString(1, likeFiltro);
            stmt.setString(2, likeFiltro);
            stmt.setString(3, likeFiltro);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Espacio espacio = new Espacio(
                        rs.getInt("codigo_espacio"),
                        rs.getString("nombre"),
                        rs.getString("pabellon"),
                        rs.getInt("planta"),
                        rs.getInt("codigo_sede"),
                        rs.getString("nombre_sede")
                );
                listaFiltrada.add(espacio);
            }

            rs.close();
        } catch (SQLException e) {
            LoggerUtils.logError("ESPACIOS", "Error al buscar espacios", e);
        }

        return listaFiltrada;
    }
}
