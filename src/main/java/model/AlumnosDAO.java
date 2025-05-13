package model;

import utils.DataBaseConection;
import utils.LoggerUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlumnosDAO {

    private Connection conn;

    // Constructor: establece la conexión a la base de datos
    public AlumnosDAO() {
        conn = DataBaseConection.getConnection();
    }

    // Obtiene la lista de sedes disponibles desde la tabla 'sedes'
    public List<Sede> obtenerSedes() {
        List<Sede> listaSedes = new ArrayList<>();
        String query = "SELECT codigo_sede, nombre FROM sedes";
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Preparar y ejecutar la consulta
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            LoggerUtils.logQuery("ALUMNOS", "Cargar sedes para el ComboBox", query);

            // Recorrer el resultado y crear objetos Sede
            while (rs.next()) {
                Sede sede = new Sede(rs.getInt("codigo_sede"), rs.getString("nombre"));
                listaSedes.add(sede);
                LoggerUtils.logInfo("ALUMNOS", "Sede cargada → Código: " + sede.getCodigoSede() + ", Nombre: " + sede.getNombre());
            }
        } catch (SQLException e) {
            LoggerUtils.logError("ALUMNOS", "Error al cargar sedes", e);
        } finally {
            // Liberar recursos
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ignored) {
            }
        }

        return listaSedes;
    }

    // Obtiene la lista de alumnos desde la base de datos, incluyendo nombre de sede
    public List<Alumno> obtenerAlumnos() {
        List<Alumno> listaAlumnos = new ArrayList<>();
        String query = "SELECT a.codigo_alumno, a.nombre, a.curso, a.codigo_sede, s.nombre AS nombre_sede "
                + "FROM alumnos a JOIN sedes s ON a.codigo_sede = s.codigo_sede";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Ejecutar la consulta
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            // Crear objetos Alumno con los resultados
            while (rs.next()) {
                Alumno alumno = new Alumno(
                        rs.getInt("codigo_alumno"),
                        rs.getString("nombre"),
                        rs.getString("curso"),
                        rs.getString("nombre_sede"),
                        rs.getInt("codigo_sede")
                );
                listaAlumnos.add(alumno);
            }
        } catch (SQLException e) {
            LoggerUtils.logError("ALUMNOS", "Error al cargar alumnos", e);
        } finally {
            // Liberar recursos
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ignored) {
            }
        }

        return listaAlumnos;
    }

    // Inserta un nuevo alumno en la base de datos
    public boolean insertarAlumno(String nombre, String curso, int codigoSede) {
        String insertSQL = "INSERT INTO alumnos (nombre, curso, codigo_sede) VALUES (?, ?, ?)";
        PreparedStatement stmt = null;

        try {
            // Preparar la consulta con los datos
            stmt = conn.prepareStatement(insertSQL);
            stmt.setString(1, nombre);
            stmt.setString(2, curso);
            stmt.setInt(3, codigoSede);

            // Ejecutar y verificar si se insertó correctamente
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                LoggerUtils.logInfo("ALUMNOS", "Alumno insertado → Nombre: " + nombre + ", Curso: " + curso + ", Sede: " + codigoSede);
                return true;
            }
        } catch (SQLException e) {
            LoggerUtils.logError("ALUMNOS", "Error al insertar alumno", e);
        } finally {
            // Liberar recursos
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ignored) {
            }
        }

        return false;
    }

    // Actualiza un alumno existente según su código
    public boolean actualizarAlumno(int codigoAlumno, String nombre, String curso, int codigoSede) {
        String updateSQL = "UPDATE alumnos SET nombre = ?, curso = ?, codigo_sede = ? WHERE codigo_alumno = ?";
        PreparedStatement stmt = null;

        try {
            // Configurar consulta con los nuevos valores
            stmt = conn.prepareStatement(updateSQL);
            stmt.setString(1, nombre);
            stmt.setString(2, curso);
            stmt.setInt(3, codigoSede);
            stmt.setInt(4, codigoAlumno);

            // Ejecutar y confirmar éxito
            int filas = stmt.executeUpdate();
            if (filas > 0) {
                LoggerUtils.logInfo("ALUMNOS", "Alumno actualizado → Código: " + codigoAlumno);
                return true;
            }
        } catch (SQLException e) {
            LoggerUtils.logError("ALUMNOS", "Error al actualizar alumno", e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ignored) {
            }
        }

        return false;
    }

    // Elimina un alumno por su código
    public boolean eliminarAlumno(int codigoAlumno) {
        String sql = "DELETE FROM alumnos WHERE codigo_alumno = ?";
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, codigoAlumno);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                LoggerUtils.logInfo("ALUMNOS", "Alumno eliminado correctamente: " + codigoAlumno);
                return true;
            }
        } catch (SQLException e) {
            LoggerUtils.logError("ALUMNOS", "Error al eliminar alumno", e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ignored) {
            }
        }

        return false;
    }

    // Elimina todos los registros de la tabla alumnos
    public int eliminarTodosAlumnos() {
        String sql = "DELETE FROM alumnos";
        int filasEliminadas = 0;
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);
            filasEliminadas = stmt.executeUpdate(); // Ejecuta eliminación masiva
            LoggerUtils.logInfo("ALUMNOS", "Total de alumnos eliminados: " + filasEliminadas);
        } catch (SQLException e) {
            LoggerUtils.logError("ALUMNOS", "Error al eliminar todos los alumnos", e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ignored) {
            }
        }

        return filasEliminadas;
    }

    // Busca alumnos filtrando por nombre o curso (LIKE %filtro%)
    public List<Alumno> buscarAlumnos(String filtro) {
        List<Alumno> listaFiltrada = new ArrayList<>();
        String query = "SELECT a.codigo_alumno, a.nombre, a.curso, a.codigo_sede, s.nombre AS nombre_sede "
                + "FROM alumnos a JOIN sedes s ON a.codigo_sede = s.codigo_sede "
                + "WHERE a.nombre LIKE ? OR a.curso LIKE ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement(query);
            String filtroBusqueda = "%" + filtro + "%";
            stmt.setString(1, filtroBusqueda);
            stmt.setString(2, filtroBusqueda);

            rs = stmt.executeQuery();

            while (rs.next()) {
                Alumno alumno = new Alumno(
                        rs.getInt("codigo_alumno"),
                        rs.getString("nombre"),
                        rs.getString("curso"),
                        rs.getString("nombre_sede"),
                        rs.getInt("codigo_sede")
                );
                listaFiltrada.add(alumno);
            }
        } catch (SQLException e) {
            LoggerUtils.logError("ALUMNOS", "Error al buscar alumnos", e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ignored) {
            }
        }

        return listaFiltrada;
    }
}
