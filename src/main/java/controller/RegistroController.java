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
import dao.AccesoRegistroDAO;
import utils.PasswordHasher;
import utils.LoggerUtils;

/**
 * Controlador para la pantalla de registro de usuarios. Permite registrar un
 * nuevo usuario con nombre, email y contraseña.
 */
public class RegistroController {

    // =====================
    // Elementos FXML del formulario
    // =====================
    @FXML
    private TextField txtUsuario;         // Campo de entrada para el nombre de usuario
    @FXML
    private TextField txtEmail;           // Campo de entrada para el correo electrónico
    @FXML
    private PasswordField txtPassword;    // Campo de entrada para la contraseña
    @FXML
    private Button btnRegistrarse;        // Botón para registrar al usuario
    @FXML
    private Button btnLogin;              // Botón para volver a la pantalla de login

    // DAO para gestionar el acceso y registro de usuarios
    private final AccesoRegistroDAO accesoRegistroDAO = new AccesoRegistroDAO();

    // =====================
    // Acción: Volver a login
    // =====================
    @FXML
    private void btnActionLogin(ActionEvent event) throws IOException {
        LoggerUtils.logInfo("REGISTRO", "Redirección a pantalla de login");
        App.loadView("/views/Acceso"); // Carga la vista de inicio de sesión
    }

    // =====================
    // Acción: Registrar nuevo usuario
    // =====================
    @FXML
    private void btnActionRegistrarse(ActionEvent event) {
        LoggerUtils.logSection("REGISTRO");

        // Obtener valores de los campos
        String usuario = txtUsuario.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        // Validar que los campos no estén vacíos
        if (usuario.isEmpty() || email.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios");
            LoggerUtils.logInfo("REGISTRO", "Campos vacíos en intento de registro");
            return;
        }

        // Validar formato de correo electrónico
        if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            mostrarAlerta("Error", "Ingrese un correo electrónico válido");
            LoggerUtils.logInfo("REGISTRO", "Email inválido → " + email);
            return;
        }

        // Hashear la contraseña para mayor seguridad
        String hashedPassword = PasswordHasher.hashPassword(password);
        LoggerUtils.logInfo("REGISTRO", "Intentando registrar usuario: " + usuario);

        // Registrar el usuario en la base de datos
        if (accesoRegistroDAO.registrarUsuario(usuario, email, hashedPassword)) {
            mostrarAlerta("Éxito", "Usuario registrado correctamente");
            LoggerUtils.logInfo("REGISTRO", "Usuario registrado correctamente: " + usuario + ", Email: " + email);
            limpiarCampos(); // Limpiar campos tras éxito
        } else {
            mostrarAlerta("Error", "No se pudo registrar el usuario");
            LoggerUtils.logInfo("REGISTRO", "Fallo al registrar usuario: " + usuario);
        }
    }

    // =====================
    // Utilidad: Mostrar alerta al usuario
    // =====================
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // =====================
    // Utilidad: Limpiar campos del formulario
    // =====================
    private void limpiarCampos() {
        txtUsuario.clear();
        txtEmail.clear();
        txtPassword.clear();
    }
}
