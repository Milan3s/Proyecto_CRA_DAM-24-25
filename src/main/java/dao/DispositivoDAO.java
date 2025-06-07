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

/**
 * Clase encargada de realizar los accesos a la base de datos relacionados con la tabla de dispositivos.
 * Contiene los llamados métodos CRUD (Create, Read, Update, Delete) para tal finalidad.
 * 
 */
public class DispositivoDAO {
    private Connection conn;
    private CentroEducativo centro;
    
    public DispositivoDAO() {
        conn = DataBaseConection.getConnection();
        centro = Session.getInstance().getCentroActivo();
    }
    
    /**
     * Devuelve un ObservableList con todos los dispositivos de la tabla.
     * 
     * @return ObservableList<Dispositivo>
     */
    public ObservableList<Dispositivo> obtenerDispositivos() {
        ObservableList<Dispositivo> listaDispositivos = FXCollections.observableArrayList();
        
        String sql = "SELECT DISTINCT d.codigo_dispositivo, d.nombre, d.modelo, d.num_serie, d.fecha_adquisicion, d.mac, d.imei, d.num_etiqueta, d.coment_reg"
         + " , p.codigo_proveedor, p.nombre AS nombre_prov, a.codigo_alumno, a.nombre AS nombre_alu, a.curso"
         + " , d.codigo_categoria, d.codigo_marca, d.codigo_espacio, d.codigo_espacio, d.codigo_programa, d.prestado, d.observaciones"
         + " , c.nombre AS nombre_cat, m.nombre AS nombre_marca, e.nombre AS nombre_esp, prog.nombre AS nombre_prog"
         + " , COALESCE(s1.codigo_sede, s2.codigo_sede) AS codigo_sede, COALESCE(s1.nombre, s2.nombre) AS nombre_sede"
         + " FROM dispositivos d"
         + " LEFT OUTER JOIN proveedores p ON d.codigo_proveedor = p.codigo_proveedor"
         + " LEFT OUTER JOIN prestamos prest ON d.codigo_dispositivo = prest.codigo_dispositivo AND prest.fecha_fin IS NULL"
         + " LEFT OUTER JOIN alumnos a ON prest.codigo_alumno = a.codigo_alumno"
         + " LEFT OUTER JOIN categorias c ON d.codigo_categoria = c.codigo_categoria"
         + " LEFT OUTER JOIN marcas m ON d.codigo_marca = m.codigo_marca"
         + " LEFT OUTER JOIN espacios e ON d.codigo_espacio = e.codigo_espacio"
         + " LEFT OUTER JOIN programas_edu prog ON d.codigo_programa = prog.codigo_programa"
         + " LEFT OUTER JOIN sedes s1 ON e.codigo_sede = s1.codigo_sede"
         + " LEFT OUTER JOIN sedes s2 ON a.codigo_sede = s2.codigo_sede";
        
        if (centro != null) {
            // Si se ha establecido un centro activo, se muestran los dispositivos asociados a dicho centro
            // (por Espacio o por Alumno) y también aquellos que aún no se hayan asociado a ninguno.
            String codigoCentro = centro.getCodigoCentro();
            sql += " WHERE s1.codigo_centro = '" + codigoCentro + "' OR s2.codigo_centro = '" + codigoCentro + "'";
            sql += " OR (e.codigo_espacio IS NULL AND a.codigo_alumno IS NULL)";
        }
        
        try {
            Statement stmt = conn.createStatement(); 
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Proveedor prov = null;
                Alumno alu = null;
                Categoria categoria = null;
                Marca marca = null;
                Espacio espacio = null;
                ProgramasEdu programae = null;
                Sede sede = null;
                
                // Si el código en la base de datos es NULL, entonces rs.getInt() devuelve 0 (cero)
                // Se comprueba esto para no crear objetos con código 0 que realmente no existen en la base de datos
                if (rs.getInt("codigo_proveedor") != 0) prov = new Proveedor(rs.getInt("codigo_proveedor"), rs.getString("nombre_prov"));
                if (rs.getInt("codigo_alumno") != 0) alu = new Alumno(rs.getInt("codigo_alumno"), rs.getString("nombre_alu"), rs.getString("curso"));
                if (rs.getInt("codigo_categoria") != 0) categoria = new Categoria(rs.getInt("codigo_categoria"), rs.getString("nombre_cat"));
                if (rs.getInt("codigo_marca") != 0) marca = new Marca(rs.getInt("codigo_marca"), rs.getString("nombre_marca"));
                if (rs.getInt("codigo_espacio") != 0) espacio = new Espacio(rs.getInt("codigo_espacio"), rs.getString("nombre_esp"));
                if (rs.getInt("codigo_programa") != 0) programae = new ProgramasEdu(rs.getInt("codigo_programa"), rs.getString("nombre_prog"));
                if (rs.getInt("codigo_sede") != 0) sede = new Sede(rs.getInt("codigo_sede"), rs.getString("nombre_sede"));
                
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
                    rs.getBoolean("prestado"),
                    rs.getString("observaciones")
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
    
    /**
     * Inserta en la tabla dispositivos de la base de datos el dispositivo que se pasa como parámetro.
     * 
     * @param disp Dispositivo
     */
    public void insertarDispositivo(Dispositivo disp) {
        String sql = "INSERT INTO dispositivos (nombre, codigo_categoria, codigo_marca, modelo, num_serie, fecha_adquisicion, mac, imei, num_etiqueta"
            + ", coment_reg, codigo_proveedor, codigo_programa, codigo_espacio, prestado, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
      
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
            stmt.setString(15, disp.getObservaciones());
            
            int filas = stmt.executeUpdate();

        } catch (SQLException e) {
            mostrarAlerta2("Error SQL", "No se pudo guardar el dispositivo.\nDetalles: " + e.getMessage(), Alert.AlertType.ERROR);
            LoggerUtils.logError("DISPOSITIVOS", "Error al ejecutar alta de dispositivo", e);
        }
    }
    
    /**
     * Actualiza en la tabla dispositivos los datos del dispositivo que se pasa como parámetro.
     * 
     * @param disp Dispositivo
     */
    public void actualizarDispositivo(Dispositivo disp) {
        String sql = "UPDATE dispositivos SET nombre = ?, codigo_categoria = ?, codigo_marca = ?, modelo = ?, num_serie = ?, fecha_adquisicion = ?" 
                + ", mac = ?, imei = ?, num_etiqueta= ?, coment_reg = ?, codigo_proveedor = ?, codigo_programa = ?, codigo_espacio = ?, prestado = ?"
                + ", observaciones = ?"
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
            stmt.setString(15, disp.getObservaciones());
            stmt.setInt(16, disp.getCodigo());
            
            int filas = stmt.executeUpdate();
            
            if (filas > 0) {
                mostrarAlerta2("Éxito", "Dispostivo actualizado correctamente.", Alert.AlertType.INFORMATION);
            }
        } catch (SQLException e) {
            mostrarAlerta2("Error SQL", "No se pudo actualizar el dispositivo.\nDetalles: " + e.getMessage(), Alert.AlertType.ERROR);
            LoggerUtils.logError("DISPOSITIVOS", "Error al ejecutar la actalización del dispositivo", e);
        }
    }
    
    /**
     * Elimina de la tabla dispositivos de la base de datos el dispositivo
     * con el identificador que se pasa como parámetro.
     * 
     * @param codDisp int
     * @return int
     */
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
    
    /**
     * Actualiza el campo prestado en el dispositivo con el identificador que se pasa como parámetro.
     * 
     * @param codigoDisp int
     * @param prestado boolean
     */
    public void actualizarPrestado(int codigoDisp, boolean prestado) {
        String sql = "UPDATE dispositivos SET prestado = ? WHERE codigo_dispositivo = ?";
        
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setBoolean(1, prestado);
            stmt.setInt(2, codigoDisp);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            mostrarAlerta2("Error SQL", "No se pudo actualizar el dispositivo.\nDetalles: " + e.getMessage(), Alert.AlertType.ERROR);
            LoggerUtils.logError("DISPOSITIVOS", "Error al ejecutar el campo prestado.", e);
        }
    }
    
    /**
     * Devuelve el código del dispostivo que tenga el número de serie que se pasa como parámetro.
     * Si no se encuentra ninguno devuelve -1
     * 
     * @param numSerie String
     * @return int
     */
    public int buscarCodigoXSerie(String numSerie) {
        int codigoDisp = -1;
        String sql = "SELECT codigo_dispositivo FROM dispositivos WHERE num_serie = '" + numSerie + "'";
        
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                codigoDisp = rs.getInt("codigo_dispositivo");
            }
            
        } catch (SQLException e) {
            LoggerUtils.logError("DISPOSITIVOS", "Error en buscarCodigoXSerie" + e.getMessage(), e);
        }
        
        return codigoDisp;
    }
    
    /**
     * Método para evitar un error al asignar valor a un parámetro del PreparedStatement correspondiente
     * al identificador de un objeto si dicho objeto es null.
     * 
     * @param stmt
     * @param index
     * @param value
     * @throws SQLException 
     */
    private void setIntOrNull(PreparedStatement stmt, int index, Integer value) throws SQLException {
        if (value != null) {
            stmt.setInt(index, value);
        } else {
            stmt.setNull(index, java.sql.Types.INTEGER);
        }
    }
}
