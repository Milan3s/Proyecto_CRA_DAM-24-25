package model;

import utils.DataBaseConection;
import utils.LoggerUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para manejar operaciones CRUD sobre la tabla
 * centros_edu en la base de datos.
 */
public class CentroEducativoDAO {

    // Conexión a la base de datos
    private Connection conn;

    // Constructor que establece la conexión al crear el DAO
    public CentroEducativoDAO() {
        conn = DataBaseConection.getConnection();
    }

    /**
     * Obtiene todos los centros educativos de la base de datos.
     *
     * @return Lista de objetos CentroEducativo
     */
    public List<CentroEducativo> obtenerCentros() {
        List<CentroEducativo> listaCentros = new ArrayList<>();
        String query = "SELECT * FROM centros_edu";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            // Recorrer resultados y construir objetos CentroEducativo
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

        return listaCentros;
    }

    /**
     * Inserta un nuevo centro educativo en la base de datos.
     *
     * @param centro Objeto CentroEducativo con los datos a insertar
     * @return true si se insertó correctamente, false en caso contrario
     */
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
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ignored) {
            }
        }

        return false;
    }

    /**
     * Actualiza un centro educativo existente.
     *
     * @param centro Objeto CentroEducativo con los nuevos datos
     * @return true si la actualización fue exitosa
     */
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
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ignored) {
            }
        }

        return false;
    }

    /**
     * Elimina un centro educativo según su código.
     *
     * @param codigoCentro Código del centro a eliminar
     * @return true si se eliminó correctamente
     */
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
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ignored) {
            }
        }

        return false;
    }

    /**
     * Elimina todos los centros educativos de la base de datos.
     *
     * @return número de filas eliminadas
     */
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
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ignored) {
            }
        }

        return filas;
    }

    /**
     * Busca centros educativos por nombre, localidad, municipio, provincia o
     * código.
     *
     * @param filtro Texto de búsqueda
     * @return Lista filtrada de centros educativos
     */
    public List<CentroEducativo> buscarCentros(String filtro) {
        List<CentroEducativo> listaCentros = new ArrayList<>();
        String query = "SELECT * FROM centros_edu WHERE nombre LIKE ? OR localidad LIKE ? OR municipio LIKE ? OR provincia LIKE ? OR codigo_centro LIKE ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement(query);
            String searchTerm = "%" + filtro + "%";

            // Asignar el filtro a cada campo
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

        return listaCentros;
    }
}
