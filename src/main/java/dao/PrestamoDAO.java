package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import model.Alumno;
import model.Dispositivo;
import model.Prestamo;
import utils.DataBaseConection;
import utils.LoggerUtils;
import static utils.Utilidades.mostrarAlerta2;

public class PrestamoDAO {
    private Connection conn;
    
    public PrestamoDAO() {
        conn = DataBaseConection.getConnection();
    }
    
    public ObservableList<Prestamo> obtenerPrestamos() {
        ObservableList<Prestamo> listaPrestamos = FXCollections.observableArrayList();
        String sql = "SELECT * FROM prestamos";
        
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Dispositivo disp = null;
                Alumno alu = null;
                
                Prestamo prestamo = new Prestamo(
                        disp,
                        alu,
                        rs.getDate("fecha_inicio"),
                        rs.getDate("fecha_fin")
                );
                listaPrestamos.add(prestamo);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            LoggerUtils.logError("PRESTAMOS", "Error al cargar prestamos: " + e.getMessage(), e);
        }    
        return listaPrestamos;
    }
    
    public void insertarPrestamo(Prestamo prestamo) {
        String sql = "INSERT INTO prestamos (codigo_dispositivo, codigo_alumno, fecha_inicio)"
                + " VALUES (?, ?, ?)";
        
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, prestamo.getDispositivo().getCodigo());
            stmt.setInt(2, prestamo.getAlumno().getCodigo());
            stmt.setDate(3, prestamo.getFecha_inicio());
            
            int filas = stmt.executeUpdate();
        } catch (SQLException e) {
            mostrarAlerta2("Error SQL", "No se pudo guardar el prestamo.\nDetalles: " + e.getMessage(), Alert.AlertType.ERROR);
            LoggerUtils.logError("PRESTAMOS", "Error al ejecutar alta de préstamo", e);
        }
    }
    
    public void actualizarPrestamo(Prestamo prestamo) {
        String sql = "UPDATE prestamos SET fecha_fin = ? WHERE codigo_dispositivo = ? AND codigo_alumno = ? AND fecha_inicio = ?";
        
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setDate(1, prestamo.getFecha_fin());
            stmt.setInt(2, prestamo.getDispositivo().getCodigo());
            stmt.setInt(3, prestamo.getAlumno().getCodigo());
            stmt.setDate(4, prestamo.getFecha_inicio());
            
            int filas = stmt.executeUpdate();
            
        } catch (SQLException e) {
            mostrarAlerta2("Error SQL", "No se pudo actualizar el préstamo.\nDetalles: " + e.getMessage(), Alert.AlertType.ERROR);
            LoggerUtils.logError("PRESTAMOS", "Error al ejecutar la actalización del préstamo", e);
        }
    }
    
    public int eliminarPrestamo(Prestamo prestamo) {
        int filas = 0;
        String sql = "DELETE FROM prestamos WHERE codigo_dispositivo = ? AND codigo_alumno = ? AND fecha_inicio = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, prestamo.getDispositivo().getCodigo());
            stmt.setInt(2, prestamo.getAlumno().getCodigo());
            stmt.setDate(3, prestamo.getFecha_inicio());
            
            filas = stmt.executeUpdate();

            if (filas > 0) {
                mostrarAlerta2("Eliminado", "Préstamo eliminado.", Alert.AlertType.INFORMATION);
                LoggerUtils.logInfo("PRESTAMOS", "Préstamo eliminado: " + prestamo.getDispositivo().getCodigo());
            } else {
                mostrarAlerta2("Error", "No se pudo eliminar el préstamo.", Alert.AlertType.ERROR);
                LoggerUtils.logInfo("PRESTAMOS", "No se eliminó ningún préstamo (dispositivo: " + prestamo.getDispositivo().getCodigo() + ")");
            }
            return filas;
            
        } catch (SQLException e) {
            mostrarAlerta2("Error de BD", "No se pudo eliminar debido a un error de base de datos.", Alert.AlertType.ERROR);
            LoggerUtils.logError("PRESTAMOS", "Error al eliminar préstamo" + e.getMessage(), e);
            return filas;
        }
    }
}
