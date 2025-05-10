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
import model.AccesoRegistroDAO;
import utils.PasswordHasher;
import utils.LoggerUtils;

public class AccesoController {

    @FXML private TextField txtUsuario;
    @FXML private TextField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Button btnRegistrarse;

    private final AccesoRegistroDAO accesoRegistroDAO = new AccesoRegistroDAO();

    @FXML
    private void btnActionRegistrarse(ActionEvent event) throws IOException {
        LoggerUtils.logInfo("ACCESO", "Redirección a pantalla de registro");
        App.loadView("/views/Registro");
    }

    @FXML
    private void btnActionLogin(ActionEvent event) throws IOException {
        LoggerUtils.logSection("ACCESO");

        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText();

        if (usuario.isEmpty() || password.isEmpty()) {
            LoggerUtils.logInfo("ACCESO", "Login fallido por campos vacíos");
            showAlert("Error", "Campos vacíos", "Por favor complete todos los campos");
            return;
        }

        String hashedPassword = PasswordHasher.hashPassword(password);
        LoggerUtils.logInfo("ACCESO", "Intento de login con usuario: " + usuario);

        if (accesoRegistroDAO.validateLogin(usuario, hashedPassword)) {
            LoggerUtils.logInfo("ACCESO", "Inicio de sesión exitoso para el usuario: " + usuario);
            redirectToInicio(usuario);
        } else {
            LoggerUtils.logInfo("ACCESO", "Login fallido para el usuario: " + usuario);
            showAlert("Error de inicio de sesión", null, "Usuario o contraseña incorrectos");
        }
    }

    private void redirectToInicio(String usuario) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Dashboard.fxml"));
        Parent root = loader.load();

        DashboardController inicioController = loader.getController();
        inicioController.setUserName(usuario);

        Stage stage = (Stage) btnLogin.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        LoggerUtils.logInfo("ACCESO", "Usuario redirigido al panel principal: " + usuario);
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
