package dao;

import utils.DataBaseConection;
import utils.LoggerUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Marca;

public class MarcaDAO {

    // Conexión a la base de datos
    private Connection conn;

    // Constructor que inicializa la conexión usando la clase utilitaria DataBaseConection
    public MarcaDAO() {
        conn = DataBaseConection.getConnection();
    }

    // Método para obtener todas las marcas ordenadas por código ascendente
    public List<Marca> obtenerMarcas() {
        List<Marca> lista = new ArrayList<>();
        String query = "SELECT codigo_marca, nombre FROM marcas ORDER BY codigo_marca ASC";
        // Usamos try-with-resources para cerrar automáticamente Statement y ResultSet
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                // Crear objeto Marca con datos de cada fila y agregar a la lista
                Marca m = new Marca(rs.getInt("codigo_marca"), rs.getString("nombre"));
                lista.add(m);
            }
        } catch (SQLException e) {
            LoggerUtils.logError("MARCAS", "Error al obtener marcas", e);
        }
        return lista; // Devolver la lista con todas las marcas
    }

    // Método para insertar una marca, verificando si el código ya existe o está en 0
    public boolean insertarMarca(Marca marca) {
        try {
            // Si el código ya existe, obtener un código nuevo libre y asignarlo
            if (existeCodigo(marca.getCodigo())) {
                int nuevoCodigo = obtenerNuevoCodigoLibre();
                LoggerUtils.logInfo("MARCAS", "Código " + marca.getCodigo() + " ya existe. Se usará código alternativo: " + nuevoCodigo);
                marca.setCodigo(nuevoCodigo);
            } else if (marca.getCodigo() == 0) {
                // Si código es 0 (no asignado), asignar uno nuevo automáticamente
                int nuevoCodigo = obtenerNuevoCodigoLibre();
                marca.setCodigo(nuevoCodigo);
            }

            String query = "INSERT INTO marcas (codigo_marca, nombre) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, marca.getCodigo());  // Setear código
                stmt.setString(2, marca.getNombre()); // Setear nombre
                int filas = stmt.executeUpdate();
                return filas > 0; // True si insertó al menos una fila
            }

        } catch (SQLException e) {
            LoggerUtils.logError("MARCAS", "Error al insertar marca", e);
        }

        return false; // Retorna false si hubo error
    }

    // Método para actualizar el nombre de una marca según su código
    public boolean actualizarMarca(int codigo, String nombre) {
        String query = "UPDATE marcas SET nombre = ? WHERE codigo_marca = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombre); // Nuevo nombre
            stmt.setInt(2, codigo);     // Código marca a actualizar
            int filas = stmt.executeUpdate();
            return filas > 0; // True si actualizó alguna fila
        } catch (SQLException e) {
            LoggerUtils.logError("MARCAS", "Error al actualizar marca", e);
        }
        return false; // False si hubo error
    }

    // Método para eliminar una marca según su código
    public boolean eliminarMarca(int codigo) {
        String query = "DELETE FROM marcas WHERE codigo_marca = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, codigo); // Código marca a eliminar
            int filas = stmt.executeUpdate();
            return filas > 0; // True si eliminó alguna fila
        } catch (SQLException e) {
            LoggerUtils.logError("MARCAS", "Error al eliminar marca", e);
        }
        return false; // False si hubo error
    }

    // Método para eliminar todas las marcas
    public int eliminarTodasMarcas() {
        String query = "DELETE FROM marcas";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            return stmt.executeUpdate(); // Retorna número de filas eliminadas
        } catch (SQLException e) {
            LoggerUtils.logError("MARCAS", "Error al eliminar todas las marcas", e);
        }
        return 0; // Retorna 0 si hubo error
    }

    // Método para buscar marcas por nombre con filtro LIKE, ordenando por código ascendente
    public List<Marca> buscarMarcas(String filtro) {
        List<Marca> lista = new ArrayList<>();
        String query = "SELECT codigo_marca, nombre FROM marcas WHERE nombre LIKE ? ORDER BY codigo_marca ASC";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + filtro + "%"); // Parámetro para LIKE con comodines
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Marca m = new Marca(rs.getInt("codigo_marca"), rs.getString("nombre"));
                    lista.add(m);
                }
            }
        } catch (SQLException e) {
            LoggerUtils.logError("MARCAS", "Error al buscar marcas", e);
        }
        return lista; // Devuelve lista de resultados
    }

    /**
     * Inserta una lista de marcas.
     * Solo inserta las marcas que no existan exactamente.
     * Devuelve:
     *  - true si se insertó al menos una marca.
     *  - false si ninguna marca fue insertada porque todas ya existían.
     */
    public boolean insertarListaMarcas(List<Marca> lista) throws SQLException {
        int insertadas = 0;

        for (Marca m : lista) {
            // Solo insertar si no existe una marca con mismo código y nombre
            if (!existeMarcaExacta(m.getCodigo(), m.getNombre())) {
                if (insertarMarca(m)) {
                    insertadas++; // Contar marcas insertadas
                }
            }
        }

        if (insertadas == 0) {
            // No se insertó ninguna porque todas ya existían
            LoggerUtils.logWarning("MARCAS", "No se insertó ninguna marca, todas ya existían.");
            return false;
        } else {
            LoggerUtils.logInfo("MARCAS", "Importación completada. Marcas insertadas: " + insertadas);
            return true;
        }
    }

    /**
     * Verifica si existe una marca con código y nombre exactos.
     */
    public boolean existeMarcaExacta(int codigo, String nombre) throws SQLException {
        String query = "SELECT 1 FROM marcas WHERE codigo_marca = ? AND nombre = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, codigo);
            stmt.setString(2, nombre);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // True si encuentra coincidencia exacta
            }
        }
    }

    // Verifica si existe alguna marca con un nombre dado
    private boolean existeNombre(String nombre) throws SQLException {
        String query = "SELECT 1 FROM marcas WHERE nombre = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombre);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Verifica si existe alguna marca con un código dado
    private boolean existeCodigo(int codigo) throws SQLException {
        String query = "SELECT 1 FROM marcas WHERE codigo_marca = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, codigo);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Obtiene un nuevo código libre para asignar, calculando MAX(codigo) + 1
    private int obtenerNuevoCodigoLibre() throws SQLException {
        String query = "SELECT MAX(codigo_marca) AS max_codigo FROM marcas";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("max_codigo") + 1;
            } else {
                return 1; // Primer código si la tabla está vacía
            }
        }
    }

    // Método no implementado para insertar por nombre solo, lanza excepción
    public boolean insertarMarca(String nombre) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
