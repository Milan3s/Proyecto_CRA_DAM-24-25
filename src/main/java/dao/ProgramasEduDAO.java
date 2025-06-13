package dao;

import utils.DataBaseConection;
import utils.LoggerUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.ProgramasEdu;

public class ProgramasEduDAO {

    // Objeto Connection para manejar la conexión a la base de datos
    private Connection conn;

    // Constructor que inicializa la conexión usando la clase utilitaria DataBaseConection
    public ProgramasEduDAO() {
        conn = DataBaseConection.getConnection();
    }

    /**
     * Obtiene todos los programas educativos ordenados por código ascendente.
     * @return Lista de objetos ProgramasEdu
     */
    public List<ProgramasEdu> obtenerProgramas() {
        List<ProgramasEdu> lista = new ArrayList<>();
        String query = "SELECT codigo_programa, nombre FROM programas_edu ORDER BY codigo_programa ASC";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Crear statement para ejecutar consulta
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            // Recorrer resultados y crear objetos ProgramasEdu para agregar a la lista
            while (rs.next()) {
                ProgramasEdu p = new ProgramasEdu(rs.getInt("codigo_programa"), rs.getString("nombre"));
                lista.add(p);
            }
        } catch (SQLException e) {
            // Loguear cualquier error que ocurra
            LoggerUtils.logError("PROGRAMAS_EDU", "Error al obtener programas", e);
        } finally {
            // Cerrar ResultSet y Statement para liberar recursos
            try { 
                if (rs != null) rs.close(); 
                if (stmt != null) stmt.close(); 
            } catch (SQLException ignored) {}
        }

        return lista; // Devolver lista de programas obtenidos
    }

    /**
     * Inserta un nuevo programa educativo con el nombre especificado.
     * @param nombre Nombre del programa educativo a insertar
     * @return true si se insertó correctamente, false en caso contrario
     */
    public boolean insertarPrograma(String nombre) {
        String query = "INSERT INTO programas_edu (nombre) VALUES (?)";
        PreparedStatement stmt = null;

        try {
            // Preparar la sentencia parametrizada para inserción
            stmt = conn.prepareStatement(query);
            stmt.setString(1, nombre);  // Setear el parámetro nombre

            int filas = stmt.executeUpdate(); // Ejecutar la inserción
            return filas > 0; // Retornar true si se insertó alguna fila
        } catch (SQLException e) {
            LoggerUtils.logError("PROGRAMAS_EDU", "Error al insertar programa", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return false; // Retornar false si hubo error
    }

    /**
     * Actualiza el nombre de un programa educativo dado su código.
     * @param codigo Código del programa a actualizar
     * @param nombre Nuevo nombre a asignar
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public boolean actualizarPrograma(int codigo, String nombre) {
        String query = "UPDATE programas_edu SET nombre = ? WHERE codigo_programa = ?";
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(query);
            stmt.setString(1, nombre);  // Setear nuevo nombre
            stmt.setInt(2, codigo);      // Setear código del programa a actualizar

            int filas = stmt.executeUpdate(); // Ejecutar actualización
            return filas > 0; // Retornar true si actualizó alguna fila
        } catch (SQLException e) {
            LoggerUtils.logError("PROGRAMAS_EDU", "Error al actualizar programa", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return false; // Retornar false si hubo error
    }

    /**
     * Elimina un programa educativo dado su código.
     * @param codigo Código del programa a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminarPrograma(int codigo) {
        String query = "DELETE FROM programas_edu WHERE codigo_programa = ?";
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, codigo);  // Setear código del programa a eliminar

            int filas = stmt.executeUpdate(); // Ejecutar eliminación
            return filas > 0; // Retornar true si eliminó alguna fila
        } catch (SQLException e) {
            LoggerUtils.logError("PROGRAMAS_EDU", "Error al eliminar programa", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return false; // Retornar false si hubo error
    }

    /**
     * Elimina todos los programas educativos de la tabla.
     * @return número de programas eliminados
     */
    public int eliminarTodosProgramas() {
        String query = "DELETE FROM programas_edu";
        PreparedStatement stmt = null;
        int filasEliminadas = 0;

        try {
            stmt = conn.prepareStatement(query);
            filasEliminadas = stmt.executeUpdate(); // Ejecutar eliminación masiva
        } catch (SQLException e) {
            LoggerUtils.logError("PROGRAMAS_EDU", "Error al eliminar todos los programas", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return filasEliminadas; // Retorna cantidad de filas eliminadas
    }

    /**
     * Busca programas educativos cuyo nombre contenga el filtro dado.
     * @param filtro Cadena para filtrar los nombres con LIKE
     * @return Lista de programas que coinciden con el filtro
     */
    public List<ProgramasEdu> buscarProgramas(String filtro) {
        List<ProgramasEdu> lista = new ArrayList<>();
        String query = "SELECT codigo_programa, nombre FROM programas_edu WHERE nombre LIKE ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + filtro + "%"); // Filtro con comodines para LIKE

            rs = stmt.executeQuery();

            // Recorrer resultados y agregar a la lista
            while (rs.next()) {
                ProgramasEdu p = new ProgramasEdu(rs.getInt("codigo_programa"), rs.getString("nombre"));
                lista.add(p);
            }
        } catch (SQLException e) {
            LoggerUtils.logError("PROGRAMAS_EDU", "Error al buscar programas", e);
        } finally {
            try { 
                if (rs != null) rs.close(); 
                if (stmt != null) stmt.close(); 
            } catch (SQLException ignored) {}
        }

        return lista; // Devolver lista con programas que cumplen el filtro
    }
}
