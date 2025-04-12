package controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import java.io.IOException;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import main.App;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import utils.DataBaseConection;
import utils.PasswordHasher;
import utils.LoggerUtils;

public class RegistroController {

    @FXML private TextField txtUsuario;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnRegistrarse;
    @FXML private Button btnLogin;

    @FXML
    private void btnActionLogin(ActionEvent event) throws IOException {
        LoggerUtils.logInfo("REGISTRO", "Redirección a pantalla de login");
        App.loadView("/views/Acceso");
    }

    @FXML
    private void btnActionRegistrarse(ActionEvent event) {
        LoggerUtils.logSection("REGISTRO");

        String usuario = txtUsuario.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        // Validaciones
        if (usuario.isEmpty() || email.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios");
            LoggerUtils.logInfo("REGISTRO", "Campos vacíos en intento de registro");
            return;
        }

        if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            mostrarAlerta("Error", "Ingrese un correo electrónico válido");
            LoggerUtils.logInfo("REGISTRO", "Email inválido → " + email);
            return;
        }

        if (password.length() < 6) {
            mostrarAlerta("Error", "La contraseña debe tener al menos 6 caracteres");
            LoggerUtils.logInfo("REGISTRO", "Contraseña demasiado corta para el usuario: " + usuario);
            return;
        }

        String hashedPassword = PasswordHasher.hashPassword(password);
        LoggerUtils.logInfo("REGISTRO", "Intentando registrar usuario: " + usuario);

        if (registrarUsuario(usuario, email, hashedPassword)) {
            mostrarAlerta("Éxito", "Usuario registrado correctamente");
            LoggerUtils.logInfo("REGISTRO", "Usuario registrado correctamente: " + usuario + ", Email: " + email);
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "No se pudo registrar el usuario");
            LoggerUtils.logInfo("REGISTRO", "Fallo al registrar usuario: " + usuario);
        }
    }

    private boolean registrarUsuario(String usuario, String email, String hashedPassword) {
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

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void limpiarCampos() {
        txtUsuario.clear();
        txtEmail.clear();
        txtPassword.clear();
    }
}
