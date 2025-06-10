package dao;

import utils.DataBaseConection; 
import utils.LoggerUtils;       
import java.sql.*;              
import java.util.ArrayList;
import java.util.List;
import model.Marca;

public class MarcaDAO {

    private Connection conn; // variable que guarda la conexión con la bd

    public MarcaDAO() { //constructor
        conn = DataBaseConection.getConnection();// obtenemos con la conexion con la bd
    }

    public List<Marca> obtenerMarcas() { // metodo para obtener todas las marcas guardadas en la tabla
        List<Marca> lista = new ArrayList<>();// creamos una lista  
        String query = "SELECT codigo_marca, nombre FROM marcas";// consulta sql que selecciona todos los registros
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();// creamos un objeto para ejecutar la consulta
            rs = stmt.executeQuery(query);// la ejecutamos y guardamos el resultado

            while (rs.next()) { // recorremos los resultados obtenidos
                Marca m = new Marca(rs.getInt("codigo_marca"), rs.getString("nombre")); //creamos un nuevo objeto con los datos de la bd
                lista.add(m); // agregamos a la lista
            }
        } catch (SQLException e) {
            LoggerUtils.logError("MARCAS", "Error al obtener marcas", e); // si ocurre un error lo registramos
        } finally {
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }// cerramos los objetos para liberar memoria

        return lista; // devolvemos la lista
    }
 // metodo para insertar una marca nueva en la bd
    public boolean insertarMarca(String nombre) {
        String query = "INSERT INTO marcas (nombre) VALUES (?)"; // consulta con un parametro(?)

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(query); // preparamos la consulta  
            stmt.setString(1, nombre); // reemplazamos el ? con el valor que recibimos

            int filas = stmt.executeUpdate(); // ejecutamos la consulta y cuantas filas afectadas

            return filas > 0; // si se añadio al menos una fila devuelve TRue
        } catch (SQLException e) {
            LoggerUtils.logError("MARCAS", "Error al insertar marca", e);// si hay error lo registramos
        } finally { // cerramos el PreparedStatement ( objetos) para liberar espacio
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return false; // si llegamos aqui hubo un problema y no se insertó
    }

    public boolean actualizarMarca(int codigo, String nombre) {
        String query = "UPDATE marcas SET nombre = ? WHERE codigo_marca = ?";

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(query);
            stmt.setString(1, nombre);
            stmt.setInt(2, codigo);

            int filas = stmt.executeUpdate();

            return filas > 0;
        } catch (SQLException e) {
            LoggerUtils.logError("MARCAS", "Error al actualizar marca", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return false;
    }

    public boolean eliminarMarca(int codigo) {
        String query = "DELETE FROM marcas WHERE codigo_marca = ?";

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, codigo);

            int filas = stmt.executeUpdate();

            return filas > 0;
        } catch (SQLException e) {
            LoggerUtils.logError("MARCAS", "Error al eliminar marca", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return false;
    }

    public int eliminarTodasMarcas() {
        String query = "DELETE FROM marcas";
        PreparedStatement stmt = null;
        int filasEliminadas = 0;

        try {
            stmt = conn.prepareStatement(query);
            filasEliminadas = stmt.executeUpdate();
        } catch (SQLException e) {
            LoggerUtils.logError("MARCAS", "Error al eliminar todas las marcas", e);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return filasEliminadas;
    }

    public List<Marca> buscarMarcas(String filtro) {
        List<Marca> lista = new ArrayList<>();
        String query = "SELECT codigo_marca, nombre FROM marcas WHERE nombre LIKE ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + filtro + "%");

            rs = stmt.executeQuery();

            while (rs.next()) {
                Marca m = new Marca(rs.getInt("codigo_marca"), rs.getString("nombre"));
                lista.add(m);
            }
        } catch (SQLException e) {
            LoggerUtils.logError("MARCAS", "Error al buscar marcas", e);
        } finally {
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }
}