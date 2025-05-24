package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import model.Alumno;
import model.Categoria;
import model.Dispositivo;
import model.Marca;
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
        
        String sql = "SELECT d.codigo_dispositivo, d.nombre AS nombre_dispositivo, d.num_etiqueta, d.modelo, d.num_serie, d.imei"
            + " , d.codigo_marca, m.nombre AS nombre_marca, d.codigo_categoria, c.nombre AS nombre_categoria"
            + " , a.codigo_alumno, a.nombre AS nombre_alumno, a.curso, a.nre, a.codigo_sede, s.nombre AS nombre_sede"
            + " , p.fecha_inicio, p.fecha_fin"
            + " FROM prestamos p"
            + " INNER JOIN dispositivos d ON p.codigo_dispositivo = d.codigo_dispositivo"
            + " INNER JOIN alumnos a ON p.codigo_alumno = a.codigo_alumno"
            + " LEFT OUTER JOIN marcas m ON d.codigo_marca = m.codigo_marca"
            + " LEFT OUTER JOIN categorias c ON d.codigo_categoria = c.codigo_categoria"
            + " LEFT OUTER JOIN sedes s ON a.codigo_sede = s.codigo_sede";
        
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Marca marca = new Marca(rs.getInt("codigo_marca"), rs.getString("nombre_marca"));
                Categoria categoria = new Categoria(rs.getInt("codigo_categoria"), rs.getString("nombre_categoria"));
                
                Dispositivo disp = new Dispositivo(
                    rs.getInt("codigo_dispositivo"),
                    rs.getString("nombre_dispositivo"),
                    rs.getString("modelo"),
                    rs.getString("num_serie"),
                    rs.getString("imei"),
                    rs.getInt("num_etiqueta"),
                    categoria,
                    marca
                );
                
                Alumno alu = new Alumno(
                    rs.getInt("codigo_alumno"), rs.getString("nombre_alumno"), rs.getString("curso"),
                    rs.getString("nombre_sede"), rs.getInt("codigo_sede"), rs.getString("nre")
                );
                
                Prestamo prestamo = new Prestamo(
                    disp,
                    alu,
                    rs.getDate("fecha_inicio"),
                    rs.getDate("fecha_fin")
                );
                listaPrestamos.add(prestamo);
            }

        } catch (SQLException e) {
            LoggerUtils.logError("PRESTAMOS", "Error al obtener prestamos: " + e.getMessage(), e);
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
    
    public void insertarPrestamo(int codigoDisp, int codigoAlu, Date fechaini) {
        String sql = "INSERT INTO prestamos (codigo_dispositivo, codigo_alumno, fecha_inicio)"
                + " VALUES (?, ?, ?)";
        
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, codigoDisp);
            stmt.setInt(2, codigoAlu);
            stmt.setDate(3, fechaini);
            
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
    
    public Prestamo buscarPrestamoActivo(Dispositivo disp, Alumno alu) {
        Prestamo prestamo = null;
        int codDisp = disp.getCodigo();
        int codAlu = alu.getCodigo();
        
        String sql = "SELECT * FROM prestamos WHERE codigo_dispositivo = " + codDisp + " AND codigo_alumno = " + codAlu + " AND fecha_fin IS NULL";
        
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                prestamo = new Prestamo(disp, alu, rs.getDate("fecha_inicio"), null);
            }
        } catch (SQLException e) {
            mostrarAlerta2("Error de BD", "Error al buscar préstamo activo.", Alert.AlertType.ERROR);
            LoggerUtils.logError("PRESTAMOS", "Error al buscar préstamo activo: " + e.getMessage(), e);
        }
        return prestamo;
    }
}
