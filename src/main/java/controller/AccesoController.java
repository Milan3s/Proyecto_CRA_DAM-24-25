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
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;
import utils.DataBaseConection;
import utils.PasswordHasher;

public class AccesoController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private TextField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnRegistrarse;

    private boolean validateLogin(String usuario, String hashedPassword) {
        String query = "SELECT * FROM usuarios WHERE usuario = ? AND password = ?";
        try (Connection connection = DataBaseConection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, usuario);
            preparedStatement.setString(2, hashedPassword);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Retorna true si hay un usuario con esas credenciales
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void btnActionRegistrarse(ActionEvent event) throws IOException {
        App.loadView("/views/Registro");
    }

    @FXML
    private void btnActionLogin(ActionEvent event) throws IOException {
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();

        if (usuario.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Campos vacíos", "Por favor complete todos los campos");
            return;
        }

        String hashedPassword = PasswordHasher.hashPassword(password);

        if (validateLogin(usuario, hashedPassword)) {
            redirectToInicio(usuario);
        } else {
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
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
