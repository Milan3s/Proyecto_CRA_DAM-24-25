package model;

import utils.DataBaseConection;
import utils.LoggerUtils;
import utils.PasswordHasher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccesoRegistroDAO {

    // Método para registrar un nuevo usuario
    public boolean registrarUsuario(String usuario, String email, String hashedPassword) {
        String query = "INSERT INTO usuarios (usuario, email, password) VALUES (?, ?, ?)";

        try (Connection connection = DataBaseConection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            LoggerUtils.logQuery("REGISTRO", "Registrar nuevo usuario", query);
            preparedStatement.setString(1, usuario);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, hashedPassword);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerUtils.logError("REGISTRO", "Error al registrar usuario en base de datos", e);
            return false;
        }
    }

    // Método para validar el login de un usuario
    public boolean validateLogin(String usuario, String hashedPassword) {
        String query = "SELECT * FROM usuarios WHERE usuario = ? AND password = ?";

        try (Connection connection = DataBaseConection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            LoggerUtils.logQuery("ACCESO", "Validación de login para usuario: " + usuario, query);

            preparedStatement.setString(1, usuario);
            preparedStatement.setString(2, hashedPassword);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                boolean encontrado = resultSet.next();
                LoggerUtils.logInfo("ACCESO", "Resultado de login → Usuario: " + usuario + ", Acceso: " + (encontrado ? "concedido" : "denegado"));
                return encontrado;
            }
        } catch (SQLException e) {
            LoggerUtils.logError("ACCESO", "Error al validar login", e);
            return false;
        }
    }
}

