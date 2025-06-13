package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import model.Proveedor;
import utils.DataBaseConection;
import utils.LoggerUtils;
import static utils.Utilidades.mostrarAlerta2;

/**
 * Clase encargada de realizar los accesos a la base de datos relacionados con la tabla de proveedores.
 * Contiene los llamados métodos CRUD (Create, Read, Update, Delete) para tal finalidad.
 * 
 */
public class ProveedorDAO {
    
    private Connection conn;
    
    public ProveedorDAO() {
        conn = DataBaseConection.getConnection();
    }
    
    /**
     * Devuelve un ObservableList con todos los proveedores de la tabla.
     * 
     * @return ObservableList<Proveedor>
     */
    public ObservableList<Proveedor> obtenerProveedores() {
        ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();
        String sql = "SELECT * FROM proveedores";
        
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Proveedor proveedor = new Proveedor(
                    rs.getInt("codigo_proveedor"),
                    rs.getString("nombre"),
                    rs.getString("calle"),
                    rs.getString("localidad"),
                    rs.getString("cp"),
                    rs.getString("municipio"),
                    rs.getString("provincia"),
                    rs.getString("telefono"),
                    rs.getString("email")
                );
                listaProveedores.add(proveedor);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            LoggerUtils.logError("PROVEEDORES", "Error al cargar proveedores: " + e.getMessage(), e);
        }    
        return listaProveedores;
    }
    
    /**
     * Inserta en la tabla proveedores de la base de datos el proveedor que se pasa como parámetro.
     * 
     * @param p Proveedor
     */
    public void insertarProveedor(Proveedor p) {
        String sql = "INSERT INTO proveedores (nombre, calle, localidad, cp, municipio, provincia, telefono, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, p.getNombre());
            stmt.setString(2, p.getCalle());
            stmt.setString(3, p.getLocalidad());
            stmt.setString(4, p.getCp());
            stmt.setString(5, p.getMunicipio());
            stmt.setString(6, p.getProvincia());
            stmt.setString(7, p.getTelefono());
            stmt.setString(8, p.getEmail());
            
            int filas = stmt.executeUpdate();

        } catch (SQLException e) {
            mostrarAlerta2("Error SQL", "No se pudo guardar el proveedor.\nDetalles: " + e.getMessage(), Alert.AlertType.ERROR);
            LoggerUtils.logError("PROVEEDORES", "Error al ejecutar alta de proveedor", e);
        }    
    }
    
    /**
     * Actualiza en la tabla proveedores los datos del proveedor que se pasa como parámetro.
     * 
     * @param p Proveedor
     */
    public void actualizarProveedor(Proveedor p) {
        String sql = "UPDATE proveedores SET nombre = ?, calle = ?, localidad = ?, cp = ?, municipio = ?, provincia = ?, telefono = ?, email = ? WHERE codigo_proveedor = ?";
        
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, p.getNombre());
            stmt.setString(2, p.getCalle());
            stmt.setString(3, p.getLocalidad());
            stmt.setString(4, p.getCp());
            stmt.setString(5, p.getMunicipio());
            stmt.setString(6, p.getProvincia());
            stmt.setString(7, p.getTelefono());
            stmt.setString(8, p.getEmail());
            stmt.setInt(9, p.getCodigo());
            
            int filas = stmt.executeUpdate();

        } catch (SQLException e) {
            mostrarAlerta2("Error SQL", "No se pudo actualizar el proveedor.\nDetalles: " + e.getMessage(), Alert.AlertType.ERROR);
            LoggerUtils.logError("PROVEEDORES", "Error al ejecutar actualización de proveedor", e);
        }
    }
    
    /**
     * Elimina de la tabla proveedores de la base de datos el proveedor 
     * con el identificador que se pasa como parámetro.
     * 
     * @param codProv int
     * @return int 
     */
    public int eliminarProveedor(int codProv) {
        int filas = 0;
        String sql = "DELETE FROM proveedores WHERE codigo_proveedor = ?";
        
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, codProv);
            filas = stmt.executeUpdate();

            if (filas > 0) {
                mostrarAlerta2("Eliminado", "Proveedor eliminado.", Alert.AlertType.INFORMATION);
                LoggerUtils.logInfo("PROVEEDORES", "Proveedor eliminado: " + codProv);
            } else {
                mostrarAlerta2("Error", "No se pudo eliminar el proveedor.", Alert.AlertType.ERROR);
                LoggerUtils.logInfo("PROVEEDORES", "No se eliminó ningún proveedor (código: " + codProv + ")");
            }
            return filas;
        } catch (SQLIntegrityConstraintViolationException e) {
            mostrarAlerta2("", "No se puede eliminar debido a una restricción de clave ajena.", Alert.AlertType.ERROR);
            LoggerUtils.logError("PROVEEDORES", "Error al eliminar proveedor por clave ajena", e);
            return filas;
        
        } catch (SQLException e) {
            mostrarAlerta2("Error de BD", "No se pudo eliminar debido a un error de base de datos.", Alert.AlertType.ERROR);
            LoggerUtils.logError("PROVEEDORES", "Error al eliminar proveedor", e);
            return filas;
        }
    }
}
