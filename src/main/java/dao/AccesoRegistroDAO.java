package dao;

import utils.DataBaseConection;
import utils.LoggerUtils;
import utils.PasswordHasher;

import java.sql.*;

public class AccesoRegistroDAO {

    private Connection conn;

    public AccesoRegistroDAO() {
        conn = DataBaseConection.getConnection();
    }

    // Método para registrar un nuevo usuario
    public boolean registrarUsuario(String usuario, String email, String hashedPassword) {
        String query = "INSERT INTO usuarios (usuario, email, password) VALUES (?, ?, ?)";
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(query);
            LoggerUtils.logQuery("REGISTRO", "Registrar nuevo usuario", query);

            stmt.setString(1, usuario);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            LoggerUtils.logError("REGISTRO", "Error al registrar usuario en base de datos", e);
            return false;

        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }

    // Método para validar el login de un usuario
    public boolean validateLogin(String usuario, String hashedPassword) {
        String query = "SELECT * FROM usuarios WHERE usuario = ? AND password = ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement(query);
            LoggerUtils.logQuery("ACCESO", "Validación de login para usuario: " + usuario, query);

            stmt.setString(1, usuario);
            stmt.setString(2, hashedPassword);

            rs = stmt.executeQuery();
            boolean encontrado = rs.next();

            LoggerUtils.logInfo("ACCESO", "Resultado de login → Usuario: " + usuario + ", Acceso: " + (encontrado ? "concedido" : "denegado"));
            return encontrado;

        } catch (SQLException e) {
            LoggerUtils.logError("ACCESO", "Error al validar login", e);
            return false;

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }
}
