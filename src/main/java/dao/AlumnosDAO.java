package dao;

import utils.DataBaseConection;
import utils.LoggerUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import main.Session;
import model.Alumno;
import model.CentroEducativo;
import model.Sede;

public class AlumnosDAO {

    private Connection conn;
    private CentroEducativo centro;

    public AlumnosDAO() {
        conn = DataBaseConection.getConnection();
        centro = Session.getInstance().getCentroActivo();
    }

    public List<Sede> obtenerSedes() {
        List<Sede> listaSedes = new ArrayList<>();
        String query = "SELECT codigo_sede, nombre FROM sedes";
        
        if (centro != null) {
            query += " WHERE codigo_centro = " + centro.getCodigoCentro();
        }

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            LoggerUtils.logQuery("ALUMNOS", "Cargar sedes para el ComboBox", query);

            while (rs.next()) {
                Sede sede = new Sede(rs.getInt("codigo_sede"), rs.getString("nombre"));
                listaSedes.add(sede);
                LoggerUtils.logInfo("ALUMNOS", "Sede cargada → Código: " + sede.getCodigoSede() + ", Nombre: " + sede.getNombre());
            }

        } catch (SQLException e) {
            LoggerUtils.logError("ALUMNOS", "Error al cargar sedes", e);
        }

        return listaSedes;
    }

    public List<Alumno> obtenerAlumnos() {
        List<Alumno> listaAlumnos = new ArrayList<>();
        String query = "SELECT a.codigo_alumno, a.nombre, a.curso, a.nre, a.telefono_tutor1, a.telefono_tutor2, a.codigo_sede, s.nombre AS nombre_sede "
                     + "FROM alumnos a JOIN sedes s ON a.codigo_sede = s.codigo_sede";
        
        if (centro != null) {
            query += " WHERE s.codigo_centro = " + centro.getCodigoCentro();
        }

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Alumno alumno = new Alumno(
                        rs.getInt("codigo_alumno"),
                        rs.getString("nombre"),
                        rs.getString("curso"),
                        rs.getString("nombre_sede"),
                        rs.getInt("codigo_sede")
                );
                alumno.setNre(rs.getString("nre"));
                alumno.setTelTutor1(rs.getString("telefono_tutor1"));
                alumno.setTelTutor2(rs.getString("telefono_tutor2"));
                listaAlumnos.add(alumno);
            }

        } catch (SQLException e) {
            LoggerUtils.logError("ALUMNOS", "Error al cargar alumnos (verifica columnas nre y telefonos en la BBDD)", e);
        }

        return listaAlumnos;
    }

    public boolean insertarAlumno(String nombre, String curso, int codigoSede, String nre, String telTutor1, String telTutor2) {
        String insertSQL = "INSERT INTO alumnos (nombre, curso, codigo_sede, nre, telefono_tutor1, telefono_tutor2) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
            stmt.setString(1, nombre);
            stmt.setString(2, curso);
            stmt.setInt(3, codigoSede);
            stmt.setString(4, nre);
            stmt.setString(5, telTutor1);
            stmt.setString(6, telTutor2);

            int rows = stmt.executeUpdate();
            LoggerUtils.logInfo("ALUMNOS", "Alumno insertado: " + nombre + ", NRE: " + nre);
            return rows > 0;

        } catch (SQLException e) {
            LoggerUtils.logError("ALUMNOS", "Error al insertar alumno", e);
            return false;
        }
    }

    public boolean actualizarAlumno(int codigoAlumno, String nombre, String curso, int codigoSede, String nre, String telTutor1, String telTutor2) {
        String updateSQL = "UPDATE alumnos SET nombre = ?, curso = ?, codigo_sede = ?, nre = ?, telefono_tutor1 = ?, telefono_tutor2 = ? WHERE codigo_alumno = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updateSQL)) {
            stmt.setString(1, nombre);
            stmt.setString(2, curso);
            stmt.setInt(3, codigoSede);
            stmt.setString(4, nre);
            stmt.setString(5, telTutor1);
            stmt.setString(6, telTutor2);
            stmt.setInt(7, codigoAlumno);

            int rows = stmt.executeUpdate();
            LoggerUtils.logInfo("ALUMNOS", "Alumno actualizado: Código " + codigoAlumno);
            return rows > 0;

        } catch (SQLException e) {
            LoggerUtils.logError("ALUMNOS", "Error al actualizar alumno", e);
            return false;
        }
    }

    public boolean eliminarAlumno(int codigoAlumno) {
        String sql = "DELETE FROM alumnos WHERE codigo_alumno = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, codigoAlumno);
            int filas = stmt.executeUpdate();
            LoggerUtils.logInfo("ALUMNOS", "Alumno eliminado correctamente: " + codigoAlumno);
            return filas > 0;

        } catch (SQLException e) {
            LoggerUtils.logError("ALUMNOS", "Error al eliminar alumno", e);
            return false;
        }
    }

    public int eliminarTodosAlumnos() {
        String sql = "DELETE FROM alumnos";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int filas = stmt.executeUpdate();
            LoggerUtils.logInfo("ALUMNOS", "Alumnos eliminados: " + filas);
            return filas;

        } catch (SQLException e) {
            LoggerUtils.logError("ALUMNOS", "Error al eliminar todos los alumnos", e);
            return 0;
        }
    }

    public List<Alumno> buscarAlumnos(String filtro) {
        List<Alumno> listaFiltrada = new ArrayList<>();
        String query = "SELECT a.codigo_alumno, a.nombre, a.curso, a.nre, a.telefono_tutor1, a.telefono_tutor2, a.codigo_sede, s.nombre AS nombre_sede "
                     + "FROM alumnos a JOIN sedes s ON a.codigo_sede = s.codigo_sede "
                     + "WHERE a.nombre LIKE ? OR a.curso LIKE ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            String filtroBusqueda = "%" + filtro + "%";
            stmt.setString(1, filtroBusqueda);
            stmt.setString(2, filtroBusqueda);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Alumno alumno = new Alumno(
                            rs.getInt("codigo_alumno"),
                            rs.getString("nombre"),
                            rs.getString("curso"),
                            rs.getString("nombre_sede"),
                            rs.getInt("codigo_sede")
                    );
                    alumno.setNre(rs.getString("nre"));
                    alumno.setTelTutor1(rs.getString("telefono_tutor1"));
                    alumno.setTelTutor2(rs.getString("telefono_tutor2"));
                    listaFiltrada.add(alumno);
                }
            }

        } catch (SQLException e) {
            LoggerUtils.logError("ALUMNOS", "Error al buscar alumnos", e);
        }

        return listaFiltrada;
    }
    
    public int buscarCodigoXnre(String nre) {
        int codigoAlu = -1;
        String sql = "SELECT codigo_alumno FROM alumnos WHERE nre = '" + nre + "'";
        
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                codigoAlu = rs.getInt("codigo_alumno");
            }
            
        } catch (SQLException e) {
            LoggerUtils.logError("ALUMNOS", "Error en buscarCodigoXnre" + e.getMessage(), e);
        }
        
        return codigoAlu;
    }
}
