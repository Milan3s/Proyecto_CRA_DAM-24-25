package controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import main.App;
import dao.AccesoRegistroDAO;
import utils.PasswordHasher;
import utils.LoggerUtils;

public class AccesoController {

    // Campos del formulario de acceso
    @FXML private TextField txtUsuario;       // Campo para ingresar nombre de usuario
    @FXML private TextField txtPassword;      // Campo para ingresar la contraseña
    @FXML private Button btnLogin;            // Botón para iniciar sesión
    @FXML private Button btnRegistrarse;      // Botón para ir a la pantalla de registro

    // DAO para validar credenciales
    private final AccesoRegistroDAO accesoRegistroDAO = new AccesoRegistroDAO();

    // Acción del botón "Registrarse"
    @FXML
    private void btnActionRegistrarse(ActionEvent event) throws IOException {
        // Registrar intento de navegación al formulario de registro
        LoggerUtils.logInfo("ACCESO", "Redirección a pantalla de registro");

        // Cargar la vista de registro
        App.loadView("/views/Registro");
    }

    // Acción del botón "Iniciar sesión"
    @FXML
    private void btnActionLogin(ActionEvent event) throws IOException {
        LoggerUtils.logSection("ACCESO");

        // Obtener los datos del formulario
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText();

        // Validar campos vacíos
        if (usuario.isEmpty() || password.isEmpty()) {
            LoggerUtils.logInfo("ACCESO", "Login fallido por campos vacíos");
            showAlert("Error", "Campos vacíos", "Por favor complete todos los campos");
            return;
        }

        // Hashear la contraseña antes de validarla
        String hashedPassword = PasswordHasher.hashPassword(password);

        // Log del intento de acceso
        LoggerUtils.logInfo("ACCESO", "Intento de login con usuario: " + usuario);

        // Validar las credenciales en la base de datos
        if (accesoRegistroDAO.validateLogin(usuario, hashedPassword)) {
            // Acceso concedido
            LoggerUtils.logInfo("ACCESO", "Inicio de sesión exitoso para el usuario: " + usuario);
            redirectToInicio(usuario); // Redirigir al dashboard
        } else {
            // Acceso denegado
            LoggerUtils.logInfo("ACCESO", "Login fallido para el usuario: " + usuario);
            showAlert("Error de inicio de sesión", null, "Usuario o contraseña incorrectos");
        }
    }

    // Redirige al panel principal (dashboard) si el login es exitoso
    private void redirectToInicio(String usuario) throws IOException {
        // Cargar el FXML del dashboard
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Dashboard.fxml"));
        Parent root = loader.load();

        // Pasar el nombre de usuario al controlador del dashboard
        DashboardController inicioController = loader.getController();
        inicioController.setUserName(usuario);

        // Obtener la ventana actual y cambiar la escena
        Stage stage = (Stage) btnLogin.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        LoggerUtils.logInfo("ACCESO", "Usuario redirigido al panel principal: " + usuario);
    }

    // Muestra una alerta al usuario
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);         // Título de la alerta
        alert.setHeaderText(header);   // Cabecera (puede ser null)
        alert.setContentText(content); // Mensaje principal
        alert.showAndWait();           // Mostrar la alerta y esperar acción del usuario
    }
}
