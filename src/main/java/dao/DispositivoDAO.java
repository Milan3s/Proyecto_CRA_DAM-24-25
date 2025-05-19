package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import main.Session;
import model.Alumno;
import model.Categoria;
import model.CentroEducativo;
import model.Dispositivo;
import model.Espacio;
import model.Marca;
import model.ProgramasEdu;
import model.Proveedor;
import model.Sede;
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
        CentroEducativo centro = Session.getInstance().getCentroActivo();
        String codigoCentro;
        
        String query = "SELECT DISTINCT d.codigo_dispositivo, d.nombre, d.modelo, d.num_serie, d.fecha_adquisicion, d.mac, d.imei, d.num_etiqueta, d.coment_reg";
        query += " , p.codigo_proveedor, p.nombre AS nombre_prov, a.codigo_alumno, a.nombre AS nombre_alu";
        query += " , d.codigo_categoria, d.codigo_marca, d.codigo_espacio, d.codigo_espacio, d.codigo_programa, d.prestado";
        query += " , c.nombre AS nombre_cat, m.nombre AS nombre_marca, e.nombre AS nombre_esp, prog.nombre AS nombre_prog";
        query += " , COALESCE(s1.codigo_sede, s2.codigo_sede) AS codigo_sede , COALESCE(s1.nombre, s2.nombre) AS nombre_sede";
        query += " FROM dispositivos d";
        query += " LEFT OUTER JOIN proveedores p ON d.codigo_proveedor = p.codigo_proveedor";
        query += " LEFT OUTER JOIN prestamos prest ON d.codigo_dispositivo = prest.codigo_dispositivo AND prest.fecha_fin IS NULL";
        query += " LEFT OUTER JOIN alumnos a ON prest.codigo_alumno = a.codigo_alumno";
        query += " LEFT OUTER JOIN categorias c ON d.codigo_categoria = c.codigo_categoria";
        query += " LEFT OUTER JOIN marcas m ON d.codigo_marca = m.codigo_marca";
        query += " LEFT OUTER JOIN espacios e ON d.codigo_espacio = e.codigo_espacio";
        query += " LEFT OUTER JOIN programas_edu prog ON d.codigo_programa = prog.codigo_programa";
        query += " LEFT OUTER JOIN sedes s1 ON e.codigo_sede = s1.codigo_sede";
        query += " LEFT OUTER JOIN sedes s2 ON a.codigo_sede = s2.codigo_sede";
        
        if (centro != null) {
            // Si se ha establecido un centro activo, se muestran los dispositivos asociados a dicho centro
            // (por Espacio o por Alumno) y también aquellos que aún no se hayan asociado a ninguno.
            codigoCentro = centro.getCodigoCentro();
            query += " WHERE s1.codigo_centro = '" + codigoCentro + "' OR s2.codigo_centro = '" + codigoCentro + "'";
            query += " OR (e.codigo_espacio IS NULL AND a.codigo_alumno IS NULL)";
        }
        
        try {
            Statement stmt = conn.createStatement(); 
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {               
                Proveedor prov = new Proveedor(rs.getInt("codigo_proveedor"), rs.getString("nombre_prov"));
                Alumno alu = null;
                Categoria categoria = new Categoria(rs.getInt("codigo_categoria"), rs.getString("nombre_cat"));
                Marca marca = new Marca(rs.getInt("codigo_marca"), rs.getString("nombre_marca"));
                Espacio espacio = new Espacio(rs.getInt("codigo_espacio"), rs.getString("nombre_esp"));
                ProgramasEdu programae = new ProgramasEdu(rs.getInt("codigo_programa"), rs.getString("nombre_prog"));
                Sede sede = null;
                
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
                    rs.getString("coment_reg"),
                    categoria, marca, espacio, programae, sede,
                    rs.getBoolean("prestado")
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
        String sql = "INSERT INTO dispositivos (nombre, codigo_categoria, codigo_marca, modelo, num_serie, fecha_adquisicion, mac, imei, num_etiqueta"
            + ", coment_reg, codigo_proveedor, codigo_programa, codigo_espacio, prestado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
      
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, disp.getNombre());
            setIntOrNull(stmt, 2, (disp.getCategoria()!= null) ? disp.getCategoria().getCodigo() : null);
            setIntOrNull(stmt, 3, (disp.getMarca() != null) ? disp.getMarca().getCodigo() : null);
            stmt.setString(4, disp.getModelo());
            stmt.setString(5, disp.getNum_serie());
            stmt.setDate(6, disp.getFecha_adquisicion());
            stmt.setString(7, disp.getMac());
            stmt.setString(8, disp.getImei());
            stmt.setInt(9, disp.getNum_etiqueta());
            stmt.setString(10, disp.getComentario());
            setIntOrNull(stmt, 11, (disp.getProveedor() != null) ? disp.getProveedor().getCodigo() : null);
            setIntOrNull(stmt, 12, (disp.getProgramae() != null) ? disp.getProgramae().getCodigo() : null);
            setIntOrNull(stmt, 13, (disp.getEspacio() != null) ? disp.getEspacio().getCodigoEspacio() : null);
            stmt.setBoolean(14, disp.isPrestado());
            
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
        String sql = "UPDATE dispositivos SET nombre = ?, codigo_categoria = ?, codigo_marca = ?, modelo = ?, num_serie = ?, fecha_adquisicion = ?" 
                + ", mac = ?, imei = ?, num_etiqueta= ?, coment_reg = ?, codigo_proveedor = ?, codigo_programa = ?, codigo_espacio = ?, prestado = ?"
                + " WHERE codigo_dispositivo = ?";
        
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, disp.getNombre());
            setIntOrNull(stmt, 2, (disp.getCategoria()!= null) ? disp.getCategoria().getCodigo() : null);
            setIntOrNull(stmt, 3, (disp.getMarca() != null) ? disp.getMarca().getCodigo() : null);
            stmt.setString(4, disp.getModelo());
            stmt.setString(5, disp.getNum_serie());
            stmt.setDate(6, disp.getFecha_adquisicion());
            stmt.setString(7, disp.getMac());
            stmt.setString(8, disp.getImei());
            stmt.setInt(9, disp.getNum_etiqueta());
            stmt.setString(10, disp.getComentario());
            setIntOrNull(stmt, 11, (disp.getProveedor() != null) ? disp.getProveedor().getCodigo() : null);
            setIntOrNull(stmt, 12, (disp.getProgramae() != null) ? disp.getProgramae().getCodigo() : null);
            setIntOrNull(stmt, 13, (disp.getEspacio() != null) ? disp.getEspacio().getCodigoEspacio() : null);
            stmt.setBoolean(14, disp.isPrestado());
            stmt.setInt(15, disp.getCodigo());
            
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
