package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import utils.DataBaseConection;
import utils.LoggerUtils;
import static utils.Utilidades.mostrarAlerta2;

public class DispositivoDAO {
    private Connection conn;
    
    public DispositivoDAO() {
        conn = DataBaseConection.getConnection();
    }
    
    public ObservableList<Dispositivo> obtenerDispositivos() {
        ObservableList<Dispositivo> listaDispositivos = FXCollections.observableArrayList();
        
        String query = "SELECT d.codigo_dispositivo, d.nombre, d.modelo, d.num_serie, d.fecha_adquisicion, d.mac, d.imei, d.num_etiqueta, d.coment_reg";
        query += " , p.codigo_proveedor, p.nombre AS nombre_prov, a.codigo_alumno, a.nombre AS nombre_alu";
        query += " FROM dispositivos d";
        query += " LEFT OUTER JOIN proveedores p ON d.codigo_proveedor = p.codigo_proveedor";
        query += " LEFT OUTER JOIN alumnos a ON d.codigo_alumno = a.codigo_alumno";
        
        try {
            Statement stmt = conn.createStatement(); 
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {               
                Proveedor prov = new Proveedor(rs.getInt("codigo_proveedor"), rs.getString("nombre_prov"));
                Alumno alu = null;
                Dispositivo disp = new Dispositivo(
                    rs.getInt("codigo_dispositivo"),
                    rs.getString("nombre"),
                    rs.getString("modelo"),     
                    rs.getString("num_serie"),
                    rs.getDate("fecha_adquisicion"),
                    rs.getString("mac"),
                    rs.getString("imei"),
                    rs.getInt("num_etiqueta"),
                    prov, alu, 
                    rs.getString("coment_reg")
                );
                listaDispositivos.add(disp);    
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            LoggerUtils.logError("DISPOSITIVOS", "Error al cargar dispositivos: " + e.getMessage(), e);
        }        
        return listaDispositivos;
    }
    
    public void insertarDispositivo(Dispositivo disp) {
        //String sql = "INSERT INTO dispositivos (nombre, codigo_categoria, codigo_marca, modelo, num_serie, fecha_adquisicion, mac, imei, num_etiqueta"
        //    + ", coment_reg, codigo_proveedor, codigo_programa, codigo_espacio, codigo_alumno) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sql = "INSERT INTO dispositivos (nombre, modelo, num_serie, fecha_adquisicion, mac, imei, num_etiqueta"
            + ", coment_reg, codigo_proveedor, codigo_alumno) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, disp.getNombre());
            // codigo categoria
            // codigo marca
            stmt.setString(2, disp.getModelo());
            stmt.setString(3, disp.getNum_serie());
            stmt.setDate(4, disp.getFecha_adquisicion());
            stmt.setString(5, disp.getMac());
            stmt.setString(6, disp.getImei());
            stmt.setInt(7, disp.getNum_etiqueta());
            stmt.setString(8, disp.getComentario());
            setIntOrNull(stmt, 9, (disp.getProveedor() != null) ? disp.getProveedor().getCodigo() : null);
            // codigo programa
            // codigo espacio
            setIntOrNull(stmt, 10, (disp.getAlumno() != null) ? disp.getAlumno().getCodigo() : null);
            
            int filas = stmt.executeUpdate();
            
            if (filas > 0) {
                mostrarAlerta2("Éxito", "Dispostivo guardado correctamente.", Alert.AlertType.INFORMATION);
            }
        } catch (SQLException e) {
            mostrarAlerta2("Error SQL", "No se pudo guardar el dispositivo.\nDetalles: " + e.getMessage(), Alert.AlertType.ERROR);
            LoggerUtils.logError("DISPOSITIVOS", "Error al ejecutar alta de dispositivo", e);
        }
    }
    
    public void actualizarDispositivo(Dispositivo disp) {
        /*
        String sql = "UPDATE dispositivos SET nombre = ?, codigo_categoria = ?, codigo_marca = ?, modelo = ?, num_serie = ?, fecha_adquisicion = ?" 
                + ", mac = ?, imei = ?, num_etiqueta= ?, coment_reg = ?, codigo_proveedor = ?, codigo_programa = ?, codigo_espacio = ?"
                + ", codigo_alumno = ? WHERE codigo_dispositivo = ?";
        */
        String sql = "UPDATE dispositivos SET nombre = ?, modelo = ?, num_serie = ?, fecha_adquisicion = ?" 
                + ", mac = ?, imei = ?, num_etiqueta= ?, coment_reg = ?, codigo_proveedor = ?"
                + ", codigo_alumno = ? WHERE codigo_dispositivo = ?";
        
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, disp.getNombre());
            // codigo categoria
            // codigo marca
            stmt.setString(2, disp.getModelo());
            stmt.setString(3, disp.getNum_serie());
            stmt.setDate(4, disp.getFecha_adquisicion());
            stmt.setString(5, disp.getMac());
            stmt.setString(6, disp.getImei());
            stmt.setInt(7, disp.getNum_etiqueta());
            stmt.setString(8, disp.getComentario());
            setIntOrNull(stmt, 9, (disp.getProveedor() != null) ? disp.getProveedor().getCodigo() : null);
            // codigo programa
            // codigo espacio
            setIntOrNull(stmt, 10, (disp.getAlumno() != null) ? disp.getAlumno().getCodigo() : null);
            stmt.setInt(11, disp.getCodigo());
            
            int filas = stmt.executeUpdate();
            
            if (filas > 0) {
                mostrarAlerta2("Éxito", "Dispostivo actualizado correctamente.", Alert.AlertType.INFORMATION);
            }
        } catch (SQLException e) {
            mostrarAlerta2("Error SQL", "No se pudo actualizar el dispositivo.\nDetalles: " + e.getMessage(), Alert.AlertType.ERROR);
            LoggerUtils.logError("DISPOSITIVOS", "Error al ejecutar la actalización del dispositivo", e);
        }
    }
    
    public int eliminarDispositivo(int codDisp) {
        int filas = 0;
        String sql = "DELETE FROM dispositivos WHERE codigo_dispositivo = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            LoggerUtils.logQuery("DISPOSITIVOS", "Eliminar dispositivo con código: " + codDisp, sql);

            stmt.setInt(1, codDisp);
            filas = stmt.executeUpdate();

            if (filas > 0) {
                mostrarAlerta2("Eliminado", "Dispositivo eliminado.", Alert.AlertType.INFORMATION);
                LoggerUtils.logInfo("DISPOSITIVOS", "dispositivo eliminado: " + codDisp);
            } else {
                mostrarAlerta2("Error", "No se pudo eliminar el dispositivo.", Alert.AlertType.ERROR);
                LoggerUtils.logInfo("DISPOSITIVOS", "No se eliminó ningún dispositivo (código: " + codDisp + ")");
            }
            return filas;
            
        } catch (SQLException e) {
            mostrarAlerta2("Error de BD", "No se pudo eliminar debido a un error de base de datos.", Alert.AlertType.ERROR);
            LoggerUtils.logError("DISPOSITIVOS", "Error al eliminar dispositivo" + e.getMessage(), e);
            return filas;
        }
    }
    
    private void setIntOrNull(PreparedStatement stmt, int index, Integer value) throws SQLException {
        // Método para evitar un error al asignar valor a un parámetro del PreparedStatement correspondiente
        // al identificador de un objeto si dicho objeto es null
        if (value != null) {
            stmt.setInt(index, value);
        } else {
            stmt.setNull(index, java.sql.Types.INTEGER);
        }
    }
}
