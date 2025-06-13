package dao;

import utils.DataBaseConection; // Para conectarse a la base de datos
import utils.LoggerUtils; // Para registrar errores en logs

import java.sql.*; // Clases para manejar SQL y la conexión a base de datos
import java.util.ArrayList;
import java.util.List;

import main.Session; // Para obtener información de la sesión actual
import model.CentroEducativo; // Modelo que representa un centro educativo
import model.Sede; // Modelo que representa una sede

public class SedeDAO {

    private Connection conn; // Objeto para manejar la conexión a la base de datos
    private CentroEducativo centro; // Centro educativo activo en la sesión

    // Constructor que inicializa la conexión y obtiene el centro activo de la sesión
    public SedeDAO() {
        conn = DataBaseConection.getConnection(); // Obtener conexión
        centro = Session.getInstance().getCentroActivo(); // Obtener centro activo
    }

    // Método que obtiene una lista de sedes (posiblemente filtrada por centro activo)
    public List<Sede> obtenerSede() {
        List<Sede> lista = new ArrayList<>(); // Lista donde se almacenarán las sedes
        String query = "SELECT codigo_sede, nombre, calle, localidad, cp, municipio, provincia, telefono, codigo_centro FROM sedes";

        // Si hay un centro activo, filtra las sedes por ese centro
        if (centro != null) {
            query += " WHERE codigo_centro = " + centro.getCodigoCentro();
        }

        // Ordena los resultados por código de sede ascendente
        query += " ORDER BY codigo_sede ASC";

        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement(); // Crear statement para consulta
            rs = stmt.executeQuery(query); // Ejecutar la consulta

            // Iterar sobre los resultados y crear objetos Sede para agregarlos a la lista
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
                lista.add(sede); // Añadir sede a la lista
            }
        } catch (SQLException e) {
            // Registrar cualquier error ocurrido durante la consulta
            LoggerUtils.logError("SEDES", "Error al obtener sedes", e);
        } finally {
            // Cerrar ResultSet y Statement para liberar recursos
            try { 
                if (rs != null) rs.close(); 
                if (stmt != null) stmt.close(); 
            } catch (SQLException ignored) {}
        }

        return lista; // Retornar la lista de sedes obtenidas
    }

    // Método para insertar una nueva sede con sus datos específicos
    public boolean insertarSede(String nombre, String calle, String localidad, String cp,
                                String municipio, String provincia, String telefono, int codigoCentro) {
        String sql = "INSERT INTO sedes (nombre, calle, localidad, cp, municipio, provincia, telefono, codigo_centro) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql); // Preparar la sentencia parametrizada
            stmt.setString(1, nombre);
            stmt.setString(2, calle);
            stmt.setString(3, localidad);
            stmt.setString(4, cp);
            stmt.setString(5, municipio);
            stmt.setString(6, provincia);
            stmt.setString(7, telefono);
            stmt.setInt(8, codigoCentro);

            int filas = stmt.executeUpdate(); // Ejecutar inserción
            return filas > 0; // Retorna true si se insertó al menos una fila
        } catch (SQLException e) {
            LoggerUtils.logError("SEDES", "Error al insertar sede", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return false; // Retorna false si hubo error
    }

    // Sobrecarga del método anterior para insertar usando un objeto Sede
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

    // Método para actualizar los datos de una sede existente mediante sus atributos
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

            int filas = stmt.executeUpdate(); // Ejecutar actualización
            return filas > 0; // Retorna true si se actualizó alguna fila
        } catch (SQLException e) {
            LoggerUtils.logError("SEDES", "Error al actualizar sede", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return false; // Retorna false si hubo error
    }

    // Sobrecarga para actualizar una sede usando un objeto Sede
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

    // Método para eliminar una sede dado su código
    public boolean eliminarSede(int codigo) {
        String sql = "DELETE FROM sedes WHERE codigo_sede = ?";
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, codigo);

            int filas = stmt.executeUpdate(); // Ejecutar eliminación
            return filas > 0; // Retorna true si eliminó alguna fila
        } catch (SQLException e) {
            LoggerUtils.logError("SEDES", "Error al eliminar sede" + e.getMessage(), e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return false; // Retorna false si hubo error
    }

    // Método para eliminar todas las sedes
    public int eliminarTodasSedes() {
        String sql = "DELETE FROM sedes";
        PreparedStatement stmt = null;
        int filas = 0;

        try {
            stmt = conn.prepareStatement(sql);
            filas = stmt.executeUpdate(); // Ejecutar eliminación masiva
        } catch (SQLException e) {
            LoggerUtils.logError("SEDES", "Error al eliminar todas las sedes", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return filas; // Retorna la cantidad de filas eliminadas
    }

    // Método para buscar sedes que coincidan con un filtro en varios campos
    public List<Sede> buscarSedes(String filtro) {
        List<Sede> lista = new ArrayList<>();
        String sql = "SELECT codigo_sede, nombre, calle, localidad, cp, municipio, provincia, telefono, codigo_centro " +
                "FROM sedes WHERE nombre LIKE ? OR calle LIKE ? OR localidad LIKE ? OR municipio LIKE ? " +
                "ORDER BY codigo_sede ASC";
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement(sql);
            String filtroLike = "%" + filtro + "%";
            // Se usan comodines para buscar coincidencias parciales en varios campos
            stmt.setString(1, filtroLike);
            stmt.setString(2, filtroLike);
            stmt.setString(3, filtroLike);
            stmt.setString(4, filtroLike);

            rs = stmt.executeQuery();

            // Se recorren los resultados y se agregan a la lista
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
            try { 
                if (rs != null) rs.close(); 
                if (stmt != null) stmt.close(); 
            } catch (SQLException ignored) {}
        }

        return lista; // Retorna la lista de sedes filtradas
    }

    // Método para comprobar si una sede tiene aulas asociadas (dependencias)
    public boolean tieneDependencias(int codigoSede) {
        String sql = "SELECT COUNT(*) FROM aulas WHERE codigo_sede = ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, codigoSede);
            rs = stmt.executeQuery();

            if (rs.next()) {
                // Si el número de aulas asociadas es mayor que 0, retorna true
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LoggerUtils.logError("SEDES", "Error al verificar dependencias de sede", e);
        } finally {
            try { 
                if (rs != null) rs.close(); 
                if (stmt != null) stmt.close(); 
            } catch (SQLException ignored) {}
        }

        return false; // Retorna false si no tiene dependencias o hubo error
    }
}
